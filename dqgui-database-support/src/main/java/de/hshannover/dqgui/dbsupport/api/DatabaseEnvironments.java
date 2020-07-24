package de.hshannover.dqgui.dbsupport.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.tinylog.Logger;
import de.hshannover.dqgui.dbsupport.exceptions.NonUniqueIdentifierException;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;
import de.hshannover.dqgui.framework.api.Recoverable;
import de.hshannover.dqgui.framework.signal.Signal;

/**
 * A collection of {@link DatabaseEnvironment} with the constraint of {@link DatabaseEnvironment#getIdentifier()} being unique.
 *
 * @author Marc Herschel
 *
 */
public final class DatabaseEnvironments implements Recoverable {
    
    @Override
    public void recoverHook() {
        for(DatabaseEnvironment env : stored) {
            Set<DatabaseConnection> remove = env.getConnections().stream().filter(c -> !c.engineIdentifierIsRegistered()).collect(Collectors.toSet());
            if(!remove.isEmpty()) {
                for(DatabaseConnection r : remove)
                    Logger.warn("Removing connection {} from environment {} because the associated engine was not found.", r.getIdentifier(), env.getIdentifier());
                env.remove(remove);
            }
            env.registerParent(this).forEach(this::handleAddition);
        }
    }

    /**
     * Default identifier for all DatabaseEnvironments
     */
    public static final String DEFAULT_ENVIRONMENT = "default";

    private final transient Signal contentUpdateSignal = new Signal();
    private final transient Map<String, Integer> identifierRefCount = new HashMap<>();
    private final transient Set<String> databaseIdentifiers = new HashSet<>();
    private final HashSet<String> identifiers = new HashSet<>();
    private final Set<DatabaseEnvironment> stored = new HashSet<>();
    private final DatabaseEnvironment defaultEnvironment;

    /**
     * Construct a new DatabaseEnvironments with the {@link DatabaseEnvironments#DEFAULT_ENVIRONMENT default identifier}.
     */
    public DatabaseEnvironments() {
        identifiers.add(DEFAULT_ENVIRONMENT);
        defaultEnvironment = new DatabaseEnvironment(DEFAULT_ENVIRONMENT);
        stored.add(defaultEnvironment);
    }

    /**
     * @return amount of environments stored
     */
    public int size() {
        return stored.size();
    }

    /**
     * Check if an identifier already exists.
     * @param identifier to check for
     * @return true if exists
     */
    public synchronized boolean identifierExists(String identifier) {
        return identifiers.contains(identifier);
    }

    /**
     * Add environments to the super set.
     * @param environments to add.
     * @throws NonUniqueIdentifierException if identifier already exists.
     */
    public void add(Set<DatabaseEnvironment> environments) throws NonUniqueIdentifierException {
        for(DatabaseEnvironment e : environments) {
            add(e);
        }
    }

    /**
     * Add an environment to the super set.
     * @param environment to add
     * @throws NonUniqueIdentifierException if identifier already exists
     */
    public synchronized void add(DatabaseEnvironment environment) throws NonUniqueIdentifierException {
        if(environment.getIdentifier().equals(DEFAULT_ENVIRONMENT)) {
            defaultEnvironment.add(environment.getConnections());
            return;
        }
        if(identifiers.contains(environment.getIdentifier()))
                throw new NonUniqueIdentifierException(this, environment.getIdentifier());
        identifiers.add(environment.getIdentifier());
        stored.add(environment);
        environment.registerParent(this).forEach(this::handleAddition);
        contentUpdateSignal.fire();
    }

    /**
     * Remove an environments from the super set.
     * @param environments to remove
     * @throws IllegalArgumentException if trying to remove the default environment
     */
    public void remove(Set<DatabaseEnvironment> environments) {
        environments.forEach(this::remove);
    }

    /**
     * Remove an environment from the super set.
     * @param environment to remove
     * @throws IllegalArgumentException if trying to remove the default environment
     * @throws NullPointerException if null
     */
    public synchronized void remove(DatabaseEnvironment environment) {
        if(environment == null)
            throw new NullPointerException("Environment should not be null");
        if(environment.getIdentifier().equals(DEFAULT_ENVIRONMENT))
            throw new IllegalArgumentException("You are not allowed to delete the default environment.");
        identifiers.remove(environment.getIdentifier());
        stored.remove(environment);
        environment.unregisterParent(this).forEach(this::handleRemoval);
        contentUpdateSignal.fire();
    }

    /**
     * Remove by identifier.
     * @param identifier to remove for
     * @throws NoSuchElementException if not found
     */
    public void remove(String identifier) {
        List<DatabaseEnvironment> l = stored.stream().filter(e -> e.getIdentifier().equals(identifier)).collect(Collectors.toList());
        if(l.isEmpty()) { throw new NoSuchElementException(); }
        remove(l.get(0));
    }

    /**
     * @return sorted, unmodifiable copy of all contained {@link DatabaseEnvironment}
     */
    public synchronized List<DatabaseEnvironment> getEnvironments() {
        ArrayList<DatabaseEnvironment> e = new ArrayList<>(stored);
        Collections.sort(e);
        return Collections.unmodifiableList(e);
    }

