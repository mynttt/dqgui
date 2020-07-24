package de.hshannover.dqgui.core.wizard.controllers;

import de.hshannover.dqgui.core.wizard.RenameComponentWizard;
import de.hshannover.dqgui.execution.DSLService;
import de.hshannover.dqgui.execution.model.DSLComponent;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class RenameComponent {
    @FXML Label header, validation;
    @FXML TextField identifier;
    @FXML Button renameButton;
    
    private DSLComponent from;
    private RenameComponentWizard w;
    
    @FXML
    void rename() {
       w.finish(DSLComponent.of(identifier.getText().trim(), from.getType(), from.getExtraData(), from.isGlobal()));
    }
   
    @FXML
    void cancel() {
       w.cancel();
    }

    public void init(DSLComponent from, DSLService service, RenameComponentWizard renameComponentWizard) {
        this.from = from;
        this.w = renameComponentWizard;
        header.setText("Rename " + from.getIdentifier());
        BooleanProperty p = new SimpleBooleanProperty(true);
        identifier.setPromptText(from.getIdentifier());
        identifier.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN), this::cancel);
        identifier.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE), this::cancel);
        identifier.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && !renameButton.isDisable())
                rename();
        });
        renameButton.disableProperty().bind(identifier.textProperty().isEmpty().or(p).or(Bindings.createBooleanBinding(() -> 
            identifier.getText().trim().isEmpty(),
            identifier.textProperty())));
        identifier.textProperty().addListener(e -> {
            if(identifier.getText() == null || identifier.getText().trim().isEmpty()) {
                validation.setText("Enter a new name for the identifier.");
                validation.setStyle("-fx-text-fill: red;");
                p.set(true);
                return;
            }
            if(service.existsInNamespace(DSLComponent.of(identifier.getText().trim(), from.getType(), from.isGlobal()))) {
                validation.setText("Component '" + identifier.getText().trim() +"' already exists within the repository.");
                validation.setStyle("-fx-text-fill: red;");
                p.set(true);
                return;
            }
            p.set(false);
            validation.setText("Name has been validated.");
            validation.setStyle("");
        });
    }
}
