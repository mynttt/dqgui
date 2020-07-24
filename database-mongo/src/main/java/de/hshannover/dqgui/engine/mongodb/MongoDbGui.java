package de.hshannover.dqgui.engine.mongodb;

import java.util.Collections;
import java.util.Map;
import org.controlsfx.validation.Validator;
import de.hshannover.dqgui.dbsupport.gui.AbstractEngineUpdateCreate;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;
import de.hshannover.dqgui.execution.database.api.DatabaseCredential;
import de.hshannover.dqgui.execution.database.api.DatabaseSocket;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

/**
 * Wizard for MongoDB
 *
 * @author Marc Herschel
 *
 */
public final class MongoDbGui extends AbstractEngineUpdateCreate {
    @FXML
    TextField host, port, database, username, key, value;
    @FXML
    PasswordField password;
    @FXML
    Button backOrCancel, finishOrSave;
    @FXML
    ToggleButton authToggle;
    
    private final ChangeListener<Boolean> authListener = (obs, o, n) -> {
        authToggle.setId(n ? "auth-disable" : "auth-enable");
        password.setDisable(!n);
        username.setDisable(!n);
        if(!n) {
            password.clear();
            username.clear();
        } else {
            password.setText("user");
        }
    };

    @Override
    protected void onInitialize() {
        getValidationSupport().registerValidator(database, Validator.createEmptyValidator("Database must be set."));
        getValidationSupport().registerValidator(host, Validator.createEmptyValidator("Host must be set."));
        getValidationSupport().registerValidator(port, Validator.createPredicateValidator(p -> verifyPort(port.getText()), "Port must be int 0 <= port <= 65535."));
        getValidationSupport().registerValidator(username, Validator.createPredicateValidator(p -> verifyUsernameAndPassword(), "Username/Password must be set."));
        getValidationSupport().registerValidator(password, Validator.createPredicateValidator(p -> verifyUsernameAndPassword(), "Username/Password must be set."));
        registerWizardCloseHook(() -> authToggle.selectedProperty().removeListener(authListener));
        authToggle.selectedProperty().addListener(authListener);
    }
    
    private boolean verifyUsernameAndPassword() {
        if(!authToggle.isSelected()) return true;
        return username.getText() != null 
                && !username.getText().trim().isEmpty()
                && password.getText() != null 
                && !password.getText().trim().isEmpty();
    }

    @Override
    protected void onUpdate() {
        boolean isProtected = oldConnection().getCredential().getPassword() != null && oldConnection().getCredential().getUsername() != null;
        authToggle.setSelected(isProtected);
        if(isProtected) {
            password.setText(oldConnection().getCredential().getPassword());
            username.setText(oldConnection().getCredential().getUsername());
        }
        port.setText(Integer.toString(oldConnection().getSocket().getPort()));
        host.setText(oldConnection().getSocket().getHost());
        database.setText(oldConnection().getCredential().getDatabase());
    }

    @Override
    protected void onCreation() {
        port.setText(engine().defaultPort().map(s -> Integer.toString(s)).orElse(""));
        authToggle.setSelected(false);
    }

    @Override
    protected DatabaseConnection retrieveResult() {
        Map<String, String> dummy = Collections.emptyMap();
        DatabaseSocket s = new DatabaseSocket(host.getText(), Integer.parseInt(port.getText()));
        DatabaseCredential c = new DatabaseCredential(authToggle.isSelected() ? username.getText() : null, authToggle.isSelected() ? password.getText() : null, database.getText());
        return DatabaseConnection.from(getIdentifier(),  engine(),  s, c, dummy, dummy);
    }
}
