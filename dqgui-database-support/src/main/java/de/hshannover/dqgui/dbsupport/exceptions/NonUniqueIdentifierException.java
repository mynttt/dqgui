package de.hshannover.dqgui.dbsupport.exceptions;

import de.hshannover.dqgui.dbsupport.api.DatabaseEnvironments;

/**
 * In case an identifier already exists.
 *
 * @author Marc Herschel
 *
 */
public final class NonUniqueIdentifierException extends Exception {
    private static final long serialVersionUID = 1L;

    public NonUniqueIdentifierException(String identifier) {
        super(String.format("Identifier: %s is not unique and already exists within the Database Environment!", identifier));
    }

    public NonUniqueIdentifierException(DatabaseEnvironments d, String identifier) {
        super(String.format("Identifier: %s is not unique and already exists within the Database Environments!", identifier));
    }
}