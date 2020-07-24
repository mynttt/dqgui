package de.hshannover.dqgui.core.ui;

import static de.hshannover.dqgui.core.util.SyntaxHighlighting.computeHighlighting;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.event.MouseOverTextEvent;
import org.fxmisc.richtext.model.StyleSpans;
import org.reactfx.Subscription;
import org.reactfx.value.Val;
import de.hshannover.dqgui.core.Executors;
import de.hshannover.dqgui.core.model.EditorIssue;
import de.hshannover.dqgui.core.model.IntervalMap;
import de.hshannover.dqgui.core.util.IconFactory;
import de.hshannover.dqgui.core.util.StaticAnalysis;
import de.hshannover.dqgui.core.util.SyntaxHighlighting;
import de.hshannover.dqgui.core.util.RepositorySearch.SearchHit;
import de.hshannover.dqgui.core.util.StaticAnalysis.ComponentLink;
import de.hshannover.dqgui.core.util.StaticAnalysis.EditorLink;
import de.hshannover.dqgui.core.util.StaticAnalysis.StaticAnalysisResult;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDLexer;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDParser;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.util.Pair;
import me.tomassetti.antlr4c3.api.ApiKt;

public final class CodeEditor extends Tab {
    private static final Collection<String> EDITOR_LINK = Collections.singleton("editor-link");
    
    private DSLComponent component;
    private final VirtualizedScrollPane<CodeArea> pane;
    private final CodeArea editor;
    private final Subscription cleanupWhenNoLongerNeedIt;
    private final TableColumn<EditorIssue, EditorIssue> description = new TableColumn<>();
    private final AutocompleteBox autocomplete;
    private IntervalMap<EditorLink> links;
    private boolean textHasBeenChanged, ctrlDown, overLink, autoCorrect, autoIndent;
    private final ObservableList<EditorIssue> editorWarnings = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private final Consumer<StaticAnalysisResult> warningConsumer = c -> {
        Platform.runLater(() -> {
            links = c.getLinks();
            editorWarnings.clear();
            editorWarnings.addAll(c.getWarnings());
        });
    };
    
    @SuppressWarnings("rawtypes")
    private class EditorWarningCell extends TableCell {
        private final String text;

        private EditorWarningCell(String text) {
            this.text = text;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);
            
            if(empty || item == null) {
                setText(null);
                setTooltip(null);
                setContextMenu(null);
                setGraphic(null);
                return;
            }
            
            EditorIssue w = (EditorIssue) item;
            
            switch(text) {
            case "Type":
                type(w);
                break;
            case "Description":
                desc(w);
                break;
            case "Location":
                source(w);
                break;
            default:
                throw new AssertionError("This should not happen");
            }
        }
        
        private void type(EditorIssue item) {
            setText(" " + item.getCategory());
            setGraphic(item.getType().icon());
            setTooltip(new Tooltip(item.getCategory()));
        }
        
        private void desc(EditorIssue item) {
            Text t = new Text(item.getDescription());
            setGraphic(t);
            setPrefHeight(Control.USE_COMPUTED_SIZE);
            t.getStyleClass().add("coltext");
            t.wrappingWidthProperty().bind(description.widthProperty());
        }
        
