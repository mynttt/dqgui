package de.hshannover.dqgui.dbsupport.gui.engines;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.controlsfx.validation.Validator;
import de.hshannover.dqgui.dbsupport.gui.AbstractEngineUpdateCreate;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;
import de.hshannover.dqgui.execution.database.api.DatabaseCredential;
import de.hshannover.dqgui.execution.database.api.DatabaseSocket;
import de.hshannover.dqgui.execution.database.gui.IconSupport;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.util.Pair;

/**
 * Wizard for all JDBC Connections that are common (means they require: Username, Password, Database, Host, Port) and allows to set connection parameters.<br>
 *
 * @author Marc Herschel
 *
 */
public final class JDBCCommon extends AbstractEngineUpdateCreate {
    private Map<String, String> keyValue = new HashMap<>();
    private ObservableList<Pair<String, String>> list = FXCollections.observableArrayList();

    @FXML
    ImageView image;
    @FXML
    TextField host, port, database, username, key, value;
    @FXML
    PasswordField password;
    @FXML
    TableView<Pair<String, String>> keyTable;

    @Override
    protected void onInitialize() {
        image.setImage(IconSupport.requestWizardIcon(engine().guiConfiguration()));
        keyTable.setItems(new SortedList<>(list, Comparator.comparing(Pair::getKey)));
        keyTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("key"));
        keyTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("value"));
        MenuItem mi1 = new MenuItem("Remove");
        mi1.setOnAction(e -> {
            keyValue.remove(keyTable.getSelectionModel().getSelectedItem().getKey());
            updateTable();
        });
        keyTable.setContextMenu(new ContextMenu(mi1));
        keyTable.setPlaceholder(new Label("No JDBC parameters specified"));
        getValidationSupport().registerValidator(database, Validator.createEmptyValidator("Database must be set."));
        getValidationSupport().registerValidator(host, Validator.createEmptyValidator("Host must be set."));
        getValidationSupport().registerValidator(username, Validator.createEmptyValidator("Username must be set."));
        getValidationSupport().registerValidator(password, Validator.createEmptyValidator("Password must be set."));
        getValidationSupport().registerValidator(port, Validator.createPredicateValidator(p -> verifyPort(port.getText()), "Port must be int 0 <= port <= 65535"));
    }

    @Override
    protected void onUpdate() {
        port.setText(Integer.toString(oldConnection().getSocket().getPort()));
        host.setText(oldConnection().getSocket().getHost());
        username.setText(oldConnection().getCredential().getUsername());
        password.setText(oldConnection().getCredential().getPassword());
        database.setText(oldConnection().getCredential().getDatabase());
        oldConnection().getCustomParameters().forEach(((k, v) -> keyValue.put(k, v)));
        updateTable();
    }

    @Override
    protected void onCreation() {
        port.setText(engine().defaultPort().map(s -> Integer.toString(s)).orElse(""));
    }

    @Override
    protected DatabaseConnection retrieveResult() {
        DatabaseSocket s = new DatabaseSocket(host.getText(), Integer.parseInt(port.getText()));
        DatabaseCredential c = new DatabaseCredential(username.getText(), password.getText(), database.getText());
        return DatabaseConnection.from(getIdentifier(),  engine(),  s, c, new HashMap<>(0), keyValue);
    }

    @FXML
    void addParameter() {
        if(key.getText() == null || value.getText() == null || key.getText().trim().isEmpty() || value.getText().trim().isEmpty())
            return;
        keyValue.put(key.getText(), value.getText());
        updateTable();
    }

    private void updateTable() {
        list.clear();
        keyValue.forEach((k, v) -> list.add(new Pair<>(k, v)));
    }
}
