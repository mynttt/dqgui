package de.hshannover.dqgui.core.controllers;

import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.framework.AbstractWindowController;
import de.hshannover.dqgui.framework.JavaFXTools;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

final class AboutController extends AbstractWindowController {

    @FXML Label nameLabel, versionLabel;

    @FXML
    private void initialize() {
        nameLabel.setText("Project " + Config.APPLICATION_NAME);
        versionLabel.setText(Config.APPLICATION_VERSION);
    }

    @FXML
    void url1() {
        JavaFXTools.sendUrlToDefaultSystemBrowser("https://www.hs-hannover.de/");
    }

    @FXML
    void url2() {
        JavaFXTools.sendUrlToDefaultSystemBrowser("https://www.tamu.edu/");
    }

    @FXML
    void url3() {
        JavaFXTools.sendUrlToDefaultSystemBrowser("https://iqm4hd.wp.hs-hannover.de/english.html");
    }

    @FXML
    void ok() {
        unregister();
    }

    @Override
    protected void keyBindRegisterHook(ObservableMap<KeyCombination, Runnable> bindings) {
        bindings.put(new KeyCodeCombination(KeyCode.ESCAPE), () -> ok());
    }
}
