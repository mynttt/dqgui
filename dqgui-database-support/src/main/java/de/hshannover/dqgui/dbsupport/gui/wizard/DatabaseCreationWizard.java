package de.hshannover.dqgui.dbsupport.gui.wizard;

import de.hshannover.dqgui.dbsupport.api.DatabaseEnvironment;
import de.hshannover.dqgui.dbsupport.api.DatabaseEnvironments;
import de.hshannover.dqgui.dbsupport.dialogs.DatabaseSupportResourceConfiguration;
import de.hshannover.dqgui.dbsupport.exceptions.NonUniqueIdentifierException;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;
import de.hshannover.dqgui.execution.database.api.DatabaseEngine;
import de.hshannover.dqgui.framework.ErrorUtility;
import de.hshannover.dqgui.framework.FXMLLoadingFactory;
import de.hshannover.dqgui.framework.FXMLLoadingFactory.WindowContent;
import de.hshannover.dqgui.framework.wizards.AbstractWizard;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;

public final class DatabaseCreationWizard extends AbstractWizard<DatabaseConnection> {
    private final DatabaseEnvironments environments;
    private DatabaseEnvironment selectedEnvironment;
    private DatabaseEngine loadedFor;
    private Scene main, specific;

    public DatabaseCreationWizard(DatabaseEnvironments environments) {
        super("Database Connection Wizard", DatabaseSupportResourceConfiguration.FAV);
        this.environments = environments;
    }

    public DatabaseCreationWizard(DatabaseEnvironments environments, DatabaseEnvironment selectedEnvironment) {
        super("Database Connection Wizard", DatabaseSupportResourceConfiguration.FAV);
        this.environments = environments;
        this.selectedEnvironment = selectedEnvironment;
    }

    /**
     * Create and store first page of wizard.
     */
    @Override
    protected void setup() {
        WindowContent obj = FXMLLoadingFactory.load("/dqgui/dbsupport/fxml/DatabaseCreationFirstPage.fxml", DatabaseSupportResourceConfiguration.CSS);
        obj.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE), () -> cancel());
        main = obj.getScene();
        DatabaseCreationFirstPage controller = (DatabaseCreationFirstPage) obj.getController();
        controller.injectCreatorWizard(this);
        controller.addDependencies(environments);
        controller.init(selectedEnvironment);
        setScene(main);
    }

    /**
     * Create and store second page (Database Connection Fetcher).
     * @param engine to store
     */
    public void loadForEngine(DatabaseEngine engine) {
        if(loadedFor == engine)
            return;
        loadedFor = engine;
        specific = SharedLoading.load(engine, getDialogContext(), null, selectedEnvironment, false, this, () -> cancel());
        setScene(specific);
    }

    public void setEnvironment(DatabaseEnvironment environment) {
        this.selectedEnvironment = environment;
    }

    public void showDatabaseCredentialPage() {
        setScene(specific);
    }

    public void backToFirstPage() {
        setScene(main);
    }

    @Override
    protected void finishHook() {
        try {
            selectedEnvironment.add(result);
        } catch (NonUniqueIdentifierException e) {
            throw ErrorUtility.rethrow(e);
        }
    }

    @Override
    protected void cancelHook() {
        return;
    }

}
