package de.hshannover.dqgui.core.controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.core.Config.OS;
import de.hshannover.dqgui.core.configuration.Components;
import de.hshannover.dqgui.core.model.ApplicationProperties;
import de.hshannover.dqgui.core.model.ProjectHandle;
import de.hshannover.dqgui.core.rsupport.RServeInstance;
import de.hshannover.dqgui.core.util.ExceptionRecoveryTips;
import de.hshannover.dqgui.execution.model.RepoType;
import de.hshannover.dqgui.framework.AbstractWindowController;
import de.hshannover.dqgui.framework.ErrorUtility;
import de.hshannover.dqgui.framework.FXMLLoadingFactory.ComponentContent;
import de.hshannover.dqgui.framework.dialogs.DialogContext.DialogStyle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Pair;

/**
 * @author Daniel Diele
 * @author Marc Herschel
 */

public final class PropertiesController extends AbstractWindowController {
    public Map<RepoType, Pair<Tab, PropertiesRepoSave>> repoMap = new HashMap<>();
    
    public interface PropertiesRepoSave {
        public boolean saveValidation();
    }
    
    /*
     * Injected by ApplicationContext
     */
    private final ApplicationProperties properties = null;
    private final RServeInstance rServeInstance = null;
    private final ProjectHandle projectHandle = null;

    @FXML
    ComboBox<RepoType> serviceBox;
    @FXML
    TextField pathR, pathRScript;
    @FXML
    CheckBox rserveAutostart;
    @FXML
    TextArea rServeArgs;
    @FXML
    BorderPane fsPane, dbPane;
    @FXML
    Tab fsTab, dbTab;
    @FXML
    TabPane repoTabs;
    
    @FXML
    void initialize() {
        List<RepoType> repos = Arrays.asList(RepoType.values());
        Collections.sort(repos, (r1, r2) -> r1.toString().compareTo(r2.toString()));
        serviceBox.setItems(FXCollections.observableArrayList(repos));
        pathR.setText(properties.getrPath());
        pathRScript.setText(properties.getrScriptPath());
        rServeArgs.setText(properties.getrArgs());
        rserveAutostart.setSelected(properties.isRServeAutoStart());
        ComponentContent fs = loadComponent(Components.PROPERTIES_FS_REPO, properties, projectHandle);
        PropertiesRepoSave fsp = (PropertiesRepoSave) fs.getController();
        fsPane.setCenter(fs.getParent());
        repoMap.put(RepoType.FILE_SYSTEM, new Pair<Tab, PropertiesRepoSave>(fsTab, fsp));
        ComponentContent db = loadComponent(Components.PROPERTIES_DB_REPO, properties, projectHandle);
        PropertiesRepoSave dbp = (PropertiesRepoSave) db.getController();
        dbPane.setCenter(db.getParent());
        repoMap.put(RepoType.DATABASE, new Pair<Tab, PropertiesRepoSave>(dbTab, dbp));
        serviceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> repoTabs.getSelectionModel().select(repoMap.get(newV).getKey()));
        repoTabs.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            for(Map.Entry<RepoType, Pair<Tab, PropertiesRepoSave>> entry : repoMap.entrySet()) {
                if(entry.getValue().getKey() == newV)
                    serviceBox.getSelectionModel().select(entry.getKey());
            }
        });
        serviceBox.getSelectionModel().select(properties.getRepoType());
    }

    @FXML
    void openAppdata() {
        try {
            Desktop.getDesktop().open(Paths.get(Config.APPLICATION_CONFIG).toFile());
        } catch (IOException e) {
            throw ErrorUtility.rethrow(e);
        }
    }

    @FXML
    void setPathR() {
        File selectedFile = getDialogContext().fileDialog("Please select your R executable", properties.getrPath(), 
                Config.PROCESS_HOST == OS.WIN ? 
                        new ExtensionFilter[] { new ExtensionFilter("Executable (*.exe)", "*.exe") } 
                      : new ExtensionFilter[0]);
        if(selectedFile != null)
            pathR.setText(selectedFile.getAbsolutePath());
    }

    @FXML
    void setPathRScript() {
        ExtensionFilter filter = new ExtensionFilter("R Script (*.r)", "*.r", "*.R");
        File selectedFile = getDialogContext().fileDialog("Please select your R starter script", properties.getrScriptPath(), filter);
        if(selectedFile != null)
            pathRScript.setText(selectedFile.getAbsolutePath());
    }

    @FXML
    void cancel() {
        unregister();
    }

    @FXML
    void save() {
        boolean verify = repoMap.get(serviceBox.getSelectionModel().getSelectedItem()).getValue().saveValidation();
        if(!verify)
            return;
        properties.setRepoType(serviceBox.getSelectionModel().getSelectedItem());
        projectHandle.verifyRepoType(properties);
        properties.setRServeAutoStart(rserveAutostart.isSelected());
        if(!Objects.equals(properties.getrArgs(), rServeArgs.getText()) || !Objects.equals(properties.getrPath(), pathR.getText()) || !Objects.equals(properties.getrScriptPath(), pathRScript.getText())) {
            properties.setRSettings(pathR.getText(), pathRScript.getText(), rServeArgs.getText());
            if(rServeInstance.alive()) {
                boolean killRserve = getDialogContext().confirmCancelDialog(DialogStyle.WARNING, "RServe has to die!", "RServe is currently alive and needs to be restarted to use the new shell script location.\n\nAre you sure you want to kill RServe?\n\nThis can have unforeseen consequences when an action that depends on RServe to evaluate is still running.");
                if(!killRserve) { return; }
            }
            rServeInstance.swapValues(properties.getrPath(), properties.getrScriptPath(), properties.getrArgs());
        }
        if(!rServeInstance.alive()) {
            try {
                rServeInstance.setAutoStart(properties.isRServeAutoStart());
            } catch(Exception e) {
                getDialogContext().exceptionDialog(e, "Failed to start RServe instance!", ExceptionRecoveryTips.RSERVE.getTip());
            }
        }
        unregister();
    }
    
    @Override
    protected void keyBindRegisterHook(ObservableMap<KeyCombination, Runnable> bindings) {
        bindings.put(new KeyCodeCombination(KeyCode.ESCAPE), () -> cancel());
    }

}
