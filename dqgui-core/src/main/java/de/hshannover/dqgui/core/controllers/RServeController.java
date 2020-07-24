package de.hshannover.dqgui.core.controllers;

import org.tinylog.Logger;
import de.hshannover.dqgui.core.model.ApplicationProperties;
import de.hshannover.dqgui.core.rsupport.RMetaInformation;
import de.hshannover.dqgui.core.rsupport.RServeInstance;
import de.hshannover.dqgui.core.util.ExceptionRecoveryTips;
import de.hshannover.dqgui.framework.AbstractWindowController;
import de.hshannover.dqgui.framework.signal.SignalHandler;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

final class RServeController extends AbstractWindowController {
    /*
     * Injected by ApplicationContext
     */
    private final RServeInstance rServeInstance = null;
    private final ApplicationProperties properties = null;

    @FXML
    Label status, r, pwd, script, args, autostart;
    @FXML
    TextArea output;
    @FXML
    Button start, stop;
    
    private final SignalHandler handleLocation = () -> updateMeta(rServeInstance.requestMeta()),
                                handleStop = () -> startStop(false),
                                handleStart = () -> startStop(true),
                                handleAutoStart = () -> updateMeta(rServeInstance.requestMeta());

    @FXML
    void initialize() {
        ChangeListener<String> textListener = (obs, oldV, newV) -> {
            output.appendText(newV);
            output.appendText("\n");
        };
        
        properties.getrServeLocationChangeSignal().register(handleLocation);
        properties.getrServeAutoStartSignal().register(handleAutoStart);
        rServeInstance.getrServeStartSignal().register(handleStart);
        rServeInstance.getrServeStopSignal().register(handleStop);
        
        startStop(rServeInstance.alive());
        updateMeta(rServeInstance.requestMeta());
        if(rServeInstance.alive()) {
            output.setText(rServeInstance.getBufferedString());
        }
        output.selectPositionCaret(output.getLength());
        output.deselect();
        rServeInstance.beginUiUpdate(textListener);
    }

    @FXML
    void start() {
        if(properties.getrPath() == null || properties.getrScriptPath() == null) {
            getDialogContext().warning("Invalid R configuration", "Configure R in the properties first before attempting to start it.");
            return;
        }
        output.clear();
        try {
            rServeInstance.startInstance();
        } catch (Exception e) {
            Logger.error("Failed to start RServe instance!");
            Logger.error(e);
            getDialogContext().exceptionDialog(e, "Failed to start RServe instance!", ExceptionRecoveryTips.RSERVE.getTip());
        }
    }

    @FXML
    void stop() {
        rServeInstance.stopInstance();
    }

    @FXML
    void close() {
        unregister();
    }

    private void startStop(boolean start) {
        updateMeta(rServeInstance.requestMeta());
        status.setText(start ? "ONLINE" : "OFFLINE");
        status.setId(start ? "rserve-online" : "rserve-offline");
        if(start) {
            this.start.setDisable(true);
            this.stop.setDisable(false);
        } else {
            this.start.setDisable(false);
            this.stop.setDisable(true);
        }
    }

    private void updateMeta(RMetaInformation meta) {
        r.setText(meta.R);
        pwd.setText(meta.PWD);
        args.setText(meta.args);
        script.setText(meta.script);
        autostart.setText(properties.isRServeAutoStart() ? "ENABLED" : "DISABLED");
        autostart.setId(properties.isRServeAutoStart() ? "rserve-online" : "rserve-offline");
    }

    @Override
    public void onDestruction() {
        properties.getrServeLocationChangeSignal().unregister(handleLocation);
        properties.getrServeAutoStartSignal().unregister(handleAutoStart);
        rServeInstance.getrServeStartSignal().unregister(handleStart);
        rServeInstance.getrServeStopSignal().unregister(handleStop);
        rServeInstance.stopUiUpdate();
    }
    
    @Override
    protected void keyBindRegisterHook(ObservableMap<KeyCombination, Runnable> bindings) {
        bindings.put(new KeyCodeCombination(KeyCode.ESCAPE), () -> close());
    }
}
