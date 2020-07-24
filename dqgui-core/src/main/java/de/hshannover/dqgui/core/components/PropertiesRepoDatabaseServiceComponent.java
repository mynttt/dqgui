package de.hshannover.dqgui.core.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import org.tinylog.Logger;
import com.google.common.base.Objects;
import de.hshannover.dqgui.core.controllers.PropertiesController.PropertiesRepoSave;
import de.hshannover.dqgui.core.model.ApplicationProperties;
import de.hshannover.dqgui.core.model.ProjectHandle;
import de.hshannover.dqgui.dbsupport.api.DatabaseEnvironment;
import de.hshannover.dqgui.dbsupport.exceptions.NonUniqueIdentifierException;
import de.hshannover.dqgui.dbsupport.gui.wizard.DatabaseUpdateWizard;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;
import de.hshannover.dqgui.execution.database.api.DatabaseCredential;
import de.hshannover.dqgui.execution.database.api.DatabaseEngine;
import de.hshannover.dqgui.execution.database.api.DatabaseSocket;
import de.hshannover.dqgui.execution.database.api.EngineManager;
import de.hshannover.dqgui.execution.database.api.Repository;
import de.hshannover.dqgui.execution.database.api.Repository.ValidationReport;
import de.hshannover.dqgui.framework.AbstractComponentController;
import de.hshannover.dqgui.framework.JavaFXTools;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;

final class PropertiesRepoDatabaseServiceComponent extends AbstractComponentController implements PropertiesRepoSave {
    private final ApplicationProperties properties;
    private final ProjectHandle handle;
    
    private ReadOnlyObjectWrapper<DatabaseConnection> selected = new ReadOnlyObjectWrapper<>();
    private ObservableList<DatabaseConnection> repos;
    
    @FXML
    Label status;
    @FXML
    TableView<DatabaseConnection> table;
    @FXML
    ComboBox<DatabaseEngine> dbEngines;
    @FXML
    Button runButton;
    
