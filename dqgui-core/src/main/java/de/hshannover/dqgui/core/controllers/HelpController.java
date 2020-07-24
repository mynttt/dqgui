package de.hshannover.dqgui.core.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import com.google.common.io.CharStreams;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.framework.AbstractWindowController;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

final class HelpController extends AbstractWindowController {
    @FXML WebView web;
    @FXML Label lastGenerated;
    
    private WebEngine engine;
    
    @FXML
    void initialize() throws URISyntaxException, IOException {
        engine = web.getEngine();
        InputStream s = getClass().getResourceAsStream(Config.APPLICATION_DOCUMENTATION_LASTGEN);
        if(s == null) {
            lastGenerated.setText(":run WILL NOT GENERATE THIS");
        } else {
            lastGenerated.setText(CharStreams.toString(new InputStreamReader(s, Config.APPLICATION_CHARSET)));
        }
        help();
    }
    
    @FXML
    void close() {
        unregister();
    }
    
    private void loadPage(String page) throws URISyntaxException {
        URL url = getClass().getResource(page);
        if(url == null) {
            engine.loadContent(Config.DOCU_NOT_FOUND);
            return;
        }
        engine.load(url.toURI().toString());
    }
    
    @FXML 
    void help() throws URISyntaxException {
        loadPage(Config.APPLICATION_DOCUMENTATION_INDEX);
    }
    
    @FXML 
    void javadoc() throws URISyntaxException {
        loadPage(Config.APPLICATION_DOCUMENTATION_JAVADOC);
    }
    
    @FXML
    void back() {
        Platform.runLater(() -> engine.executeScript("history.back()"));
    }
    
    @FXML
    void next() {
        Platform.runLater(() -> engine.executeScript("history.forward()"));
    }

    @Override
    protected void keyBindRegisterHook(ObservableMap<KeyCombination, Runnable> bindings) {
        bindings.put(new KeyCodeCombination(KeyCode.ESCAPE), this::close);
    }
}

