package de.hshannover.dqgui.core.wizard.controllers;

import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.framework.wizards.AbstractWizard;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public final class SaveComponent {
    @FXML Label question;
    private AbstractWizard<Boolean> wizard;

    public void init(DSLComponent component, AbstractWizard<Boolean> wizard) {
        this.wizard = wizard;
        question.setText(String.format("Do you want to save %s?", component.getIdentifier()));
        question.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), this::save);
        question.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN), this::cancel);
        question.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE), this::cancel);
        question.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN), this::skip);
    }

    @FXML
    void cancel() {
        wizard.cancel();
    }

    @FXML
    void skip() {
        wizard.finish(Boolean.FALSE);
    }

    @FXML
    void save() {
        wizard.finish(Boolean.TRUE);
    }

}
