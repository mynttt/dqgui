package de.hshannover.dqgui.core.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.tinylog.Logger;
import de.hshannover.dqgui.core.concurrency.ActiveTasks;
import de.hshannover.dqgui.core.concurrency.CompletedTasks;
import de.hshannover.dqgui.core.concurrency.LogUpdateTask;
import de.hshannover.dqgui.core.concurrency.TaskHandler;
import de.hshannover.dqgui.core.io.DSLFileService;
import de.hshannover.dqgui.core.io.DatabaseProjectHandler;
import de.hshannover.dqgui.core.io.FilesystemProjectHandler;
import de.hshannover.dqgui.core.io.api.ProjectHandler;
import de.hshannover.dqgui.core.ui.NotificationService;
import de.hshannover.dqgui.core.ui.TaskUiUpdateService;
import de.hshannover.dqgui.dbsupport.api.DatabaseEnvironments;
import de.hshannover.dqgui.execution.DSLService;
import de.hshannover.dqgui.execution.DSLService.RepositoryStatus;
import de.hshannover.dqgui.execution.database.api.Repository;
import de.hshannover.dqgui.execution.database.api.Repository.ValidationReport;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData;
import de.hshannover.dqgui.execution.model.Project;
import de.hshannover.dqgui.execution.model.RepoType;
import de.hshannover.dqgui.execution.model.remote.RemoteResult;
import de.hshannover.dqgui.framework.ErrorUtility;
import de.hshannover.dqgui.framework.model.Pointer;
import de.hshannover.dqgui.framework.signal.Signal;
import de.hshannover.dqgui.framework.signal.SignalHandler;
import de.mvise.iqm4hd.api.ExecutionReport;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.collections.ListChangeListener;

/**
 * A ProjectHandle is a global handle that manages project dependent contexts and assures that project updates are properly propagated within the DQGUI GUI domain specific layer.
 * 
 * @author Marc Herschel
 *
 */

public class ProjectHandle implements AutoCloseable {
    public static final Project UNKNOWN_PROJECT = new Project(null, "UNKNOWN", null, null);
    
    private final DSLServiceProvider serviceProvider = new DSLServiceProvider();
    private final EnumMap<RepoType, ProjectHandler> mapping = new EnumMap<>(RepoType.class);
    private final ReadOnlyBooleanWrapper activeProjectProperty = new ReadOnlyBooleanWrapper(false),
                                         validDatabaseRepositoryProperty = new ReadOnlyBooleanWrapper(false);
    private final BooleanBinding validProjectBinding = serviceProvider.validRepositoryProperty().and(activeProjectProperty); 
    private final Signal projectLoadedSignal = new Signal(), 
                         projectUnloadedSignal = new Signal(), 
                         projectChangedSignal = new Signal(),
                         projectFailedToLoadSignal = new Signal();
    private final TaskHandler taskHandler;
    
    //Project lookup cache
    private final Map<String, Project> projectLookupCached = new ConcurrentHashMap<>();
    private final Set<String> projectLookupFailed = new ConcurrentSkipListSet<String>();
    
    //Listener cache
    private final List<ListChangeListener<Iqm4hdMetaData>> metadataListListeners = Collections.synchronizedList(new ArrayList<>());
    private final List<SignalHandler> databaseEnvironmentHandlers = Collections.synchronizedList(new ArrayList<>());
    
    private final Pointer<TaskUiUpdateService> uiUpdateService = new Pointer<>();
    private final Pointer<NotificationService> uiNotificationService = new Pointer<>();
    
    //Must be freed on unload
    private final Pointer<DatabaseEnvironments> currentEnvironment = new Pointer<>();
    private final Pointer<Project> activeProject = new Pointer<>();
    private final Pointer<CompletedTasks> completed = new Pointer<>();
    private final Pointer<ActiveTasks> activeTasks = new Pointer<>();
    private final Pointer<Repository<?>> repository = new Pointer<>();

    public ProjectHandle(ApplicationProperties properties) {
        mapping.put(RepoType.FILE_SYSTEM, new FilesystemProjectHandler(activeProject, properties));
        mapping.put(RepoType.DATABASE, new DatabaseProjectHandler(activeProject, repository));
        this.taskHandler = new TaskHandler(this, activeTasks, uiUpdateService);
        
        if(properties.getRepoType() == RepoType.DATABASE) {
            properties.getSelectedDbRepo().ifPresent(con -> {
                Repository<?> r = con.getEngine().createRepository(con);
                ValidationReport rep = r.validate();
                validDatabaseRepositoryProperty.set(rep.success);
                if(rep.success) {
                    repository.set(r);
                } else {
                    properties.setSelectedProject(null);
                    properties.getSelectedDbRepo().ifPresent(db -> properties.getDbRepositories().remove(db));
                    properties.setDbRepository(null, properties.getDbRepositories());
                    Logger.error("Failed to restore database repository: {}", rep.message);
                }
            });
        }
        
        if(properties.getSelectedProject().isPresent()) {
            try {
                loadProject(properties.getSelectedProject().get(), properties);
            } catch (Exception e) {
                throw ErrorUtility.rethrow(e);
            }
        }
    }
    
