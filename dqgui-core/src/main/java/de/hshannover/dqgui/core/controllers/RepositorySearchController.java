package de.hshannover.dqgui.core.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.tinylog.Logger;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.core.Utility;
import de.hshannover.dqgui.core.model.ApplicationState;
import de.hshannover.dqgui.core.model.ProjectHandle;
import de.hshannover.dqgui.core.model.ApplicationState.LastSelectedSearchConfiguration;
import de.hshannover.dqgui.core.ui.ReadOnlyCodeArea;
import de.hshannover.dqgui.core.util.IconFactory;
import de.hshannover.dqgui.core.util.RepositorySearch;
import de.hshannover.dqgui.core.util.RepositorySearch.RepositorySearchResult;
import de.hshannover.dqgui.core.util.RepositorySearch.RepositorySearchResults;
import de.hshannover.dqgui.core.util.RepositorySearch.SearchHit;
import de.hshannover.dqgui.core.util.RepositorySearch.SearchOptions;
import de.hshannover.dqgui.core.util.RepositorySearch.SearchOptions.PatternInterpretation;
import de.hshannover.dqgui.core.util.comparators.DSLComponentComparator;
import de.hshannover.dqgui.execution.DSLServiceException;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import de.hshannover.dqgui.framework.AbstractWindowController;
import de.hshannover.dqgui.framework.control.SpecificTreeItem;
import de.hshannover.dqgui.framework.control.SpecificTreeItemCell;
import de.hshannover.dqgui.framework.model.ObjectWrapper;
import de.hshannover.dqgui.framework.serialization.Serialization;
import de.hshannover.dqgui.framework.signal.SignalHandler;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;

final class RepositorySearchController extends AbstractWindowController {
    private static final Image LINE = new Image(RepositorySearch.class.getResourceAsStream(
            Config.APPLICATION_PATH_ASSETS_IMAGE_UI+"minus.png"), 10, 10, true, true);
    
    /*
     * Injected by ApplicationContext
     */
    private final ProjectHandle projectHandle = null;
    private final ApplicationState applicationState = null;
    
    private final Map<RadioButton, PatternInterpretation> patternInterpretation = new HashMap<>();
    private final Map<CheckBox, DSLComponentType> scope = new HashMap<>();
    
    private final SignalHandler suicideHandler = () -> unregister();
    private final ReadOnlyCodeArea readOnly = new ReadOnlyCodeArea();
    private final VirtualizedScrollPane<ReadOnlyCodeArea> pane = new VirtualizedScrollPane<ReadOnlyCodeArea>(readOnly);
    private final TreeItem<String> root = new TreeItem<>();
    private final BiConsumer<DSLComponent, Pair<SearchHit, Integer>> handler;
    
    private List<RepositorySearchResult> resultList;
    
    public RepositorySearchController(ObjectWrapper handler) {
        this.handler = handler.unpack();
    }
    
    @FXML TextField searchBar;
    @FXML CheckBox action, check, source;
    @FXML RadioButton regEx, caseInsensitive, byValue, regExInsensitive;
    @FXML ToggleGroup searchBehavior;
    @FXML Button searchButton;
    @FXML Label results, statistics;
    @FXML VBox codeAreaReplaceBox;
    @FXML HBox noSearchResults;
    @FXML ComboBox<String> filter;
    @FXML TreeView<String> resultTree;
    
