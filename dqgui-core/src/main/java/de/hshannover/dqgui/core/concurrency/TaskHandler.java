package de.hshannover.dqgui.core.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import de.hshannover.dqgui.core.model.ProjectHandle;
import de.hshannover.dqgui.core.ui.TaskUiUpdateService;
import de.hshannover.dqgui.framework.model.Pointer;
import de.mvise.iqm4hd.api.RService;
import de.mvise.iqm4hd.client.RServiceRservImpl;

/**
 * Handler for iqm4hd tasks.
 *
 * @author Marc Herschel
 *
 */
public final class TaskHandler {
    private final RService SERVICE = new RServiceRservImpl();
    private final ProjectHandle projectHandle;
    private final Pointer<TaskUiUpdateService> uiUpdateService;
    private final Pointer<ActiveTasks> activeTasks;
    private final ExecutorService executor = Executors.newFixedThreadPool(4, r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
    });

    public TaskHandler(ProjectHandle projectHandle, Pointer<ActiveTasks> activeTasks, Pointer<TaskUiUpdateService> uiUpdateService) {
        this.projectHandle = projectHandle;
        this.uiUpdateService = uiUpdateService;
        this.activeTasks = activeTasks;
    }

    /**
     * Run a new IQM4HD Task.
     * The task might not start immediately if all a threshold of used threads is reached.
     * @param action to execute.
     * @param environment to execute in.
     * @param optimize true/false
     * @return created task object containing metadata.
     */
    public String runTask(String action, String environment, boolean optimize) {
        if(!projectHandle.isValidProject())
            throw new IllegalStateException("called runTask while invalid project");
        Iqm4hdTask t = new Iqm4hdTask(action, projectHandle,
                projectHandle.getCurrentEnvironment().unsafeGet().getEnvironment(environment), 
                projectHandle.getServiceProvider().getService(), 
                SERVICE, activeTasks.unsafeGet(), optimize);
        activeTasks.unsafeGet().registerTask(t);
        executor.execute(t.getRunner());
        uiUpdateService.safeGet().ifPresent(s -> s.registerNewTask(t));
        return t.getIdentifier();
    }
}
