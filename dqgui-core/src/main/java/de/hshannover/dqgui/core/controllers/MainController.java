package de.hshannover.dqgui.core.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.controlsfx.control.SearchableComboBox;
import org.tinylog.Logger;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.core.Executors;
import de.hshannover.dqgui.core.concurrency.TaskHandler;
import de.hshannover.dqgui.core.configuration.Dialogs;
import de.hshannover.dqgui.core.configuration.Views;
import de.hshannover.dqgui.core.model.ApplicationProperties;
import de.hshannover.dqgui.core.model.ApplicationState;
import de.hshannover.dqgui.core.model.DSLComponentCollection;
import de.hshannover.dqgui.core.model.ProjectHandle;
import de.hshannover.dqgui.core.ui.CodeEditor;
import de.hshannover.dqgui.core.ui.ComponentAwareCell;
import de.hshannover.dqgui.core.ui.DatabaseTreeItem;
import de.hshannover.dqgui.core.ui.NotificationService;
import de.hshannover.dqgui.core.ui.TaskUiUpdateService;
import de.hshannover.dqgui.core.util.ExceptionRecoveryTips;
import de.hshannover.dqgui.core.util.ExtraData;
import de.hshannover.dqgui.core.util.HtmlLoader;
import de.hshannover.dqgui.core.util.NotificationTools;
import de.hshannover.dqgui.core.util.RemoteConnection;
import de.hshannover.dqgui.core.util.RemoteExecution;
import de.hshannover.dqgui.core.util.RemoteExecution.ExecutionStrategy;
import de.hshannover.dqgui.core.util.RepositorySearch.SearchHit;
import de.hshannover.dqgui.core.util.StaticAnalysis;
import de.hshannover.dqgui.core.util.StaticAnalysis.ComponentLink;
import de.hshannover.dqgui.core.util.StaticAnalysis.EditorLink;
import de.hshannover.dqgui.core.util.comparators.DSLComponentComparator;
import de.hshannover.dqgui.core.wizard.CreateComponentWizard;
import de.hshannover.dqgui.core.wizard.RenameComponentWizard;
import de.hshannover.dqgui.core.wizard.SaveComponentAsWizard;
import de.hshannover.dqgui.core.wizard.SaveComponentWizard;
import de.hshannover.dqgui.dbsupport.api.DatabaseEnvironment;
import de.hshannover.dqgui.dbsupport.api.DatabaseEnvironments;
import de.hshannover.dqgui.dbsupport.gui.wizard.DatabaseCreationWizard;
import de.hshannover.dqgui.dbsupport.gui.wizard.DatabaseUpdateWizard;
import de.hshannover.dqgui.execution.DSLService.RepositoryStatus;
import de.hshannover.dqgui.execution.DSLServiceException;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;
import de.hshannover.dqgui.execution.database.api.DatabaseTests.DatabaseTestResult;
import de.hshannover.dqgui.execution.database.gui.IconSupport;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import de.hshannover.dqgui.execution.model.RepoType;
import de.hshannover.dqgui.execution.model.remote.RemoteError;
import de.hshannover.dqgui.execution.model.remote.RemoteStatusReport;
import de.hshannover.dqgui.framework.AbstractWindowController;
import de.hshannover.dqgui.framework.ErrorUtility;
import de.hshannover.dqgui.framework.control.SpecificTreeItemCell;
import de.hshannover.dqgui.framework.dialogs.AbstractDialog.Operation;
import de.hshannover.dqgui.framework.dialogs.DialogContext.DialogStyle;
import de.hshannover.dqgui.framework.model.ObjectWrapper;
import de.hshannover.dqgui.framework.serialization.Serialization;
import de.hshannover.dqgui.framework.signal.SignalHandler;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import javafx.util.Pair;

final class MainController extends AbstractWindowController {

    /*
     * CONSTANTS
     */
    private static final int DATABASE_TREE_IMAGE_DIMENSION = 32;

    /*
     * All these are injected by ApplicationContext
     */
    private final ApplicationProperties properties = null;
    private final TaskHandler taskHandler = null;
    private final ApplicationState applicationState = null;
    private final ProjectHandle projectHandle = null;
    private final RemoteConnection remoteConnection = null;

    /*
     * Relations
     */
    private final EnumMap<DSLComponentType, TableView<DSLComponent>> tableMap = new EnumMap<>(DSLComponentType.class);

    /*
     * GUI Components
     */
    private final ContextMenu treeMenuEnvironment = new ContextMenu(), treeMenuConnection = new ContextMenu();
    private final TreeItem<String> rootItem = new TreeItem<>("Database Environments");
    private final Tab repoErrorTab = new Tab("Invalid Repository or Project");
    private TaskUiUpdateService taskUpdateService;

    /*
     * Component Collections
     */
    private DSLComponentCollection components;
    
    /*
     * Callbacks
     */
    private Consumer<EditorLink> linkHandler;
    private BiConsumer<DSLComponent, Pair<SearchHit, Integer>> openEditor;
    private final SignalHandler databaseEnvironmentChange = () -> updateDatabaseEnvironment();
    
    /*
     * FXML Injects
     */
    @FXML BorderPane leftSidePane;
    @FXML SplitPane splitpane;
    @FXML Tab welcome;
    @FXML TabPane editorTabs, fileDatabaseManagementTab;
    @FXML MenuItem undoButton, redoButton, close, closeAll, saveAs, save, saveAll, repositoryReload,
                   globalSearch, repositoryImport, repositoryExport, exploreReports, databaseWizard,
                   manageProjects, databaseEnvironmentComparison;
    @FXML ComboBox<String> databaseEnvironment;
    @FXML ComboBox<DSLComponent> actionBox;
    @FXML ComboBox<ExecutionStrategy> executionEnvironment;
    @FXML CheckBox optimize, autosuggest, autoindent;
    @FXML TreeView<String> treeView, reportView;
    @FXML TextField searchField;
    @FXML TableView<DSLComponent> sourcesTable, actionsTable, checksTable;
    @FXML HBox navigation;
    @FXML VBox reportsVbox;
    @FXML Button generateReportButton, searchResetButton, repositoryReloadButton;
    @FXML Menu newComponentMenu;
    @FXML SeparatorMenuItem importExportSeperator;

