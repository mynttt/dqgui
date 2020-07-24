package de.hshannover.dqgui.core.controllers;

import java.io.IOException;
import de.hshannover.dqgui.core.concurrency.LogUpdateTask;
import de.hshannover.dqgui.core.model.ProjectHandle;
import de.hshannover.dqgui.core.util.RemoteConnection;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData;
import de.hshannover.dqgui.execution.model.remote.RemoteError;
import de.hshannover.dqgui.execution.model.remote.RemoteProgressResponse;
import de.hshannover.dqgui.framework.AbstractWindowController;
import de.hshannover.dqgui.framework.model.ReconstructedException;
import de.hshannover.dqgui.framework.signal.SignalHandler;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

final class LivelogController extends AbstractWindowController {
    @FXML
    Label logFor;
    @FXML
    TextArea log;
    @FXML
    Button refreshButton;

    private final ProjectHandle projectHandle = null;
    
    private boolean remote;
    private Iqm4hdMetaData meta;
    private SignalHandler suicideHandler;
    
    private RemoteConnection con;
    private String host, key, projectId, jobId;

    // Local live log
    private LivelogController(Iqm4hdMetaData meta) {
        this.meta = meta;
        suicideHandler = () -> unregister();
        refreshButton.setVisible(false);
    }
    
    // Remote live log
    private LivelogController(RemoteConnection con, String host, String key, String projectId, String jobId) {
        remote = true;
        this.con = con;
        this.host = host;
        this.key = key;
        this.jobId = jobId;
        this.projectId = projectId;
    }

    @FXML
    void initialize() {
        if(!remote) {
            projectHandle.projectChangedSignal().register(suicideHandler);
            logFor.setText(String.format("Log for %s", meta.getIdentifier()));
            if(projectHandle.isActiveTask(meta.getIdentifier())) {
                projectHandle.beginRealTimeLogging(meta.getIdentifier(), new LogUpdateTask(log));
            } else {
                try {
                    log.setText(projectHandle.logFor(meta));
                } catch (IOException e) {
                    log.setText(String.format("Failed to retrieve log for %s.%n%n%s", meta.getIdentifier(), e.getMessage()));
                }
            }
        } else {
            logFor.setText("Remote log for " + jobId);
            refreshRemote();
        }
    }

    @FXML
    void refreshRemote() {
        con.requestProgress(projectId, jobId, host, key)
            .thenAccept(r -> {
                if(r instanceof RemoteError) {
                    Platform.runLater(() -> log.setText(String.format("Server Error!%n%nMessage: %s%n", ((RemoteError) r).getMessage())));
                    return;
                }
                if(r instanceof RemoteProgressResponse) {
                    Platform.runLater(() -> log.setText(((RemoteProgressResponse) r).getLog()));
                    return;
                }
                throw new AssertionError();
            })
            .exceptionally(e -> {
                ReconstructedException ex = new ReconstructedException(e);
                Platform.runLater(() -> log.setText(String.format("Client Error!%n%nException: %s%nMessage:%s%nTrace:%s", ex.getExceptionClassSimpleName(), ex.getMessage(), ex.getStacktrace())));
                return null;
            });
    }
    
    @FXML
    void close() {
        if(!remote)
            projectHandle.shutdownRealTimeLogging(meta.getIdentifier());
        unregister();
    }
    
    @Override
    public void onDestruction() {
        if(suicideHandler != null)
            projectHandle.projectChangedSignal().unregister(suicideHandler);
    }

    @Override
    protected void keyBindRegisterHook(ObservableMap<KeyCombination, Runnable> bindings) {
        bindings.put(new KeyCodeCombination(KeyCode.ESCAPE), () -> close());
    }
}