    /**
     * @return sorted, unmodifiable copy of all contained {@link DatabaseEnvironment#getIdentifier() DatabaseEnvironment identifiers}
     */
    public synchronized List<String> getEnvironmentIdentifiers() {
        ArrayList<String> list = new ArrayList<>(identifiers);
        Collections.sort(list);
        return list;
    }

    /**
     * Rename an environment within the super set.
     * @param toRename environment
     * @param newName new name of it
     * @throws NonUniqueIdentifierException if the new identifier already exists in the super set
     * @throws IllegalArgumentException if the new identifier is null or empty
     */
    public synchronized void rename(DatabaseEnvironment toRename, String newName) throws NonUniqueIdentifierException {
        if(toRename.getIdentifier().equals(newName))
            return;
        if(toRename.getIdentifier().equals(DEFAULT_ENVIRONMENT))
            throw new IllegalArgumentException("You are not allowed to rename the default environment.");
        if(identifiers.contains(newName))
            throw new NonUniqueIdentifierException(this, newName);
        if(newName == null || newName.trim().isEmpty())
            throw new IllegalArgumentException("Identifier must be not null/empty.");
        stored.remove(toRename);
        toRename.unregisterParent(this).forEach(this::handleRemoval);
        DatabaseEnvironment newEnv = new DatabaseEnvironment(newName, toRename.getConnections());
        newEnv.registerParent(this).forEach(this::handleAddition);
        stored.add(newEnv);
        identifiers.remove(toRename.getIdentifier());
        identifiers.add(newName);
        contentUpdateSignal.fire();
    }

    /**
     * Get an environment by identifier.
     * @param identifier to search for
     * @return environment if existing
     * @throws NoSuchElementException if not in super set
     */
    public synchronized DatabaseEnvironment getEnvironment(String identifier) {
        if(identifier == null)
            throw new IllegalArgumentException("Identifier must not be null");
        for(DatabaseEnvironment d : stored) {
            if(identifier.equals(d.getIdentifier()))
                return d;
        }
        throw new NoSuchElementException(String.format("Identifier %s not found.", identifier));
    }

    /**
     * Get an environment by connection.
     * @param connection to search for.
     * @return environment if existing.
     * @throws NoSuchElementException if not in super set.
     */
    public synchronized DatabaseEnvironment getEnvironment(DatabaseConnection connection) {
        for(DatabaseEnvironment d : stored) {
            for(DatabaseConnection c : d.getConnections()) {
                if(c.equals(connection))
                    return d;
            }
        }
        throw new NoSuchElementException(String.format("Connection %s not found in Environments.", connection.getIdentifier()));
    }
    
    /**
     * Get a set of all contained connection identifiers
     * @return set of all connection identifiers within all environments
     */
    public synchronized Set<String> getAllContainedConnectionIdentifiers() {
        return Collections.unmodifiableSet(databaseIdentifiers);
    }
    
    /**
     * Search for the requested GUID.
     * @param guid to lookup for
     * @return optional of the found connection
     */
    public synchronized Optional<DatabaseConnection> lookupGuid(UUID guid) {
        return stored.stream()
                .map(e -> e.lookupGuid(guid))
                .filter(e -> e.isPresent())
                .map(Optional::get)
                .findAny();
    }

    public Signal getContentUpdateSignal() {
        return contentUpdateSignal;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((defaultEnvironment == null) ? 0 : defaultEnvironment.hashCode());
        result = prime * result + ((identifiers == null) ? 0 : identifiers.hashCode());
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
        DatabaseEnvironments other = (DatabaseEnvironments) obj;
        if (defaultEnvironment == null) {
            if (other.defaultEnvironment != null)
                return false;
        } else if (!defaultEnvironment.equals(other.defaultEnvironment))
            return false;
        if (identifiers == null) {
            if (other.identifiers != null)
                return false;
        } else if (!identifiers.equals(other.identifiers))
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
        return "DatabaseEnvironments [identifiers=" + identifiers + ", stored=" + stored + ", defaultEnvironment="
                + defaultEnvironment + "]";
    }

    void handleRemoval(DatabaseConnection connection) {
        Integer i = identifierRefCount.get(connection.getIdentifier());
        if(i == null || i == 0) return;
        if(i == 1) {
            databaseIdentifiers.remove(connection.getIdentifier());
            identifierRefCount.remove(connection.getIdentifier());
        } else {
            identifierRefCount.compute(connection.getIdentifier(), (k, v) -> v - 1);
        }
    }

    void handleAddition(DatabaseConnection connection) {
        if(identifierRefCount.containsKey(connection.getIdentifier())) {
            identifierRefCount.compute(connection.getIdentifier(), (k, v) -> v + 1);
        } else {
            databaseIdentifiers.add(connection.getIdentifier());
            identifierRefCount.put(connection.getIdentifier(), 0);
        }
    }
    
    void fireChildUpdate() {
        contentUpdateSignal.fire();
    }
}
