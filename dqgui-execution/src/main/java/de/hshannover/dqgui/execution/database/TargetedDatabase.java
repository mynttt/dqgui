package de.hshannover.dqgui.execution.database;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;

public class TargetedDatabase {
    private final UUID guid;
    private final String identifier, host, engine, language, query;
    private final Instant called;

    TargetedDatabase(DatabaseConnection connection, String query) {
        this.query = query.trim();
        called = Instant.now();
        identifier = connection.getIdentifier();
        host = connection.getSocket().asAddress();
        engine = connection.getEngine().toString();
        language = connection.getEngine().language().toString();
        guid = connection.getGuid();
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getHost() {
        return host;
    }

    public String getEngine() {
        return engine;
    }

    public String getLanguage() {
        return language;
    }

    public Instant getCalled() {
        return called;
    }
    
    public UUID getGuid() {
        return guid;
    }
    
    public String getQuery() {
        return query;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, host, engine, language, called);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TargetedDatabase other = (TargetedDatabase) obj;
        if (called == null) {
            if (other.called != null)
                return false;
        } else if (!called.equals(other.called))
            return false;
        if (engine == null) {
            if (other.engine != null)
                return false;
        } else if (!engine.equals(other.engine))
            return false;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (identifier == null) {
            if (other.identifier != null)
                return false;
        } else if (!identifier.equals(other.identifier))
            return false;
        if (language == null) {
            if (other.language != null)
                return false;
        } else if (!language.equals(other.language))
            return false;
        return true;
    }
}