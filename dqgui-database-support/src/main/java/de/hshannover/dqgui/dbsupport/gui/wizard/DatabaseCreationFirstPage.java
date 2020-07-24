package de.hshannover.dqgui.dbsupport.gui.wizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import de.hshannover.dqgui.dbsupport.api.DatabaseEnvironment;
import de.hshannover.dqgui.dbsupport.api.DatabaseEnvironments;
import de.hshannover.dqgui.dbsupport.exceptions.NonUniqueIdentifierException;
import de.hshannover.dqgui.execution.database.api.DatabaseEngine;
import de.hshannover.dqgui.execution.database.api.EngineManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public final class DatabaseCreationFirstPage {
    private DatabaseCreationWizard wizard;
    private DatabaseEnvironments existingEnvironments;
    private HashMap<String, DatabaseEngine> cache = new HashMap<>();

    @FXML
    ComboBox<DatabaseEngine> engines;
    @FXML
    ComboBox<String> environments;
    @FXML
    TextField environmentInput;
    @FXML
    ListView<String> environmentList;

    public void addDependencies(DatabaseEnvironments environments) {
        this.existingEnvironments = environments;
    }

    public void injectCreatorWizard(DatabaseCreationWizard wizard) {
        this.wizard = wizard;
    }

    public void init(DatabaseEnvironment selectedEnvironment) {
        List<DatabaseEngine> ec = EngineManager.enginesIqm4hd();
        ec.forEach(e -> cache.put(String.format("%s (%s)", e.name(), e.toString()), e));
        StringConverter<DatabaseEngine> converter = new StringConverter<DatabaseEngine>() {

            @Override
            public DatabaseEngine fromString(String string) {
                return cache.get(string);
            }

            @Override
            public String toString(DatabaseEngine e) {
                return String.format("%s (%s)", e.name(), e.toString());
            }
        };
        engines.setConverter(converter);
        engines.setItems(FXCollections.observableArrayList(ec));
        engines.getSelectionModel().select(0);
        updateList(selectedEnvironment);
        MenuItem remove = new MenuItem("Remove environment");
        remove.setOnAction(e -> {
            String s = environmentList.getSelectionModel().getSelectedItem();
            if(s == null) return;
            if(DatabaseEnvironments.DEFAULT_ENVIRONMENT.equals(s)) {
                wizard.getDialogContext().error("Removal of default environment", "You are not allowed to remove the default environment.");
                return;
            }
            existingEnvironments.remove(s);
            updateList(null);
        });
        environmentList.setCellFactory(TextFieldListCell.forListView());
        environmentList.setOnEditCommit((e) -> {
            if(e.getNewValue() == null || e.getNewValue().trim().isEmpty()) {
                wizard.getDialogContext().error("Invalid identifier", "New identifier must not be empty!");
                return;
            }
            try {
                existingEnvironments.rename(existingEnvironments.getEnvironment(environmentList.getSelectionModel().getSelectedItem()), e.getNewValue());
            } catch (NonUniqueIdentifierException er) {
                wizard.getDialogContext().error("Identifier already exists", String.format("An environment with the identifier %s already exists.", e.getNewValue()));
                return;
            } catch (IllegalArgumentException er2) {
                wizard.getDialogContext().error("Renaming of default environment", "You are not allowed to rename the default environment.");
                return;
            }
            updateList(null);
        });
        environmentList.setContextMenu(new ContextMenu(remove));
    }

    @FXML
    void createEnvironment() {
        DatabaseEnvironment dbe = null;
        try {
            dbe = new DatabaseEnvironment(environmentInput.getText());
        } catch(IllegalArgumentException e) {
            wizard.getDialogContext().error("Invalid input!", e.getMessage());
            return;
        }
        try {
            existingEnvironments.add(dbe);
            if(dbe.getIdentifier().equals(DatabaseEnvironments.DEFAULT_ENVIRONMENT))
                throw new NonUniqueIdentifierException(existingEnvironments, dbe.getIdentifier());
            environmentInput.clear();
        } catch(NonUniqueIdentifierException e) {
            wizard.getDialogContext().error("This identifier already exists!", e.getMessage());
            return;
        }
        updateList(null);
    }

    @FXML
    void next() {
        wizard.setEnvironment(existingEnvironments.getEnvironment(environments.getValue()));
        wizard.loadForEngine(engines.getValue());
        wizard.showDatabaseCredentialPage();
    }

    @FXML
    void close() {
        wizard.cancel();
    }

    private void updateList(DatabaseEnvironment selectedEnvironment) {
        List<String> l = new ArrayList<>();
        existingEnvironments.getEnvironments().forEach(e -> l.add(e.getIdentifier()));
        Collections.sort(l);
        ObservableList<String> envs = FXCollections.observableArrayList(l);
        environments.setItems(envs);
        environmentList.setItems(envs);
        if(selectedEnvironment != null) {
            environments.getSelectionModel().select(selectedEnvironment.getIdentifier());
        } else {
            environments.getSelectionModel().select(0);
        }
    }

}
