package de.hshannover.dqgui.dbsupport.gui.engines;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import de.hshannover.dqgui.dbsupport.gui.AbstractEngineUpdateCreate;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;
import de.hshannover.dqgui.execution.database.api.DatabaseCredential;
import de.hshannover.dqgui.execution.database.api.DatabaseSocket;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Pair;

public class EngineFallback extends AbstractEngineUpdateCreate {
    private Map<String, String> attributes = new HashMap<>(), parameters = new HashMap<>();
    private ObservableList<Pair<String, String>> listAtt = FXCollections.observableArrayList(), listParam = FXCollections.observableArrayList();

    @FXML
    TextField database, host, port, username, password, key, value;
    @FXML
    Label engineLabel;
    @FXML
    Tab attributeTab, parameterTab;
    @FXML
    TableView<Pair<String, String>> tableAttributes, tableConnection;
    @FXML
    TabPane tabs;

    @Override
    protected void onInitialize() {
        engineLabel.setText("Fallback for " + engine().name());
        prepareTable(tableAttributes, listAtt);
        prepareTable(tableConnection, listParam);
        tableConnection.setPlaceholder(new Label("No parameters specified"));
        tableAttributes.setPlaceholder(new Label("No attributes specified"));
        tabs.getSelectionModel().select(0);
    }

    @Override
    protected void onUpdate() {
        port.setText(Integer.toString(oldConnection().getSocket().getPort()));
        host.setText(oldConnection().getSocket().getHost());
        username.setText(oldConnection().getCredential().getUsername());
        password.setText(oldConnection().getCredential().getPassword());
        database.setText(oldConnection().getCredential().getDatabase());
        oldConnection().getCustomParameters().forEach(((k, v) -> parameters.put(k, v)));
        oldConnection().getCustomAttributes().forEach(((k, v) -> attributes.put(k, v)));
        updateTable(tableAttributes, attributes, listAtt);
        updateTable(tableConnection, parameters, listParam);
    }

    @Override
    protected void onCreation() {
        port.setText(engine().defaultPort().map(s -> Integer.toString(s)).orElse(""));
    }

    @Override
    protected DatabaseConnection retrieveResult() {
        String portText = port.getText() == null || port.getText().trim().isEmpty() ? "-1" : port.getText();
        DatabaseSocket s = new DatabaseSocket(host.getText(), Integer.parseInt(portText));
        DatabaseCredential c = new DatabaseCredential(username.getText(), password.getText(), database.getText());
        return DatabaseConnection.from(getIdentifier(),  engine(),  s, c, attributes, parameters);
    }

    @FXML
    void addParameter() {
        if(key.getText() == null || value.getText() == null || key.getText().trim().isEmpty() || value.getText().trim().isEmpty())
            return;
        Tab selected = tabs.getSelectionModel().getSelectedItem();
        if(selected == attributeTab)
            addParameter(tableAttributes, attributes, listAtt);
        if(selected == parameterTab)
            addParameter(tableConnection, parameters, listParam);
        key.clear();
        value.clear();
    }

    private void addParameter(TableView<Pair<String, String>> table, Map<String, String> content, ObservableList<Pair<String, String>> list) {
        content.put(key.getText(), value.getText());
        updateTable(table, content, list);
    }

    private void prepareTable(TableView<Pair<String, String>> table, ObservableList<Pair<String, String>> list) {
        MenuItem mi1 = new MenuItem("Remove");
        table.setItems(new SortedList<>(list, Comparator.comparing(Pair::getKey)));
        table.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("key"));
        table.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("value"));
        mi1.setOnAction(e -> {
            Tab selected = tabs.getSelectionModel().getSelectedItem();
            if(selected == attributeTab) {
                attributes.remove(tableAttributes.getSelectionModel().getSelectedItem().getKey());
                updateTable(tableAttributes, attributes, list);
            }
            if(selected == parameterTab) {
                parameters.remove(tableConnection.getSelectionModel().getSelectedItem().getKey());
                updateTable(tableConnection, parameters, list);
            }
        });
        ContextMenu m = new ContextMenu(mi1);
        table.setContextMenu(m);
    }

    private void updateTable(TableView<Pair<String, String>> table, Map<String, String> content, ObservableList<Pair<String, String>> list) {
        list.clear();
        content.forEach((k, v) -> list.add(new Pair<>(k, v)));
    }

}
