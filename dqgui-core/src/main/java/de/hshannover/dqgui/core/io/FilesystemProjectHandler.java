package de.hshannover.dqgui.core.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.tinylog.Logger;
import de.hshannover.dqgui.core.Utility;
import de.hshannover.dqgui.core.concurrency.CompletedTasks;
import de.hshannover.dqgui.core.io.api.ProjectHandler;
import de.hshannover.dqgui.core.model.ApplicationProperties;
import de.hshannover.dqgui.core.model.Iqm4hdFeedback;
import de.hshannover.dqgui.core.serialization.ExecutionReportSerialization;
import de.hshannover.dqgui.dbsupport.api.DatabaseEnvironments;
import de.hshannover.dqgui.execution.Rethrow;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData;
import de.hshannover.dqgui.execution.model.Project;
import de.hshannover.dqgui.framework.model.Pointer;
import de.hshannover.dqgui.framework.serialization.Serialization;
import de.mvise.iqm4hd.api.ExecutionReport;

public class FilesystemProjectHandler extends ProjectHandler {
    private final ApplicationProperties properties;

    public FilesystemProjectHandler(Pointer<Project> project, ApplicationProperties properties) {
        super(project);
        this.properties = properties;
    }

    @Override
    public void dumpEnvironments(DatabaseEnvironments environments) {
        project().ifPresent(p -> Serialization.dumpUnregistered(Paths.get(p.getIdentifier()).resolve("environments.json"), environments));
    }

    @Override
    public DatabaseEnvironments loadEnvironments() {
        if(project().isPresent())
            return Serialization.recoverUnregistered(Paths.get(project().get().getIdentifier()).resolve("environments.json"), DatabaseEnvironments.class);
        throw new IllegalStateException("invalid project");
    }

    @Override
    public void removeReport(Iqm4hdMetaData result, CompletedTasks completedTasks) {
        project().ifPresent(c -> {
            Path results = Paths.get(project().get().getIdentifier()).resolve("results");
            synchronized(completedTasks) {
                Serialization.dumpUnregistered(results.resolve("finished-workers.json"), completedTasks);
            }
            try {
                Files.deleteIfExists(results.resolve("reports").resolve(result.getIdentifier() + "-report.json"));
                Files.deleteIfExists(results.resolve("logs").resolve(result.getIdentifier() + ".log"));
                Files.deleteIfExists(results.resolve("feedbacks").resolve(result.getIdentifier() + "-feedback.json"));
            } catch (IOException e) {
                Logger.error(e);
            }
        });
    }
    
    @Override
    public void dumpReport(Iqm4hdMetaData meta, ExecutionReport report, Iqm4hdFeedback feedback, CompletedTasks completedTasks) {
        project().ifPresent(c -> {
            Path results = Paths.get(project().get().getIdentifier()).resolve("results");
            synchronized(completedTasks) {
                Serialization.dumpUnregistered(results.resolve("finished-workers.json"), completedTasks);
            }
            Serialization.dumpUnregistered(results.resolve("feedbacks").resolve(meta.getIdentifier() + "-feedback.json"), feedback);
            ExecutionReportSerialization.dump(report, results.resolve("reports").resolve(meta.getIdentifier() + "-report.json"));
        });
    }

    @Override
    public CompletedTasks loadReports() {
        if(project().isPresent())
            return Serialization.recoverUnregistered(Paths.get(project().get().getIdentifier()).resolve("results").resolve("finished-workers.json"), CompletedTasks.class);
        throw new IllegalStateException("invalid project");
    }