    public TaskHandler getTaskHandler() {
        return taskHandler;
    }

    public DSLServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    public Pointer<DatabaseEnvironments> getCurrentEnvironment() {
        return currentEnvironment;
    }

    public Pointer<Project> getSelectedProject() {
        return activeProject;
    }
    
    public BooleanBinding validProjectBinding() {
        return validProjectBinding;
    }
    
    public ReadOnlyBooleanProperty validDatabaseRepositoryProperty() {
        return validDatabaseRepositoryProperty.getReadOnlyProperty();
    }
    
    public boolean isValidProject() {
        return validProjectBinding.getValue();
    }
    
    public boolean hasTasksRunning() {
        if(isValidProject())
            return activeTasks.unsafeGet().hasTasksRunning();
        return false;
    }
    
    public boolean isProjectLoaded(Project p) {
        return Objects.equals(p, activeProject.unsafeGet());
    }

    public Signal projectChangedSignal() {
        return projectChangedSignal;
    }
    
    public Signal projectLoadedSignal() {
        return projectLoadedSignal;
    }
    
    public Signal projectUnloadedSignal() {
        return projectUnloadedSignal;
    }
    
    public Signal projectFailedToLoadSignal() {
        return projectFailedToLoadSignal;
    }
    
    public synchronized void loadProject(Project p, ApplicationProperties properties) throws Exception {
        Objects.requireNonNull(p);
        Objects.requireNonNull(properties);
        
        if(isValidProject())
            throw new IllegalStateException("Must unload a project first before overriding another loaded project");
        if(p.getType() != properties.getRepoType())
            throw new IllegalArgumentException("RepoType not matching: Project has: " + p.getType() + " | required: " + properties.getRepoType());
        if(properties.getRepoType() == RepoType.DATABASE && repository.isNull())
            throw new IllegalStateException("RELATIONAL_DATABASE requested but Pointer<Repository<?>> is null.");
        
        try {
            activeProject.set(p);
            
            ProjectHandler handler = forProject(p.getType());
            currentEnvironment.set(handler.loadEnvironments());
            currentEnvironment.unsafeGet().getContentUpdateSignal().register(() -> handler.dumpEnvironments(currentEnvironment.unsafeGet()));
            databaseEnvironmentHandlers.forEach(h -> currentEnvironment.unsafeGet().getContentUpdateSignal().register(h));

            completed.set(handler.loadReports());
            completed.unsafeGet().registerDependencies(uiNotificationService, handler);
            metadataListListeners.forEach(l -> completed.unsafeGet().addCompletedListener(l));
            
            activeTasks.set(new ActiveTasks(completed.unsafeGet(), uiUpdateService));
            
            DSLService service;
            
            switch(p.getType()) {
            case FILE_SYSTEM:
                service = new DSLFileService(properties.getGlobalChecks(), p);
                break;
            case DATABASE:
                service = repository.unsafeGet().createDslService(p.getIdentifier());
                break;
            default:
                throw new AssertionError("Invalid repo type:" + p.getType());
            }
            
            RepositoryStatus s = serviceProvider.replaceService(service);
            
            if(!s.isValid())
                throw new IllegalArgumentException("Invalid DSLService: " + s.message());
        } catch(Exception e) {
            unloadProject(properties.getRepoType());
            projectFailedToLoadSignal.fire();
            throw ErrorUtility.rethrow(e);
        }
        
        activeProjectProperty.set(true);
        projectLoadedSignal.fire();
        projectChangedSignal.fire();
        
        Logger.info("Loaded project: {}", p);
    }
    
