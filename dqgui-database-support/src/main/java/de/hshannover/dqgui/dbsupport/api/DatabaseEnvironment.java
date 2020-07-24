package de.hshannover.dqgui.dbsupport.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import de.hshannover.dqgui.dbsupport.exceptions.NonUniqueIdentifierException;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;

/**
 * A collection of {@link DatabaseConnection} with the constraint of {@link DatabaseConnection#getIdentifier()} being unique.
 *
 * @author Marc Herschel
 *
 */
public final class DatabaseEnvironment implements Comparable<DatabaseEnvironment> {
    private final String identifier;
    private final Set<String> connectionIdentifiers = new HashSet<>();
    private final Set<DatabaseConnection> stored = new TreeSet<>();
    private final transient List<DatabaseEnvironments> parents = new ArrayList<>();

    /**
     * Construct an empty environment with the identifier <i>"unidentified"</i>.
     */
    public DatabaseEnvironment() {
        this.identifier = "unidentified";
    }

    /**
     * Construct an empty environment with a given identifier.
     * @param identifier to name the environment after
     * @throws IllegalArgumentException if identifier is null or empty
     */
    public DatabaseEnvironment(String identifier) {
        checkIdentifier(identifier);
        this.identifier = identifier;
    }

    /**
     * Construct an environment with a given identifier and connections.
     * @param identifier to name the environment after
     * @param connections to add to the environment.
     * @throws IllegalArgumentException if identifier is null or empty
     * @throws NonUniqueIdentifierException if the identifier of a connection is not unique
     */
    public DatabaseEnvironment(String identifier, Set<DatabaseConnection> connections) throws NonUniqueIdentifierException {
        checkIdentifier(identifier);
        this.identifier = identifier;
        add(connections);
    }

    /**
     * Check if an identifier already exists.
     * @param identifier to check for
     * @return true if exists
     */
    public synchronized boolean identifierExists(String identifier) {
        return connectionIdentifiers.contains(identifier);
    }

    /**
     * @return size of environment
     */
    public int size() {
        return stored.size();
    }

    /**
     * Clear the environment and all contained connections
     */
    public synchronized void clear() {
        connectionIdentifiers.clear();
        stored.clear();
        parents.forEach(DatabaseEnvironments::fireChildUpdate);
    }

    /**
     * @param connections to remove from environment
     */
    public synchronized void remove(Set<DatabaseConnection> connections) {
        connections.forEach(this::removeInternal);
        parents.forEach(DatabaseEnvironments::fireChildUpdate);
    }

    /**
     * @param connection to remove from environment
     */
    public synchronized void remove(DatabaseConnection connection) {
        removeInternal(connection);
        parents.forEach(DatabaseEnvironments::fireChildUpdate);
    }

    private void removeInternal(DatabaseConnection connection) {
        stored.remove(connection);
        connectionIdentifiers.remove(connection.getIdentifier());
        parents.forEach(p -> p.handleRemoval(connection));
    }

    /**
     * Add connections to the environment.
     * @param connections to add.
     * @throws NonUniqueIdentifierException if identifier already exists.
     */
    public synchronized void add(Set<DatabaseConnection> connections) throws NonUniqueIdentifierException {
        for(DatabaseConnection c : connections) {
            addInternal(c);
        }
        parents.forEach(DatabaseEnvironments::fireChildUpdate);
    }

    /**
     * Add a connection to the environment.
     * @param connection to add.
     * @throws NonUniqueIdentifierException if identifier already exists.
     */
    public synchronized void add(DatabaseConnection connection) throws NonUniqueIdentifierException {
        addInternal(connection);
        parents.forEach(DatabaseEnvironments::fireChildUpdate);
    }

    private void addInternal(DatabaseConnection connection) throws NonUniqueIdentifierException {
        if(connectionIdentifiers.contains(connection.getIdentifier()))
            throw new NonUniqueIdentifierException(connection.getIdentifier());
        connectionIdentifiers.add(connection.getIdentifier());
        stored.add(connection);
        parents.forEach(p -> p.handleAddition(connection));
    }

    /**
     * @return an immutable copy of the currently contained {@link DatabaseConnection}
     */
    public synchronized Set<DatabaseConnection> getConnections() {
        Set<DatabaseConnection> s = new TreeSet<>();
        s.addAll(stored);
        return Collections.unmodifiableSet(s);
    }
    
    /**
     * Search for the requested GUID.
     * @param guid to lookup for
     * @return optional of the found connection
     */
    public synchronized Optional<DatabaseConnection> lookupGuid(UUID guid) {
        return stored.stream().filter(c -> c.getGuid().equals(guid)).findAny();
    }

    /**
     * @return identifier of environment
     */
    public synchronized String getIdentifier() {
        return identifier;
    }

    private void checkIdentifier(String s) {
        if(s == null || s.trim().isEmpty())
            throw new IllegalArgumentException("Identifier must be not null/empty.");
    }

    @Override
    public int compareTo(DatabaseEnvironment o) {
        return identifier.compareTo(o.identifier);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + ((stored == null) ? 0 : stored.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DatabaseEnvironment other = (DatabaseEnvironment) obj;
        if (identifier == null) {
            if (other.identifier != null)
                return false;
        } else if (!identifier.equals(other.identifier))
            return false;
        if (stored == null) {
            if (other.stored != null)
                return false;
        } else if (!stored.equals(other.stored))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DatabaseEnvironment [identifier=" + identifier + ", connectionIdentifiers=" + connectionIdentifiers
                + ", stored=" + stored + "]";
    }

    Set<DatabaseConnection> registerParent(DatabaseEnvironments databaseEnvironments) {
        parents.add(databaseEnvironments);
        return stored;
    }

    Set<DatabaseConnection> unregisterParent(DatabaseEnvironments environments) {
        parents.remove(environments);
        return stored;
    }
}
