package de.hshannover.dqgui.execution.database.exceptions;

import de.hshannover.dqgui.execution.database.api.DatabaseEngine;

/**
 * In case an {@link DatabaseEngine} does not support JDBC but has been called a JDBC operation on.
 *
 * @author Marc Herschel
 *
 */
public class JDBCNotSupportedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public JDBCNotSupportedException(DatabaseEngine database) {
        super(String.format("Engine does not support JDBC! [databaseSystem=%s, language=%s]", database, database.language()));
    }
}