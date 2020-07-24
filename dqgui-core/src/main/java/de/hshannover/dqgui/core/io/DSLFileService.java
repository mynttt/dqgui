package de.hshannover.dqgui.core.io;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.google.common.collect.Lists;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.core.Utility;
import de.hshannover.dqgui.execution.ComponentSplitter;
import de.hshannover.dqgui.execution.DSLService;
import de.hshannover.dqgui.execution.DSLServiceException;
import de.hshannover.dqgui.execution.Rethrow;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import de.hshannover.dqgui.execution.model.Project;
import de.hshannover.dqgui.framework.api.Recoverable;
import de.hshannover.dqgui.framework.serialization.Serialization;
import de.mvise.iqm4hd.api.RuleService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * File System based implementation of DSLService.
 *
 * @author Marc Herschel
 * @author Kevin Duan
 */
public final class DSLFileService implements DSLService {
    private static final String EXTENSION = "iqm4hd";
    private static final String DOT_EXTENSION = "." + EXTENSION;

    private final EnumMap<DSLComponentType, Path[]> mapping = new EnumMap<>(DSLComponentType.class);
    private final Path efsPath;
    private final ExtraDataFilesystemStorage efs;
    
    private final static class ExtraDataFilesystemStorage implements Recoverable {
        private Map<String, Map<String, String>> payload = new HashMap<>();
        
        void clean(List<DSLComponent> components) {
            Set<String> exist = components.stream()
                    .map(this::convert)
                    .collect(Collectors.toSet());
            payload.keySet().removeIf(k -> !exist.contains(k));
        }
        
        void dump(DSLComponent c) {
            payload.put(convert(c), c.getExtraData());
        }
        
        DSLComponent request(DSLComponent c) {
            Map<String, String> v = payload.get(convert(c));
            if(v != null)
                c.getExtraData().putAll(v);
            return c;
        }
        
        String convert(DSLComponent c) {
            return c.getType().identifier +c.getIdentifier();
        }

        void remove(DSLComponent from) {
            payload.remove(convert(from));
        }
    }

    public DSLFileService(String globalChecks, Project project) {
        Path globalChecksPath = str2pth(globalChecks);
        Path repositoryRootPath = str2pth(project.getIdentifier());
        mapping.put(DSLComponentType.ACTION, new Path[] { assertPath(repositoryRootPath.resolve("actions")) });
        mapping.put(DSLComponentType.SOURCE, new Path[] { assertPath(repositoryRootPath.resolve("sources")) });
        mapping.put(DSLComponentType.CHECK, new Path[] { globalChecksPath, assertPath(repositoryRootPath.resolve("checks")) });
        
        this.efsPath = repositoryRootPath.resolve("fs-extra.json");
        this.efs = Serialization.recoverUnregistered(efsPath, ExtraDataFilesystemStorage.class);
    }
    
    private Path str2pth(String s) {
        return assertPath(Paths.get(s));
    }
    
    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
    private Path assertPath(Path p) {
        if(p == null || !Files.isDirectory(p))
            throw new IllegalArgumentException("Invalid path: " + p == null ? "null" : p.toAbsolutePath().toString());
        return p;
    }
    
    private Path[] of(DSLComponent where) {
        switch(where.getType()) {
        case ACTION:
        case SOURCE:
            return new Path[] { mapping.get(where.getType())[0].resolve(where.getIdentifier() + DOT_EXTENSION) };
        case CHECK:
            Path[] r = mapping.get(where.getType());
            return new Path[] { r[0].resolve(where.getIdentifier() + DOT_EXTENSION), r[1].resolve((where.getIdentifier() + DOT_EXTENSION)) };
        default:
            throw new IllegalArgumentException("Unknown Type: " + where.getType());
        }
    }
    
    private Path ofWithGlobal(DSLComponent where) {
        switch(where.getType()) {
        case ACTION:
        case SOURCE:
            return mapping.get(where.getType())[0].resolve(where.getIdentifier() + DOT_EXTENSION);
        case CHECK:
            Path[] r = mapping.get(where.getType());
            return where.isGlobal() ? r[0].resolve(where.getIdentifier() + DOT_EXTENSION) : r[1].resolve(where.getIdentifier() + DOT_EXTENSION);
        default:
            throw new IllegalArgumentException("Unknown Type: " + where.getType()); 
        }
    }

    @Override
    public boolean exists(DSLComponent where) {
        return Files.exists(ofWithGlobal(where));
    }
    
    @Override
    public boolean existsInNamespace(DSLComponent where) {
        for(Path p : of(where))
            if(Files.exists(p)) return true;
        return false;
    }

    @Override
    public boolean isGlobalCheck(String checkIdentifier) {
        return Files.exists(mapping.get(DSLComponentType.CHECK)[0].resolve(checkIdentifier + DOT_EXTENSION));
    }

    @Override
    public void create(DSLComponent where) throws DSLServiceException {
        try {
            Files.write(ofWithGlobal(where), Collections.singletonList(""), Config.APPLICATION_CHARSET);
            efs.dump(where);
        } catch (IOException | UnsupportedOperationException | SecurityException  e) {
            throw new DSLServiceException(e);
        }
        Serialization.dumpUnregistered(efsPath, efs);
    }

    @Override
    public String read(DSLComponent where) throws DSLServiceException {
        try {
            return new String(Files.readAllBytes(ofWithGlobal(where)), Config.APPLICATION_CHARSET);
        } catch (IOException | OutOfMemoryError | SecurityException e) {
            throw new DSLServiceException(e);
        }
    }

