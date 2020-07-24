package de.hshannover.dqgui.execution.model;

/**
 * Permitted DSL components.<br>
 * Min. value for IDs is 0.
 *
 * @author Marc Herschel
 *
 */
public enum DSLComponentType {
    SOURCE(0),
    ACTION(1),
    CHECK(2);
    
    /**
     * Identifier used in the database for the type
     */
    public final int identifier;
    
    DSLComponentType(int identifier) {
        this.identifier = identifier;
    }
    
    private static DSLComponentType[] LOOKUP;
    
    static {
        int max = 0;
        for(DSLComponentType c : values()) {
            if(c.identifier > max)
                max = c.identifier;
        }
        LOOKUP = new DSLComponentType[max+1];
        for(DSLComponentType t : values()) {
            if(LOOKUP[t.identifier] != null)
                throw new IllegalArgumentException("duplicate DSLComponentType id found: " + t.identifier);
            LOOKUP[t.identifier] = t;
        }
    }
    
    public static DSLComponentType of(int identifier) {
        return LOOKUP[identifier];
    }
}