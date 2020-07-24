package de.hshannover.dqgui.core.concurrency;

import java.io.IOException;
import de.hshannover.dqgui.core.model.ProjectHandle;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * Update a currently running task to a TextArea.
 *
 * @author Marc Herschel
 *
 */
public final class LogUpdateTask implements Runnable {
    private Iqm4hdTask task;
    private Thread t;
    private TextArea log;
    private ProjectHandle projectHandle;
    private boolean waiting;
    private volatile boolean canceled;
    private int previous = 0;

    public LogUpdateTask(TextArea log) {
        this.log = log;
    }

    public void setHandler(ProjectHandle projectHandle) {
        this.projectHandle = projectHandle;
    }

    void prepare(Iqm4hdTask task, ActiveTasks tasks) {
        this.task = task;
        this.t = tasks.getLogIdentifiers().get(task.getIdentifier());
    }

    @Override
    public void run() {
        while(task.getStateProperty().getValue() == Iqm4hdTask.State.AWAIT) {
            if(!waiting) {
                updateMessage("Currently queued.");
                waiting = true;
            }
        }

        while(!task.isCompleted()) {
            if(canceled || !InterceptablePrintStream.registered(t))
                break;
            updateMessage(InterceptablePrintStream.preview(t));
        //Update every 150ms to not spam the JavaFX application thread event queue
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {}
        }

     //Give some time to the event queue
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {}

        if(canceled)
            return;

        try {
            updateMessage(projectHandle.logFor(task.getMetadata()));
        } catch (IOException e) {
            updateMessage(e.getMessage());
        }
    }

    void cancel() {
        canceled = true;
    }

    private void updateMessage(String msg) {
        if(msg.length() == previous)
            return;
        String toAppend = msg.substring(previous, msg.length());
        previous = msg.length();
        Platform.runLater(() -> log.appendText(toAppend));
    }

}