    /*
     *        _______  __ __  _____       _____ __    ____  ___________
     *       / ____/ |/ //  |/  / /      / ___// /   / __ \/_  __/ ___/
     *      / /_   |   // /|_/ / /       \__ \/ /   / / / / / /  \__ \
     *     / __/  /   |/ /  / / /___    ___/ / /___/ /_/ / / /  ___/ /
     *    /_/    /_/|_/_/  /_/_____/   /____/_____/\____/ /_/  /____/
     *
     */

    @FXML
    public void initialize() {
        projectHandle.addEnvironmentSignalHandler(databaseEnvironmentChange);
        
        taskUpdateService = new TaskUiUpdateService(reportView, getContext(), projectHandle);
        projectHandle.setUiUpdateService(taskUpdateService);
        projectHandle.setNotificationService(new NotificationService(getContext(), getDialogContext(), Pos.TOP_RIGHT, 3000));
        
        components = new DSLComponentCollection();
        StaticAnalysis.registerComponentLookup(components);
        StaticAnalysis.registerDatabaseEnvironments(projectHandle.getCurrentEnvironment());

    //Split Pane Resize Behavior
        SplitPane.setResizableWithParent(leftSidePane, Boolean.FALSE);
        SplitPane.setResizableWithParent(reportsVbox, Boolean.FALSE);
        
    //Bindings
        BooleanBinding invalidProject = projectHandle.validProjectBinding().not();
        
        newComponentMenu.disableProperty().bind(invalidProject);
        generateReportButton.disableProperty().bind(invalidProject.or(components.isEmpty(DSLComponentType.ACTION)));
        searchField.disableProperty().bind(invalidProject);
        searchResetButton.disableProperty().bind(invalidProject);
        globalSearch.disableProperty().bind(invalidProject);
        exploreReports.disableProperty().bind(invalidProject);
        databaseWizard.disableProperty().bind(invalidProject);
        repositoryReload.disableProperty().bind(invalidProject);
        repositoryReloadButton.disableProperty().bind(invalidProject);
        databaseEnvironment.disableProperty().bind(invalidProject);
        executionEnvironment.disableProperty().bind(invalidProject);
        databaseEnvironmentComparison.disableProperty().bind(invalidProject);
        
        BooleanBinding manageProject = properties.repoTypeProperty().isEqualTo(RepoType.FILE_SYSTEM).or(projectHandle.validDatabaseRepositoryProperty());
        manageProjects.disableProperty().bind(manageProject.not());
        
        BooleanBinding editorEmpty = Bindings.size(editorTabs.getTabs()).isEqualTo(0);
        save.disableProperty().bind(editorEmpty);
        saveAll.disableProperty().bind(editorEmpty);
        close.disableProperty().bind(editorEmpty);
        closeAll.disableProperty().bind(editorEmpty);
        saveAs.disableProperty().bind(editorEmpty);
        
        ListChangeListener<Tab> l = changed -> {
            if(changed.getList().isEmpty()) {
                undoButton.disableProperty().bind(editorEmpty);
                redoButton.disableProperty().bind(editorEmpty);
            }
        };
        
        editorTabs.getTabs().addListener(l);
        editorTabs.getSelectionModel()
            .selectedItemProperty()
            .addListener((obs, oldV, newV) -> {
                if(newV instanceof CodeEditor) {
                    undoButton.disableProperty().bind(BooleanBinding.booleanExpression((((CodeEditor) newV).undoAvailable())).not());
                    redoButton.disableProperty().bind(BooleanBinding.booleanExpression((((CodeEditor) newV).redoAvailable())).not());
                    ((CodeEditor) newV).requestFocus();
                    return;
                }
                redoButton.disableProperty().unbind(); redoButton.setDisable(true);
                undoButton.disableProperty().unbind(); undoButton.setDisable(true);
            });
        
        importExportSeperator.disableProperty().bind(invalidProject);
        repositoryImport.disableProperty().bind(invalidProject);
        repositoryExport.disableProperty().bind(invalidProject);
        
    //Callbacks
        Function<DSLComponent, Optional<CodeEditor>> shared = component -> {
            Optional<CodeEditor> ed = getLoadedTab(component);
            if(!ed.isPresent()) {
                return loadComponentAsTab(component);
            }
            editorTabs.getSelectionModel().select(ed.get());
            return ed;
        };
        
        openEditor = (component, fromSearch) -> {
            if(!components.exists(component)) {
                getDialogContext().error(component.getType() + " Component '" + component.getIdentifier() + "' no longer exists.");
                return;
            }
            shared.apply(component).ifPresent(t -> {
                if(!t.markHit(fromSearch)) {
                    getDialogContext().warning("Content has changed. Result position is invalid.");
                } else {
                    toFront();
                }});            
        };
        
        linkHandler = link -> {
            if(link instanceof ComponentLink) {
                ComponentLink cl = (ComponentLink) link;
                if(components.exists(cl.c)) {
                    shared.apply(cl.c);
                } else {
                    queryUserForNewComponent(cl.c);
                }
            }
        };
        
        projectHandle.projectFailedToLoadSignal().register(() -> {
            editorTabs.getTabs().add(repoErrorTab);
            editorTabs.getSelectionModel().select(repoErrorTab);
            getDialogContext().warning("Invalid project / repository", "DQGUI has no valid repository / project loaded.");
        });
        
        projectHandle.projectUnloadedSignal().register(() -> {
            editorTabs().collect(Collectors.toList()).forEach(t -> editorTabs.getTabs().remove(t));
            actionBox.setItems(FXCollections.observableArrayList());
            databaseEnvironment.getItems().clear();
            taskUpdateService.reset();
            treeView.getRoot().getChildren().clear();
            components.unloadAll();
        });
        
        projectHandle.projectLoadedSignal().register(() -> {
            editorTabs.getTabs().remove(repoErrorTab);
            taskUpdateService.reset();
            internalReloadRepository();
            updateDatabaseEnvironment();
            databaseEnvironment.getSelectionModel().selectFirst();
            if(properties.isShowWelcome())
                editorTabs.getTabs().add(welcome);
        });
        
    //Welcome View
        if(properties.isShowWelcome()) {
            WebView web = new WebView();
            WebEngine engine = web.getEngine();
            ((BorderPane) welcome.getContent()).setCenter(web);
            engine.loadContent(HtmlLoader.loadInternal(Config.APPLICATION_PATH_LANDING_PAGE, 
                    "VERSION", Config.APPLICATION_VERSION));
        } else {
            editorTabs.getTabs().clear();
        }
        
    //Hint Tab
        WebView web = new WebView();
        WebEngine engine = web.getEngine();
        engine.loadContent(HtmlLoader.loadInternal(Config.APPLICATION_PATH_ERROR_PAGE));
        repoErrorTab.setContent(web);
        
        if(!projectHandle.isValidProject()) {
            editorTabs.getTabs().add(repoErrorTab);
            editorTabs.getSelectionModel().select(repoErrorTab);
        }
        
    //Table init
        sourcesTable.setPlaceholder(new Label("No Sources found in repo."));
        actionsTable.setPlaceholder(new Label("No Actions found in repo."));
        checksTable.setPlaceholder(new Label("No Checks found in repo."));
        tableMap.put(DSLComponentType.SOURCE, sourcesTable);
        tableMap.put(DSLComponentType.ACTION, actionsTable);
        tableMap.put(DSLComponentType.CHECK, checksTable);
        tableMap.values().forEach(this::prepareTable);

        if(projectHandle.isValidProject()) {
            try {
                List<DSLComponent> discovered = projectHandle.getServiceProvider().getService().discover();
                ExtraData.firstLoadProcessing(projectHandle.getServiceProvider().getService(), discovered);
                components.addAll(discovered);
            } catch (DSLServiceException e) {
                throw ErrorUtility.rethrow(e);
            }
        }

    //Replace default ComboBox with search supporting ComboBox
        SearchableComboBox<DSLComponent> comboSearch = new SearchableComboBox<>(components.of(DSLComponentType.ACTION));
        comboSearch.setMaxHeight(actionBox.getHeight());
        comboSearch.setPrefWidth(actionBox.getPrefWidth());
        comboSearch.setPadding(actionBox.getPadding());
        navigation.getChildren().set(navigation.getChildren().indexOf(actionBox), comboSearch);
        actionBox = comboSearch;
        actionBox.getSelectionModel().select(0);
        actionBox.disableProperty().bind(invalidProject);
        
        // Hack since controlsfx searchablecombobox throws index not found exceptions when updating and removing
        ListChangeListener<DSLComponent> searchableComboBoxListBugMitigation = (changed) -> {
            while(changed.next()) {
                Platform.runLater(() -> {
                    DSLComponent selected = actionBox.getSelectionModel().getSelectedItem();
                    ObservableList<DSLComponent> ls = FXCollections.observableArrayList();
                    ls.addAll(components.of(DSLComponentType.ACTION));
                    ls.sort(DSLComponentComparator.getInstance());
                    actionBox.setItems(ls);
                    if(actionBox.getItems().contains(selected)) {
                        actionBox.getSelectionModel().select(selected);
                    } else {
                        actionBox.getSelectionModel().selectFirst();
                    }
                });
            }
        };
        
        components.addListener(searchableComboBoxListBugMitigation, DSLComponentType.ACTION);
        generateReportButton.disableProperty().bind(actionBox.getSelectionModel().selectedIndexProperty().isEqualTo(-1));

    //Remote execution
        executionEnvironment.setItems(FXCollections.observableArrayList(Arrays.asList(ExecutionStrategy.values())));
        executionEnvironment.getSelectionModel().select(0);
        
    //Database Environment and Connections
        if(projectHandle.isValidProject()) {
            databaseEnvironment.setItems(FXCollections.observableArrayList(projectHandle.getCurrentEnvironment().unsafeGet().getEnvironmentIdentifiers()));
            databaseEnvironment.getSelectionModel().select(0);
            rebuildTreeMenu();
        }
        
        treeView.setRoot(rootItem);
        rootItem.setExpanded(true);

        treeView.setOnMouseClicked(e -> {
            if(e.getClickCount() == 2) {
                TreeItem<String> selected = treeView.getSelectionModel().getSelectedItem();
                if(selected instanceof DatabaseTreeItem) {
                    if(((DatabaseTreeItem) selected).getCon() != null)
                        new DatabaseUpdateWizard(((DatabaseTreeItem) selected).getEnv(), ((DatabaseTreeItem) selected).getCon()).begin();
                    if(((DatabaseTreeItem) selected).getCon() == null && ((DatabaseTreeItem) selected).getEnv().size() == 0)
                        new DatabaseCreationWizard(projectHandle.getCurrentEnvironment().unsafeGet(), ((DatabaseTreeItem) selected).getEnv()).begin();
                }
            }
        });

    //Database Environment Context Menu
        MenuItem createConnectionIn = new MenuItem("Create Connection in");
        createConnectionIn.setOnAction(e -> new DatabaseCreationWizard(projectHandle.getCurrentEnvironment().unsafeGet(), ((DatabaseTreeItem) treeView.getSelectionModel().getSelectedItem()).getEnv()).begin());

        MenuItem setAsEnvironment = new MenuItem("Select");
        setAsEnvironment.setOnAction(e -> databaseEnvironment.getSelectionModel().select(treeView.getSelectionModel().getSelectedItem().getValue()));

        MenuItem editEnvironment = new MenuItem("Rename");
        editEnvironment.setOnAction(e -> new DatabaseCreationWizard(projectHandle.getCurrentEnvironment().unsafeGet()).begin());

        MenuItem deleteEnvironment = new MenuItem("Remove");
        deleteEnvironment.setOnAction(e -> {
            DatabaseTreeItem t = (DatabaseTreeItem) treeView.getSelectionModel().getSelectedItem();
            if(t.getEnv().getIdentifier().equals(DatabaseEnvironments.DEFAULT_ENVIRONMENT)) {
                getDialogContext().error("Removal of default environment", "You are not allowed to remove the default environment.");
                return;
            }
            boolean confirm = getDialogContext().confirmCancelDialog(DialogStyle.CONFIRMATION, "Removal of environment: " + t.getEnv().getIdentifier() , String.format("Are you sure to remove and delete the environment %s?", t.getEnv().getIdentifier()));
            if(confirm)
                projectHandle.getCurrentEnvironment().unsafeGet().remove(t.getEnv());
        });

        MenuItem resetEnvironment = new MenuItem("Clear");
        resetEnvironment.setOnAction(e -> {
            DatabaseTreeItem t = (DatabaseTreeItem) treeView.getSelectionModel().getSelectedItem();
            if(t.getEnv().size() == 0)
                return;
            boolean confirm = getDialogContext().confirmCancelDialog(DialogStyle.CONFIRMATION, "Clearing of environment: " + t.getEnv().getIdentifier() , String.format("Are you sure to clear the environment %s?%nThis operation is irreversible and will destroy all of the contained connections.", t.getEnv().getIdentifier()));
            if(confirm)
                t.getEnv().clear();
        });

        MenuItem editConnection = new MenuItem("Edit");
        editConnection.setOnAction(e ->  {
            DatabaseTreeItem t = (DatabaseTreeItem) treeView.getSelectionModel().getSelectedItem();
            new DatabaseUpdateWizard(t.getEnv(), t.getCon()).begin();
        });

        MenuItem deleteConnection = new MenuItem("Remove");
        deleteConnection.setOnAction(e -> {
            DatabaseTreeItem t = (DatabaseTreeItem) treeView.getSelectionModel().getSelectedItem();
            boolean confirm = getDialogContext().confirmCancelDialog(DialogStyle.CONFIRMATION, "Removal of connection: " + t.getCon().getIdentifier() , String.format("Are you sure to remove and delete the %s connection %s?", t.getCon().getEngine().name(), t.getCon().getIdentifier()));
            if(confirm)
                t.getEnv().remove(t.getCon());
        });
        
        MenuItem testConnection = new MenuItem("Test");
        testConnection.setOnAction(e -> {
            DatabaseTreeItem t = (DatabaseTreeItem) treeView.getSelectionModel().getSelectedItem();
            DatabaseTestResult r = t.getCon().getEngine().test(t.getCon());
            if(r.isSuccess()) {
                getDialogContext().information("Test succeeded!", r.getMessage());
            } else {
                getDialogContext().textErrorDialog("Test failed", r.getExceptionName(), r.getMessage());
            }
        });

        MenuItem createNewEnvironment = new MenuItem("Create Environment");
        createNewEnvironment.setOnAction(e -> projectHandle.getCurrentEnvironment().safeGet().ifPresent(env -> new DatabaseCreationWizard(env).begin()));

        treeMenuEnvironment.getItems().addAll(setAsEnvironment, createConnectionIn, editEnvironment, deleteEnvironment, resetEnvironment);
        treeMenuConnection.getItems().addAll(testConnection, editConnection, deleteConnection);
        treeView.setContextMenu(new ContextMenu(createNewEnvironment));
        treeView.setCellFactory(SpecificTreeItemCell.createFactory());

    //Search
        searchField.textProperty().addListener(e -> search(searchField.getText()));
        
    //Listeners
        autosuggest.selectedProperty().addListener((obs, oldV, newV) -> {
            properties.setAutoSuggest(newV);
            editorTabs().forEach(t -> t.setAutoSuggest(newV));
        });
        
        autoindent.selectedProperty().addListener((obs, oldV, newV) -> {
            properties.setAutoIndent(newV);
            editorTabs().forEach(t -> t.setAutoIndent(newV));
        });

        autosuggest.setSelected(properties.isAutoSuggest());
        
        editorTabs.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if(!projectHandle.isValidProject()) {
                setTitle("No valid project loaded - DQGUI");
                return;
            }
            setTitle(newV != null ? 
                    projectHandle.getSelectedProject().unsafeGet().getName() + " - " + newV.getText() + " - DQGUI" : 
                        projectHandle.getSelectedProject().unsafeGet().getName() + " - DQGUI");
        });
        
        setTitle("No valid project loaded - DQGUI");
        if(projectHandle.isValidProject()) {
            setTitle(projectHandle.getSelectedProject().unsafeGet().getName() + " - DQGUI");
            if(applicationState.getExecutionEnvironment() != null)
                executionEnvironment.getSelectionModel().select(applicationState.getExecutionEnvironment());
            if(applicationState.getEnvironmentComboBoxValue() != null && projectHandle.getCurrentEnvironment().unsafeGet().identifierExists(applicationState.getEnvironmentComboBoxValue()))
                databaseEnvironment.setValue(applicationState.getEnvironmentComboBoxValue());
            if(applicationState.getLastSelectedActionComboBox() != null && components.exists(applicationState.getLastSelectedActionComboBox()))
                actionBox.getSelectionModel().select(applicationState.getLastSelectedActionComboBox());
            int loaded = 0;
            if(applicationState.getOpenedTabs() != null && !applicationState.getOpenedTabs().isEmpty()) {
                for(DSLComponent c : applicationState.getOpenedTabs()) {
                    if(!projectHandle.getServiceProvider().getService().exists(c))
                        continue;
                    loadComponentAsTab(c);
                    loaded++;
                }
            }
            
            // Bugged Tab Mitigation
                if(applicationState.getOpenedTabs() != null && loaded > 0) {
                    registerAfterRenderHook(() -> {
                        CodeEditor e = editorTabs().findFirst().get();
            
                        int index = editorTabs.getTabs().indexOf(e);
                        loadComponentAsTab(e.getComponent());
                        editorTabs.getTabs().remove(index);
                        if(applicationState.getOpenedTabs().size() > 1) {
                            Tab clone = editorTabs.getTabs().remove(editorTabs.getTabs().size()-1);
                            editorTabs.getTabs().add(index, clone);
                        }
                        editorTabs.getSelectionModel().select(applicationState.getTabIndex());
                    });
                }
        }
    }

    @FXML
    void newSource() {
        queryUserForNewComponent(DSLComponentType.SOURCE);
    }

    @FXML
    void newAction() {
        queryUserForNewComponent(DSLComponentType.ACTION);
    }

    @FXML
    void newCheck() {
        queryUserForNewComponent(DSLComponentType.CHECK);
    }

    @FXML
    void closeTab() {
        closeTab(editorTabs.getSelectionModel().getSelectedItem());
    }

    @FXML
    void closeAllTabs() {
        List<Tab> copy = new ArrayList<>(editorTabs.getTabs());
        copy.forEach(this::closeTab);
    }

    @FXML
    void saveSelected() {
        Tab tab = editorTabs.getSelectionModel().getSelectedItem();
        if(!(tab instanceof CodeEditor))
            return;
        CodeEditor editor = castToCodeEditor(tab);
        updateComponent(editor);
    }

    @FXML
    void saveSelectedAs() {
        Tab tab = editorTabs.getSelectionModel().getSelectedItem();
        if(!(tab instanceof CodeEditor))
            return;
        CodeEditor editor = castToCodeEditor(tab);
        Optional<DSLComponent> result = new SaveComponentAsWizard(projectHandle.getServiceProvider().getService(), editor.getComponent(), editor.getEditorContent()).begin();
        if(result.isPresent()) {
            editorTabs.getTabs().remove(editor);
            editor.cleanup();
            createComponent(result.get());
        }
    }

    @FXML
    void saveAll() {
        editorTabs().filter(CodeEditor::isChanged).forEach(this::updateComponent);
    }

    @FXML
    public void exit() {
        getContext().completeShutdown();
    }

    @FXML
    void undo() {
        Tab tab = editorTabs.getSelectionModel().getSelectedItem();
        if(tab instanceof CodeEditor)
            castToCodeEditor(tab).undo();
    }

    @FXML
    void redo() {
        Tab tab = editorTabs.getSelectionModel().getSelectedItem();
        if(tab instanceof CodeEditor)
            castToCodeEditor(tab).redo();
    }
    
    @FXML
    void globalSearch() {
        getContext().load(Views.REPOSITORY_SEARCH, "Repository Search", new ObjectWrapper(openEditor));
    }

    @FXML
    void exploreReports() {
        getContext().load(Views.REPORTS, "Reports");
    }

    @FXML
    void rserveInstanceWindow() {
        getContext().load(Views.RSERVE, "RServe Instance");
    }

    @FXML
    void databaseWizardWindow() {
        new DatabaseCreationWizard(projectHandle.getCurrentEnvironment().unsafeGet()).begin();
    }
    
    @FXML
    void databaseEnvironmentComparison() {
        getContext().loadBlocking(Views.DB_ENV_COMPARE, "Database Environment Comparison", this);
    }
    
    @FXML
    void dqguiRemote() {
        getContext().load(Views.REMOTE, "Remote Execution Configuration");
    }

    @FXML
    void propertiesWindow() {
        getContext().loadBlocking(Views.PROPERTIES, Config.APPLICATION_NAME + " Properties", this);
    }

    @FXML
    void invokeIQM4HD() {
        if(executionEnvironment.getSelectionModel().getSelectedItem() == ExecutionStrategy.LOCAL) {
            taskHandler.runTask(actionBox.getValue().getIdentifier(), databaseEnvironment.getValue(), optimize.isSelected());
        } else {
            remoteConnection.submitJob(RemoteExecution.createJob(
                    actionBox.getValue().getIdentifier(), 
                    optimize.isSelected(), 
                    projectHandle.getServiceProvider().getService().createRuleService(), 
                    projectHandle.getCurrentEnvironment().unsafeGet().getEnvironment(databaseEnvironment.getValue()), 
                    projectHandle.getSelectedProject().unsafeGet()),
                    properties.getRemoteHost(),
                    properties.getRemoteKey())
            .thenAccept(e -> {
                if(e instanceof RemoteError) {
                    RemoteError r = (RemoteError) e;
                    Logger.info("Remote job rejected: {} => {}", r.getExceptionName(), r.getMessage());
                    Platform.runLater(() -> getDialogContext().textErrorDialog("Remote job rejected", r.getExceptionName(), r.getMessage()));
                    return;
                }
                if(e instanceof RemoteStatusReport) {
                    RemoteStatusReport r = (RemoteStatusReport) e;
                    Logger.info("Remote job sent: {} @ {} => {} | {}", r.getAction(), r.getEnvironment(), r.getJobId(), r.getState());
                    remoteConnection.getJobsChangedSignal().fire();
                    Platform.runLater(() -> 
                        NotificationTools.success("Remote job transmitted", "Remote execution server recived job.", "Job is being processed. State: " + r.getState())
                            .onAction(ev -> dqguiRemote())
                            .position(Pos.TOP_RIGHT)
                            .hideAfter(Duration.millis(3000))
                            .show());
                    return;
                }
                throw new AssertionError();
            })
            .exceptionally(e -> {
                Logger.error(e);
                Platform.runLater(() -> getDialogContext().exceptionDialog(e, "Remote execution error", ExceptionRecoveryTips.REMOTE_ERROR.getTip()));
                return null;
            });
        }
    }

    @FXML
    void manageProjects() {
        getContext().loadBlocking(Views.PROJECTS, "Manage Projects", this);
    }
    
    @FXML
    void reloadRepository() {
        if(!displayRepositoryReloadWarning("Reload repository", "Reloading the repository will close all currently opened components without saving. Proceed?"))
            return;
        internalReloadRepository();
        getDialogContext().information("Reload successful", "Repository has been reloaded.");
    }
    
    @FXML
    void importRepository() {
        if(!displayRepositoryReloadWarning("You might lose your work in progress!", "Importing new IQM4HD components will close all currently opened components without saving. Proceed?"))
            return;
        getContext().loadBlocking(Views.REPO_IMPORT, "Repository Component Import Assistant", this, new Runnable() {
            
            @Override
            public void run() {
                internalReloadRepository();
            }});
    }
    
    @FXML
    void exportRepository() {
        getContext().loadBlocking(Views.REPO_EXPORT, "Repository Component Export Assistant", this);
    }
    
    @FXML
    void resetSearchResults() {
        searchField.clear();
        resetSearch();
    }

    @FXML
    void helpWindow() {
        getContext().load(Views.HELP, "DQGUI Documentation and Help");
    }

    @FXML
    void aboutWindow() {
        getContext().loadBlocking(Views.ABOUT, "About " + Config.APPLICATION_NAME, this);
    }
    
    @FXML
    void licensingWindow() {
        getContext().loadBlocking(Views.LICENSES, "Licensing", this);
    }

    @FXML
    void disableWelcome() {
        properties.setShowWelcome(false);
    }

    /*
     *        __  __________    ____  __________  _____
     *       / / / / ____/ /   / __ \/ ____/ __ \/ ___/
     *      / /_/ / __/ / /   / /_/ / __/ / /_/ /\__ \
     *     / __  / /___/ /___/ ____/ /___/ _, _/___/ /
     *    /_/ /_/_____/_____/_/   /_____/_/ |_|/____/
     *
     */
    
    private void updateComponent(CodeEditor ed) {
        try {
            if(ExtraData.decideConstOrQuery(ed.getEditorContent(), ed.getComponent()))
                sourcesTable.refresh();
            projectHandle.getServiceProvider().getService().update(ed.getComponent(), ed.getEditorContent());
            ed.markAsSaved();
        } catch (DSLServiceException e) {
            DSLComponent err = ed.getComponent();
            getDialogContext().exceptionDialog(e, "Failed to update component: " + err.getIdentifier(), String.format("Updating of %s component %s failed due to %s.", err.getType(), err.getIdentifier(), e.getMessage()));
            Logger.error(e);
        }
    }

    private void queryUserForNewComponent(DSLComponent comp) {
        new CreateComponentWizard(comp.getType(), projectHandle.getServiceProvider().getService(), comp.getIdentifier()).begin().ifPresent(this::createComponent);
    }
    
    private void queryUserForNewComponent(DSLComponentType type) {
        new CreateComponentWizard(type, projectHandle.getServiceProvider().getService()).begin().ifPresent(this::createComponent);
    }

    private void createComponent(DSLComponent component) {
        components.add(component);
        loadComponentAsTab(component);
    }

    private boolean closeTab(Tab tab) {
        if(tab == null)
            return true;
        if(tab instanceof CodeEditor) {
            return closeEditor(castToCodeEditor(tab));
        }
        editorTabs.getTabs().remove(tab);
        return true;
    }

    private boolean closeEditor(CodeEditor editor) {
        if(editor.isChanged() && savingAborted(editor))
            return false;
        editor.cleanup();
        editorTabs.getTabs().remove(editor);
        return true;
    }

    private boolean savingAborted(CodeEditor editor) {
        Optional<Boolean> saved = new SaveComponentWizard(projectHandle.getServiceProvider().getService(), editor.getComponent(), editor.getEditorContent()).begin();
        return !saved.get();
    }

    private void cleanup(Tab tab) {
        if(tab instanceof CodeEditor)
            castToCodeEditor(tab).cleanup();
    }

    private Optional<CodeEditor> loadComponentAsTab(DSLComponent component) {
        try {
            CodeEditor newEditor = new CodeEditor(linkHandler, component, projectHandle.getServiceProvider().getService().read(component), properties.isAutoSuggest());
            editorTabs.getTabs().add(newEditor);
            editorTabs.getSelectionModel().select(newEditor);
            newEditor.setOnCloseRequest(e -> {
                if(!closeTab(newEditor))
                    e.consume();
            });
            return Optional.ofNullable(newEditor);
        } catch (DSLServiceException e) {
            getDialogContext().exceptionDialog(e, "Failed to read component: " + component.getIdentifier(), String.format("Reading of %s component %s failed due to %s.", component.getType(), component.getIdentifier(), e.getMessage()));
            Logger.error(e);
        }
        return Optional.empty();
    }

    private void loadComponentsFromTable(TableView<DSLComponent> table) {
        List<DSLComponent> selectedComponents = table.getSelectionModel().getSelectedItems();
        if(selectedComponents == null || selectedComponents.isEmpty())
            return;
        Map<DSLComponent, CodeEditor> editors = editorTabs.getTabs()
                .stream()
                .filter(this::isCodeEditor)
                .map(this::castToCodeEditor)
                .collect(Collectors.toMap(CodeEditor::getComponent, Function.identity()));
        
        for(DSLComponent c : selectedComponents) {
            CodeEditor cd = editors.get(c);
            if(cd == null) {
                loadComponentAsTab(c);
            } else {
                editorTabs.getSelectionModel().select(cd);
            }
        }
    }

    private Optional<CodeEditor> getLoadedTab(DSLComponent c) {
        return editorTabs().filter(e -> e.getComponent().equals(c))
                .findFirst();
    }
    
    private boolean isCodeEditor(Tab tab) {
        return tab instanceof CodeEditor;
    }

    private CodeEditor castToCodeEditor(Tab tab) {
        return (CodeEditor) tab;
    }
    
    private boolean displayRepositoryReloadWarning(String dialogHeader, String dialogTitle) {
        if(!projectHandle.getServiceProvider().validRepositoryProperty().get()) {
            RepositoryStatus s = projectHandle.getServiceProvider().verify();
            if(!s.isValid())
                getDialogContext().textErrorDialog("Repository verification failed", "Repository verification failed!", s.message());
            return false;
        }
        
        if(editorTabs().filter(CodeEditor::isChanged).count() > 0) {
            boolean cont = getDialogContext()
                    .confirmCancelDialog(DialogStyle.WARNING, dialogHeader, dialogTitle);
            if(!cont) return false;
        }
        
        return true;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void prepareTable(TableView<DSLComponent> table) {
        DSLComponentType type = null;
        for(Map.Entry<DSLComponentType, TableView<DSLComponent>> entry : tableMap.entrySet()) {
            if(entry.getValue().equals(table)) {
                table.setItems(components.of(entry.getKey()));
                type = entry.getKey();
                break;
            }
        }
        
        final DSLComponentType tFinal = type;
        
        // Context Menu

        Runnable removeTask = () -> {
            List<DSLComponent> selected = table.getSelectionModel().getSelectedItems();
            if(selected == null || selected.isEmpty())
                return;
            boolean delete = getDialogContext().customDialogNoTitle(Dialogs.MULTI_REMOVE,
                    String.format("Remove %s item%s?", selected.size(), selected.size() == 1 ? "" : "s"),
                    "Are you sure to remove and delete these items?",
                    new ObjectWrapper(selected)
                    ).getKey() == Operation.CONFIRM;
            
            if(!delete)
                return;

            List<CodeEditor> editors = editorTabs()
                    .filter(e -> selected.contains(e.getComponent()))
                    .collect(Collectors.toList());

            editors.forEach(CodeEditor::cleanup);
            editorTabs.getTabs().removeAll(editors);

            for(DSLComponent c : selected) {
                try {
                    projectHandle.getServiceProvider().getService().delete(c);
                } catch (DSLServiceException e) {
                    getDialogContext().exceptionDialog(e, 
                            "Failed to remove component: " + c.getIdentifier(), 
                            String.format("Removal of %s component %s failed due to %s.", 
                                    c.getType(), 
                                    c.getIdentifier(), 
                                    e.getMessage()
                                    )
                            );
                    Logger.error(e);
                    return;
                } 
            }

            for(DSLComponent c : selected) {
                if(actionBox.getSelectionModel().getSelectedItem() == c)
                    actionBox.getSelectionModel().clearSelection();
            }
            
            components.removeAll(selected);

            if(actionBox.getSelectionModel().getSelectedItem() == null && !actionBox.getItems().isEmpty())
                actionBox.getSelectionModel().select(0);
        };
        
        table.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.SPACE)
                loadComponentsFromTable(table);
            if(e.getCode() == KeyCode.DELETE)
                removeTask.run();
        });

        table.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2)
                loadComponentsFromTable(table);
        });


        MenuItem newComponent = new MenuItem("New");
        newComponent.setOnAction(e -> {
            if(projectHandle.isValidProject())
                queryUserForNewComponent(tFinal);
        });
        ContextMenu contextMenuTable = new ContextMenu(newComponent);

        MenuItem openComponent = new MenuItem("Open");
        openComponent.setOnAction(e -> loadComponentsFromTable(table));
        
        MenuItem renameComponent = new MenuItem("Rename");
        renameComponent.setOnAction(e -> {
            Optional<DSLComponent> comp = getComponentFromTable(table);
            comp.ifPresent(c -> {
                Optional<CodeEditor> tab = editorTabs()
                        .filter(ed -> ed.getComponent().equals(c))
                        .findFirst();
                Optional<DSLComponent> renamed = new RenameComponentWizard(comp.get(), projectHandle.getServiceProvider().getService()).begin();
                renamed.ifPresent(r -> {
                    components.remove(c);
                    components.add(r);
                    tab.ifPresent(t -> t.hotSwap(renamed));
                });
            });
        });
        
        MenuItem removeComponent = new MenuItem("Remove");
        removeComponent.setOnAction(event -> removeTask.run());
        ContextMenu contextMenuItem = new ContextMenu(openComponent, renameComponent, removeComponent);
        
        IntegerBinding sizeBinding = Bindings.size(table.getItems());
        table.getColumns().get(0).textProperty().bind(sizeBinding.asString("%d Entries"));
        table.getColumns().get(0).setCellValueFactory(p -> new SimpleObjectProperty(p.getValue()));

        table.getColumns().get(0).setContextMenu(contextMenuTable);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        if(type == DSLComponentType.ACTION) {
            MenuItem setForExecution = new MenuItem("Select as Action");
            setForExecution.setOnAction(e -> {
                DSLComponent c = table.getSelectionModel().getSelectedItem();
                if(c == null)
                    return;
                actionBox.getSelectionModel().select(c);
            });
            contextMenuItem.getItems().add(setForExecution);
        }
        
        if(type == DSLComponentType.CHECK) {
            MenuItem demote = new MenuItem("Demote to local check");
            demote.setOnAction(e -> {
                List<DSLComponent> l = table.getSelectionModel().getSelectedItems().stream()
                        .filter(DSLComponent::isGlobal)
                        .collect(Collectors.toList());
                if(l.isEmpty()) return;
                if(!promoteDemotePreCheck(l, "demotion")) return;
                for(DSLComponent c : l) {
                    DSLComponent demoted = projectHandle.demoteToLocal(c, properties);
                    components.remove(c);
                    components.add(demoted);
                }
                getDialogContext().information("Demotion completed", "Selected components have been demoted to local check.");
            });
            
            MenuItem promote = new MenuItem("Promote to global check");
            promote.setOnAction(e -> {
                List<DSLComponent> l = table.getSelectionModel().getSelectedItems().stream()
                        .filter(f -> !f.isGlobal())
                        .collect(Collectors.toList());
                if(l.isEmpty()) return;
                if(!promoteDemotePreCheck(l, "promotion")) return;
                for(DSLComponent c : l) {
                    DSLComponent promoted = projectHandle.promoteToGlobal(c, properties);
                    components.remove(c);
                    components.add(promoted);
                }
                getDialogContext().information("Promotion completed", "Selected components have been promoted to global check.");
            });
            
            ContextMenu withPromoteToGlobal = new ContextMenu(openComponent, renameComponent, promote, removeComponent);
         // Hack to deal with JavaFXs Node can only one parent constraint
            ContextMenu withDemoteToGlobal = new ContextMenu(new MenuItem(openComponent.getText()), new MenuItem(renameComponent.getText()), demote, new MenuItem(removeComponent.getText()));
            withDemoteToGlobal.getItems().get(0).setOnAction(openComponent.getOnAction());
            withDemoteToGlobal.getItems().get(1).setOnAction(renameComponent.getOnAction());
            withDemoteToGlobal.getItems().get(3).setOnAction(removeComponent.getOnAction());
            table.getColumns().get(0).setCellFactory(c -> new ComponentAwareCell(contextMenuItem, withPromoteToGlobal, withDemoteToGlobal));
        } else {
            table.getColumns().get(0).setCellFactory(c -> new ComponentAwareCell(contextMenuItem, null, null));
        }
        
    }
    
    private boolean promoteDemotePreCheck(List<DSLComponent> l, String msg) {
        List<DSLComponent> openUnsaved = editorTabs()
            .filter(CodeEditor::isChanged)
            .map(CodeEditor::getComponent)
            .filter(l::contains)
            .collect(Collectors.toList());
        if(!openUnsaved.isEmpty()) {
            getDialogContext().error("Components must be saved before " + msg, "Unsaved components must be saved before " + msg + ": " + openUnsaved.toString());
            return false;
        }
        editorTabs.getTabs().removeAll(
                editorTabs()
                    .filter(e -> l.contains(e.getComponent()))
                    .collect(Collectors.toList()));
        for(DSLComponent c : l) {
            if(!projectHandle.getServiceProvider().getService().exists(c)) {
                getDialogContext().error("Sync error", c.getType() + ": " + c.getIdentifier() + " does not exist anymore. Refresh your repository.");
                return false;
            }
        }
        return true;
    }

    private Optional<DSLComponent> getComponentFromTable(TableView<DSLComponent> table) {
        return Optional.ofNullable(table.getSelectionModel().getSelectedItem());
    }

    private void rebuildTreeMenu() {
        rootItem.getChildren().clear();
        for(DatabaseEnvironment env : projectHandle.getCurrentEnvironment().unsafeGet().getEnvironments()) {
            DatabaseTreeItem environmentItem = new DatabaseTreeItem(env);
            environmentItem.setContextMenu(treeMenuEnvironment);
            ImageView en = new ImageView(new Image(getClass().getResourceAsStream(Config.APPLICATION_PATH_ASSETS_IMAGE_DB_TREE+"_DATABASE.png"), DATABASE_TREE_IMAGE_DIMENSION, DATABASE_TREE_IMAGE_DIMENSION, true, false));
            environmentItem.setGraphic(en);
            for(DatabaseConnection con : env.getConnections()) {
                ImageView view = new ImageView(IconSupport.requestListIcon(con.getEngine().guiConfiguration()));
                view.setFitHeight(DATABASE_TREE_IMAGE_DIMENSION);
                view.setFitWidth(DATABASE_TREE_IMAGE_DIMENSION);
                DatabaseTreeItem connectionItem = new DatabaseTreeItem(con, env);
                connectionItem.setContextMenu(treeMenuConnection);
                connectionItem.setGraphic(view);
                environmentItem.getChildren().add(connectionItem);
            }
            environmentItem.setExpanded(true);
            rootItem.getChildren().add(environmentItem);
        }
    }

    private void search(String s) {
        tableMap.values().forEach(t -> ((FilteredList<DSLComponent>) t.getItems()).setPredicate(p -> p.getIdentifier().toLowerCase().contains(searchField.getText().toLowerCase())));
    }

    private void resetSearch() {
        tableMap.values().forEach(t -> ((FilteredList<DSLComponent>) t.getItems()).setPredicate(p -> true));
    }
    
    private Stream<CodeEditor> editorTabs() {
        return editorTabs.getTabs().stream()
                .filter(this::isCodeEditor)
                .map(this::castToCodeEditor);
    }

    private void shiftTab(int index, boolean right) {
        int offest = right ? 1 : -1;
        Tab tab = editorTabs.getTabs().get(index);
        editorTabs.getTabs().remove(index);
        editorTabs.getTabs().add(index+offest, tab);
        editorTabs.getSelectionModel().select(index+offest);
    }
    
    private void updateDatabaseEnvironment() {
        List<String> expanded = new ArrayList<>();
        rootItem.getChildren().forEach(e -> {
            if(e.isExpanded())
                expanded.add(e.getValue());
        });
        expanded.removeIf(e -> !projectHandle.getCurrentEnvironment().unsafeGet().identifierExists(e));
        databaseEnvironment.setItems(FXCollections.observableArrayList(projectHandle.getCurrentEnvironment().unsafeGet().getEnvironmentIdentifiers()));
        rebuildTreeMenu();
        rootItem.getChildren().forEach(e -> {
            if(expanded.contains(e.getValue()))
                e.setExpanded(true);
        });
        editorTabs().forEach(CodeEditor::requestStaticAnalysis);
    }
    
    private void internalReloadRepository() {
        resetSearch();
        editorTabs.getTabs().forEach(this::cleanup);
        editorTabs.getTabs().clear();

        DSLComponent previousAction = actionBox.getSelectionModel().getSelectedItem();
        actionBox.setItems(FXCollections.observableArrayList());
        
        try {
            List<DSLComponent> discovered = projectHandle.getServiceProvider().getService().discover();
            ExtraData.firstLoadProcessing(projectHandle.getServiceProvider().getService(), discovered);
            components.repopulate(discovered);
            tableMap.entrySet().forEach(e -> e.getValue().setItems(components.of(e.getKey())));
        } catch (DSLServiceException e) {
            throw ErrorUtility.rethrow(e);
        }
        
        projectHandle.reloadReports();
        projectHandle.reloadEnvironment();
        rebuildTreeMenu();
        
        // Hack since the searchable combobox from controlsfx is so broken and has to be filled with the weird listener shit
        // this delay should ensure that the list is filled before reseting to the previously selected component
        Executors.SERVICE.submit(() -> {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) { e.printStackTrace(); }
            Platform.runLater(() -> {
                if(actionBox.getItems().contains(previousAction)) {
                    actionBox.getSelectionModel().select(previousAction);
                } else {
                    actionBox.getSelectionModel().selectFirst();
                }
            });
        });
    }

    /*
     *       ____ _    ____________  ____  ________  ___________
     *      / __ \ |  / / ____/ __ \/ __ \/  _/ __ \/ ____/ ___/
     *     / / / / | / / __/ / /_/ / /_/ // // / / / __/  \__ \
     *    / /_/ /| |/ / /___/ _, _/ _, _// // /_/ / /___ ___/ /
     *    \____/ |___/_____/_/ |_/_/ |_/___/_____/_____//____/
     *
     */

    @Override
    public void onCloseRequest() {
        List<CodeEditor> items = editorTabs()
            .filter(CodeEditor::isChanged)
            .collect(Collectors.toList());
        if(items.isEmpty()) {
            exit();
            return;
        }
        Operation op = getDialogContext().customDialogNoTitle(Dialogs.BATCH_SAVE, new ObjectWrapper(items), projectHandle.getServiceProvider().getService()).getKey();
        if(op == Operation.CANCEL)
            return;
        exit();
    }

    @Override
    protected void keyBindRegisterHook(ObservableMap<KeyCombination, Runnable> bindings) {
        for(int i = 1; i <= 9; i++) {
            final int idx = i;
            bindings.put(new KeyCodeCombination(KeyCode.valueOf(String.format("DIGIT%d", i)), KeyCombination.CONTROL_DOWN), () -> {
                if(editorTabs.getTabs().size()>=idx)
                    editorTabs.getSelectionModel().select(idx-1);
            });
        }
        bindings.put(new KeyCodeCombination(KeyCode.F1), () -> {
            int index = editorTabs.getSelectionModel().getSelectedIndex();
            if(index<=0) return;
            editorTabs.getSelectionModel().select(index-1);
        });
        bindings.put(new KeyCodeCombination(KeyCode.F2), () -> {
            int index = editorTabs.getSelectionModel().getSelectedIndex();
            if(index>=editorTabs.getTabs().size()-1) return;
            editorTabs.getSelectionModel().select(index+1);
        });
        bindings.put(new KeyCodeCombination(KeyCode.F4), () -> {
            int index = editorTabs.getSelectionModel().getSelectedIndex();
            if(index>=editorTabs.getTabs().size()-1)
                return;
            shiftTab(index, true);
        });
        bindings.put(new KeyCodeCombination(KeyCode.F3), () -> {
            int index = editorTabs.getSelectionModel().getSelectedIndex();
            if(index<=0)
                return;
            shiftTab(index, false);
        });
    }

    @Override
    public void onDestruction() {
        projectHandle.removeEnvironmentSignal(databaseEnvironmentChange);
        applicationState.setEnvironmentComboBoxValue(databaseEnvironment.getValue());
        applicationState.setLastSelectedActionComboBox(actionBox.getValue());
        applicationState.setExecutionEnvironment(executionEnvironment.getSelectionModel().getSelectedItem());
        ArrayList<DSLComponent> openedComponents = new ArrayList<>();
        for(Tab tab : editorTabs.getTabs()) {
            if(tab instanceof CodeEditor)
                openedComponents.add(castToCodeEditor(tab).getComponent());
        }
        applicationState.setOpenedTabs(openedComponents);
        applicationState.setTabIndex(editorTabs.getSelectionModel().getSelectedIndex());
        Serialization.dump(applicationState);
        projectHandle.setUiUpdateService(null);
        projectHandle.setNotificationService(null);
        taskUpdateService.destroy();
    }
}
