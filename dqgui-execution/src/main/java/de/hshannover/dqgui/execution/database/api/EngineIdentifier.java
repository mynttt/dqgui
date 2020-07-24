package de.hshannover.dqgui.execution.database.api;

import java.util.HashSet;
import java.util.Objects;

/**
 * An EngineIdentifier uniquely identifies an engine to the {@link EngineManager}.<br>
 * The identifier will always be converted to an upper case string.<br>
 * It can be queries as a lower case string tho as it will be converted automatically.<br>
 * 
 * @author myntt
 *
 */
public final class EngineIdentifier {
    private static final HashSet<String> CONSTRAINT = new HashSet<>();
    private final String identifier;
    
    private EngineIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    static EngineIdentifier of(String identifier) {
        Objects.requireNonNull(identifier, "identifier must not be null.");
        if(identifier.trim().isEmpty())
            throw new IllegalArgumentException("identifier must not be blank.");
        if(CONSTRAINT.contains(identifier.toUpperCase()))
            throw new IllegalArgumentException("identifier is not unique and can only be created once: " + identifier);
        identifier = identifier.toUpperCase();
        CONSTRAINT.add(identifier);
        return new EngineIdentifier(identifier);
    }
    
    @Override
    public String toString() {
        return identifier;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EngineIdentifier other = (EngineIdentifier) obj;
        return Objects.equals(identifier, other.identifier);
    }
    
}
