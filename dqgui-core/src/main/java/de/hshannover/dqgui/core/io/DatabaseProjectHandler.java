package de.hshannover.dqgui.core.io;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.tinylog.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.core.concurrency.CompletedTasks;
import de.hshannover.dqgui.core.io.api.ProjectHandler;
import de.hshannover.dqgui.core.model.ApplicationProperties;
import de.hshannover.dqgui.core.model.Iqm4hdFeedback;
import de.hshannover.dqgui.core.serialization.ExecutionReportSerialization;
import de.hshannover.dqgui.dbsupport.api.DatabaseEnvironments;
import de.hshannover.dqgui.execution.Rethrow;
import de.hshannover.dqgui.execution.database.api.Repository;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData;
import de.hshannover.dqgui.execution.model.Project;
import de.hshannover.dqgui.framework.model.Pointer;
import de.hshannover.dqgui.framework.serialization.Serialization;
import de.mvise.iqm4hd.api.ExecutionReport;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class DatabaseProjectHandler extends ProjectHandler {
    private static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();
    private static final Path CACHE = Config.DATABASE_REPO_CACHE;
    private final Pointer<Repository<?>> repository;

    public DatabaseProjectHandler(Pointer<Project> project, Pointer<Repository<?>> repository) {
        super(project);
        this.repository = repository;
    }

    private Path checkCache(String uuid, Project prj) {
        Path p = CACHE.resolve(uuid).resolve(prj.getIdentifier());
        if(!Files.isDirectory(p))
            try {
                Files.createDirectories(p);
            } catch (IOException e) {
                throw Rethrow.rethrow(e);
            }
        return p;
    }
    
    @Override
    public void dumpEnvironments(DatabaseEnvironments environments) {
        if(project().isPresent()) {
            try {
                repository.unsafeGet().updateDatabaseEnvironments(GSON.toJson(environments), project().get().getIdentifier());
            } catch (Exception e) {
                throw Rethrow.rethrow(e);
            }
        }
    }

    @Override
    public DatabaseEnvironments loadEnvironments() {
        if(project().isPresent()) {
            try {
                DatabaseEnvironments env = GSON.fromJson(repository.unsafeGet().fetchDatabaseEnvironments(project().get().getIdentifier()), DatabaseEnvironments.class);
                env.recoverHook();
                return env;
            } catch (Exception e) {
                throw Rethrow.rethrow(e);
            }
        }
        throw new IllegalStateException("no project loaded");
    }

    private void storeLocalReportCopy(String hash, ExecutionReport report) {
        Path p = checkCache(repository.unsafeGet().getBackingConnection().getGuid().toString(), project().get());
        try {
            ExecutionReportSerialization.dump(report, p.resolve(hash + "-report.json"));
        } catch(Exception e) {
            throw Rethrow.rethrow(e);
        }
    }
    
    private void removeLocalReportCopy(Iqm4hdMetaData meta) {
        Path p = checkCache(repository.unsafeGet().getBackingConnection().getGuid().toString(), project().get());
        try {
            Files.deleteIfExists(p.resolve(meta.getHash() + "-report.json"));
            Files.deleteIfExists(p.resolve(meta.getHash() + ".log"));
        } catch(Exception e) {
            throw Rethrow.rethrow(e);
        }
    }
    
    @Override
    public void dumpReport(Iqm4hdMetaData meta, ExecutionReport report, Iqm4hdFeedback feedback, CompletedTasks completedTasks) {
        if(project().isPresent()) {
            try {
                storeLocalReportCopy(meta.getHash(), report);
                Serialization.dumpUnregistered(checkCache(repository.unsafeGet().getBackingConnection().getGuid().toString(), project().get()).resolve("finished-workers.json"), completedTasks);
                if(!meta.isError())
                    repository.unsafeGet().writeResult(meta.getHash(), GSON.toJson(report), GSON.toJson(meta), GSON.toJson(feedback), project().get().getIdentifier()); 
            } catch (Exception e) {
                throw Rethrow.rethrow(e);
            }
        }
    }

    @Override
    public void removeReport(Iqm4hdMetaData result, CompletedTasks completedTasks) {
        if(project().isPresent()) {
            removeLocalReportCopy(result);
            try {
                repository.unsafeGet().removeResult(result.getHash(), project().get().getIdentifier());
            } catch(Exception e) {
                throw Rethrow.rethrow(e);
            }
        }
    }

    @Override
    @SuppressFBWarnings("REC_CATCH_EXCEPTION")
    public CompletedTasks loadReports() {
        if(!project().isPresent()) throw new IllegalStateException("no project loaded");
        
        try {
            Path p = checkCache(repository.unsafeGet().getBackingConnection().getGuid().toString(), project().get());
            CompletedTasks completed = Serialization.recoverUnregistered(p.resolve("finished-workers.json"), CompletedTasks.class);
            String identifier = project().get().getIdentifier();
            Repository<?> repo = repository.unsafeGet();
            Set<String> results = repo.listResults(identifier);
            List<String> download = new ArrayList<>();
            List<String> remove = new ArrayList<>();
            HashSet<String> localHashes = new HashSet<>();
            completed.getAllReports().forEach(s -> localHashes.add(s.getHash()));
            for(String result : results) {
                if(!localHashes.contains(result))
                    download.add(result);
            }
            for(String local : localHashes) {
                if(!results.contains(local))
                    remove.add(local);
            }
            List<Iqm4hdMetaData> downloaded = new ArrayList<>();
            for(String dl : download) {
                Logger.info("Downloading result from database: {}", dl);
                String[] result = repo.fetchResult(dl, identifier);
                Iqm4hdMetaData data = GSON.fromJson(result[1], Iqm4hdMetaData.class);
                ExecutionReportSerialization.dumpRaw(result[0], p.resolve(data.getHash() + "-report.json"));
                downloaded.add(data);
            }
            completed.downloadFromRepo(downloaded);
            for(String rm : remove) {
                Logger.info("Removing result from local copy (not in DB): {}", rm);
                Files.deleteIfExists( p.resolve(rm + "-report.json"));
                Files.deleteIfExists( p.resolve(rm + ".log"));
                completed.removeFromLocalCopy(rm);
            }
            Serialization.dumpUnregistered(p.resolve("finished-workers.json"), completed);
            return completed;
        } catch(Exception e) {
            throw Rethrow.rethrow(e);
        }
    }

    @Override
    public void dumpLog(Iqm4hdMetaData meta, String log) {
        if(project().isPresent()) {
            String uuid = repository.unsafeGet().getBackingConnection().getGuid().toString();
            Path p = checkCache(uuid, project().get());
            try {
                Files.write(p.resolve(meta.getHash() + ".log"), log.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw Rethrow.rethrow(e);
            }
        }
    }

    @Override
    public String loadLog(Iqm4hdMetaData meta) {
        if(project().isPresent()) {
            String uuid = repository.unsafeGet().getBackingConnection().getGuid().toString();
            Path p = checkCache(uuid, project().get()).resolve(meta.getHash()+ ".log");
            if(Files.exists(p)) {
                try {
                    return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw Rethrow.rethrow(e);
                }
            } else {
                return "Log downloaded from database. No log associated.";
            }
        }
        throw new IllegalStateException("no project loaded");
    }

    @Override
    public ExecutionReport loadReport(Iqm4hdMetaData meta) {
        if(project().isPresent()) {
            return ExecutionReportSerialization.recover(checkCache(repository.unsafeGet().getBackingConnection().getGuid().toString(), project().get()).resolve(meta.getHash() + "-report.json"));
        }
        throw new IllegalStateException("no project loaded");
    }

    @Override
    public boolean deleteProject(Project p, ApplicationProperties properties) throws Exception {
        repository.unsafeGet().deleteProject(p.getIdentifier());
        Path cache = checkCache(repository.unsafeGet().getBackingConnection().getGuid().toString(), p).resolve(p.getIdentifier());
        if(Files.isDirectory(cache)) {
            Files.list(cache).forEach(t -> {
                try {
                    Files.deleteIfExists(t);
                } catch (IOException e) {
                    Logger.error(e);
                }
            });
            Files.deleteIfExists(cache);
        }
        return true;
    }

    @Override
    public boolean createProject(Project p, ApplicationProperties properties) throws Exception {
        Objects.requireNonNull(p.getName(), "name must nut be null");
        String ident = repository.unsafeGet().createProject(p.getName(), p.getGuid(), GSON.toJson(new DatabaseEnvironments()));
        // Reflection hack to inject the identifier, since with a database it can't be known before insertion
        Field f = Project.class.getDeclaredField("identifier");
        f.setAccessible(true);
        f.set(p, ident);
        return true;
    }

    @Override
    public Set<Project> listProjects(ApplicationProperties properties) {
        if(!repository.isNull())
            try {
                return repository.unsafeGet().listProjects();
            } catch (Exception e) {
                throw Rethrow.rethrow(e);
            }
        throw new IllegalStateException("no project loaded");
    }

    @Override
    public Iqm4hdFeedback loadFeedback(Iqm4hdMetaData meta) {
        if(project().isPresent()) {
            try {
                Iqm4hdFeedback f = GSON.fromJson(repository.unsafeGet().readFeedback(project().get().getIdentifier(), meta.getHash()), Iqm4hdFeedback.class);
                return f;
            } catch (Exception e) {
                throw Rethrow.rethrow(e);
            }
        }
        throw new IllegalStateException("no project loaded");
    }

    @Override
    public void dumpFeedback(Iqm4hdMetaData meta, Iqm4hdFeedback feedback) {
        if(project().isPresent()) {
            try {
                repository.unsafeGet().writeFeedback(project().get().getIdentifier(), meta.getHash(), GSON.toJson(feedback));
            } catch (Exception e) {
                throw Rethrow.rethrow(e);
            }
        }
    }

    @Override
    public DSLComponent demoteToLocal(DSLComponent c) {
        try {
            repository.unsafeGet().demoteToLocal(project().get().getIdentifier(), c);
            return DSLComponent.of(c.getIdentifier(), c.getType(), false);
        } catch (Exception e) {
            throw Rethrow.rethrow(e);
        }
    }

    @Override
    public DSLComponent promoteToGlobal(DSLComponent c) {
        try {
            repository.unsafeGet().promoteToGlobal(project().get().getIdentifier(), c);
            return DSLComponent.of(c.getIdentifier(), c.getType(), true);
        } catch (Exception e) {
            throw Rethrow.rethrow(e);
        }
    }

    @Override
    public void removeReports(List<Iqm4hdMetaData> toRemove, CompletedTasks completedTasks) {
        toRemove.forEach(t -> removeReport(t, null));
    }
}