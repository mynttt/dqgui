package de.hshannover.dqgui.execution;

import java.util.Iterator;
import de.hshannover.dqgui.execution.model.DSLComponent;

/**
 * Provides an array of iterators that will be used by the threaded text search.<br>
 * These iterators will be shared by different threads so the underlying data structure must already support concurrency to avoid race conditions.
 * 
 * @author Marc Herschel
 *
 */
public interface ComponentSplitter {
    
    /**
     * Maps a component to the responding source code
     *
     */
    public static class ComponentSourceMapping {
        public final DSLComponent component;
        public final String source;
        
        public ComponentSourceMapping(DSLComponent component, String source) {
            this.component = component;
            this.source = source;
        }
    }
    
    Iterator<ComponentSourceMapping>[] splitBy(int splitBy); 
}