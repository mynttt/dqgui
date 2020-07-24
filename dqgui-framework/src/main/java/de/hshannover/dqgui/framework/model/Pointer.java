package de.hshannover.dqgui.framework.model;

import java.util.Optional;

/**
 * A handle for java (C++ equivalent is a double pointer)
 * 
 * @author Marc Herschel
 *
 * @param <T> type of stored reference
 */
public class Pointer<T> {
    private T reference;
    
    public void free() {
        reference = null;
    }
    
    public boolean isNull() {
        return reference == null;
    }
    
    public T unsafeGet() {
        return reference;
    }
    
    public Optional<T> safeGet() {
        return Optional.ofNullable(reference);
    }
    
    public void set(T reference) {
        this.reference = reference;
    }
}