    @Override
    public void dumpLog(Iqm4hdMetaData meta, String log) {
        if(project().isPresent()) {
            try {
                Files.write(Paths.get(project().get().getIdentifier()).resolve("results/logs").resolve(meta.getIdentifier()+".log"), log.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw Rethrow.rethrow(e);
            }
        } else {
            throw new IllegalStateException("invalid project"); 
        }
    }

    @Override
    public String loadLog(Iqm4hdMetaData meta) {
        if(project().isPresent()) {
            try {
                return new String(Files.readAllBytes(Paths.get(project().get().getIdentifier()).resolve("results/logs").resolve(meta.getIdentifier()+".log")), StandardCharsets.UTF_8);
            } catch (IOException e) {
                return "No log exists.";
            }
        }
        throw new IllegalStateException("invalid project");
    }

    @Override
    public ExecutionReport loadReport(Iqm4hdMetaData meta) {
        if(project().isPresent())
            return ExecutionReportSerialization.recover(Paths.get(project().get().getIdentifier()).resolve("results/reports").resolve(meta.getIdentifier() + "-report.json"));
        throw new IllegalStateException("invalid project");
    }
    
    @Override
    public boolean deleteProject(Project p, ApplicationProperties properties) throws Exception {
        Utility.deleteFolderRecursively(Paths.get(p.getIdentifier()));
        properties.removeFilesystemProject(p);
        return true;
    }

    @Override
    public boolean createProject(Project p, ApplicationProperties properties) throws Exception {
        Utility.prepareFilesystemProject(p);
        properties.addFilesystemProject(p);
        return true;
    }

    @Override
    public Set<Project> listProjects(ApplicationProperties properties) {
        properties.getFileSystemProjectsReadOnlyCopy().stream()
            .filter(f -> !Files.isDirectory(Paths.get(f.getIdentifier())))
            .peek(e -> Logger.warn("Removed project (PATH_NOT_FOUND): {}", e.getIdentifier()))
            .forEach(properties::removeFilesystemProject);
        return new HashSet<>(properties.getFileSystemProjectsReadOnlyCopy());
    }

    @Override
    public Iqm4hdFeedback loadFeedback(Iqm4hdMetaData meta) {
        if(project().isPresent()) {
            Path results = Paths.get(project().get().getIdentifier()).resolve("results");
            return Serialization.recoverUnregistered(results.resolve("feedbacks").resolve(meta.getIdentifier() + "-feedback.json"), Iqm4hdFeedback.class);
        }
        throw new IllegalStateException("invalid project");
    }

    @Override
    public void dumpFeedback(Iqm4hdMetaData meta, Iqm4hdFeedback feedback) {
        if(project().isPresent()) {
            Path results = Paths.get(project().get().getIdentifier()).resolve("results");
            Serialization.dumpUnregistered(results.resolve("feedbacks").resolve(meta.getIdentifier() + "-feedback.json"), feedback);
        }
    }

    @Override
    public DSLComponent demoteToLocal(DSLComponent c) {
        Path from = Paths.get(properties.getGlobalChecks()).resolve(c.getIdentifier() + ".iqm4hd");
        Path to = Paths.get(project().get().getIdentifier()).resolve("checks").resolve(c.getIdentifier() + ".iqm4hd");
        try {
            Files.move(from, to);
            return DSLComponent.of(c.getIdentifier(), c.getType(), false);
        } catch (IOException e) {
            throw Rethrow.rethrow(e);
        }
    }

    @Override
    public DSLComponent promoteToGlobal(DSLComponent c) {
        Path from = Paths.get(project().get().getIdentifier()).resolve("checks").resolve(c.getIdentifier() + ".iqm4hd");
        Path to = Paths.get(properties.getGlobalChecks()).resolve(c.getIdentifier() + ".iqm4hd");
        try {
            Files.move(from, to);
            return DSLComponent.of(c.getIdentifier(), c.getType(), true);
        } catch (IOException e) {
            throw Rethrow.rethrow(e);
        }
    }

    @Override
    public void removeReports(List<Iqm4hdMetaData> toRemove, CompletedTasks completedTasks) {
        project().ifPresent(c -> {
            Path results = Paths.get(project().get().getIdentifier()).resolve("results");
            for(Iqm4hdMetaData result : toRemove) {
                try {
                    Files.deleteIfExists(results.resolve("reports").resolve(result.getIdentifier() + "-report.json"));
                    Files.deleteIfExists(results.resolve("logs").resolve(result.getIdentifier() + ".log"));
                    Files.deleteIfExists(results.resolve("feedbacks").resolve(result.getIdentifier() + "-feedback.json"));
                } catch (IOException e) {
                    Logger.error(e);
                }
            }
            synchronized (completedTasks) {
                Serialization.dumpUnregistered(results.resolve("finished-workers.json"), completedTasks);
            }
        });
    }
}