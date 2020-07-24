package de.hshannover.dqgui.execution.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class DSLComponent {
    private final boolean global;
    private final String identifier;
    private final DSLComponentType type;
    private final Map<String, String> extraData = new HashMap<>(2);

    private DSLComponent(String identifier, DSLComponentType type, boolean isGlobal) {
        this.identifier = identifier;
        this.type = type;
        if(isGlobal && this.type != DSLComponentType.CHECK)
            throw new IllegalArgumentException("only CHECK can be global");
        global = isGlobal;
    }
    
    public static DSLComponent of(String identifier, DSLComponentType type, boolean isGlobal) {
        return new DSLComponent(identifier, type, isGlobal);
    }
    
    public static DSLComponent of(String identifier, DSLComponentType type, Map<String, String> extraData, boolean isGlobal) {
        DSLComponent c = of(identifier, type, isGlobal);
        c.extraData.putAll(extraData);
        return c;
    }

    public String getIdentifier() {
        return identifier;
    }

    public DSLComponentType getType() {
        return type;
    }
    
    public boolean isGlobal() {
        return global;
    }

    /**
     * Extra data is modeled as a map that can be queried for keys within the application.
     * @return extra data
     */
    public Map<String, String> getExtraData() {
        return extraData;
    }

    @Override
    public int hashCode() {
        return Objects.hash(global, identifier, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DSLComponent other = (DSLComponent) obj;
        return global == other.global && Objects.equals(identifier, other.identifier) && type == other.type;
    }

    @Override
    public String toString() {
        return identifier;
    }

}