        private void source(EditorIssue item) {
            setText(item.getLineDescription());
            setTooltip(new Tooltip(item.getLineDescription()));
        }
        
    }
    
    private static class AutocompleteBox extends Popup implements Supplier<Void> {
        private static final Set<Integer> ALLOW_KEYWORDS_RULES = SyntaxHighlighting.rules();
        private static final List<String> ALLOW_KEYWORDS = SyntaxHighlighting.keywords();
        private static final int W = 100, H = 125;
        
        private final ListView<String> autocomplete = new ListView<>();
        private final ObservableList<String> suggestions = FXCollections.observableArrayList();
        private final CodeArea area;
        private Bounds caretBounds;
        
        private boolean lock;
        
        private AutocompleteBox(CodeArea editor) {
            this.area = editor;
            getContent().add(autocomplete);
            autocomplete.setItems(suggestions);
            autocomplete.setMaxHeight(H);
            autocomplete.setMaxWidth(W);
            area.caretBoundsProperty().addListener((obs, oldV, newV) -> {
                newV.ifPresent(c -> {
                    this.caretBounds = c;
                });
            });
            autocomplete.getStylesheets().add(getClass().getResource("/dqgui/core/javafx/autocorrect.css").toExternalForm());
            autocomplete.getStyleClass().add("autocorrect-box");
            autocomplete.setFocusTraversable(false);
            autocomplete.setOnMouseClicked(e -> {
                if(e.getClickCount() == 2)
                    commit();
            });
        }

        public Void get() {
            if(lock) return null;
            String original = area.getText().substring(0, area.getCaretPosition());
            Pair<String, Integer> prepared = preprocessText(original);
            Set<Integer> completeIncomplete = ApiKt.completions(prepared.getKey(), IQM4HDLexer.class, IQM4HDParser.class);
            int s1 = completeIncomplete.size();
            completeIncomplete.addAll(ApiKt.completions(original, IQM4HDLexer.class, IQM4HDParser.class));
            int s2 = completeIncomplete.size();
            List<String> tokens = completeIncomplete
                    .stream()
                    .filter(ALLOW_KEYWORDS_RULES::contains)
                    .map(SyntaxHighlighting::keywordLookup)
                    .collect(Collectors.toList());
            if(prepared.getValue() > 0 && s1 == s2) {
                firstWord(area.getText().trim().substring(prepared.getValue()))
                    .ifPresent(w -> tokens.removeIf(s -> !s.startsWith(w)));
            }
            Platform.runLater(() -> {
                suggestions.clear();
                suggestions.addAll(tokens);
                if(suggestions.isEmpty()) {
                    hide();
                    return;
                }
                if(isShowing()) {
                    setX(x());
                    setY(caretBounds.getMaxY()+3);
                } else {
                    show(area, x(), caretBounds.getMaxY()+3);
                }
            });
            return null;
        }
        
        private double x() {
            Bounds local = area.screenToLocal(caretBounds);
            Bounds global = area.localToScreen(area.getBoundsInLocal());
            if(local.getMinX() + W > area.getBoundsInLocal().getMaxX())
                return global.getMaxX() - W - 10;
            if(caretBounds.getMinX() < global.getMinX())
                return global.getMinX() + 20;
            return caretBounds.getMinX();
        }

        private Optional<String> firstWord(String s) {
            for(int i = 0; i < s.length(); i++)
                if(Character.isWhitespace(s.charAt(i))) return Optional.of(s.substring(0, i-1));
            return Optional.empty();
        }
        
        private Optional<String> lastWord(String s) {
            for(int i = s.length()-1; i >= 0; i--)
                if(Character.isWhitespace(s.charAt(i))) return Optional.of(s.substring(i+1));
            return Optional.empty();
        }
        
        /*
         * AutoSuggester is unable to process partly correct written tokens,
         * so if a string does not end with a correct token we substring until the last token.
         */
        private Pair<String, Integer> preprocessText(String string) {
            String s = string.trim();
            for(String keyword : ALLOW_KEYWORDS)
                if(s.endsWith(keyword)) return new Pair<>(string, -1);
            for(int i = s.length()-1; i >= 0; i--) {
                if(Character.isWhitespace(s.charAt(i))) return new Pair<>(s.substring(0, i+1), i+1);
            }
            return new Pair<>(string, -1);
        }

        private void sendUp() {
            if(autocomplete.getSelectionModel().isEmpty() || autocomplete.getSelectionModel().getSelectedIndex() == 0)
                return;
            autocomplete.getSelectionModel().select(autocomplete.getSelectionModel().getSelectedIndex() - 1);
        }

        private void sendDown() {
            if(autocomplete.getSelectionModel().isEmpty()) {
                autocomplete.getSelectionModel().selectFirst();
                return;
            }
            if(autocomplete.getSelectionModel().getSelectedIndex() + 1 < autocomplete.getItems().size())
                autocomplete.getSelectionModel().select(autocomplete.getSelectionModel().getSelectedIndex() + 1);
        }

        private boolean commit() {
            if(!autocomplete.getSelectionModel().isEmpty()) {
                complete(autocomplete.getSelectionModel().getSelectedItem());
                return true;
            }
            return false;
        }

        @SuppressWarnings("unused")
        private void insertFirst() {
            if(!autocomplete.getItems().isEmpty())
                complete(autocomplete.getItems().get(0));
        }
        
        // Hack to keep the areas text property ignoring changes from the auto complete method
        private void executeLocked(Runnable r) {
            lock = true;
            r.run();
            hide();
            lock = false;
        }
        
        private void complete(String suggestion) {
            String editorContent = area.getText().substring(0, area.getCaretPosition());
            if(editorContent.length() == 0) {
                executeLocked(() -> area.replaceSelection(whitespacePad(suggestion, area.getText(), 0, 0)));
                return;
            }
            if(Character.isWhitespace(editorContent.charAt(area.getCaretPosition()-1))) {
                executeLocked(() -> area.replaceSelection(whitespacePad(suggestion, area.getText(), area.getCaretPosition(), area.getCaretPosition())));
            } else {
                for(int i = editorContent.length() - 1; i >= 0; i--) {
                   if(Character.isWhitespace(editorContent.charAt(i))) {
                       final int idx = i;
                       if(lastWord(editorContent).map(s -> {
                           String sugLow = suggestion.toLowerCase();
                           String sLow = s.toLowerCase();
                           if(sugLow.startsWith(sLow)) {
                               int low = editorContent.length()-s.length();
                               executeLocked(() ->  area.replaceText(new IndexRange(low, editorContent.length()), whitespacePad(suggestion, area.getText(), low, editorContent.length())));
                           } else {
                               if(subStringCheck(sLow, sugLow)
                                   .map(stlen -> {
                                       int low = idx+sLow.length()-stlen+1;
                                       executeLocked(() -> area.replaceText(new IndexRange(low, editorContent.length()), whitespacePad(suggestion, area.getText(), low, editorContent.length())));
                                       return true;
                                   }).orElse(false)) {} else {
                                       executeLocked(() -> area.replaceSelection(whitespacePad(suggestion, area.getText(), area.getCaretPosition(), area.getCaretPosition())));
                                   }
                           }
                           return true;
                       }).orElse(false)) {} else {
                           executeLocked(() -> area.replaceText(new IndexRange(idx+1, editorContent.length()), whitespacePad(suggestion, area.getText(), idx+1, editorContent.length())));
                       }
                       break;
                   }
                }
            }
        }

        private Optional<Integer> subStringCheck(String sLow, String sugLow) {
            for(int i = sLow.length()-1; i >= 0; i--) {
                if(sugLow.startsWith(sLow.substring(i))) return Optional.of(sLow.length()-i);
            }
            return Optional.empty();
        }
        
        private String whitespacePad(String suggestion, String text, int low, int high) {
            if(low == 0) {
                return text.isEmpty() || !Character.isWhitespace(text.charAt(high)) ? suggestion + " " : suggestion;
            }
            
            boolean startOfLine = area.getParagraphLength(area.visibleParToAllParIndex(area.getVisibleParagraphs().size() - 1)) == 0;
            
            if(high == text.length()) {
                if(startOfLine) return suggestion;
                return Character.isWhitespace(text.charAt(low-1)) ? suggestion : " " + suggestion;
            }
            
            if(startOfLine)
                return high+1 >= text.length() 
                    ? suggestion + " "
                    : Character.isWhitespace(text.charAt(high+1)) ? suggestion : suggestion + " ";
            
            boolean l = Character.isWhitespace(text.charAt(low-1));
            boolean h = Character.isWhitespace(text.charAt(high));

            if(h && l) return suggestion;
            if(!h && !l) return " " + suggestion + " ";
            if(l && !h) return suggestion + " ";
            if(!l && h) return " " + suggestion;
            return suggestion;
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public CodeEditor(Consumer<EditorLink> linkHandler, DSLComponent component, String content, boolean autoSuggest) {
        super(component.getIdentifier());
        
        this.autoCorrect = autoSuggest;
        this.component = component;
        this.editor = new CodeArea(content);
        this.editor.setWrapText(false);
        this.pane = new VirtualizedScrollPane<>(editor);
        this.autocomplete = new AutocompleteBox(editor);
        
        MenuItem cut = new MenuItem("Cut");
        cut.setOnAction(e -> editor.cut());
        MenuItem copy = new MenuItem("Copy");
        copy.setOnAction(e -> editor.copy());
        MenuItem paste = new MenuItem("Paste");
        paste.setOnAction(e -> editor.paste());
        MenuItem select = new MenuItem("Select All");
        select.setOnAction(e -> editor.selectAll());
        MenuItem replaceTabs = new MenuItem("Replace Tab => 4 spaces");
        replaceTabs.setOnAction(e -> {
            editor.replaceText(editor.getText().replaceAll("\t", "    "));
            rerunStaticAnalysis();
        });
        MenuItem toggleLineWrap = new MenuItem("Toggle Line Wrap");
        toggleLineWrap.setOnAction(e -> editor.setWrapText(!editor.isWrapText()));
        
        Popup infoTooltip = new Popup();
        Label infoMsg = new Label();
        infoMsg.getStyleClass().add("editor-info-tooltip");
        infoTooltip.getContent().add(infoMsg);
        
        editor.setContextMenu(new ContextMenu(cut, copy, paste, select, replaceTabs, new SeparatorMenuItem(), toggleLineWrap));
        editor.setMouseOverTextDelay(Duration.ofMillis(300));
        editor.textProperty().addListener((obs, oldV, newV) -> {
            if(autoCorrect)
                CompletableFuture.supplyAsync(autocomplete, Executors.SERVICE);
        });
        
        editor.focusedProperty().addListener((obs, oldV, newV) -> {
            if(!newV) {
                autocomplete.hide();
                if((ctrlDown || overLink)) {
                   rerunStaticAnalysis();
                   ctrlDown = overLink = false;
                   editor.setCursor(Cursor.TEXT);
                }
            }
        });
        
        editor.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if(e.getCode() == KeyCode.CONTROL) {
                for(EditorLink l : links.getValues()) {
                    if(l instanceof ComponentLink) {
                        ComponentLink c = (ComponentLink) l;
                        editor.setStyle(c.start, c.stop, EDITOR_LINK);
                    }
                }
                ctrlDown = true;
            }
            if(e.getCode() == KeyCode.TAB) {
                // 4 Spaces instead of 8 indentation Tab
                editor.replaceSelection("    ");
                e.consume();
            }
            if(e.getCode() == KeyCode.SPACE && ctrlDown && autoCorrect) {
                CompletableFuture.supplyAsync(autocomplete, Executors.SERVICE);
            }
            if(autocomplete.isShowing()) {
                switch(e.getCode()) {
                case LEFT:
                case RIGHT:
                    autocomplete.hide();
                    break;
                case UP:
                    autocomplete.sendUp();
                    e.consume();
                    break;
                case DOWN:
                    autocomplete.sendDown();
                    e.consume();
                    break;
                case ENTER:
                    if(autocomplete.commit())
                        e.consume();
                    break;
                default:
                    break;
                }
            }
        });
        
        editor.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            if(e.getCode() == KeyCode.CONTROL) {
                ctrlDown = false;
                rerunStaticAnalysis();
                if(overLink) {
                    editor.setCursor(Cursor.TEXT);
                    overLink = false;
                }
            }
        });
        
        editor.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            if(!ctrlDown) return;
            if(e.getTarget() instanceof Text && ((Text) e.getTarget()).getStyleClass().contains("editor-link")) {
                if(!overLink) {
                    editor.setCursor(Cursor.HAND);
                    overLink = true;
                }
            } else {
                if(overLink) {
                    editor.setCursor(Cursor.TEXT);
                    overLink = false;
                }
            }
        });
        
        editor.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            autocomplete.hide();
            if(!ctrlDown) return;
            links.get(editor.getCaretPosition()).ifPresent(l -> {
                linkHandler.accept(l);
            });
        });
        
        editor.addEventFilter(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, e -> {
            if(ctrlDown && overLink) {
                links.get(e.getCharacterIndex())
                    .ifPresent(l -> {
                        Point2D pos = e.getScreenPosition();
                        infoMsg.setText(l.stringRepresentation());
                        infoTooltip.show(editor, pos.getX() + 13, pos.getY() + 12);
                    });
            }
        });
        
        editor.addEventFilter(MouseOverTextEvent.MOUSE_OVER_TEXT_END, e -> {
            infoTooltip.hide();
        });
        
        setGraphic(IconFactory.of(component.getType()));
        TableView<EditorIssue> issues = new TableView<>();
        SplitPane pane = new SplitPane(this.pane, issues);
        pane.setOrientation(Orientation.VERTICAL);
        pane.setDividerPosition(0, 0.7);
        issues.getStyleClass().add("focus-fix-main");
        issues.setPlaceholder(new Label("No warnings to display"));
        TableColumn<EditorIssue, EditorIssue> type = new TableColumn<>();
        TableColumn<EditorIssue, EditorIssue> source = new TableColumn<>();
        issues.getColumns().addAll(description, type, source);
        issues.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        description.setMaxWidth(1f * Integer.MAX_VALUE * 0.6);
        source.setMaxWidth(1f * Integer.MAX_VALUE * 0.2);
        type.setMaxWidth(1f * Integer.MAX_VALUE * 0.2);
        description.setSortable(false);
        source.setSortable(false);
        type.setComparator(EditorIssue.COMPARATOR);
        type.setText("Type");
        description.setText("Description");
        source.setText("Location");
        setContent(pane);
        issues.setItems(editorWarnings);
        for(TableColumn<EditorIssue, ?> t : issues.getColumns()) {
            t.setCellFactory(c -> new EditorWarningCell(t.getText()));
            t.setCellValueFactory(c -> new SimpleObjectProperty(c.getValue()));
        }
        editor.setParagraphGraphicFactory(LineNumberFactory.get(editor));
        cleanupWhenNoLongerNeedIt = editor.multiPlainChanges()
                .successionEnds(Duration.ofMillis(200))
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(editor.multiPlainChanges())
                .filterMap(t -> {
                    if(t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);
        final Pattern whiteSpace = Pattern.compile( "^\\s+" );
        editor.addEventHandler(KeyEvent.KEY_PRESSED, KE -> {
            if (autoIndent && KE.getCode() == KeyCode.ENTER) {
                int caretPosition = editor.getCaretPosition();
                int currentParagraph = editor.getCurrentParagraph();
                Matcher m0 = whiteSpace.matcher(editor.getParagraph(currentParagraph - 1).getSegments().get(0));
                if (m0.find())
                    Platform.runLater(() -> editor.insertText(caretPosition, m0.group()));
            }
        });
        rerunStaticAnalysis();
        editor.textProperty().addListener((obs, oldV, newV) -> {
            if(!textHasBeenChanged)
                setText("*"+component.getIdentifier());
            textHasBeenChanged = true;
        });
    }

    public void undo() {
        editor.getUndoManager().undo();
    }

    public void redo() {
        editor.getUndoManager().redo();
    }

    public String getEditorContent() {
        return editor.getText();
    }

    public DSLComponent getComponent() {
        return component;
    }

    public void cleanup() {
        cleanupWhenNoLongerNeedIt.unsubscribe();
    }
    
    @SuppressWarnings("unchecked")
    public Val<Boolean> undoAvailable() {
        return editor.getUndoManager().undoAvailableProperty();
    }
    
    @SuppressWarnings("unchecked")
    public Val<Boolean> redoAvailable() {
        return editor.getUndoManager().redoAvailableProperty();
    }

    public boolean isChanged() {
        return textHasBeenChanged;
    }

    public void markAsSaved() {
        textHasBeenChanged = false;
        setText(component.getIdentifier());
    }
    
    public void requestStaticAnalysis() {
        rerunStaticAnalysis();
    }
    
    public void hotSwap(Optional<DSLComponent> renamed) {
        renamed.ifPresent(c -> {
            this.component = c;  
            setText(textHasBeenChanged ? "*" + component.getIdentifier() : component.getIdentifier());
            rerunStaticAnalysis();
        });
    }
    
    private Task<Pair<StyleSpans<Collection<String>>, List<EditorIssue>>> computeHighlightingAsync() {
        String text = editor.getText();
      
        editorWarnings.clear();
        Task<Pair<StyleSpans<Collection<String>>, List<EditorIssue>>> task = new Task<Pair<StyleSpans<Collection<String>>, List<EditorIssue>>>() {
            @Override
            protected Pair<StyleSpans<Collection<String>>, List<EditorIssue>> call() throws Exception {
                return new Pair<>(computeHighlighting(text), StaticAnalysis.run(editor.getText(), component, warningConsumer).getWarnings());
            }
        };
        Executors.SERVICE.execute(task);
        return task;
    }
    
    private void applyHighlighting(Pair<StyleSpans<Collection<String>>, List<EditorIssue>> highlighting) {
        editor.setStyleSpans(0, highlighting.getKey());
        final int len = editor.getText().length();
        highlighting.getValue().stream()
            .filter(v -> v.getStartIndex() >= 0 || v.getStartIndex() < len)
            .forEach(s -> editor.setStyle(s.getStartIndex(), s.getStopIndex(), s.getType().getStyle()));
    }
    
    private void rerunStaticAnalysis() {
        applyHighlighting(new Pair<>(computeHighlighting(editor.getText()), StaticAnalysis.run(editor.getText(), component, warningConsumer).getWarnings()));
    }

    public void requestFocus() {
        Platform.runLater(() -> editor.requestFocus());
    }

    public boolean markHit(Pair<SearchHit, Integer> fromSearch) {
        if(editor.getText().hashCode() != fromSearch.getValue()) {
            return false;
        }
        rerunStaticAnalysis();
        editor.setStyle(fromSearch.getKey().getStart(), fromSearch.getKey().getEnd(), Collections.singleton("search-highlight-selected"));
        editor.moveTo(fromSearch.getKey().getStart());
        editor.getCaretBounds().ifPresent(this::scrollToCaret);
        return true;
    }
    
    private void scrollToCaret(Bounds b) {
        Bounds c = editor.screenToLocal(b), e = editor.getBoundsInLocal();
        scrollX(e.getMinX(), e.getMaxX(), c.getMinX(), c.getMaxX());
        scrollY(e.getMinY(), e.getMaxY(), c.getMinY(), c.getMaxY());
    }
    
    private void scrollX(double editorMinX, double editorMaxX, double caretMinX, double caretMaxX) {
        if(caretMinX < editorMinX)
            pane.scrollXBy(caretMinX - editorMinX - editor.getWidth());
        if(caretMaxX > editorMaxX)
            pane.scrollXBy(caretMaxX - editorMaxX + editor.getWidth());
    }
    
    private void scrollY(double editorMinY, double editorMaxY, double caretMinY, double caretMaxY) {
        if(caretMinY < editorMinY)
            pane.scrollYBy(caretMinY - editorMinY - editor.getHeight());
        if(caretMaxY > editorMaxY)
            pane.scrollYBy(caretMaxY - editorMaxY + editor.getHeight());
    }

    public void setAutoSuggest(boolean autoSuggest) {
        this.autoCorrect = autoSuggest;
        if(autocomplete.isShowing())
            autocomplete.hide();
    }

    public void setAutoIndent(boolean autoIndent) {
        this.autoIndent = autoIndent;
    }
}