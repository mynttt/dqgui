package de.hshannover.dqgui.core.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.tinylog.Logger;
import de.hshannover.dqgui.core.Utility;
import de.hshannover.dqgui.core.configuration.Views;
import de.hshannover.dqgui.core.model.ApplicationProperties;
import de.hshannover.dqgui.core.model.ProjectHandle;
import de.hshannover.dqgui.core.util.ExceptionRecoveryTips;
import de.hshannover.dqgui.core.util.NotificationTools;
import de.hshannover.dqgui.core.util.RemoteConnection;
import de.hshannover.dqgui.execution.model.remote.RemoteError;
import de.hshannover.dqgui.execution.model.remote.RemoteStatusReport;
import de.hshannover.dqgui.execution.model.remote.RemoteStatusReports;
import de.hshannover.dqgui.framework.AbstractWindowController;
import de.hshannover.dqgui.framework.signal.SignalHandler;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.util.Duration;

final class RemoteExecutionController extends AbstractWindowController {
    @FXML TextField ip, key;
    @FXML Button connectionButton, reloadButton, disconnectButton, downloadButton;
    @FXML Label status;
    @FXML TableView<RemoteStatusReport> table;
    @FXML TableColumn<RemoteStatusReport, String> action, environment, project, state, submitted;
    
    /*
     * Injected by ApplicationContext
     */
    private final ApplicationProperties properties = null;
    private final RemoteConnection remoteConnection = null;
    private final ProjectHandle projectHandle = null;
    
    private final ObservableList<RemoteStatusReport> statusReports = FXCollections.observableArrayList();
    private final SignalHandler remoteConnectionUpdateHandler = this::listRemoteJobs;
    private final ReadOnlyBooleanWrapper running = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyObjectWrapper<ConnectionStates> states = new ReadOnlyObjectWrapper<>();
    private final ChangeListener<Boolean> conListener = (obs, o, n) -> {
        states.set(n ? ConnectionStates.CONNECTED : ConnectionStates.DISCONNECTED);
    };
    
    private enum ConnectionStates {
        UNSET, DISCONNECTED, CONNECTED;
    }
    
    @FXML
    void initialize() {
        remoteConnection.getJobsChangedSignal().register(remoteConnectionUpdateHandler);
        
        states.addListener((obs, o, n) -> {
            status.setId(n == ConnectionStates.CONNECTED ? "success" : "connection-error");
            switch(n) {
            case CONNECTED:
                status.setText("Connected to event socket.");
                break;
            case DISCONNECTED:
                status.setText("Disconnected from event socket. No auto collect.");
                break;
            case UNSET:
                status.setText("No connection set.");
                break;
            default:
                break;
            }
        });
        
        table.setItems(statusReports);
        table.setPlaceholder(new Label("No jobs running on server"));
        remoteConnection.isConnectedWithEventSocket().addListener(conListener);
        
        MenuItem viewLiveLog = new MenuItem("View Livelog");
        viewLiveLog.setOnAction(e -> {
            RemoteStatusReport r = table.getSelectionModel().getSelectedItem();
            if(r == null) return;
            getContext().loadIdentified(Views.LIVELOG, r.getJobId(), "Remote Livelog - " + r.getJobId(), 
                    remoteConnection, 
                    properties.getRemoteHost(), 
                    properties.getRemoteKey(), 
                    r.getProjectGuid(),
                    r.getJobId());
        });
        
        table.setContextMenu(new ContextMenu(viewLiveLog));
        
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        
        action.setCellValueFactory(new PropertyValueFactory<>("action"));
        environment.setCellValueFactory(new PropertyValueFactory<>("environment"));
        state.setCellValueFactory(new PropertyValueFactory<>("state"));
        submitted.setCellValueFactory(c -> new SimpleStringProperty(f.format(LocalDateTime.ofInstant(c.getValue().getSubmitted(), ZoneId.systemDefault()))));
        project.setCellValueFactory(c -> new SimpleStringProperty(projectHandle.lookupProjectId(properties, c.getValue().getProjectGuid()).getName()));
        
        BooleanBinding hasText = ip.textProperty().isEmpty().or(key.textProperty().isEmpty()).or(
                Bindings.createBooleanBinding(() -> ip.getText().trim().isEmpty(), ip.textProperty())
                .or(Bindings.createBooleanBinding(() -> key.getText().trim().isEmpty(), key.textProperty()))
                .or(running));
       
        connectionButton.disableProperty().bind(hasText);
        reloadButton.disableProperty().bind(properties.serverSetProperty().not().or(running));
        disconnectButton.disableProperty().bind(remoteConnection.isConnectedWithEventSocket().not().or(running));
        downloadButton.disableProperty().bind(properties.serverSetProperty().not().or(running).or(projectHandle.validProjectBinding().not()));
        
        states.set(ConnectionStates.UNSET);
        if(properties.serverSetProperty().get())
            states.set(ConnectionStates.DISCONNECTED);
        if(remoteConnection.isConnectedWithEventSocket().get())
            states.set(ConnectionStates.CONNECTED);

        ip.setText(properties.getRemoteHost());
        key.setText(properties.getRemoteKey());
        
        listRemoteJobs();
    }
    