    @Override
    public void update(DSLComponent where, String toUpdate) throws DSLServiceException {
        try {
            Files.write(ofWithGlobal(where), toUpdate.getBytes(Config.APPLICATION_CHARSET));
            efs.dump(where);
        } catch (IOException | UnsupportedOperationException | SecurityException e) {
            throw new DSLServiceException(e);
        }
        Serialization.dumpUnregistered(efsPath, efs);
    }

    @Override
    public void delete(DSLComponent where) throws DSLServiceException {
        try {
            Files.delete(ofWithGlobal(where));
            efs.remove(where);
        } catch (IOException | SecurityException e) {
            throw new DSLServiceException(e);
        }
        Serialization.dumpUnregistered(efsPath, efs);
    }

    @Override
    public void move(DSLComponent from, DSLComponent to) throws DSLServiceException {
        try {
            Files.move(ofWithGlobal(from), ofWithGlobal(to));
            efs.dump(to);
            efs.remove(from);
        } catch(IOException | SecurityException | UnsupportedOperationException e) {
            throw new DSLServiceException(e);
        }
        Serialization.dumpUnregistered(efsPath, efs);
    }

    @Override
    public List<DSLComponent> discover() throws DSLServiceException {
        List<DSLComponent> components = new ArrayList<>(150);
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(String.format("glob:**.%s", EXTENSION));

        for(Map.Entry<DSLComponentType, Path[]> entry : mapping.entrySet()) {
            switch(entry.getKey()) {
            case ACTION:
            case SOURCE:
                components.addAll(walk(entry.getValue()[0], entry.getKey(), false, pathMatcher));
                break;
            case CHECK:
                components.addAll(walk(entry.getValue()[0], entry.getKey(), true, pathMatcher));
                components.addAll(walk(entry.getValue()[1], entry.getKey(), false, pathMatcher));
                break;
            default:
                throw new IllegalArgumentException("Unknown Type: " + entry.getKey());
            }
        }
        
        components.forEach(efs::request);
        efs.clean(components);
        
        return components;
    }
    
    private final List<DSLComponent> walk(Path p, DSLComponentType t, boolean global, PathMatcher pathMatcher) throws DSLServiceException {
        if(p == null) return Collections.emptyList();
        try {
            return Files.walk(p)
            .map(fp -> Utility.uncheckCall(fp::toRealPath))
            .filter(pathMatcher::matches)
            .map(Path::getFileName)
            .map(Path::toString)
            .filter(s -> s.endsWith(EXTENSION))
            .map(s -> s.substring(0, s.length()-(EXTENSION.length()+1)))
            .map(s -> DSLComponent.of(s, t, global))
            .collect(Collectors.toList());
        } catch (IOException | SecurityException e) {
            throw new DSLServiceException(e);
        }
    }

    @Override
    public RuleService createRuleService() {
        return new FileSystemRuleService(this);
    }

    @Override
    public RepositoryStatus validateRepository() {
        for(Map.Entry<DSLComponentType, Path[]> entry : mapping.entrySet()) {
            for(Path p : entry.getValue())
                if(!Files.isDirectory(p))
                    return new RepositoryStatus(String.format("Invalid mapping %s -> %s.%nPlease check your project and global check configuration.", entry.getKey(), p.toAbsolutePath()));
        }
        return new RepositoryStatus();
    }

    @Override
    public void updateExtraDataOnly(DSLComponent where) throws DSLServiceException {
        efs.dump(where);
        Serialization.dumpUnregistered(efsPath, efs);
    }

    private class FileComponentSplitter implements ComponentSplitter {
        private final DSLFileService service;
        private final List<DSLComponent> components;

        private FileComponentSplitter(DSLFileService service, List<DSLComponentType> forTypes) throws DSLServiceException {
            this.service = service;
            this.components = discover().stream()
                    .filter(f -> forTypes.contains(f.getType()))
                    .collect(Collectors.toList());
        }

        @Override
        public Iterator<ComponentSourceMapping>[] splitBy(int splitBy) {
            int n = components.size() < 30 ? components.size() : (int) (components.size() / (splitBy * 1.0)) + 1;
            List<List<DSLComponent>> partitioned = Lists.partition(components, n);
            @SuppressWarnings("unchecked")
            Iterator<ComponentSourceMapping>[] iters = (Iterator<ComponentSourceMapping>[]) new Iterator[partitioned.size()];
            for(int i = 0; i < partitioned.size(); i++) {
                final int idx = i;
                Iterator<ComponentSourceMapping> it = new Iterator<ComponentSourceMapping>() {
                    private final Iterator<DSLComponent> components = partitioned.get(idx).iterator();
                    private final DSLFileService s = service;
                    
                    @Override
                    public ComponentSourceMapping next() {
                        DSLComponent next = components.next();
                        try {
                            return new ComponentSourceMapping(next, s.read(next));
                        } catch (DSLServiceException e) {
                            throw Rethrow.rethrow(e);
                        }
                    }
                    
                    @Override
                    public boolean hasNext() {
                        return components.hasNext();
                    }
                };
                iters[i] = it;
            }
            return iters;
        }
    }
    
    @Override
    public ComponentSplitter createComponentSplitter(List<DSLComponentType> forTypes) throws DSLServiceException {
        return new FileComponentSplitter(this, forTypes);
    }
}
