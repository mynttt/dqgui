package de.hshannover.dqgui.core.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.tinylog.Logger;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.core.Executors;
import de.hshannover.dqgui.core.model.ProjectHandle;
import de.hshannover.dqgui.execution.DSLService;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import de.hshannover.dqgui.framework.AbstractWindowController;
import de.hshannover.dqgui.framework.ErrorUtility;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

final class RepoImport extends AbstractWindowController {
    @FXML TextField selected;
    @FXML VBox replaceVbox;
    @FXML Button select, execute, close;
    @FXML CheckBox forceOverwrite;
    
    private final StyleClassedTextArea log = new StyleClassedTextArea();
    private final Runnable repopulateRepoCallback;
    private volatile boolean running = false;
    private Path path;
    
    // Injected by application context
    private final ProjectHandle projectHandle = null;
    
    RepoImport(Runnable repopulateRepoCallback) {
        this.repopulateRepoCallback = repopulateRepoCallback;
    }
    
    @FXML
    void initialize() {
        replaceVbox.getChildren().set(1, new VirtualizedScrollPane<StyleClassedTextArea>(log));
        log.setEditable(false);
        log.setPrefHeight(300);
        log.appendText("No import started...");
        log.getStyleClass().add("log-export-import-db");
        VBox.setVgrow(replaceVbox.getChildren().get(1), Priority.ALWAYS);
        selected.textProperty().addListener((obs, oldV, newV) -> path = Paths.get(newV));
    }
    
    @FXML
    void select() {
        File f = getDialogContext().directoryDialog("Select a folder to import components from...");
        if(f != null) {
            selected.setText(f.toPath().toAbsolutePath().toString());
        }
    }
    
    @FXML
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    void execute() throws IOException {
        if(path == null || !Files.exists(path)) {
            getDialogContext().error("Invalid path", "Set a valid path first!");
            return;
        }
        
        boolean overwrite = forceOverwrite.isSelected();
        log.clear();
        
        log.appendText("Beginning import...\n\n");
        log.appendText("Scanning for files ending in '.iqm4hd'\n"); 
        
        begin();
        
        CompletableFuture.supplyAsync(() -> {
            List<Path> found;
            
            try {
                found = Files.walk(path)
                        .filter(f -> !Files.isDirectory(f))
                        .filter(s -> !s.getFileName().endsWith(".iqm4hd"))
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw ErrorUtility.rethrow(e);
            }
            
            pushMessage("Found: " + found.size() + "\n\n");
            
            if(found.isEmpty()) {
                pushMessage("Nothing to import.");
                return null;
            }
            
            pushMessage("Importing:\n");
            found.forEach(f -> pushMessage(f.toAbsolutePath().toString() + "\n"));
            pushMessage("\n");
            
            Platform.runLater(() -> log.showParagraphAtBottom(log.getParagraphs().size()-1));
            int error = 0;
            
            for(Path p : found) {
                try {
                    String content = new String(Files.readAllBytes(p), Config.APPLICATION_CHARSET);
                    String componentCheck = content.toLowerCase();
                    DSLComponent c = null;
                    String identifier = p.getFileName().toString().substring(0, p.getFileName().toString().length()-7);
                    
                    if(componentCheck.startsWith("source")) {
                        c = DSLComponent.of(identifier, DSLComponentType.SOURCE, false);
                    } else if(componentCheck.startsWith("action")) {
                        c = DSLComponent.of(identifier, DSLComponentType.ACTION, false);
                    } else if(componentCheck.startsWith("check")) {
                        c = DSLComponent.of(identifier, DSLComponentType.CHECK, false);
                    } else {
                        pushError("Failed to import: Unable to determine component type (must start with SOURCE, ACTION or CHECK): " + p.toAbsolutePath());
                        error++;
                        continue;
                    }
                    
                    DSLService service = projectHandle.getServiceProvider().getService();
                    
                    if(service.existsInNamespace(c)) {
                        if(overwrite) {
                            if(c.getType() == DSLComponentType.CHECK && service.isGlobalCheck(c.getIdentifier())) {
                                pushError("Failed to import: Not allowed to overwrite global check (" + c.getIdentifier() + ")");
                                error++;
                                continue;
                            }
                            service.update(c, content);
                        } else {
                            pushError("Failed to import: " + c.getType() + " (" + c.getIdentifier() + ")" + " already exists in repository. Skipping to prevent overwrite.");
                            error++;
                            continue;
                        }
                    } else {
                        service.create(c);
                        service.update(c, content);
                    }
                    
                    pushSuccess("Imported: " + c.getType() + " (" + c.getIdentifier() + ")");
                } catch(Exception e) {
                    error++;
                    pushError("Failed to import: " + p.toAbsolutePath() + "[" + e.getClass().getSimpleName() + "]");
                    Logger.error(e);
                }
            }
            
            Platform.runLater(() -> log.setStyle(log.getParagraphs().size()-1, Collections.emptyList()));
            pushMessage("\nImported: " + (found.size() - error) + " | Errors: " + error);
            
            if((found.size() - error) > 0)
                Platform.runLater(() -> repopulateRepoCallback.run());
            
            return null;
        }, Executors.SERVICE)
            .thenRun(() -> Platform.runLater(this::end))
            .exceptionally(t -> {
                Platform.runLater(() -> {
                    getDialogContext().exceptionDialog(t);
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
    
    private void pushError(String error) {
        Platform.runLater(() -> {
            log.appendText(error);
            log.setStyle(log.getText().length() - error.length(), log.getText().length(), Collections.singleton("log-export-import-db-fail"));
            log.appendText("\n");
            log.showParagraphAtBottom(log.getParagraphs().size()-1);
        });
    }
    
    private void pushSuccess(String s) {
        Platform.runLater(() -> {
            log.appendText(s);
            log.setStyle(log.getText().length() - s.length(), log.getText().length(), Collections.singleton("log-export-import-db-success"));
            log.appendText("\n");
            log.showParagraphAtBottom(log.getParagraphs().size()-1);
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
    public void onCloseRequest() {
        if(running) return;
        unregister();
    }

    @Override
    protected void keyBindRegisterHook(ObservableMap<KeyCombination, Runnable> bindings) {
        bindings.put(new KeyCodeCombination(KeyCode.ESCAPE), () -> close());
    }
}
