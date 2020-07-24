package de.hshannover.dqgui.execution.database.api;

import de.mvise.iqm4hd.api.DatabaseEntryIterator;

/**
 * Database Fetcher Interface.
 *
 * @author Marc Herschel
 *
 */
public abstract class DatabaseFetcher implements AutoCloseable {
    protected final DatabaseConnection connectionData;

    public DatabaseFetcher(DatabaseConnection connection) {
        this.connectionData = connection;
    }

    /**
     * @return connection of fetcher
     */
    public final DatabaseConnection getConnection() {
        return connectionData;
    }

    /**
     * Creates and stores the database connection.
     */
    public abstract void initiate();

    /**
     * Create Iterator for IQM4HD.
     * @param query to execute on driver.
     * @return IQM4HD compatible iterator.
     */
    public abstract DatabaseEntryIterator fetch(String query);
}