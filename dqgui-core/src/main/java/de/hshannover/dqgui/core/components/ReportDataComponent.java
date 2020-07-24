package de.hshannover.dqgui.core.components;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import com.google.common.collect.Ordering;
import de.hshannover.dqgui.core.Executors;
import de.hshannover.dqgui.core.Utility;
import de.hshannover.dqgui.core.configuration.Components;
import de.hshannover.dqgui.core.controllers.ReportsController.MenuIndexReference;
import de.hshannover.dqgui.core.model.Iqm4hdFeedback;
import de.hshannover.dqgui.core.model.Iqm4hdFeedback.FeedbackState;
import de.hshannover.dqgui.core.model.ProjectHandle;
import de.hshannover.dqgui.core.util.IconFactory;
import de.hshannover.dqgui.core.util.TableGenerator;
import de.hshannover.dqgui.execution.database.TargetedDatabase;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData;
import de.hshannover.dqgui.framework.AbstractComponentController;
import de.hshannover.dqgui.framework.ErrorUtility;
import de.hshannover.dqgui.framework.FXMLLoadingFactory.ComponentContent;
import de.hshannover.dqgui.framework.JavaFXTools;
import de.hshannover.dqgui.framework.model.ObjectWrapper;
import de.mvise.iqm4hd.api.ExecutionIssue;
import de.mvise.iqm4hd.api.ExecutionReport;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

final class ReportDataComponent extends AbstractComponentController {
    private static MethodHandle GETTER_SCORE;
    private static MethodHandle GETTER_SEVERITY;

