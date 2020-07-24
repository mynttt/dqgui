package de.hshannover.dqgui.execution.database;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;
import org.tinylog.Logger;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;
import de.hshannover.dqgui.execution.database.api.DatabaseFetcher;
import de.mvise.iqm4hd.api.DatabaseEntryIterator;
import de.mvise.iqm4hd.api.DatabaseService;
import de.mvise.iqm4hd.core.exception.types.Iqm4hdException;

/**
 * DQGUI Database Service Implementation.
 *
 * @author Marc Herschel
 *
 */
public final class DqguiDatabaseService implements DatabaseService, AutoCloseable {
    private final HashMap<String, DatabaseConnection> identifierConnectionMap = new HashMap<>();
    private final HashMap<String, DatabaseFetcher> fetchers = new HashMap<>();
    private final HashMap<String, TargetedDatabase> calledDatabases = new HashMap<>();

    /**
     * Create a new DQGUI Database Service
     * @param connections to use within the service
     */
    public DqguiDatabaseService(Set<DatabaseConnection> connections) {
        connections.forEach(c -> identifierConnectionMap.put(c.getIdentifier(), c));
    }

    /**
     * @return all identifiers and their mapped connection that were called by the service
     */
    public Collection<TargetedDatabase> getCalledDatabases() {
        return Collections.unmodifiableCollection(calledDatabases.values());
    }

    @Override
    public DatabaseEntryIterator getEntryListOfSource(String identifier, String query) throws Iqm4hdException {
        if(fetchers.containsKey(identifier))
            return fetchers.get(identifier).fetch(query);
        if(!identifierConnectionMap.containsKey(identifier)) {
            throw new NoSuchElementException("No connection with identifier '" + identifier + "' within the DatabaseService.");
        }
        DatabaseFetcher fetcher = identifierConnectionMap.get(identifier).getEngine().createFetcher(identifierConnectionMap.get(identifier));
        fetcher.initiate();
        fetchers.put(identifier, fetcher);
        calledDatabases.put(identifier, new TargetedDatabase(identifierConnectionMap.get(identifier), query));
        return fetcher.fetch(query);
    }

    @Override
    public String getQueryLanguage(String identifier) {
        if(!identifierConnectionMap.containsKey(identifier)) {
            throw new NoSuchElementException("No connection with identifier: " + identifier + " inside the DatabaseService.");
        }
        return identifierConnectionMap.get(identifier).getEngine().language().toString();
    }

    @Override
    public void close() {
        fetchers.values().forEach(v -> {
            try {
                v.close();
            } catch(Exception e) {
                Logger.warn(String.format("Possible Resource Leak: ShutdownHook for %s with identifier %s threw %s.", v.getConnection().getEngine(), v.getConnection().getIdentifier(), e.getClass().getSimpleName()));
                Logger.error(e);
            }
        });
    }

}