    @FXML
    void initialize() {
        projectHandle.projectChangedSignal().register(suicideHandler);
        patternInterpretation.put(regEx, PatternInterpretation.REGEX);
        patternInterpretation.put(caseInsensitive, PatternInterpretation.CASE_INSENSITIVE);
        patternInterpretation.put(byValue, PatternInterpretation.AS_VALUE);
        patternInterpretation.put(regExInsensitive, PatternInterpretation.REGEX_INSENSITIVE);
        scope.put(action, DSLComponentType.ACTION);
        scope.put(check, DSLComponentType.CHECK);
        scope.put(source, DSLComponentType.SOURCE);
        VBox.setVgrow(pane, Priority.ALWAYS);
        searchButton.disableProperty().bind(searchBar.textProperty().isEmpty()
                .or(Bindings.createBooleanBinding(() -> searchBar.getText().trim().isEmpty(), searchBar.textProperty())));
        searchBar.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER && !searchButton.isDisabled())
                search();
        });
        resultTree.setRoot(root);
        resultTree.setCellFactory(c -> {
            SpecificTreeItemCell cell = new SpecificTreeItemCell();
            cell.getStyleClass().add("tree-cell-no-padding");
            return cell;
        });
        resultTree.setOnMouseClicked(e -> {
            selectItem();
            if(e.getClickCount() == 2)
                goToComponent();
        });
        resultTree.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER)
                selectItem();
        });
        readOnly.setCursor(Cursor.DEFAULT);
        readOnly.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            if(e.getTarget() instanceof Text && (((Text) e.getTarget()).getStyleClass().contains("search-highlight") 
                    || ((Text) e.getTarget()).getStyleClass().contains("search-highlight-selected"))) {
                readOnly.setCursor(Cursor.HAND);
            } else {
                readOnly.setCursor(Cursor.DEFAULT);
            }
        });
        
        readOnly.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if(e.getTarget() instanceof Text && (((Text) e.getTarget()).getStyleClass().contains("search-highlight") 
                    || ((Text) e.getTarget()).getStyleClass().contains("search-highlight-selected"))) {
                SearchResultItem parent = getParent();
                SearchHit h = parent.hits.stream()
                        .filter(hit -> hit.getStart() <= readOnly.getCaretPosition() && hit.getEnd() >= readOnly.getCaretPosition())
                        .findFirst()
                        .map(Function.identity())
                        .orElse(new SearchHit(readOnly.getCaretPosition(), readOnly.getCaretPosition()));
                handler.accept(parent.searched, new Pair<>(h, parent.text.hashCode()));
            }
        });
        
        if(applicationState.getSearch() != null) {
            patternInterpretation.entrySet().stream()
                .filter(e -> e.getValue() == applicationState.getSearch().getPattern())
                .findFirst().ifPresent(e -> e.getKey().setSelected(true));
            scope.entrySet().forEach(e -> e.getKey().setSelected(applicationState.getSearch().getScope().contains(e.getValue())));
        }
    }

    private SearchResultItem getParent() {
        TreeItem<String> i = resultTree.getSelectionModel().getSelectedItem();
        if(i instanceof SearchHitItem)
            return ((SearchHitItem) i).parent;
        if(i instanceof SearchResultItem)
            return (SearchResultItem) i;
        throw new AssertionError("should not happen");
    }

    @FXML
    void search() {
        try {
            List<DSLComponentType> types = getSelectedTypes();
            RepositorySearchResults r = RepositorySearch.search(new SearchOptions(
                        searchBar.getText().trim(), 
                        patternInterpretation.get(searchBehavior.getSelectedToggle()),
                        types),
                    projectHandle.getServiceProvider().getService().createComponentSplitter(types));
            searchResultUpdate(r.getOccurenceCount() > 0);
            results.setText(formatSearchResults(r));
            statistics.setText(r.getStatistics());
            resultList = r.getResults();
            resultList.sort((c1, c2) -> DSLComponentComparator.getInstance().compare(c1.getSearchedComponent(), c2.getSearchedComponent()));
            rebuildTree();
        } catch (DSLServiceException e) {
            Logger.error(e);
            getDialogContext().exceptionDialog(e);
        }
    }
    
    private String formatSearchResults(RepositorySearchResults r) {
        if(r.getOccurenceCount() == 0)
            return "No search results available";
        String results = r.getOccurenceCount() == 1 ? "1 search result" : r.getOccurenceCount() + " search results";
        return results + " in " + (r.getResults().size() == 1 ? "1 component" : r.getResults().size() + " components");
    }

    private List<DSLComponentType> getSelectedTypes() {
        return scope.entrySet().stream()
                .filter(e -> e.getKey().isSelected())
                .map(e -> e.getValue())
                .collect(Collectors.toList());
    }
    
    private void goToComponent() {
        TreeItem<String> item = resultTree.getSelectionModel().getSelectedItem();
        if(item instanceof SearchHitItem) {
            handler.accept(((SearchHitItem) item).parent.searched, new Pair<>(((SearchHitItem) item).hit, ((SearchHitItem) item).parent.text.hashCode()));
        }
    }
    
    private void searchResultUpdate(boolean codeArea) {
        Platform.runLater(() -> {
            codeAreaReplaceBox.getChildren().remove(1);
            codeAreaReplaceBox.getChildren().add(codeArea ? pane : noSearchResults);
        });
    }
    
    private void selectItem() {
        TreeItem<String> item = resultTree.getSelectionModel().getSelectedItem();
        if(item instanceof SearchResultItem) {
            readOnly.setText(((SearchResultItem) item).text, ((SearchResultItem) item).hits);
        }
        if(item instanceof SearchHitItem) {
            readOnly.setText(((SearchHitItem) item).parent.text, ((SearchHitItem) item).parent.hits, ((SearchHitItem) item).hit);
        }
    }
    
    private void rebuildTree() {
        root.getChildren().clear();
        for(RepositorySearchResult s : resultList) {
            SearchResultItem i = new SearchResultItem(s.getSearchedComponent(), s.getSearchedText(), s.getHits());
            root.getChildren().add(i);
            int hit = 1;
            for(SearchHit h : s.getHits()) {
                i.getChildren().add(new SearchHitItem(i, h, hit++));
            }
        }
        if(!root.getChildren().isEmpty()) {
            resultTree.getSelectionModel().select(root.getChildren().get(0));
            selectItem();
        }
    }
    
    @Override
    public void onDestruction() {
        projectHandle.projectChangedSignal().unregister(suicideHandler);
        applicationState.setSearch(new LastSelectedSearchConfiguration(patternInterpretation.get(searchBehavior.getSelectedToggle()), getSelectedTypes()));
        Serialization.dump(applicationState);
    }
    
    @Override
    protected void keyBindRegisterHook(ObservableMap<KeyCombination, Runnable> bindings) {
        bindings.put(new KeyCodeCombination(KeyCode.ESCAPE), () -> unregister());
    }

    private static class SearchResultItem extends SpecificTreeItem {
        private final DSLComponent searched;
        private final String text;
        private final List<SearchHit> hits;

        private SearchResultItem(DSLComponent searched, String text, List<SearchHit> hits) {
            super(searched.getIdentifier(), IconFactory.of(searched.getType()));
            this.searched = searched;
            this.text = text;
            this.hits = hits;
        }
        
    }
    
    private static class SearchHitItem extends SpecificTreeItem {
        private final SearchResultItem parent;
        private final SearchHit hit;
        
        private SearchHitItem(SearchResultItem parent, SearchHit hit, int hitCount) {
            super(Utility.ordinal(hitCount) + " result [" + hit.getStart() + ", " + hit.getEnd() + "]", new ImageView(LINE));
            this.parent = parent;
            this.hit = hit;
        }
        
    }
}
