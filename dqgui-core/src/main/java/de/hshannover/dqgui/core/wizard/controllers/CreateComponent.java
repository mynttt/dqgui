package de.hshannover.dqgui.core.wizard.controllers;

import java.util.Arrays;
import de.hshannover.dqgui.execution.DSLService;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import de.hshannover.dqgui.framework.wizards.AbstractWizard;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public final class CreateComponent {
    @FXML Label validation, createComponent;
    @FXML TextField identifier;
    @FXML ComboBox<DSLComponentType> type;
    @FXML Button createButton;
    
    private AbstractWizard<DSLComponent> wizard;

    private String upper(String s) {
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
    
    public void init(DSLComponentType shortcut, String initialName, DSLService service, AbstractWizard<DSLComponent> wizard) {
        if(initialName != null)
            identifier.setText(initialName);
        identifier.setPromptText("Component identifier");
        type.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> createComponent.setText("Create " + upper(n.toString().toLowerCase())));
        type.setItems(FXCollections.observableArrayList(Arrays.asList(DSLComponentType.values())));
        type.getItems().sort((c1, c2) -> c1.toString().compareTo(c2.toString()));
        this.wizard = wizard;
        type.getSelectionModel().select(0);
        if(shortcut != null)
            type.getSelectionModel().select(shortcut);
        identifier.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN), this::cancel);
        identifier.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE), this::cancel);
        identifier.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && !createButton.isDisable())
                create();
        });
        BooleanProperty p = new SimpleBooleanProperty(true);
        createButton.disableProperty().bind(identifier.textProperty().isEmpty().or(p).or(Bindings.createBooleanBinding(() -> 
        identifier.getText().trim().isEmpty(),
        identifier.textProperty())));
        identifier.textProperty().addListener(e -> {
            if(identifier.getText() == null || identifier.getText().trim().isEmpty()) {
                validation.setText("Enter a new name for the identifier.");
                validation.setStyle("-fx-text-fill: red;");
                p.set(true);
                return;
            }
            if(service.existsInNamespace(DSLComponent.of(identifier.getText().trim(), type.getValue(), false))) {
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

    @FXML
    void create() {
        wizard.finish(DSLComponent.of(identifier.getText().trim(), type.getValue(), false));
    }

    @FXML
    void cancel() {
        wizard.cancel();
    }

}
