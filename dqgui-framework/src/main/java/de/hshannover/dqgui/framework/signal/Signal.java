package de.hshannover.dqgui.framework.signal;

import java.util.HashSet;
import java.util.Set;

/**
 * A signal notifies that something happens by firing registered {@link SignalHandler} instances.<br>
 * The signal firing is not guaranteed to happen on the JavaFX UI thread.
 * 
 * @author myntt
 *
 */
public class Signal {
    private Set<SignalHandler> handlers = new HashSet<>();
    
    /**
     * Fire a signal and notify all registered handlers
     */
    public void fire() {
        synchronized (handlers) {
            handlers.forEach(SignalHandler::handle);
        }
    }
    
    /**
     * Clear all handlers
     */
    public void clear() {
        synchronized(handlers) {
            handlers.clear();
        }
    }
    
    /**
     * Register a handler
     * @param handler to register
     */
    public void register(SignalHandler handler) {
        synchronized (handlers) {
            handlers.add(handler);
        }
    }
    
    /**
     * Unregister a handler
     * @param handler to unregister
     */
    public void unregister(SignalHandler handler) {
        synchronized (handlers) {
            handlers.remove(handler);
        }
    }

    /**
     * Register a handler if it is not registered to avoid duplicates
     * @param handler to register
     */
    public void registerIfAbsent(SignalHandler handler) {
        synchronized(handlers) {
            if(!handlers.contains(handler))
                handlers.add(handler);
        }
    }
}