    @FXML
    void connect() {
        if(running.get()) return;
        running.set(true);
        String formated = Utility.formatHost(ip.getText());
        remoteConnection.testConnection(formated, key.getText())
            .thenAccept(s -> {
                if(s.isValid()) {
                    Platform.runLater(() -> ip.setText(formated));
                    properties.setRemote(formated, key.getText());
                    remoteConnection.connectEventSocket(formated, properties.getRemoteKey());
                    listRemoteJobs();
                } else {
                    Platform.runLater(() -> getDialogContext().textErrorDialog("Connection Error", "Failed to add remote connection", s.message()));
                }
            })
            .exceptionally(e -> {
                Logger.error(e);
                Platform.runLater(() -> getDialogContext().exceptionDialog(e, "Failed to add remote connection or connect to event socket", ExceptionRecoveryTips.REMOTE_ERROR.getTip()));
                return null;
            }).thenRun(() -> {
                Platform.runLater(() -> running.set(false));
            });
    }
    
    private void listRemoteJobs() {
        if(!properties.serverSetProperty().get())
            return;
        remoteConnection.listRemoteJobs(properties.getRemoteHost(), properties.getRemoteKey())
            .thenAccept(jobs -> {
                if(jobs instanceof RemoteError) {
                    RemoteError e = (RemoteError) jobs;
                    Platform.runLater(() -> {
                        statusReports.clear();
                        NotificationTools.error("Remote job fetching failed", "Failed to retrive remote jobs from server", "Click on notification to see more info.")
                        .position(Pos.TOP_RIGHT)
                        .hideAfter(Duration.millis(3000))
                        .onAction(ev -> {
                            Platform.runLater(() 
                                    -> getDialogContext().textErrorDialog("Remote job fetching failed", "Failed to retrive remote jobs from server", e.getMessage()));
                        }).show();
                    });
                    return;
                }
                
                if(jobs instanceof RemoteStatusReports) {
                    Platform.runLater(() -> {
                        statusReports.clear();
                        statusReports.addAll(((RemoteStatusReports) jobs).getResults());
                    });
                    return;
                }
                throw new AssertionError();
            }).exceptionally(e -> {
                Logger.error(e);
                Platform.runLater(() -> {
                    statusReports.clear();
                    NotificationTools.error("Remote job fetching failed", "Failed to retrive remote jobs from server", "Click on notification to see more info.")
                    .position(Pos.TOP_RIGHT)
                    .hideAfter(Duration.millis(3000))
                    .onAction(ev -> {
                        Platform.runLater(() 
                            -> getDialogContext().exceptionDialog(e, "Failed to retrive remote jobs from server", ExceptionRecoveryTips.REMOTE_ERROR.getTip()));
                     }).show();
                });
                return null;
            });
    }
    
    @FXML
    void reload() {
        listRemoteJobs();
    }
    
    @FXML
    void download() {
        if(running.get()) return;
        running.set(true);
        remoteConnection.collectAll(projectHandle.getSelectedProject().unsafeGet(), properties.getRemoteHost(), properties.getRemoteKey())
            .thenRun(() -> Platform.runLater(() -> running.set(false)));
    }
    
    @FXML
    void disconnect() {
        remoteConnection.disconnectEventSocket();
    }

    @Override
    public void onCloseRequest() {
        if(!running.get()) unregister();
    }

    @Override
    protected void keyBindRegisterHook(ObservableMap<KeyCombination, Runnable> bindings) {
        bindings.put(new KeyCodeCombination(KeyCode.ESCAPE), () -> unregister());
    }

    @Override
    public void onDestruction() {
        remoteConnection.getJobsChangedSignal().unregister(remoteConnectionUpdateHandler);
        remoteConnection.isConnectedWithEventSocket().removeListener(conListener);
    }
}
