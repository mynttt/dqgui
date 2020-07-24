package de.hshannover.dqgui.execution.database.api;

import java.nio.channels.UnsupportedAddressTypeException;

/**
 * A handle for a connection that guarantees the connection is always open.
 * 
 * @author myntt
 *
 * @param <T> of handle
 */

public interface ConnectionHandle<T> extends AutoCloseable {
    
    /**
     * Either creates the connection or checks if it is closed.<br>
     * In that case a reconnect is attempted.
     * @return open connection
     */
    T get();
    
    /**
     * Commits a transaction
     * @throws UnsupportedOperationException if the underlying handle does not support this
     */
    default void commit() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Rollbacks a connection
     * @throws UnsupportedOperationException if the underlying handle does not support this
     */
    default void rollback() {
        throw new UnsupportedAddressTypeException();
    }
}
