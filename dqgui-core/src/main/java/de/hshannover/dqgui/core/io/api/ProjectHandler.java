package de.hshannover.dqgui.core.io.api;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import de.hshannover.dqgui.core.concurrency.CompletedTasks;
import de.hshannover.dqgui.core.model.ApplicationProperties;
import de.hshannover.dqgui.core.model.Iqm4hdFeedback;
import de.hshannover.dqgui.dbsupport.api.DatabaseEnvironments;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData;
import de.hshannover.dqgui.execution.model.Project;
import de.hshannover.dqgui.framework.model.Pointer;
import de.mvise.iqm4hd.api.ExecutionReport;

public abstract class ProjectHandler {
    private final Pointer<Project> currentProject;
    
    public ProjectHandler(Pointer<Project> currentProject) {
        this.currentProject = currentProject;
    }
    
    public final Optional<Project> project() {
        return currentProject.safeGet();
    }
    
    public abstract void dumpEnvironments(DatabaseEnvironments environments);
    public abstract DatabaseEnvironments loadEnvironments();
    public abstract void dumpReport(Iqm4hdMetaData meta, ExecutionReport report, Iqm4hdFeedback feedback, CompletedTasks completedTasks);
    public abstract void removeReport(Iqm4hdMetaData result, CompletedTasks completedTasks);
    public abstract CompletedTasks loadReports();
    public abstract void dumpLog(Iqm4hdMetaData meta, String log);
    public abstract String loadLog(Iqm4hdMetaData meta);
    public abstract ExecutionReport loadReport(Iqm4hdMetaData meta);
    public abstract boolean deleteProject(Project p, ApplicationProperties properties) throws Exception;
    public abstract boolean createProject(Project p, ApplicationProperties properties) throws Exception;
    public abstract Set<Project> listProjects(ApplicationProperties properties);
    public abstract Iqm4hdFeedback loadFeedback(Iqm4hdMetaData meta);
    public abstract void dumpFeedback(Iqm4hdMetaData meta, Iqm4hdFeedback feedback);
    public abstract DSLComponent demoteToLocal(DSLComponent c);
    public abstract DSLComponent promoteToGlobal(DSLComponent c);
    public abstract void removeReports(List<Iqm4hdMetaData> toRemove, CompletedTasks completedTasks);
}