    public PropertiesRepoDatabaseServiceComponent(ApplicationProperties properties, ProjectHandle projectHandle) {
        this.properties = properties;
        this.handle = projectHandle;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @FXML
    void initialize() {
        ObservableList<DatabaseEngine> engines = FXCollections.observableArrayList(EngineManager.enginesRepository());
        if(engines.size() == 0) {
            dbEngines.setDisable(true);
        } else {
            dbEngines.setItems(engines);
            dbEngines.getSelectionModel().select(0);
        }
        JavaFXTools.tableColumnAutoResize(table);
        table.setPlaceholder(new Label("No database repository connections created yet."));
        status.setText("No database repository selected");
        selected.getReadOnlyProperty().addListener((obs, o, n) -> status.setText(n == null ? "No database repository selected" : toString(n)));
        repos = FXCollections.observableArrayList(properties.getDbRepositories());
        if(properties.getSelectedDbRepo().isPresent())
            selected.set(properties.getSelectedDbRepo().get());
        table.setItems(repos);
        table.setOnMouseClicked(e -> {
            if(e.getClickCount() == 2) {
                DatabaseConnection r = table.getSelectionModel().getSelectedItem();
                if(r == null) return;
                selected.set(r);
            }
        });
        table.getColumns().get(0).setCellValueFactory(data -> new SimpleObjectProperty(data.getValue().getIdentifier()));
        table.getColumns().get(1).setCellValueFactory(data -> new SimpleObjectProperty(data.getValue().getSocket().asAddress()));
        table.getColumns().get(2).setCellValueFactory(data -> new SimpleObjectProperty(data.getValue().getEngine().toString()));
        table.getColumns().get(3).setCellValueFactory(data -> new SimpleObjectProperty(data.getValue().getCredential().getDatabase()));
        table.getColumns().get(4).setCellValueFactory(data -> new SimpleObjectProperty(data.getValue().getCredential().getUsername()));
        runButton.disableProperty().bind(Bindings.createBooleanBinding(engines::isEmpty, engines));
        
        MenuItem select = new MenuItem("Select as active connection");
        select.setOnAction(e -> {
            DatabaseConnection repo = table.getSelectionModel().getSelectedItem();
            if(repo == null) return;
            selected.set(repo);
        });
        
        MenuItem modify = new MenuItem("Modify connection");
        modify.setOnAction(e -> {
            DatabaseConnection repo = table.getSelectionModel().getSelectedItem();
            if(repo == null) return;
            try {
                Optional<DatabaseConnection> updated = new DatabaseUpdateWizard(new DatabaseEnvironment("dummy", new HashSet<>()), repo).begin();
                if(!updated.isPresent()) return;
                repos.remove(repo);
                selected.set(null);
                repo = updated.get();
                selected.set(repo);
                if(!setup(repo)) {
                    getDialogContext().error("Invalid repository", "Setup for repository connection failed. Connection has been removed.");
                    return;
                }
                repos.add(repo);
            } catch (NonUniqueIdentifierException e1) {
                Logger.error(e1);
            }
        });
        MenuItem remove = new MenuItem("Remove");
        remove.setOnAction(e -> {
            DatabaseConnection repo = table.getSelectionModel().getSelectedItem();
            if(repo == null) return;
            repos.remove(repo);
            if(Objects.equal(selected.get(), repo) || repos.isEmpty())
                selected.set(null);
        });
        table.setContextMenu(new ContextMenu(select, modify, remove));
    }
    
    @FXML
    void runRepoWizard() {
        DatabaseEngine engine = dbEngines.getSelectionModel().getSelectedItem();
        try {
            Optional<DatabaseConnection> con = new DatabaseUpdateWizard(new DatabaseEnvironment("dummy", new HashSet<>()), 
                    DatabaseConnection.from("unknown repo", engine, 
                            new DatabaseSocket("localhost", engine.defaultPort().map(p -> p).orElse(-1)), 
                            new DatabaseCredential("", "", ""))).begin();
            if(!con.isPresent()) return;
            DatabaseConnection repo = con.get();
            if(setup(repo)) {
                repos.add(repo);
                if(selected.get() == null)
                    selected.set(repo);
            }
        } catch (NonUniqueIdentifierException e) {}
    }

    private boolean setup(DatabaseConnection repo) {
        try(Repository<?> r = repo.getEngine().createRepository(repo)) {
            ValidationReport result = r.create();
            if(!result.success) {
                getDialogContext().textErrorDialog("Repository set-up failed", "Repository set-up failed", result.message);
                return false;
            }
        } catch (Exception e) {
            getDialogContext().exceptionDialog(e, "Setting up database repository failed!", "Something went wrong when trying to set-up your remote repository.");
            Logger.error(e);
            return false;
        }
        getDialogContext().information("Repository has been set-up", "Repository has been set-up successfully and is now usable.");
        return true;
    }
    
    @Override
    public boolean saveValidation() {
        if(handle.hasTasksRunning()) {
            getDialogContext().error("Tasks pending", "Tasks depending on this repository are still running. Try later again.");
            return false;
        }
        if(repos.isEmpty()) {
            properties.resetDbRepo();
            handle.registerDatabaseRepository(null);
            return true;
        }
        if(selected == null) {
            getDialogContext().error("No connection selected", "You must select a connection first!");
            return false;
        }
        @SuppressWarnings("rawtypes")
        Repository r = selected.get().getEngine().createRepository(selected.get());
        try {
        } catch(Exception e) {
            try {
                r.close();
            } catch (Exception e1) {
                Logger.error(e1);
            }
            getDialogContext().exceptionDialog(e, "Failure creating remote repository connection", "Something went wrong while creating your remote repository connection.");
            return false;
        }
        handle.registerDatabaseRepository(r);
        ValidationReport valid = r.validate();
        if(!valid.success) {
            getDialogContext().textErrorDialog("Validation failed", "Repository validation failed", valid.message);
            return false;
        }
        properties.setDbRepository(selected.get(), new ArrayList<>(repos));
        handle.registerDatabaseRepository(r);
        return true;
    }
    
    private String toString(DatabaseConnection con) {
        return "Selected: " + con.getIdentifier() + " [" + con.getEngine() + "]";
    }

}
