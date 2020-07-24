package de.hshannover.dqgui.core.controllers;

import static de.hshannover.dqgui.core.Utility.integer;
import static de.hshannover.dqgui.core.Utility.requireNonNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import de.hshannover.dqgui.core.configuration.Components;
import de.hshannover.dqgui.core.model.ProjectHandle;
import de.hshannover.dqgui.core.ui.MetaDataListCell;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData;
import de.hshannover.dqgui.framework.AbstractWindowController;
import de.hshannover.dqgui.framework.FXMLLoadingFactory.ComponentContent;
import de.hshannover.dqgui.framework.annotations.InjectedCallback;
import de.hshannover.dqgui.framework.signal.SignalHandler;
import de.mvise.iqm4hd.api.ExecutionReport;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public final class ReportsController extends AbstractWindowController {
    /*
     * Very bad to have this static but it makes a lot of things easier with the filter enum.
     * A ReportsController can only exist once at a time so this is bad but alright in this context.
     */
    private static StringProperty filterMaskTextProperty;

    private final Map<String, ComponentContent> componentDataCache = new HashMap<>(), componentHeaderCache = new HashMap<>();
    private final ObservableList<Iqm4hdMetaData> results = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private final FilteredList<Iqm4hdMetaData> filtered = new FilteredList<>(results, p -> true);
    private final SortedList<Iqm4hdMetaData> sorted = new SortedList<>(filtered, SortingStrategy.DATE_DESC.comparator);
    private final String initialIdentifier;
    private final MenuIndexReference collapsedMenuIndexSelected = new MenuIndexReference();
    private final SignalHandler suicideHandler = () -> unregister();
    private Parent noReportsAvailable;
    private ListChangeListener<Iqm4hdMetaData> listener;
    private boolean ctrlOrShiftDown;

    /*
     * Injected by ApplicationContext
     */
    private final ProjectHandle projectHandle = null;
    
    public static class MenuIndexReference {
        public int index = 0;
    }

    private enum SortingStrategy {
        NAME_ASC
            ("Name (ascending)", (m1, m2) -> m1.getHumanReadable().getHumanReadable().compareTo(m2.getHumanReadable().getHumanReadable())),
        NAME_DESC
            ("Name (descending)", (m1, m2) -> m2.getHumanReadable().getHumanReadable().compareTo(m1.getHumanReadable().getHumanReadable())),
        DATE_ASC
            ("Date (ascending)", (m1, m2) -> m1.getFinished().compareTo(m2.getFinished())),
        DATE_DESC
            ("Date (descending)", (m1, m2) -> m2.getFinished().compareTo(m1.getFinished())),
        SIZE_DESC
            ("Issues (ascending)", (m1, m2) -> Integer.compare(m1.getIssues(), m2.getIssues())),
        SIZE_ASC
            ("Issues (descending)", (m1, m2) -> Integer.compare(m2.getIssues(), m1.getIssues()));

        final String name;
        final Comparator<Iqm4hdMetaData> comparator;

        SortingStrategy(String name, Comparator<Iqm4hdMetaData> comparator) {
            requireNonNull(name, comparator);
            this.comparator = comparator;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private enum FilterStrategy {
        NONE
            ("Default - No filtering", t -> true),
        IS_WITH_ISSUES
            ("Behavior - With Issues", t -> t.getIssues() > 0),
        IS_WITHOUT_ISSUES
            ("Behavior - Without Issues", t -> t.getIssues() == 0),
        ISSUE_COUNT
            ("Custom - Issues == #INPUT", (t, s) -> integer(s).map(i -> i == t.getIssues()).orElse(Boolean.FALSE)),
        ISSUE_LT
            ("Custom - Issue count < #INPUT", (t, s) -> integer(s).map(i -> t.getIssues() < i).orElse(Boolean.FALSE)),
        ISSUE_GT
            ("Custom - Issue count > #INPUT", (t, s) -> integer(s).map(i -> t.getIssues() > i).orElse(Boolean.FALSE)),
        ISSUE_LTEQ
            ("Custom - Issue count <= #INPUT", (t, s) -> integer(s).map(i -> t.getIssues() <= i).orElse(Boolean.FALSE)),
        ISSUE_GTEQ
            ("Custom - Issue count >= #INPUT", (t, s) -> integer(s).map(i -> t.getIssues() >= i).orElse(Boolean.FALSE)),
        NAME_LIKE
            ("Custom - lower(Name) LIKE lower(#INPUT)", (t, s) -> t.getHumanReadable().getHumanReadable().toLowerCase().contains(s.toLowerCase())),
        ENV_LIKE
            ("Custom - lower(Environment) LIKE lower(#INPUT)", (t, s) -> t.getEnvironment().toLowerCase().contains(s.toLowerCase())),
        ACTION_LIKE
            ("Custom - lower(Action) LIKE lower(#INPUT)", (t, s) -> t.getAction().toLowerCase().contains(s.toLowerCase())),
        TIME_FINISHED_TODAY
            ("Time 1 - Finished this day", t -> LocalDateTime.ofInstant(t.getFinished(), ZoneId.systemDefault()).compareTo(LocalDate.now().minusDays(1).atStartOfDay()) >= 0),
        TIME_WEEK_AGO
            ("Time 2 - Finished this week", t -> LocalDateTime.ofInstant(t.getFinished(), ZoneId.systemDefault()).compareTo(LocalDate.now().minusWeeks(1).atStartOfDay()) >= 0),
        TIME_MONTH_AGO
            ("Time 3 - Finished this month", t -> LocalDateTime.ofInstant(t.getFinished(), ZoneId.systemDefault()).compareTo(LocalDate.now().minusMonths(1).atStartOfDay()) >= 0),
        TIME_YEAR_AGO
            ("Time 4 - Finished this year", t -> LocalDateTime.ofInstant(t.getFinished(), ZoneId.systemDefault()).compareTo(LocalDate.now().minusYears(1).atStartOfDay()) >= 0),
        TIME_DECADE_AGO
            ("Time 5 - Finished this decade", t -> LocalDateTime.ofInstant(t.getFinished(), ZoneId.systemDefault()).compareTo(LocalDate.now().minusYears(10).atStartOfDay()) >= 0)
		;

        private final String name;
        private final Predicate<Iqm4hdMetaData> filter;
        private final boolean isCustom;
        private final BiFunction<Iqm4hdMetaData, String, Boolean> customFilter;

        FilterStrategy(String name, Predicate<Iqm4hdMetaData> filter) {
            requireNonNull(name, filter);
            this.name = name;
            this.filter = filter;
            isCustom = false;
            customFilter = null;
        }

        FilterStrategy(String name, BiFunction<Iqm4hdMetaData, String, Boolean> customFilter) {
            requireNonNull(name, customFilter);
            this.name = name;
            isCustom = true;
            this.customFilter = customFilter;
            this.filter = null;
        }

        public String getName() {
            return name;
        }

        public Predicate<Iqm4hdMetaData> getFilter() {
            if(isCustom)
                return t -> customFilter.apply(t, sanitize(filterMaskTextProperty.getValue()));
            return filter;
        }

        public boolean isCustom() {
            return isCustom;
        }

        private String sanitize(String string) {
            if(string == null)
                return "";
            return string.trim();
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @FXML
    SplitPane mainPane;
    @FXML
    BorderPane reportPane, leftSide;
    @FXML
    HBox header;
    @FXML
    ListView<Iqm4hdMetaData> taskView;
    @FXML
    Button resetFilterMask;
    @FXML
    TextField filterMask;
    @FXML
    ComboBox<FilterStrategy> filterBox;
    @FXML
    ComboBox<SortingStrategy> sortingBox;

    private ReportsController() { initialIdentifier = null; }
    private ReportsController(String identifier) {
        initialIdentifier = identifier;
    }

    @InjectedCallback
    private void selectReport(String identifier) {
        prepareForIdentifier(identifier);
    }

    @FXML
    @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
    void initialize() {
        projectHandle.projectChangedSignal().register(suicideHandler);
        filterMaskTextProperty = filterMask.textProperty();
        SplitPane.setResizableWithParent(leftSide, Boolean.FALSE);
        listener = (changed) -> {
            while(changed.next()) {
                if(changed.wasAdded()) {
                    for(Iqm4hdMetaData result : changed.getAddedSubList()) {
                        if(result.isError())
                            continue;
                        results.add(result);
                    }
                    if(reportPane.getCenter() == noReportsAvailable)
                        prepareForIdentifier(changed.getAddedSubList().get(0));
                }
                if(changed.wasRemoved()) {
                    for(Iqm4hdMetaData result : changed.getRemoved()) {
                        if(result.isError())
                            continue;
                        results.remove(result);
                    }
                    runRemovalTasks(new ArrayList<>(changed.getRemoved()));
                }
            }
        };
        results.addAll(projectHandle.getCompletedReportsNoErrors().stream().filter(r -> !results.contains(r)).collect(Collectors.toList()));
        projectHandle.addCompletedListener(listener);
        taskView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        taskView.setItems(sorted);
        taskView.setCellFactory(param -> new MetaDataListCell());
        taskView.setOnKeyPressed(e -> ctrlOrShiftDown = e.getCode() == KeyCode.SHIFT || e.getCode() == KeyCode.CONTROL);
        taskView.setOnKeyReleased(e -> ctrlOrShiftDown = !(e.getCode() == KeyCode.SHIFT || e.getCode() == KeyCode.CONTROL));
        taskView.setOnMouseClicked(e -> {
            if(!ctrlOrShiftDown && taskView.getSelectionModel().getSelectedItem() != null && e.getButton() == MouseButton.PRIMARY)
                prepareForIdentifier(taskView.getSelectionModel().getSelectedItem());
        });
        taskView.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.DELETE)
                remove();
        });
        MenuItem remove = new MenuItem("Remove report(s)");
        remove.setOnAction(e -> remove());
        taskView.setContextMenu(new ContextMenu(remove));
        noReportsAvailable = loadComponent(Components.NO_REPORTS_AVAILABLE).getParent();
        filterBox.setItems(FXCollections.observableList(Arrays.asList(FilterStrategy.values())));
        filterBox.getSelectionModel().select(FilterStrategy.NONE);
        filterBox.valueProperty().addListener((obs, oldV, newV) -> filtered.setPredicate(newV.getFilter()));
        Collections.sort(filterBox.getItems(), Comparator.comparing(FilterStrategy::getName));
        sortingBox.setItems(FXCollections.observableList(Arrays.asList(SortingStrategy.values())));
        sortingBox.getSelectionModel().select(SortingStrategy.DATE_DESC);
        sortingBox.valueProperty().addListener((obs, oldV, newV) -> sorted.setComparator(newV.comparator));
        Collections.sort(sortingBox.getItems(), Comparator.comparing(s -> s.name));
        filterMask.textProperty().addListener((obs, oldV, newV) -> {
            if(filterBox.getSelectionModel().getSelectedItem().isCustom())
                filtered.setPredicate(filterBox.getSelectionModel().getSelectedItem().getFilter());
        });
        prepareForIdentifier(initialIdentifier);
    }

    @FXML
    void resetFilterMask() {
        filterMask.clear();
    }
    
    private void remove() {
        if(taskView.getSelectionModel().getSelectedItems() == null || taskView.getSelectionModel().getSelectedItems().isEmpty())
            return;
        projectHandle.removeReports(new ArrayList<>(taskView.getSelectionModel().getSelectedItems()));
    }

    private void prepareForIdentifier(String identifier) {
        if(identifier == null) {
            if(this.results.isEmpty()) {
                reportPane.setCenter(noReportsAvailable);
            } else {
                prepareForIdentifier(this.sorted.get(0).getIdentifier());
            }
            return;
        }
        results.stream().filter(r -> r.getIdentifier().equals(identifier)).findFirst().ifPresent(this::prepareForIdentifier);
    }

    private void prepareForIdentifier(Iqm4hdMetaData result) {
        taskView.getSelectionModel().clearSelection();
        taskView.getSelectionModel().select(result);
        
        ComponentContent c = componentDataCache.get(result.getIdentifier());
        ComponentContent h = componentHeaderCache.get(result.getIdentifier());
        if(c != null) {
            selectCorrectIndex(c.getParent());
            reportPane.setCenter(c.getParent());
            header.getChildren().clear();
            header.getChildren().add(h.getParent());
            return;
        }
        
        try {
            ExecutionReport r = projectHandle.requestExecutionReport(result);
            c = loadComponent(Components.REPORT_DATA_COMPONENT, result, r, collapsedMenuIndexSelected);
            h = loadComponent(Components.REPORT_HEADER, result);
        } catch (Exception e) {
            c = loadComponent(Components.REPORT_FAILED_TO_LOAD, e);
            h = loadComponent(Components.REPORT_HEADER_FAILED_TO_LOAD);
        }
        
        componentDataCache.put(result.getIdentifier(), c);
        componentHeaderCache.put(result.getIdentifier(), h);
        header.getChildren().clear();
        header.getChildren().add(h.getParent());
        selectCorrectIndex(c.getParent());
        reportPane.setCenter(c.getParent());
    }
    
    private void selectCorrectIndex(Parent p) {
        if(p instanceof TabPane) {
            Accordion a = (Accordion) ((TabPane) p.getChildrenUnmodifiable().get(0)).getTabs().get(0).getContent();
            int index = collapsedMenuIndexSelected.index >= a.getPanes().size() ? a.getPanes().size()-1 : collapsedMenuIndexSelected.index;
            a.setExpandedPane((TitledPane) a.getPanes().get(index));
        }
    }
    
    private void runRemovalTasks(List<? extends Iqm4hdMetaData> list) {
        if(results.size() > 0) {
            prepareForIdentifier(results.get(0));
        } else {
            header.getChildren().clear();
            reportPane.setCenter(noReportsAvailable);
        }
        for(Iqm4hdMetaData m : list) {
            unloadComponent(componentDataCache.get(m.getIdentifier()));
            unloadComponent(componentHeaderCache.get(m.getIdentifier()));
        }
    }

    @Override
    @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
    public void onDestruction() {
        projectHandle.projectChangedSignal().unregister(suicideHandler);
        projectHandle.removeCompletedListener(listener);
        filterMaskTextProperty = null;
    }
    
    @Override
    protected void keyBindRegisterHook(ObservableMap<KeyCombination, Runnable> bindings) {
        bindings.put(new KeyCodeCombination(KeyCode.ESCAPE), () -> unregister());
    }
}
