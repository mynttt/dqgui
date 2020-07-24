package de.hshannover.dqgui.core.controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import de.hshannover.dqgui.core.model.ApplicationProperties;
import de.hshannover.dqgui.core.model.ProjectHandle;
import de.hshannover.dqgui.execution.model.Project;
import de.hshannover.dqgui.execution.model.RepoType;
import de.hshannover.dqgui.framework.AbstractWindowController;
import de.hshannover.dqgui.framework.ErrorUtility;
import de.hshannover.dqgui.framework.dialogs.DialogContext.DialogStyle;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

final class ProjectsController extends AbstractWindowController {

    private final ApplicationProperties properties = null;
    private final ProjectHandle projectHandle = null;
    
    @FXML
    TableView<Project> projectTable;
    @FXML
    Label selectedProject, projectType;
    @FXML
    TextField databaseProjectName;
    @FXML
    Button addBtn;
    
    private final ReadOnlyObjectWrapper<Project> selected = new ReadOnlyObjectWrapper<>();
    private final ObservableList<Project> obs = FXCollections.observableArrayList();
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @FXML
    void initialize() {
        switch(properties.getRepoType()) {
        case FILE_SYSTEM:
            databaseProjectName.setVisible(false);
            projectType.setText("Filesystem Projects");
            break;
        case DATABASE:
            projectType.setText("Database Projects");
            databaseProjectName.setText("");
            addBtn.disableProperty().bind(Bindings.createBooleanBinding(() -> databaseProjectName.getText().trim().isEmpty(), databaseProjectName.textProperty()));
            databaseProjectName.setOnKeyPressed(e -> {
                if(e.getCode() == KeyCode.ENTER && !addBtn.disabledProperty().get())
                    addProjects();
            });
            break;
        default:
            throw new IllegalStateException("invalid repo:" + properties.getRepoType());
        }
        
        selected.getReadOnlyProperty().addListener((obs, oldv, newv) -> selectedProject.setText(newv == null ? "No project loaded" : "Project: " + newv.getName() + " loaded (" + newv.getIdentifier() + ")"));
        properties.getSelectedProject().ifPresent(selected::set);
        
        obs.addAll(projectHandle.listProjects(properties));
        SortedList<Project> s = new SortedList<Project>(obs, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        s.comparatorProperty().bind(projectTable.comparatorProperty());
        projectTable.setItems(s);
        
        projectTable.setPlaceholder(new Label("No projects added yet"));
        projectTable.getColumns().get(0).setCellValueFactory(c -> new ReadOnlyObjectWrapper(c.getValue().getName()));
        projectTable.getColumns().get(1).setCellValueFactory(c -> new ReadOnlyObjectWrapper(c.getValue().getIdentifier()));
        projectTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        projectTable.setOnMouseClicked(e -> {
            Project p = projectTable.getSelectionModel().getSelectedItem();
            if(e.getClickCount() == 2 && p instanceof Project && !projectHandle.isProjectLoaded(p))
                selectProject(p);
        });
        
        projectTable.getSortOrder().add(projectTable.getColumns().get(0));
        
        MenuItem select = new MenuItem("Load project");
        select.setOnAction(e -> {
            if(projectTable.getSelectionModel().getSelectedItem() instanceof Project)
                selectProject(projectTable.getSelectionModel().getSelectedItem());
        });
        
        MenuItem delete = new MenuItem("Delete project");
        delete.setOnAction(e -> {
            List<Project> l = projectTable.getSelectionModel().getSelectedItems();
                l.forEach(this::deleteProject);
        });
        
        if(properties.getRepoType() == RepoType.FILE_SYSTEM) {
            MenuItem remove = new MenuItem("Remove project from DQGUI");
            remove.setOnAction(e -> {
                List<Project> l = projectTable.getSelectionModel().getSelectedItems();
                if(l != null && !l.isEmpty())
                    l.forEach(this::removeFilesystemProject);
            });
            MenuItem open = new MenuItem("Open on system");
            open.setOnAction(e -> {
                List<Project> l = projectTable.getSelectionModel().getSelectedItems();
                if(l.isEmpty()) return;
                for (Project p : l)
                    try {
                        Desktop.getDesktop().open(Paths.get(p.getIdentifier()).toFile());
                    } catch (IOException e1) {
                        throw ErrorUtility.rethrow(e1);
                    }
            });
            projectTable.setContextMenu(new ContextMenu(select, open, remove, delete));
        } else {
            projectTable.setContextMenu(new ContextMenu(select, delete));
        }
    }
    
    void selectProject(Project p) {
        if(projectHandle.hasTasksRunning()) {
            getDialogContext().error("Tasks pending", "Tasks depending on this project are still running. Try later again.");
            return;
        }
        projectHandle.unloadProject(properties.getRepoType());
        try {
            projectHandle.loadProject(p, properties);
            properties.setSelectedProject(p);
            selected.set(p);
            getDialogContext().information("Project loaded", "Project '" + p.getName() + "' has been loaded successfully.");
        } catch (Exception e) {
            getDialogContext().exceptionDialog(e, "Failed to load project", "Failed to load project: " + p.getName());
        }
    }
    
    void deleteProject(Project p) {
        if(projectHandle.hasTasksRunning() && projectHandle.isProjectLoaded(p)) {
            getDialogContext().error("Tasks pending", "Tasks depending on the project '" + p.getName() + "' are still running. Try later again.");
            return;
        }
        boolean ok = getDialogContext().confirmCancelDialog(DialogStyle.WARNING, "Deletation of " + p.getName(), "Are you sure to delete project '" + p.getName() + "' (" + p.getIdentifier() + ")?");
        if(!ok) return;
        if(projectHandle.isProjectLoaded(p)) {
            projectHandle.unloadProject(properties.getRepoType());
            properties.setSelectedProject(null);
            selected.set(null);
        }
        try {
            if(projectHandle.deleteProject(properties, p)) {
                obs.remove(p);
                getDialogContext().information("Deletation completed", "Deleted project: " + p.getIdentifier());
            }
        } catch(Exception e) {
            getDialogContext().exceptionDialog(e, "Failed to delete project", "Failed to delete project: " + p.getName());
        } 
    }
    
    void removeFilesystemProject(Project p) {
        if(projectHandle.hasTasksRunning() && projectHandle.isProjectLoaded(p)) {
            getDialogContext().error("Tasks pending", "Tasks depending on the project '" + p.getName() + "' are still running. Try later again.");
            return;
        }
        if(projectHandle.isProjectLoaded(p)) {
            projectHandle.unloadProject(properties.getRepoType());
            properties.setSelectedProject(null);
            selected.set(null);
        }
        properties.removeFilesystemProject(p);
        obs.remove(p);
        getDialogContext().information("Removal completed", "Removed project: " + p.getIdentifier());
    }
    
    @FXML
    void addProjects() {
        try {
            Project p;
            switch(properties.getRepoType()) {
                case FILE_SYSTEM:
                    p = addFilesystemProject();
                    break;
                case DATABASE:
                    p = addDatabaseProject();
                    break;
                default:
                    throw new IllegalStateException("unknown repo type:" + properties.getRepoType());
            }
            if(p != null) {
                if(projectHandle.createProject(properties, p)) {
                    if(!obs.contains(p)) 
                        obs.add(p);
                    getDialogContext().information("Project created", "Project '" + p.getName() + "' has been created. You can now load the project.");
                }
            }
        } catch(Exception e) {
            getDialogContext().exceptionDialog(e, "Failed to create project", "Failed to create project.");
        }
    }
    
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    private Project addFilesystemProject() {
        File f = getDialogContext().directoryDialog("Import a DQGUI project");
        if(f == null) return null;
        Path p = f.toPath().toAbsolutePath();
        return new Project(p.toString(), p.getFileName().toString(), RepoType.FILE_SYSTEM, null);
    }
    
    private Project addDatabaseProject() {
        return new Project(null, databaseProjectName.getText().trim(), RepoType.DATABASE, UUID.randomUUID().toString());
    }
    
    @FXML
    void close() {
        unregister();
    }
    
    @Override
    protected void keyBindRegisterHook(ObservableMap<KeyCombination, Runnable> bindings) {
        bindings.put(new KeyCodeCombination(KeyCode.ESCAPE), () -> close());
    }
}