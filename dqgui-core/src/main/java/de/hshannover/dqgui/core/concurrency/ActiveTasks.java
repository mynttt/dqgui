package de.hshannover.dqgui.core.concurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import de.hshannover.dqgui.core.ui.TaskUiUpdateService;
import de.hshannover.dqgui.execution.database.TargetedDatabase;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData.Iqm4hdReturnCode;
import de.hshannover.dqgui.framework.api.Destructible;
import de.hshannover.dqgui.framework.model.Pointer;
import de.mvise.iqm4hd.api.ExecutionReport;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Allows management of active tasks.
 *
 * @author Marc Herschel
 *
 */
public final class ActiveTasks implements Destructible {
    private final Map<String, Thread> logIdentifiers = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, LogUpdateTask> realtimeLogging = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, Iqm4hdTask> active = Collections.synchronizedMap(new HashMap<>());
    private final ObservableList<Iqm4hdTask> activeUi = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private final CompletedTasks completed;
    private final Pointer<TaskUiUpdateService> uiUpdateService;

    public ActiveTasks(CompletedTasks completed, Pointer<TaskUiUpdateService> uiUpdateService) {
        this.completed = completed;
        this.uiUpdateService = uiUpdateService;
    }
    
    public boolean hasTasksRunning() {
        return !active.isEmpty();
    }

    public void beginRealtimeLogging(String identifier, LogUpdateTask task) {
        if(realtimeLogging.containsKey(identifier))
            throw new IllegalArgumentException("LogUpdateTask already registered for " + identifier);
        task.prepare(active.get(identifier), this);
        Thread logThread = new Thread(task);
        logThread.setDaemon(true);
        logThread.setName("log-update-"+identifier);
        logThread.start();
        realtimeLogging.put(identifier, task);
    }

    public void shutdownRealtimeLogging(String identifier) {
        LogUpdateTask t = realtimeLogging.get(identifier);
        if(t == null)
            return;
        t.cancel();
        realtimeLogging.remove(identifier);
    }

    Map<String, Thread> getLogIdentifiers() {
        return logIdentifiers;
    }

    void registerTask(Iqm4hdTask task) {
        active.put(task.getIdentifier(), task);
        Platform.runLater(() -> activeUi.add(task));
    }

    void complete(Iqm4hdTask task, ExecutionReport report, ArrayList<TargetedDatabase> calledDatabases) {
        task.getMetadata().registerCalledDatabases(calledDatabases);
        task.getMetadata().registerReturnCode(report.getExecutionResults().size() > 0 ? Iqm4hdReturnCode.ISSUES : Iqm4hdReturnCode.PASS);
        task.getMetadata().registerMessage("Task finished successfully.");
        complete(task, task.getMetadata(), report);
    }

    void complete(Iqm4hdTask task, Exception e) {
        task.getMetadata().registerReturnCode(Iqm4hdReturnCode.ERROR);
        task.getMetadata().registerMessage("Task finished with exception.");
        task.getMetadata().registerException(e);
        complete(task, task.getMetadata(), null);
    }

    private void complete(Iqm4hdTask task, Iqm4hdMetaData m, ExecutionReport report) {
        LogUpdateTask t = realtimeLogging.get(task.getIdentifier());
        if(t != null)
            t.cancel();
        active.remove(task.getIdentifier());
        Platform.runLater(() -> activeUi.remove(task));
        this.completed.complete(m, report);
        uiUpdateService.safeGet().ifPresent(s -> s.complete(task.getIdentifier(), m.getReturnCode(), m.isError()));
    }

    @Override
    public void destruct() {
        Set<String> s = new HashSet<>(realtimeLogging.keySet());
        s.forEach(this::shutdownRealtimeLogging);
    }

}