package de.hshannover.dqgui.dbsupport.gui.wizard;

import de.hshannover.dqgui.dbsupport.api.DatabaseEnvironment;
import de.hshannover.dqgui.dbsupport.dialogs.DatabaseSupportResourceConfiguration;
import de.hshannover.dqgui.dbsupport.exceptions.NonUniqueIdentifierException;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;
import de.hshannover.dqgui.framework.ErrorUtility;
import de.hshannover.dqgui.framework.wizards.AbstractWizard;

public final class DatabaseUpdateWizard extends AbstractWizard<DatabaseConnection> {
    private final DatabaseEnvironment environment;
    private final DatabaseConnection oldConnection;

    public DatabaseUpdateWizard(DatabaseEnvironment environment, DatabaseConnection update) {
        super(String.format("%s Update Wizard", update.getEngine().name()), DatabaseSupportResourceConfiguration.FAV);
        this.environment = environment;
        this.oldConnection = update;
    }

    @Override
    protected void setup() {
        setScene(SharedLoading.load(oldConnection.getEngine(), getDialogContext(), oldConnection, environment, true, this, () -> cancel()));
    }

    @Override
    protected void finishHook() {
        if(result.equals(oldConnection))
            return;
        try {
            environment.remove(oldConnection);
            environment.add(result);
        } catch (NonUniqueIdentifierException e) {
            throw ErrorUtility.rethrow(e);
        }
    }

    @Override
    protected void cancelHook() {
        return;
    }

}