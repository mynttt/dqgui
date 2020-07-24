package de.hshannover.dqgui.core.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.tinylog.Logger;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.core.Executors;
import de.hshannover.dqgui.core.model.ProjectHandle;
import de.hshannover.dqgui.execution.DSLServiceException;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import de.hshannover.dqgui.framework.AbstractWindowController;
import de.hshannover.dqgui.framework.ErrorUtility;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

final class RepoExport extends AbstractWindowController {
    @FXML TextField selected;
    @FXML VBox replaceVbox;
    @FXML Button select, execute, close;
    
    private final StyleClassedTextArea log = new StyleClassedTextArea();
    private volatile boolean running = false;
    private Path path;
    
    // Injected by application context
    private final ProjectHandle projectHandle = null;
    
    @FXML
    void initialize() {
        replaceVbox.getChildren().set(1, new VirtualizedScrollPane<StyleClassedTextArea>(log));
        log.setEditable(false);
        log.appendText("No export started...");
        log.setPrefHeight(300);
        log.getStyleClass().add("log-export-import-db");
        VBox.setVgrow(replaceVbox.getChildren().get(1), Priority.ALWAYS);
        selected.textProperty().addListener((obs, oldV, newV) -> path = Paths.get(newV));
    }
    
    @FXML
    void select() {
        File f = getDialogContext().directoryDialog("Select a folder to store exported results in...");
        if(f != null) {
            selected.setText(f.toPath().toAbsolutePath().toString());
        }
    }
    
    @FXML
    void execute() throws IOException, DSLServiceException {
        if(path == null || !Files.exists(path)) {
            getDialogContext().error("Invalid path", "Set a valid path first!");
            return;
        }
        
        log.clear();
        log.appendText("Beginning export...\n\n");
        
        Map<DSLComponentType, Path> mapping = new HashMap<>();
        for(DSLComponentType t : DSLComponentType.values()) {
            Path tmp = path.resolve(t.toString().toLowerCase());
            if(!Files.exists(tmp)) {
                Files.createDirectories(tmp);
            } else {
                if(!Files.isDirectory(path)) {
                    getDialogContext().error("Invalid file in export directory", "File: " + tmp.toAbsolutePath().toString() + " blocks usage as directory for: " + t);
                    return;
                }
            }
            log.appendText("Mapping " + t + " => " + tmp.toAbsolutePath() + "\n");
            mapping.put(t, tmp);
        }
        
        begin();
        
        CompletableFuture.supplyAsync(() -> {        
            pushMessage("\n");
            
            List<DSLComponent> components;
            
            try {
                components = projectHandle.getServiceProvider().getService().discover();
            } catch (DSLServiceException e) {
                throw ErrorUtility.rethrow(e);
            }
            
            pushMessage("Exporting: " + components.size() + " component(s).\n\n");
            
            int error = 0;
            
            for(DSLComponent c : components) {
                Path p = mapping.get(c.getType());
                try {
                    Files.write(p.resolve(c.getIdentifier() + ".iqm4hd"), projectHandle.getServiceProvider().getService().read(c).getBytes(Config.APPLICATION_CHARSET));
                    pushSuccess("Exported: " + c.getType() + " (" + c.getIdentifier() + ")");
                } catch(Exception e) {
                    error++;
                    pushError("Failed to export: " + c.getType() + " (" + c.getIdentifier() + ") [" + e.getClass().getSimpleName() + "]");
                    Logger.error(e);
                }
                
                Platform.runLater(() -> log.showParagraphAtBottom(log.getParagraphs().size()-1));
            }
            
            final String s = "\nExported: " + (components.size() - error) + " | Errors: " + error;
            Platform.runLater(() -> {
                log.setStyle(log.getParagraphs().size()-1, Collections.emptyList());
                log.appendText(s);
            });
            return null;
        }, Executors.SERVICE)
            .thenRun(() -> Platform.runLater(this::end))
            .exceptionally(v -> {
                Platform.runLater(() -> {
                    getDialogContext().exceptionDialog(v);
                    end();
                });
                return null;
            });
    }
    
    @FXML
    void close() {
        unregister();
    }
    
    private void pushMessage(String msg) {
        Platform.runLater(() -> log.appendText(msg));
    }
    
    private void pushError(String msg) {
        Platform.runLater(() -> {
            log.appendText(msg);
            log.setStyle(log.getText().length() - msg.length(), log.getText().length(), Collections.singleton("log-export-import-db-fail"));
            log.appendText("\n");
        });
    }
    
    private void pushSuccess(String msg) {
        Platform.runLater(() -> {
            log.appendText(msg);
            log.setStyle(log.getText().length() - msg.length(), log.getText().length(), Collections.singleton("log-export-import-db-success"));
            log.appendText("\n");
        });
    }
    
    private void begin() {
        running = true;
        select.setDisable(true);
        execute.setDisable(true);
        close.setDisable(true);
    }
    
    private void end() {
        running = false;
        select.setDisable(false);
        execute.setDisable(false);
        close.setDisable(false);
    }
    
    @Override
    protected void keyBindRegisterHook(ObservableMap<KeyCombination, Runnable> bindings) {
        bindings.put(new KeyCodeCombination(KeyCode.ESCAPE), () -> close());
    }

    @Override
    public void onCloseRequest() {
        if(running) return;
        unregister();
    }
}
