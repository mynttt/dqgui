package de.hshannover.dqgui.dbsupport.gui;

import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.controlsfx.validation.ValidationMessage;
import de.hshannover.dqgui.dbsupport.api.DatabaseEnvironment;
import de.hshannover.dqgui.dbsupport.gui.wizard.DatabaseCreationWizard;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;
import de.hshannover.dqgui.execution.database.api.DatabaseEngine;
import de.hshannover.dqgui.execution.database.api.DatabaseTests.DatabaseTestResult;
import de.hshannover.dqgui.framework.wizards.AbstractWizard;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller that allows to implement update/create operations for database connections within the GUI.<p>
 *
 * @author Marc Herschel
 *
 */
public abstract class AbstractEngineUpdateCreate {    
    private DatabaseConnection oldConnection;
    private DatabaseEngine forEngine;
    private DatabaseEnvironment environment;
    private AbstractWizard<DatabaseConnection> wizard;
    private boolean isUpdate, decided;
    @FXML private Button backOrCancel, finishOrSave;
    @FXML private Label engineLabel, validation;
    @FXML private TextField identifier;
    
    private final ValidationSupport validationSupport = new ValidationSupport();
    
    private final ChangeListener<Boolean> colorListener = (obs, o, n) -> validation.setId(n ? "validation-invalid" : "validation-valid");
    private final ChangeListener<ValidationResult> errorListener = (obs, o, n) -> validation.setText(n.getErrors()
            .stream()
            .findFirst()
            .map(ValidationMessage::getText)
            .orElse("Validation schema passed."));
    
    /**
     * Ensure nobody can hook into JavaFX's initialize method.
     */
    @FXML
    protected final void initialize() {}

    final void inject(DatabaseConnection oldConnection, DatabaseEngine forEngine, DatabaseEnvironment environment) {
        this.oldConnection = oldConnection;
        this.forEngine = forEngine;
        this.environment = environment;
    }

    final void initialize(boolean isUpdate, AbstractWizard<DatabaseConnection> wizard) {
        validationSupport.setErrorDecorationEnabled(false);
        validationSupport.setValidationDecorator(null);
        
        if(decided)
            return;
        this.wizard = wizard;
        this.isUpdate = isUpdate;
        if(wizard == null)
            throw new IllegalStateException("Preconditions failed for controller: wizard is null.");
        if(isUpdate && (oldConnection == null || environment == null))
            throw new IllegalStateException("Preconditions failed for update: oldConnection or environment is null.");
        if(!isUpdate && (forEngine == null || environment == null))
            throw new IllegalStateException("Preconditions failed for creation: forEngine or environment is null.");
        finishOrSave.setText(isUpdate ? "Save" : "Finish");
        backOrCancel.setText(isUpdate ? "Cancel" : "Back");
        validationSupport.invalidProperty().addListener(colorListener);
        validationSupport.validationResultProperty().addListener(errorListener);
        wizard.registerCloseHook(() -> validationSupport.invalidProperty().removeListener(colorListener));
        wizard.registerCloseHook(() -> validationSupport.validationResultProperty().removeListener(errorListener));
        finishOrSave.disableProperty().bind(validationSupport.invalidProperty());
        validationSupport.registerValidator(identifier, Validator.combine(
                Validator.createEmptyValidator("Identifier is not set."),
                Validator.createPredicateValidator(p -> isValidIdentifier(), "Identifier already exists in selected environment.")));
        engineLabel.setText("Configure " + forEngine.name());
        identifier.setText(isUpdate ? oldConnection().getIdentifier() : "");
        onInitialize();
        if(isUpdate) {
            onUpdate();
        } else {
            onCreation();
        }
        
        decided = true;
    }
    
    private final boolean isValidIdentifier() {
        return !(!isUpdate && environment.identifierExists(identifier.getText()) 
                || (isUpdate  && environment.identifierExists(identifier.getText()) 
                        && !identifier.getText().equals(oldConnection.getIdentifier())));
    }

    @FXML
    final void finishOrSave() {
        if(validationSupport.isInvalid())
            return;
        wizard.finish(retrieveResult());
    }

    @FXML
    final void backOrCancel() {
        if(isUpdate) {
            wizard.cancel();
        } else {
            ((DatabaseCreationWizard) wizard).backToFirstPage();
        }
    }

    @FXML
    final void testConnection() {
        if(!validationSupport.isInvalid()) {
            DatabaseConnection con = retrieveResult();
            DatabaseTestResult r = con.getEngine().test(con);
            if(r.isSuccess()) {
                wizard.getDialogContext().information("Test succeeded!", r.getMessage());
            } else {
                wizard.getDialogContext().textErrorDialog("Test failed", r.getExceptionName(), r.getMessage());
            }
        }
    }
    
    /**
     * Check if a port is valid
     * @param port to verify
     * @return true if valid port
     */
    protected final boolean verifyPort(String port) {
        if(port == null) return false;
        try {
            int i = Integer.parseInt(port);
            return i >= 0 && i <= 65535;
        } catch(NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * @param closeHook to register
     */
    protected final void registerWizardCloseHook(Runnable closeHook) {
        wizard.registerCloseHook(closeHook);
    }
    
    /**
     * @return validation support
     */
    protected final ValidationSupport getValidationSupport() {
        return validationSupport;
    }
    
    /**
     * @return text of identifier field
     */
    protected final String getIdentifier() {
        return identifier.getText().trim();
    }

    /**
     * @return currently set old connection
     * @throws IllegalArgumentException if called and not in update mode
     */
    protected final DatabaseConnection oldConnection() {
        if(!isUpdate)
            throw new IllegalArgumentException("Call only acceptable if in update mode.");
        return oldConnection;
    }

    /**
     * @return selected engine
     */
    protected final DatabaseEngine engine() {
        return forEngine;
    }

    /**
     * @return selected environment
     */
    protected final DatabaseEnvironment environment() {
        return environment;
    }

    /**
     * @return are we updating or creating?
     */
    protected final boolean isUpdate() {
        return isUpdate;
    }

    /**
     * Will always be called before {@link #onUpdate()} and {@link #onCreation()} and allows you to execute shared operations between the two.
     */
    protected abstract void onInitialize();
    
    /**
     * Will be called if your connection is being updated.<br>
     * That means you can populate your fields with the already existing data.
     */
    protected abstract void onUpdate();

    /**
     * Will be called if your connection is being created.
     */
    protected abstract void onCreation();

    /**
     * Fetch the fields the user entered and return the connection from it
     * @return a connection fetched from the latest user input
     */
    protected abstract DatabaseConnection retrieveResult();
}
