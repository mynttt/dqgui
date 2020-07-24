package de.hshannover.dqgui.core.wizard.controllers;

import java.util.Arrays;
import java.util.Comparator;
import de.hshannover.dqgui.execution.DSLService;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import de.hshannover.dqgui.framework.wizards.AbstractWizard;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public final class SaveComponentAs {
    @FXML Label info, question;
    @FXML ComboBox<DSLComponentType> type;
    @FXML TextField identifier;
    private DSLService service;
    private AbstractWizard<DSLComponent> wizard;
    private DSLComponent hint;

    public void init(DSLService service, DSLComponent hint, AbstractWizard<DSLComponent> wizard) {
        this.hint = hint;
        type.setItems(FXCollections.observableArrayList(Arrays.asList(DSLComponentType.values())));
        type.getItems().sort(Comparator.comparing(Enum::toString));
        info.setText(String.format("Save %s as...", hint.getType().toString().toLowerCase()));
        question.setText(String.format("Where do you want to save %s as?", hint.getIdentifier()));
        this.wizard = wizard;
        this.service = service;
        type.getSelectionModel().select(0);
        type.getSelectionModel().select(hint.getType());
        type.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), this::saveAs);
        type.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN), this::cancel);
        type.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE), this::cancel);
        identifier.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER)
                saveAs();
        });
    }

    @FXML
    void saveAs() {
        if(identifier.getText() == null || identifier.getText().trim().isEmpty()) {
            wizard.getDialogContext().warning("Illegal identifier", "Identifier must be set.");
            return;
        }
        DSLComponent temp = DSLComponent.of(identifier.getText(), type.getValue(), hint.getExtraData(), false);
        if(service.existsInNamespace(temp)) {
            wizard.getDialogContext().error("Component already exists", String.format("The %s %s already exists.%n%nPlease choose a different identifier.", temp.getType().toString().toLowerCase(), temp.getIdentifier()));
            return;
        }
        wizard.finish(temp);
    }

    @FXML
    void cancel() {
        wizard.cancel();
    }
}
