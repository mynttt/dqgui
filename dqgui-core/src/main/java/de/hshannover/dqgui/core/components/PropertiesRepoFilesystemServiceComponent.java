package de.hshannover.dqgui.core.components;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import de.hshannover.dqgui.core.controllers.PropertiesController.PropertiesRepoSave;
import de.hshannover.dqgui.core.model.ApplicationProperties;
import de.hshannover.dqgui.core.model.ProjectHandle;
import de.hshannover.dqgui.framework.AbstractComponentController;
import de.hshannover.dqgui.framework.ErrorUtility;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

final class PropertiesRepoFilesystemServiceComponent extends AbstractComponentController implements PropertiesRepoSave {

    @FXML
    TextField pathTextfield;
    @FXML
    Button openGlobalCheckButton;
    
    private final ApplicationProperties properties;
    private final ProjectHandle projectHandle;
    
    PropertiesRepoFilesystemServiceComponent(ApplicationProperties properties, ProjectHandle projectHandle) {
        this.properties = properties;
        this.projectHandle = projectHandle;
    }
    
    @FXML
    void initialize() {
        pathTextfield.setText(properties.getGlobalChecks());
        openGlobalCheckButton.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> !Files.exists(Paths.get(pathTextfield.getText())), pathTextfield.textProperty()));
    }
    
    @FXML
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    void setGlobalChecks() {
        File selectedDirectory = getDialogContext().directoryDialog("Please select your IQM4HD repository", properties.getGlobalChecks());
        if(selectedDirectory != null)
            pathTextfield.setText(selectedDirectory.getAbsolutePath());
    }
    
    @FXML
    void openGlobalChecks() {
        try {
            Desktop.getDesktop().open(Paths.get(pathTextfield.getText()).toFile());
        } catch (IOException e) {
            throw ErrorUtility.rethrow(e);
        }
    }
    
    private boolean validatePath() {
        Path p = Paths.get(pathTextfield.getText());
        return Files.exists(p);
    }

    @Override
    public boolean saveValidation() {
        if(projectHandle.hasTasksRunning()) {
            getDialogContext().error("Tasks pending", "Tasks depending on this repository are still running. Try later again.");
            return false;
        }
        if(pathTextfield.getText() == null)
            pathTextfield.setText("");
        
        if(!validatePath()) {
            getDialogContext().error("Invalid path", String.format("Path: %s is not a valid path!", pathTextfield.getText()));
            return false;
        }
        
        properties.setGlobalChecks(Paths.get(pathTextfield.getText()).toAbsolutePath().toString());
        return true;
    }
}