    public synchronized void unloadProject(RepoType currentRepository) {
        Objects.requireNonNull(currentRepository);
        
        if(hasTasksRunning())
            throw new IllegalStateException("Can't unload a project that still has tasks running");
        
        projectLookupCached.clear();
        projectLookupFailed.clear();
        
        activeProjectProperty.set(false);
        
        currentEnvironment.safeGet().ifPresent(e -> e.getContentUpdateSignal().clear());
        currentEnvironment.free();
        
        completed.safeGet().ifPresent(e -> {
            metadataListListeners.forEach(l -> e.removeCompletedListener(l));
            e.destruct();
        });
        completed.free();
        activeTasks.safeGet().ifPresent(ActiveTasks::destruct);
        activeTasks.free();
        
        if(currentRepository != RepoType.DATABASE) {
            if(!repository.isNull()) {
                try {
                    repository.unsafeGet().close();
                } catch (Exception ex) {
                    Logger.error(ex);
                }
            }
            repository.free();
        }
        
        serviceProvider.unloadService();
        activeProject.free();
        projectUnloadedSignal.fire();
        projectChangedSignal.fire();
        
        Logger.info("Unloaded project.");
    }
    
    private ProjectHandler forProject(RepoType type) {
        ProjectHandler h = mapping.get(type);
        if(h == null)
            throw new IllegalArgumentException("No handler registered for " + type);
        return h;
    }

    // Real time logging
    
    public String logFor(Iqm4hdMetaData meta) throws IOException {
        if(!isValidProject())
            throw new IllegalStateException("No project loaded");
        return forProject(activeProject.unsafeGet().getType()).loadLog(meta);
    }

    public void dumpLog(String log, Iqm4hdMetaData meta) {
        if(!isValidProject())
            throw new IllegalStateException("No project loaded");
        forProject(activeProject.unsafeGet().getType()).dumpLog(meta, log);
    }

    public void beginRealTimeLogging(String identifier, LogUpdateTask logUpdateTask) {
        if(!isValidProject())
            throw new IllegalStateException("No project loaded");
        logUpdateTask.setHandler(this);
        activeTasks.unsafeGet().beginRealtimeLogging(identifier, logUpdateTask);
    }

    public void shutdownRealTimeLogging(String identifier) {
        if(!isValidProject())
            throw new IllegalStateException("No project loaded");
        activeTasks.unsafeGet().shutdownRealtimeLogging(identifier);
    }
    
    public boolean isActiveTask(String identifier) {
        if(!isValidProject())
            throw new IllegalStateException("No project loaded");
        return false;
    }

    // Reports
    
    public void addCompletedListener(ListChangeListener<Iqm4hdMetaData> listener) {
        metadataListListeners.add(listener);
        completed.safeGet().ifPresent(c -> {
            // Remove and add to prevent listener duplication since JavaFX is scuffed
            c.removeCompletedListener(listener);
            c.addCompletedListener(listener);
        });
    }
    
    public void removeCompletedListener(ListChangeListener<Iqm4hdMetaData> listener) {
        metadataListListeners.remove(listener);
        completed.safeGet().ifPresent(c -> c.removeCompletedListener(listener));
    }
    
    public ExecutionReport requestExecutionReport(Iqm4hdMetaData meta) {
        return forProject(activeProject.safeGet()
                .orElseThrow(() -> new IllegalStateException("No project loaded")).getType())
                .loadReport(meta);
    }

