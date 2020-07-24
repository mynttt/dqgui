package de.hshannover.dqgui.execution.database.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import de.hshannover.dqgui.execution.database.exceptions.JDBCNotSupportedException;

/**
 * Abstracts a database connection. Use this to create a connection for your actual implementation of the engine.<p>
 *
 * {@link DatabaseConnection#getCustomAttributes() customAttributes} are key:value pairs that are required to create the database connection when the default credential and socket fields are not enough. (Example: SQLite and file path)<p>
 * {@link DatabaseConnection#getCustomParameters() customParameters} are key:value mainly for JDBC implementations. (Example: Postgres for has the parameter ssl=true. This can be managed here)<p>
 *
 * It is possible to set socket, credential to null because JDBC implementations like SQLite for example don't use sockets but file paths and do not support passwords / databases / users.<br>
 * In this rare case it is advised to use the {@link DatabaseConnection#getCustomAttributes() customAttributes} HashMap to store attributes like the file path to create a connection.<br>
 * This approach resorts to Strings so some serious documentation should be done above the {@link DatabaseEngine} constant to list all possible attributes.
 *
 * @author Marc Herschel
 *
 */
public final class DatabaseConnection implements Comparable<DatabaseConnection> {
    private static final String UNKNOWN_IDENTIFIER = "NamelessConnection";

    private final UUID guid = UUID.randomUUID();
    private final String identifier;
    private final String engineIdentifier;
    private final DatabaseSocket socket;
    private final DatabaseCredential credential;
    private final Map<String, String> customAttributes = new HashMap<>();
    private final Map<String, String> customParameters = new HashMap<>();
    
    private transient DatabaseEngine engine;

    /**
     * Simple with UNKNOWN_IDENTIFIER set.
     * @param engine to use.
     * @param socket to use.
     * @param credential to use.
     * @return connection.
     */
    public static DatabaseConnection from(DatabaseEngine engine, DatabaseSocket socket, DatabaseCredential credential) {
        return new DatabaseConnection(UNKNOWN_IDENTIFIER, engine, socket, credential, null, null);
    }

    /**
     * Simple with custom identifier.
     * @param identifier iqm4hd referenced identifier (case sensitive).
     * @param engine to use.
     * @param socket to use.
     * @param credential to use.
     * @return connection.
     */
    public static DatabaseConnection from(String identifier, DatabaseEngine engine, DatabaseSocket socket, DatabaseCredential credential) {
        return new DatabaseConnection(identifier, engine, socket, credential, null, null);
    }

    /**
     * Creation for wizards.
     * @param identifier iqm4hd referenced identifier (case insensitive).
     * @param engine to use.
     * @param socket to use.
     * @param credential to use.
     * @param customAttributes of custom attributes.
     * @param customParameters of custom parameters.
     * @return connection.
     */
    public static DatabaseConnection from(String identifier, DatabaseEngine engine, DatabaseSocket socket, DatabaseCredential credential, Map<String, String> customAttributes, Map<String, String> customParameters) {
        return new DatabaseConnection(identifier, engine, socket, credential, customAttributes, customParameters);
    }

    private DatabaseConnection(String identifier, DatabaseEngine engine, DatabaseSocket socket, DatabaseCredential credential, Map<String, String> attributes, Map<String, String> parameters) {
        Objects.requireNonNull(identifier, "DatabaseConnection identifier must not be null");
        Objects.requireNonNull(engine, "DatabaseConnection engine must not be null");
        Objects.requireNonNull(socket, "DatabaseConnection socket must not be null");
        Objects.requireNonNull(credential, "DatabaseConnection credential must not be null (DatabaseCredential can store null values)");
        
        this.identifier = caseSensitivityBehavior(identifier);
        this.engine = engine;
        this.socket = socket;
        this.credential = credential;
        this.engineIdentifier = engine.uniqueIdentifier().toString();
        
        if(attributes != null)
            customAttributes.putAll(attributes);
        
        if(parameters != null)
            customParameters.putAll(parameters);
    }
    
    /**
     * Deserialization post processing hook that aims to recover the associated database engine
     * @return false if recovery fails
     */
    public boolean engineIdentifierIsRegistered() {
        engine = EngineManager.forIdentifier(engineIdentifier);
        return engine != null;
    }
    
    /**
     * Identifier of mapped engine.
     * @return engine identifier
     */
    public String requiredEngineImplementation() {
        return engineIdentifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public DatabaseEngine getEngine() {
        return engine;
    }

    public DatabaseSocket getSocket() {
        return socket;
    }

    public DatabaseCredential getCredential() {
        return credential;
    }
    
    public UUID getGuid() {
        return guid;
    }

    public Map<String, String> getCustomAttributes() {
        return Collections.unmodifiableMap(customAttributes);
    }

    public Map<String, String> getCustomParameters() {
        return Collections.unmodifiableMap(customParameters);
    }

    public boolean supportsJDBC() {
        return engine.supportsJdbc();
    }

    /**
     * @return data source URL for connection if it supports JDBC.
     * @throws JDBCNotSupportedException if it does not support JDBC.
     */
    public String getDataSourceURL() {
        if(!engine.supportsJdbc())
            throw new JDBCNotSupportedException(engine);
        return engine.createDataSourceUrl(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(credential, customAttributes, customParameters, engineIdentifier, guid, identifier, socket);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DatabaseConnection other = (DatabaseConnection) obj;
        return Objects.equals(credential, other.credential) && Objects.equals(customAttributes, other.customAttributes)
                && Objects.equals(customParameters, other.customParameters)
                && Objects.equals(engineIdentifier, other.engineIdentifier) && Objects.equals(guid, other.guid)
                && Objects.equals(identifier, other.identifier) && Objects.equals(socket, other.socket);
    }

    @Override
    public int compareTo(DatabaseConnection other) {
        if(other == null) return 1;
        return identifier.compareTo(other.getIdentifier());
    }

    @Override
    public String toString() {
        return "DatabaseConnection [identifier=" + identifier + ", engine=" + engine + ", socket=" + socket
                + ", credential=" + credential + ", customAttributes=" + customAttributes + ", customParameters="
                + customParameters + "]";
    }

    private String caseSensitivityBehavior(String identifier) {
        identifier = identifier == null ? UNKNOWN_IDENTIFIER : (identifier.trim().isEmpty() ? UNKNOWN_IDENTIFIER : identifier);
        return identifier;
    }
}
