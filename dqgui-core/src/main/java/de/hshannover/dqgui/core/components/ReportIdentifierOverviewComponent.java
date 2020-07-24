package de.hshannover.dqgui.core.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.core.Executors;
import de.hshannover.dqgui.core.Utility;
import de.hshannover.dqgui.core.components.ReportDataComponent.FeedbackUpdateCallback;
import de.hshannover.dqgui.core.configuration.Components;
import de.hshannover.dqgui.core.model.Iqm4hdFeedback;
import de.hshannover.dqgui.core.model.Iqm4hdFeedback.FeedbackState;
import de.hshannover.dqgui.core.model.ProjectHandle;
import de.hshannover.dqgui.core.util.IconFactory;
import de.hshannover.dqgui.execution.database.TargetedDatabase;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;
import de.hshannover.dqgui.execution.database.api.DatabaseFetcher;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData;
import de.hshannover.dqgui.framework.AbstractComponentController;
import de.hshannover.dqgui.framework.ErrorUtility;
import de.hshannover.dqgui.framework.JavaFXTools;
import de.hshannover.dqgui.framework.model.ObjectWrapper;
import de.mvise.iqm4hd.api.DatabaseEntry;
import de.mvise.iqm4hd.api.DatabaseEntryIterator;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

final class ReportIdentifierOverviewComponent extends AbstractComponentController {
    private static final Image FEEDBACK = new Image(ReportIdentifierOverviewComponent.class.getResourceAsStream(Config.APPLICATION_PATH_ASSETS_IMAGE_UI+"letter.png"));
    private static Gson GSON = new Gson();
    
    /*
     * Injected by ApplicationContext;
     */
    private final ProjectHandle projectHandle = null;
    
    private Iqm4hdFeedback feedback;
    private final FeedbackUpdateCallback callback;
    private final Iqm4hdMetaData meta;
    private final Set<List<String>> identifierValues = new HashSet<>();
    private final List<String> pkColumnNames = new ArrayList<>();
    
    @FXML
    BorderPane rootPane;

    ReportIdentifierOverviewComponent(Iqm4hdMetaData meta, ObjectWrapper identifiers, Iqm4hdFeedback feedback, FeedbackUpdateCallback callback) {
        this.meta = meta;
        this.feedback = feedback;
        this.callback = callback;
        List<Object[]> idfs = identifiers.unpack();
        idfs.stream().forEach(this::findAllIdentifiers);
    }
    
    private void findAllIdentifiers(Object[] obj) {
        if(obj == null) return;
        List<String> identifiers = new ArrayList<>(3);
        for(Object o : obj) 
            identifiers.add(Utility.toString(o));
        identifierValues.add(identifiers);
    }
    
    private static class FeedbackTableData {
        private final Map<String, String> data;
        private final List<String> primaryKeys;
        
        private FeedbackTableData(Map<String, String> data, List<String> primaryKeys) {
            this.data = data;
            this.primaryKeys = primaryKeys;
        }
    }
    