    public List<Iqm4hdMetaData> getCompletedReportsNoErrors() {
        return completed.safeGet().map(c -> c.getAllReports()
                .stream()
                .filter(r -> !r.isError())
                .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }
    
    public List<Iqm4hdMetaData> getCompletedReportsWithErrors() {
        return completed.safeGet().map(CompletedTasks::getAllReports).orElse(new ArrayList<>());
    }
    
    public void removeReports(List<Iqm4hdMetaData> reports) {
        completed.safeGet().ifPresent(c -> c.removeAll(reports));
    }
    
    public void removeReport(String identifier) {
        completed.safeGet().ifPresent(c -> c.removeForIdentifier(identifier));
    }
    
    public void pruneFailedReports() {
        completed.safeGet().ifPresent(CompletedTasks::pruneFailed);
    }
    
    // Feedback
    
    public Iqm4hdFeedback requestFeedback(Iqm4hdMetaData meta) {
        if(activeProject.isNull())
            throw new IllegalStateException("no project loaded");
        return forProject(activeProject.unsafeGet().getType()).loadFeedback(meta);
    }
    
    public void dumpFeedback(Iqm4hdMetaData meta, Iqm4hdFeedback feedback) {
        if(activeProject.isNull())
            throw new IllegalStateException("no project loaded");
        forProject(activeProject.unsafeGet().getType()).dumpFeedback(meta, feedback);
    }

    // Environments
    
    public void addEnvironmentSignalHandler(SignalHandler databaseEnvironmentChange) {
        databaseEnvironmentHandlers.add(databaseEnvironmentChange);
        currentEnvironment.safeGet().ifPresent(e -> e.getContentUpdateSignal().registerIfAbsent(databaseEnvironmentChange));
    }
    
    public void removeEnvironmentSignal(SignalHandler databaseEnvironmentChange) {
        databaseEnvironmentHandlers.remove(databaseEnvironmentChange);
        currentEnvironment.safeGet().ifPresent(e -> e.getContentUpdateSignal().unregister(databaseEnvironmentChange));
    }

    // UI Services
    
    public void setUiUpdateService(TaskUiUpdateService taskUpdateService) {
        uiUpdateService.set(taskUpdateService);
    }

    public void setNotificationService(NotificationService notificationService) {
        uiNotificationService.set(notificationService);
    }

    // Support
    
    public void registerDatabaseRepository(Repository<?> repository) {
        if(repository == null) {
            if(!this.repository.isNull())
                try {
                    this.repository.unsafeGet().close();
                } catch (Exception e) {
                    Logger.error(e);
                }
            this.repository.free();
            validDatabaseRepositoryProperty.set(false);
            return;
        }
        if(this.repository.isNull()) {
            this.repository.set(repository);
            validDatabaseRepositoryProperty.set(true);
        } else {
            if(this.repository.unsafeGet() == repository)
                return;
            try {
                this.repository.unsafeGet().close();
            } catch (Exception e) {
                Logger.error(e);
            }
            this.repository.set(repository);
            validDatabaseRepositoryProperty.set(true);
        }
    }
    
    @Override
    public void close() throws Exception {
        if(!repository.isNull()) {
            repository.unsafeGet().close();
        }
    }
    
    // Helpers

    public Project lookupProjectId(ApplicationProperties properties, String guid) {
        Project prj = projectLookupCached.get(guid);
        if(prj != null) return prj;
        if(projectLookupFailed.contains(guid)) return UNKNOWN_PROJECT;
        return forProject(properties.getRepoType()).listProjects(properties)
                .stream()
                .peek(p -> projectLookupCached.putIfAbsent(p.getGuid(), p))
                .filter(p -> Objects.equals(guid, p.getGuid()))
                .findFirst()
                .map(Function.identity())
                .orElse(UNKNOWN_PROJECT);
    }
    
    public boolean createProject(ApplicationProperties properties, Project p) throws Exception {
        return forProject(p.getType()).createProject(p, properties);
    }
    
    public boolean deleteProject(ApplicationProperties properties, Project p) throws Exception {
        return forProject(p.getType()).deleteProject(p, properties);
    }

    public Set<Project> listProjects(ApplicationProperties properties) {
        return forProject(properties.getRepoType()).listProjects(properties);
    }
    
    public void addRemoteResult(RemoteResult result) {
        if(!isValidProject())
            throw new IllegalArgumentException("invalid project loaded");
        Logger.info("Collected: {}", result.getJobId());
        dumpLog(result.getLog(), result.getMeta());
        completed.unsafeGet().complete(result);
    }

    public void verifyRepoType(ApplicationProperties properties) {
        if(!isValidProject()) {
            if(properties.getRepoType() != RepoType.DATABASE && !repository.isNull())
                try {
                    repository.unsafeGet().close();
                } catch (Exception e) {
                    Logger.error(e);
                }
        } else {
            if(properties.getRepoType() != activeProject.unsafeGet().getType()) {
                unloadProject(properties.getRepoType());
                properties.setSelectedProject(null);
            }
        }
    }
    
    // Promotion / Demotion

    public DSLComponent demoteToLocal(DSLComponent c, ApplicationProperties p) {
        if(c.getType() != DSLComponentType.CHECK)
            throw new IllegalArgumentException("Only CHECK can be demoted");
        if(isValidProject())
            return forProject(p.getRepoType()).demoteToLocal(c);
        throw new IllegalStateException("No project loaded");
    }

    public DSLComponent promoteToGlobal(DSLComponent c, ApplicationProperties p) {
        if(c.getType() != DSLComponentType.CHECK)
            throw new IllegalArgumentException("Only CHECK can be promoted");
        if(isValidProject())
            return forProject(p.getRepoType()).promoteToGlobal(c);
        throw new IllegalStateException("No project loaded");
    }

    public void reloadEnvironment() {
        if(!isValidProject()) return;
        currentEnvironment.set(forProject(activeProject.unsafeGet().getType()).loadEnvironments());
    }

    public void reloadReports() {
        if(!isValidProject()) return;
        List<Iqm4hdMetaData> reports = forProject(activeProject.unsafeGet().getType()).loadReports().getAllReports();
        completed.unsafeGet().syncReports(reports);
    }
}