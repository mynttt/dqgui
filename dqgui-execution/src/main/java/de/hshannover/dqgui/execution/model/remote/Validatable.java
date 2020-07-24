package de.hshannover.dqgui.execution.model.remote;

import java.util.Collection;

/**
 * Validate an object after creation.
 * 
 * @author myntt
 *
 */
public interface Validatable<T> {
    
    /**
     * Should validate the object by throwing exceptions if a constraint is violated.
     * Upon success the instance is returned.
     * @return validated result (in most cases the own instance via this)
     */
    public T validate();
    

    @SuppressWarnings("unchecked")
    public default <V> V notNull(V toCheck, String field) {
        if(toCheck != null)
            return toCheck;
        throw new IllegalArgumentException("NotNullValidation: Field " + field + " in class " + ((T) this).getClass().getSimpleName() + " is null.");
    }
    
    @SuppressWarnings("unchecked")
    public default String notNullOrBlank(String toCheck, String field) {
        if(!notNull(toCheck, field).trim().isEmpty())
            return toCheck;
        throw new IllegalArgumentException("NotEmptyValidation: String " + field + " in class " + ((T) this).getClass().getSimpleName() + " is blank.");
    }
    
    @SuppressWarnings("unchecked")
    public default <V extends Collection<?>> V notNullOrEmpty(V toCheck, String field) {
        if(toCheck == null)
            throw new IllegalArgumentException("NotNullValidation: Collection<?> " + field + " in class " + ((T) this).getClass().getSimpleName() + " is null.");
        if(!toCheck.isEmpty())
            return toCheck;
        throw new IllegalArgumentException("NotEmptyValidation: Collection<?> " + field + " in class " + ((T) this).getClass().getSimpleName() + " is empty.");
    }
}
