package de.hshannover.dqgui.core.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import de.hshannover.dqgui.core.concurrency.Iqm4hdTask;
import de.hshannover.dqgui.core.configuration.Views;
import de.hshannover.dqgui.core.model.ProjectHandle;
import de.hshannover.dqgui.core.util.IconFactory;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData.Iqm4hdReturnCode;
import de.hshannover.dqgui.framework.ApplicationContext;
import de.hshannover.dqgui.framework.control.SpecificTreeItem;
import de.hshannover.dqgui.framework.control.SpecificTreeItemCell;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Update service for the treeview in the main window.
 *
 * @author Marc Herschel
 *
 */
public final class TaskUiUpdateService {
    private final SpecificTreeItem active, finished;
    private final HashMap<String, TreeItem<String>> lookup = new HashMap<>();
    private final ApplicationContext context;
    private final ProjectHandle handler;
    private final ListChangeListener<Iqm4hdMetaData> resultListener;
    private final Set<String> ids = Collections.synchronizedSet(new HashSet<>());

    public TaskUiUpdateService(TreeView<String> reportView, ApplicationContext context, ProjectHandle handler) {
        this.context = context;
        this.handler = handler;
        active = new SpecificTreeItem("Active");
        finished = new SpecificTreeItem("Finished");
        ContextMenu finishedMenu = new ContextMenu();
        MenuItem pruneFailed = new MenuItem("Prune failed tasks");
        pruneFailed.setOnAction(e -> handler.pruneFailedReports());
        finishedMenu.getItems().add(pruneFailed);
        finished.setContextMenu(finishedMenu);
        TreeItem<String> root = new TreeItem<>();
        root.getChildren().add(active);
        root.getChildren().add(finished);
        root.setExpanded(true);
        active.setExpanded(true);
        finished.setExpanded(true);
        active.setGraphic(IconFactory.of(Iqm4hdTask.State.RUN));
        finished.setGraphic(IconFactory.of(Iqm4hdTask.State.FINISH));
        reportView.setRoot(root);
        reportView.setCellFactory(SpecificTreeItemCell.createFactory());
        reportView.setOnMouseClicked(e -> {
            if(e.getClickCount() == 2) {
                TreeItem<String> selected = reportView.getSelectionModel().getSelectedItem();
                if(!(selected instanceof Iqm4hdStatusTreeItem))
                    return;
                Iqm4hdStatusTreeItem item = (Iqm4hdStatusTreeItem) selected;
                if(item.retrieveSpecifiedContextMenu().getItems().size() == 3) {
                    context.load(Views.REPORTS, "Reports", item.getIdentifier());
                    return;
                }
                item.retrieveSpecifiedContextMenu().getItems().get(0).getOnAction().handle(null);
            }
        });

        resultListener = (changed) -> {
            while(changed.next()) {
                if(changed.wasRemoved()) {
                    for(Iqm4hdMetaData r : changed.getRemoved()) {
                        finished.getChildren().removeIf(f -> ((Iqm4hdStatusTreeItem) f).getIdentifier().equals(r.getIdentifier()));
                    }
                }
                if(changed.wasAdded()) {
                    addItems(changed.getAddedSubList());
                }
            }
        };
        
        handler.addCompletedListener(resultListener);
        
        // Duplicate items mitigation when fetching results from server pre startup
        
        addItems(handler.getCompletedReportsNoErrors().stream().filter(r -> !ids.contains(r.getIdentifier())).collect(Collectors.toList()));
    }
    
    public void reset() {
        ids.clear();
        active.getChildren().clear();
        finished.getChildren().clear();
        addItems(handler.getCompletedReportsWithErrors());
    }
    
    private void addItems(List<? extends Iqm4hdMetaData> list) {
        ArrayList<Iqm4hdMetaData> m = new ArrayList<>(list);
        m.sort((c1, c2) -> c1.getFinished().compareTo(c2.getFinished()));
        m.forEach(this::addItem);
    }
    
    private void addItem(Iqm4hdMetaData res) {
        // Sadly we can't have sets as the backing data source here.
        // So we need to use this hack to prevent ui ghost duplicate entries
        if(ids.contains(res.getHash()))
            return;
        ids.add(res.getHash());
        Iqm4hdStatusTreeItem item = new Iqm4hdStatusTreeItem(res);
        menuFinished(item, res, res.isError());
        item.setGraphic(IconFactory.of(res.getReturnCode()));
        finished.getChildren().add(0, item);
    }

    public void registerNewTask(Iqm4hdTask t) {
        Iqm4hdStatusTreeItem item = new Iqm4hdStatusTreeItem(t.getMetadata());
        menuActive(item, t.getMetadata());
        item.setGraphic(IconFactory.of(Iqm4hdTask.State.AWAIT));
        t.getStateProperty().addListener((obs, oldV, newV) -> {
            if(newV == Iqm4hdTask.State.FINISH) {
                item.setGraphic(IconFactory.of(t.getMetadata().getReturnCode()));
            } else {
                item.setGraphic(IconFactory.of(newV));
            }
        });
        active.getChildren().add(item);
        lookup.put(t.getIdentifier(), item);
    }

    public void complete(String identifier, Iqm4hdReturnCode code, boolean isError) {
        Platform.runLater(() -> {
            Iqm4hdStatusTreeItem i = (Iqm4hdStatusTreeItem) active.getChildren().remove(active.getChildren().indexOf(lookup.remove(identifier)));
            i.updateTooltip();
        });
    }

    public void destroy() {
        handler.removeCompletedListener(resultListener);
    }

    private void menuActive(Iqm4hdStatusTreeItem item, Iqm4hdMetaData meta) {
        ContextMenu menu = new ContextMenu();
        MenuItem i = new MenuItem("View log");
        i.setOnAction(e -> context.loadIdentified(Views.LIVELOG, meta.getIdentifier(), "Log for: " + meta.getIdentifier(), meta));
        menu.getItems().add(i);
        item.setContextMenu(menu);
    }

    private void menuFinished(Iqm4hdStatusTreeItem item, Iqm4hdMetaData meta, boolean isError) {
        ContextMenu menu = new ContextMenu();
        if(!isError) {
            MenuItem i = new MenuItem("View report");
            i.setOnAction(e -> context.load(Views.REPORTS, "Reports", meta.getIdentifier()));
            menu.getItems().add(i);
        }
        MenuItem i = new MenuItem("View log");
        i.setOnAction(e -> context.loadIdentified(Views.LIVELOG, meta.getIdentifier(), "Log for: " + meta.getIdentifier(), meta));
        MenuItem r = new MenuItem("Remove report");
        r.setOnAction(e -> handler.removeReport(meta.getIdentifier()));
        menu.getItems().addAll(i, r);
        item.setContextMenu(menu);
    }

}
