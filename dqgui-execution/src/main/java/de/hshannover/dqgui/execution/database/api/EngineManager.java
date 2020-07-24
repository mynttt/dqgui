package de.hshannover.dqgui.execution.database.api;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.tinylog.Logger;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

/**
 * Handles database engine registration.
 * 
 * @author myntt
 *
 */
public final class EngineManager {
    private static final String ENGINE_PKG = "de.hshannover.dqgui.engine";
    private static final String SUPERCLASS = "de.hshannover.dqgui.execution.database.api.DatabaseEngine";
    private static final List<DatabaseEngine> ALL_ENGINES = new ArrayList<>(), 
                                              IQM4HD_ENGINES = new ArrayList<>(), 
                                              REPOSITORY_ENGINES = new ArrayList<>();
    private static final Map<String, DatabaseEngine> IDENTIFIER_MAPPING = new HashMap<>();
    
    static {
        try (ScanResult scanResult =
                new ClassGraph()
                    .enableAllInfo()             
                    .whitelistPackages(ENGINE_PKG)     
                    .scan()) {
            for(ClassInfo candidate : scanResult.getAllClasses()) {
                if(!candidate.extendsSuperclass(SUPERCLASS)) continue;
                try {
                    Class<?> clazz = candidate.loadClass();
                    Constructor<?> constructor = clazz.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    DatabaseEngine instance = (DatabaseEngine) constructor.newInstance();
                    
                    if(!instance.calledInternal[0] || !instance.calledInternal[1] || !instance.calledInternal[4])
                        throw new IllegalStateException("registerIdentifier(), registerLanguage() and registerGuiSupport() must be called in the engines constructor!");
                    
                    if(instance.name() == null)
                        throw new IllegalStateException("name() must return non-null!");
                    
                    ALL_ENGINES.add(instance);
                    
                    if(instance.allowUseForIqm4hd())
                        IQM4HD_ENGINES.add(instance);
                    
                    if(instance.allowUseForRepository())
                        REPOSITORY_ENGINES.add(instance);
                    
                    IDENTIFIER_MAPPING.put(instance.uniqueIdentifier().toString(), instance);
                    
                    Logger.info("Loaded and registered engine: {} << ({})", instance, clazz.getCanonicalName());
                } catch (Exception e) {
                    Logger.error(e);
                    Logger.error("Failed to load Engine: {}", candidate.getName());
                    throw new RuntimeException("Failed to load database engine: " + candidate.getName() + "due to " + e.getClass().getSimpleName(), e);
                }
            }
        }
        Comparator<DatabaseEngine> c = (c1, c2) -> c1.name().compareTo(c2.name());
        ALL_ENGINES.sort(c); IQM4HD_ENGINES.sort(c); REPOSITORY_ENGINES.sort(c);
    }

    /**
     * @return all registered engines.
     */
    public static List<DatabaseEngine> enginesAll() {
        return Collections.unmodifiableList(ALL_ENGINES);
    }
    
    /**
     * @return engines that should be used by iqm4hd.
     */
    public static List<DatabaseEngine> enginesIqm4hd() {
        return Collections.unmodifiableList(IQM4HD_ENGINES);
    }

    /**
     * @return engines that can be used for repositories.
     */
    public static List<DatabaseEngine> enginesRepository() {
        return Collections.unmodifiableList(REPOSITORY_ENGINES);
    }

    /**
     * @param engineIdentifier to lookup for
     * @return either the engine or null if not registered with the manager.
     */
    public static DatabaseEngine forIdentifier(String engineIdentifier) {
        return IDENTIFIER_MAPPING.get(engineIdentifier.toUpperCase());
    }
}
