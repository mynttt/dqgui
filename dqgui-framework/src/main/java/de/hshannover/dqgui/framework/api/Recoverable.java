package de.hshannover.dqgui.framework.api;

import de.hshannover.dqgui.framework.serialization.Serialization;

/**
 * All items supported by the Serialization must implement the Recoverable interface.<br>
 * This interface allows the serialization to call a hook once it finished recovering the object.<p>
 *
 * This is necessary to create observable and synchronized lists.
 *
 * @author Marc Herschel
 *
 */
public interface Recoverable {

    /**
     * Convert data if for example you require a synchronized list instead of an ArrayList here.<p>
     *
     * This should really be private but private in interfaces is Java 9+.<br>
     * DON'T EVER CALL THIS UNLESS YOU'RE NAMED Serialization.java
     */
    default void recoverHook() {}
    
    /**
     * Dumps this implementing class
     */
    default void dump() {
        Serialization.dump(this);
    }
}