    static {
        try {
            MethodHandles.Lookup lookup =  MethodHandles.publicLookup();
            GETTER_SCORE = lookup.findVirtual(ExecutionIssue.class, "getScore", MethodType.methodType(double.class));
            GETTER_SEVERITY = lookup.findVirtual(ExecutionIssue.class, "getSeverity", MethodType.methodType(String.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
            throw ErrorUtility.rethrow(e);
        }
    }

    private static final int DATA_LIST_CELL_HEIGHT_IN_PX = 24;
    private static final Insets MARGIN = new Insets(5);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withZone(TimeZone.getDefault().toZoneId());
    private static final List<String> TRACE_HEADER = Arrays.asList(new String[] {
            "Connection opened",
            "Identifier",
            "Engine",
            "Language",
            "Host"
        });

    class FeedbackUpdateCallback {
        List<Runnable> updateInIdentifierOverview = new ArrayList<>();
        
        void updateFeedback(List<List<String>> primaryKeys, FeedbackState f) {
            if(primaryKeys.isEmpty()) return;
            CompletableFuture.runAsync(() -> {
                for(List<String> s : primaryKeys)
                    feedback.setFeedback(s, f);
                projectHandle.dumpFeedback(meta, feedback);
            }, Executors.SERVICE)
            .thenRun(() -> {
                Platform.runLater(() -> {
                    issuesTable.refresh();
                    updateInIdentifier();
                });
            }).exceptionally(e -> {
                Platform.runLater(() -> getDialogContext().exceptionDialog(e));
                return null;
            });
        }
        
        void registerIdentifierCallback(Runnable updateInIdentifierOverview) {
            this.updateInIdentifierOverview.add(updateInIdentifierOverview);
        }
        
        void updateInIdentifier() {
            updateInIdentifierOverview.forEach(Runnable::run);
        }
        
        void destruct() {
            this.updateInIdentifierOverview.clear();
        }
    }
    
    private static class PaddedStringCell extends TableCell<ExecutionIssue, ExecutionIssue> {
        private final MethodHandle getter;
        private final Label paddedLabel = new Label();
        private final VBox vbox = new VBox(paddedLabel);

        private PaddedStringCell(MethodHandle getter) {
            this.getter = getter;
            vbox.setMinWidth(USE_COMPUTED_SIZE);
            vbox.setMinHeight(USE_COMPUTED_SIZE);
            VBox.setMargin(paddedLabel, MARGIN);
            paddedLabel.getStyleClass().add("dataNestedLabelBlack");
        }

        @Override
        protected void updateItem(ExecutionIssue item, boolean empty) {
            if(empty || item == null) {
                setGraphic(null);
                return;
            }

            try {
                paddedLabel.setStyle("");
                Object obj = getter.invoke(item);
                String text = Utility.toString(obj);
                paddedLabel.setText(text);
                if(text.equals("ERROR")) 
                    paddedLabel.setStyle("-fx-text-fill: red;");
                if(text.equals("WARNING"))
                    paddedLabel.setStyle("-fx-text-fill: #ff8c00;");
            } catch (Throwable e) {
                ErrorUtility.rethrow(e);
            }

            setGraphic(vbox);
        }

    }

    private static class InfoCell extends TableCell<ExecutionIssue, ExecutionIssue> {
        private final VBox vbox = new VBox();
        private final Label noInfo = new Label("No information given");

        private InfoCell() {
            noInfo.getStyleClass().add("dataNestedLabelBlack");
            VBox.setMargin(noInfo, MARGIN);
            vbox.setMinWidth(USE_COMPUTED_SIZE);
            vbox.setMinHeight(USE_COMPUTED_SIZE);
        }

        @Override
        protected void updateItem(ExecutionIssue item, boolean empty) {
            super.updateItem(item, empty);

            if(empty || item == null) {
                setGraphic(null);
                setText(null);
                return;
            }

            if(item.getInfo().isEmpty()) {
                vbox.getChildren().clear();
                vbox.getChildren().add(noInfo);
                setGraphic(vbox);
                return;
            }

            vbox.getChildren().clear();
            processInfo(item.getInfo());

            setGraphic(vbox);
            setText(null);
        }

        private void processInfo(Map<String, Object[]> info) {
            TreeMap<String, Object[]> sortedMap = new TreeMap<>(info);
            for(Map.Entry<String, Object[]> e : sortedMap.entrySet()) {
                Label infoLabel = new Label(e.getKey());
                vbox.getChildren().add(infoLabel);
                infoLabel.getStyleClass().add("dataNestedLabel");
                VBox.setMargin(infoLabel, MARGIN);
                if(e.getValue() == null || e.getValue().length == 0) {
                    infoLabel.setText(e.getKey() + " - no value given");
                    continue;
                }
                List<String> stringList = new ArrayList<>();
                for(Object o : e.getValue())
                    stringList.add(Utility.toString(o));
                ObservableList<String> infoList = FXCollections.observableArrayList(stringList);
                ListView<String> infoView = new ListView<>(infoList);
                infoView.getStyleClass().add("dataView");
                infoView.prefHeightProperty().bind(Bindings.size(infoList).multiply(DATA_LIST_CELL_HEIGHT_IN_PX));
                VBox.setMargin(infoView, MARGIN);
                vbox.getChildren().add(infoView);
            }
        }
    }

    private class DataCell extends TableCell<ExecutionIssue, ExecutionIssue> {
        private final VBox vbox = new VBox();
        private final HBox identifierBox = new HBox();
        private final Label identifier = new Label("Identifier"), value = new Label("Value");
        private final ObservableList<String> identifierList = FXCollections.observableArrayList(), valueList = FXCollections.observableArrayList();
        private final ListView<String> identifierView = new ListView<>(identifierList), valueView = new ListView<>(valueList);
        private final ContextMenu feedbackContextMenu;

        private DataCell() {
            identifier.getStyleClass().add("dataNestedLabel");
            identifier.setContentDisplay(ContentDisplay.RIGHT);
            Region r = new Region();
            identifierBox.getChildren().addAll(identifier, r, IconFactory.of(FeedbackState.NOT_EVALUATED));
            identifierBox.setAlignment(Pos.CENTER);
            HBox.setHgrow(r, Priority.ALWAYS);
            value.getStyleClass().add("dataNestedLabel");
            VBox.setMargin(identifierBox, MARGIN);
            VBox.setMargin(value, MARGIN);
            VBox.setMargin(identifierView, MARGIN);
            VBox.setMargin(valueView, MARGIN);
            vbox.setMinWidth(USE_COMPUTED_SIZE);
            vbox.setMinHeight(USE_COMPUTED_SIZE);
            identifierView.getStyleClass().add("dataView");
            identifierView.prefHeightProperty().bind(Bindings.size(identifierList).multiply(DATA_LIST_CELL_HEIGHT_IN_PX));
            
            MenuItem copyIdentifiers = new MenuItem("Copy Identifier(s)");
            copyIdentifiers.setOnAction(e -> {
                ClipboardContent c = new ClipboardContent();
                c.put(DataFormat.PLAIN_TEXT, identifierList.size() == 1 ? identifierList.get(0) : identifierList.toString());
                Clipboard.getSystemClipboard().setContent(c);
            });
            identifierView.setContextMenu(new ContextMenu(copyIdentifiers));
            
            MenuItem copyValues = new MenuItem("Copy Value(s)");
            copyValues.setOnAction(e -> {
                ClipboardContent c = new ClipboardContent();
                c.put(DataFormat.PLAIN_TEXT, valueList.size() == 1 ? valueList.get(0) : valueList.toString());
                Clipboard.getSystemClipboard().setContent(c);
            });
            valueView.setContextMenu(new ContextMenu(copyValues));
            
            valueView.getStyleClass().add("dataView");
            valueView.prefHeightProperty().bind(Bindings.size(valueList).multiply(DATA_LIST_CELL_HEIGHT_IN_PX));
            
            MenuItem success = new MenuItem("Mark as correct", IconFactory.of(FeedbackState.CORRECT));
            success.setOnAction(e -> updateItem(identifierList, FeedbackState.CORRECT));
            
            MenuItem falsepositive = new MenuItem("Mark as false positive", IconFactory.of(FeedbackState.FALSE_POSITIVE));
            falsepositive.setOnAction(e -> updateItem(identifierList, FeedbackState.FALSE_POSITIVE));
            
            MenuItem unmark = new MenuItem("Unmark", IconFactory.of(FeedbackState.NOT_EVALUATED));
            unmark.setOnAction(e -> updateItem(identifierList, FeedbackState.NOT_EVALUATED));
            
            feedbackContextMenu = new ContextMenu(success, falsepositive, unmark);
        }

        private void updateItem(ObservableList<String> identifierList, FeedbackState state) {
            feedbackCallback.updateFeedback(Collections.singletonList(identifierList), state);
        }

        @Override
        protected void updateItem(ExecutionIssue item, boolean empty) {
            super.updateItem(item, empty);
            
            if(empty || item == null) {
                setGraphic(null);
                setText(null);
                setTooltip(null);
                setContextMenu(null);
                return;
            }

            if((item.getIdentifier() == null || item.getIdentifier().length == 0) && (item.getValue() == null || item.getValue().length == 0)) {
                setText("No data available");
                return;
            }

            vbox.getChildren().clear();
            if(item.getIdentifier() != null) {
                identifierList.clear();
                vbox.getChildren().addAll(identifierBox, identifierView);
                convert(item.getIdentifier(), identifierList);
                FeedbackState f = feedback.getFeedback(identifierList);
                setContextMenu(feedbackContextMenu);
                setTooltip(new Tooltip(f.getMessage()));
                identifierBox.getChildren().set(2, IconFactory.of(f));
            }

            if(item.getValue() != null) {
                valueList.clear();
                vbox.getChildren().addAll(value, valueView);
                convert(item.getValue(), valueList);
            }

            setText(null);
            setGraphic(vbox);
        }

        private void convert(Object[] input, ObservableList<String> target) {
            for(Object o : input) {
                target.add(Utility.toString(o));
            }
        }

        
    }

    /*
     * Injected by ApplicationContext
     */
    private final ProjectHandle projectHandle = null;
    
    private Iqm4hdFeedback feedback;
    private final ExecutionReport report;
    private final Iqm4hdMetaData meta;
    private final ObservableList<ExecutionIssue> issueList;
    private final MenuIndexReference collapsedMenuIndexSelected;
    private final FeedbackUpdateCallback feedbackCallback = new FeedbackUpdateCallback();

    @FXML
    TabPane tabPane;
    @FXML
    TextArea log, databaseTrace, infoText;
    @FXML
    TitledPane info, issues;
    @FXML
    Accordion accordion;
    @FXML
    TableView<ExecutionIssue> issuesTable;
    @FXML
    TableColumn<ExecutionIssue, ExecutionIssue> colSeverity, colScore, colData, colInfo;

    ReportDataComponent(Iqm4hdMetaData meta, ExecutionReport report, MenuIndexReference collapsedMenuIndexSelected) {
        this.report = report;
        this.meta = meta;
        this.issueList = FXCollections.observableArrayList(report.getExecutionResults());
        this.collapsedMenuIndexSelected = collapsedMenuIndexSelected;
    }

    @FXML
    void initialize() {
        this.feedback = projectHandle.requestFeedback(meta);
        
        JavaFXTools.tableColumnAutoResize(issuesTable);
        colSeverity.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        colSeverity.setCellFactory(param -> new PaddedStringCell(GETTER_SEVERITY));
        colScore.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        colScore.setCellFactory(param -> new PaddedStringCell(GETTER_SCORE));
        colData.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        colData.setCellFactory(param -> new DataCell());
        colInfo.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        colInfo.setCellFactory(param -> new InfoCell());

        try {
            log.setText(projectHandle.logFor(meta));
        } catch (IOException e) {
            throw ErrorUtility.rethrow(e);
        }

        List<TargetedDatabase> dbs = meta.getDatabases();
        if(dbs.isEmpty()) {
            databaseTrace.setText("No databases have been called.");
        } else {
            dbs.sort(Comparator.comparing(TargetedDatabase::getCalled));
            TableGenerator tableGenerator = new TableGenerator();
            List<List<String>> rowsList = new ArrayList<>();
            for(TargetedDatabase db : dbs) {
                rowsList.add(Arrays.asList(new String[] {
                        FORMATTER.format(db.getCalled()),
                        db.getIdentifier(),
                        db.getEngine(),
                        db.getLanguage(),
                        db.getHost()
                }));
            }
            databaseTrace.setText(tableGenerator.generateTable(TRACE_HEADER, rowsList));
        }
        
        accordion.expandedPaneProperty().addListener((obs, oldV, newV) -> {
            int index = accordion.getChildrenUnmodifiable().indexOf(newV);
            if(index < 0)
                return;
            collapsedMenuIndexSelected.index = index;
        });
        accordion.setExpandedPane(info);
        
        int identifier = 0, value = 0;
        
        List<Object[]> identifiers = new ArrayList<>();
        
        for(ExecutionIssue issue : report.getExecutionResults()) {
            /* Magic Numbers <3
             * 0 = only identifier
             * 1 = only value
             * else = probably both?!
             */
            switch(issue.getType()) {
                case 0:
                    identifier++;
                    identifiers.add(issue.getIdentifier());
                    break;
                case 1:
                    value++;
                    break;
                default:
                    identifier++;
                    identifiers.add(issue.getIdentifier());
                    value++;
                    break;
            }
        }

        Function<Object[], String> extract = ids -> {
            if(ids.length == 0)
                return "";
            return Utility.toString(ids[0]);
        };
        
        Comparator<ExecutionIssue> comp = (i1, i2) -> {
            return Objects.compare(extract.apply(i1.getIdentifier()), extract.apply(i2.getIdentifier()), Ordering.natural());
        };
        
        colData.setSortable(true);
        colData.setComparator(comp);
        
        issuesTable.setItems(issueList);

        if(report.getExecutionResults().isEmpty())
            accordion.getPanes().remove(issues);
        
        if(identifier > 0 && !meta.getIdentifiers().isEmpty()) {
            Tab identifierFeedback = new Tab("Identifier Overview & Feedback");
            try {
                ComponentContent c = loadComponent(Components.REPORT_IDENTIFIER_OVERVIEW, meta, new ObjectWrapper(identifiers), feedback, feedbackCallback);
                identifierFeedback.setContent(c.getParent());
                tabPane.getTabs().add(identifierFeedback);
                ChangeListener<Tab> listener = (obs, oldV, newV) -> {
                    if(newV == identifierFeedback) {
                        ((ReportIdentifierOverviewComponent) c.getController()).prepareTableOnFirstCallOnly();
                    }
                };
                tabPane.getSelectionModel().selectedItemProperty().addListener(listener);
                tabPane.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> tabPane.getSelectionModel().selectedItemProperty().removeListener(listener));
            } catch(Exception e) {
                identifierFeedback.setContent(loadComponent(Components.REPORT_FAILED_TO_LOAD, e).getParent());
                tabPane.getTabs().add(identifierFeedback);
            }
        }

        TableGenerator infoGenerator = new TableGenerator();
        List<List<String>> rowsList = new ArrayList<>();
        String[][] infoArray = new String[][]{
            {"Name", meta.getHumanReadable().getHumanReadable()},
            {"Identifier", meta.getIdentifier()},
            {"Hash", meta.getHash()},
            {"Action", meta.getAction()},
            {"Environment", meta.getEnvironment()},
            {"Creator", meta.getCreator()},
            {"Executor", meta.getExecutionEnvironment()},
            {"Started", FORMATTER.format(meta.getStarted())},
            {"Finished", FORMATTER.format(meta.getFinished())},
            {"Duration", Utility.humanReadableFormat(meta.getDuration())},
            {"Message", meta.getMessage()},
            {"Return Code", meta.getReturnCode().toString()},
            {"Optimize", Boolean.toString(meta.isOptimize())},
            {TableGenerator.CREATE_PARTITION, TableGenerator.CREATE_PARTITION},
            {"Issues", Integer.toString(report.getExecutionResults().size())},
            {"Identifier Issues", Integer.toString(identifier)},
            {"Value Issues", Integer.toString(value)},
            {TableGenerator.CREATE_PARTITION, TableGenerator.CREATE_PARTITION},
            {"Referenced Identifiers", meta.getIdentifier().isEmpty() ? "None available" : meta.getIdentifiers().toString() },
            {"Referenced Values", meta.getActionValues().isEmpty() ? "None available" : meta.getActionValues().toString() }
        };
        for(String[] s : infoArray) {
            rowsList.add(Arrays.asList(s));
        }
        infoText.setText(infoGenerator.generateTable(Arrays.asList(new String[] {"Key", "Value"}), rowsList));
    }

    @Override
    public void onDestruction() {
        feedbackCallback.destruct();
    }
}