    @SuppressWarnings("unchecked")
    @FXML
    void initialize() {
        CompletableFuture<Tab>[] futures = new CompletableFuture[meta.getDatabases().size()];
        int i = 0;
        for(TargetedDatabase dbs : meta.getDatabases()) {
            futures[i++] = CompletableFuture.supplyAsync(() -> {
                Optional<DatabaseConnection> con = projectHandle.getCurrentEnvironment().unsafeGet().lookupGuid(dbs.getGuid());
                if(con.isPresent()) {
                    try {
                        return identifierTab(dbs.getIdentifier(), con.get(), dbs.getQuery(), meta.getIdentifiers());
                    } catch (Exception e) {
                        throw ErrorUtility.rethrow(e);
                    }
                } else {
                    TextArea error = new TextArea();
                    error.getStyleClass().add("log-export-import-db");
                    error.setEditable(false);
                    error.setWrapText(true);
                    error.setText(String.format("Failed to load identifiers.%n%n%s database connection %s with GUID %s does not exist anymore.", dbs.getEngine(), dbs.getIdentifier(), dbs.getGuid()));
                    return new Tab(dbs.getIdentifier(), error);
                }
            }, Executors.SERVICE)
                    .exceptionally(t -> new Tab(dbs.getIdentifier(), loadComponent(Components.REPORT_FAILED_TO_LOAD, t).getParent()));
         }
        Executors.SERVICE.submit(() -> {
            List<Tab> tabs = new ArrayList<>();
            for(CompletableFuture<Tab> t : futures)
                tabs.add(t.join());
            Platform.runLater(() -> {
                TabPane p = new TabPane();
                p.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
                p.getTabs().addAll(tabs);
                rootPane.setStyle("");
                rootPane.setCenter(p);
            });
        });
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Tab identifierTab(String dbIdentifier, DatabaseConnection con, String query, List<String> iqm4hdIdentifiers) throws Exception {
        Tab t = new Tab(dbIdentifier);
        TableView<FeedbackTableData> table = new TableView<>();
        List<FeedbackTableData> data = new ArrayList<>();
        boolean setHeader = false;
        List<String> actualIdentifiers = new ArrayList<>();
        try(DatabaseFetcher f = con.getEngine().createFetcher(con)) {
            f.initiate();
            DatabaseEntryIterator e = f.fetch(query);
            DatabaseEntry ent;
            while((ent = e.next()) != null) {
                
                if(!setHeader) {
                    List<String> headers = new ArrayList<>(ent.getKeys());
                    
                    for(String s : iqm4hdIdentifiers) {
                        if(headers.contains(s)) {
                            actualIdentifiers.add(s);
                        } else if(headers.contains(s.toLowerCase())) {
                            actualIdentifiers.add(s.toLowerCase());
                        } else if(headers.contains(s.toUpperCase())) {
                            actualIdentifiers.add(s.toUpperCase());
                        } else {
                            throw new IllegalArgumentException("could not find: " + s + " in " + iqm4hdIdentifiers);
                        }
                    }
                    
                    Collections.sort(actualIdentifiers);
                    Collections.sort(headers);
                    pkColumnNames.addAll(actualIdentifiers);
                    
                    int offset = 0;
                    
                    for(; offset < actualIdentifiers.size(); offset++) {
                        int idx = headers.indexOf(actualIdentifiers.get(offset));
                        Collections.swap(headers, offset, idx);
                    }
                    
                    for(String s : meta.getActionValues()) {
                        int idx = headers.indexOf(s);
                        if(idx < 0) {
                            idx = headers.indexOf(s.toUpperCase());
                            if(idx < 0)
                                idx = headers.indexOf(s.toLowerCase());
                        }
                        if(idx < 0) continue;
                        Collections.swap(headers, offset, idx);
                        offset++;
                    }
                    
                    for(String s : headers) {
                        TableColumn column = new TableColumn(s);
                        column.setMinWidth(Region.USE_PREF_SIZE);
                        table.getColumns().add(column);
                    }
                    
                    TableColumn feedback = new TableColumn();
                    feedback.setGraphic(new ImageView(FEEDBACK));
                    feedback.setMinWidth(Region.USE_PREF_SIZE);
                    table.getColumns().add(0, feedback);
                    
                    setHeader = true;
                }
                
                List<String> k = actualIdentifiers.stream().map(ent::getValue).map(Utility::toString).collect(Collectors.toList());
                if(identifierValues.contains(k)) {
                    HashMap<String, String> tmp = new HashMap<>();
                    final DatabaseEntry entf = ent;
                    List<String> primaryKeys = actualIdentifiers.stream()
                                .map(pk -> entf.getValue(pk))
                                .map(Utility::toString)
                                .collect(Collectors.toList());
                    for(String key : ent.getKeys())
                        tmp.put(key, Utility.toString(ent.getValue(key)));
                    data.add(new FeedbackTableData(tmp, primaryKeys));
                }
            }
        }
        
        table.setItems(FXCollections.observableArrayList(data));
        for(TableColumn c : table.getColumns().subList(1, table.getColumns().size())) 
            c.setCellValueFactory(clb -> new SimpleStringProperty(((FeedbackTableData) ((CellDataFeatures) clb).getValue()).data.getOrDefault(c.getText(), "")));
        table.getColumns().get(0).setCellValueFactory(clb -> new SimpleObjectProperty(((FeedbackTableData) ((CellDataFeatures) clb).getValue()).primaryKeys));
        table.getColumns().get(0).setCellFactory(clb -> new TableCell() {

                @Override
                protected void updateItem(Object item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if(item == null || empty) {
                        setGraphic(null);
                        setTooltip(null);
                        return;
                    }
                    
                    FeedbackState f  = feedback.getFeedback((List<String>) item);
                    
                    setGraphic(IconFactory.of(f));
                    setTooltip(new Tooltip(f.getMessage()));
                }
                
            });
        table.getStyleClass().addAll("focus-fix-main", "identifier-display-table");
        table.setPlaceholder(new Label("No identifiers have been resolved by identifier mapping."));
        t.setContent(table);
        table.getSortOrder().add(table.getColumns().get(1));
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        callback.registerIdentifierCallback(() -> table.refresh());
        JavaFXTools.tableViewResize((TableView<?>) t.getContent());
        
        MenuItem success = new MenuItem("Mark as correct", IconFactory.of(FeedbackState.CORRECT));
        success.setOnAction(e -> updateItem(table, table.getSelectionModel().getSelectedItems(), FeedbackState.CORRECT));
        
        MenuItem falsepositive = new MenuItem("Mark as false positive", IconFactory.of(FeedbackState.FALSE_POSITIVE));
        falsepositive.setOnAction(e -> updateItem(table, table.getSelectionModel().getSelectedItems(), FeedbackState.FALSE_POSITIVE));
        
        MenuItem unmark = new MenuItem("Unmark", IconFactory.of(FeedbackState.NOT_EVALUATED));
        unmark.setOnAction(e -> updateItem(table, table.getSelectionModel().getSelectedItems(), FeedbackState.NOT_EVALUATED));
        
        MenuItem copy = new MenuItem("Copy data as JSON");
        copy.setOnAction(e -> {
            List<FeedbackTableData> l = table.getSelectionModel().getSelectedItems();
            if(l.isEmpty()) return;
            ClipboardContent c = new ClipboardContent();
            c.put(DataFormat.PLAIN_TEXT, GSON.toJson(l.size() == 1 ? l.get(0).data : l.stream().map(d -> d.data).collect(Collectors.toList())));
            Clipboard.getSystemClipboard().setContent(c);
        });
        
        table.setContextMenu(new ContextMenu(success, falsepositive, unmark, copy));
        return t;
    }
    
    private void updateItem(TableView<FeedbackTableData> table, List<FeedbackTableData> observableList, FeedbackState t) {
        callback.updateFeedback(observableList.stream().map(f -> f.primaryKeys).collect(Collectors.toList()), t);
    }

    /**
     * Post processing hook that can only run when JavaFX renders the table to the screen.
     * This should only be called once in the lifetime of this object and when the table has been rendered at least one time on screen.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    void prepareTableOnFirstCallOnly() {
        Map<String, TableColumn> columns = new HashMap<>();
        Map<String, Label> labels = new HashMap<>();
        ((TabPane) rootPane.getCenter()).getTabs().stream()
            .filter(t -> t.getContent().getClass() == TableView.class)
            .map(t -> (TableView<FeedbackTableData>) t.getContent())
            .peek(t -> t.getColumns().stream().filter(tc -> !tc.getText().isEmpty())
                    .forEach(tc -> columns.put(tc.getText(), tc)))
            .forEach(t -> {
                t.lookupAll("TableColumnHeader").forEach(tch -> {
                    Node n =  tch.lookup("Label");
                    if(n instanceof Label && !((Label) n).getText().isEmpty()) {
                        Label l = (Label) n;
                        labels.put(l.getText(), l);
                        if(pkColumnNames.contains(l.getText()))
                            l.setStyle("-fx-text-fill: green");
                        if(meta.getActionValues().contains(l.getText()) ||
                                meta.getActionValues().contains(l.getText().toUpperCase()) ||
                                meta.getActionValues().contains(l.getText().toLowerCase())) {
                            l.setStyle("-fx-text-fill: red");
                        }
                    }
                });
            });
        columns.forEach((k, v) -> {
            // Magic number that is roughly the css growth equiv
            v.setMinWidth(1.8*labels.get(k).getLayoutBounds().getWidth());
        });
    }
    
}
