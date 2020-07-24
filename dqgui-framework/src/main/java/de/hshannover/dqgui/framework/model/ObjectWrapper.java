package de.hshannover.dqgui.framework.model;

import java.util.Objects;
import de.hshannover.dqgui.framework.ApplicationContext;

/**
 * Allows to wrap a generic object in a nullsafe way and hide the fact that it is generic to allow the framework to 
 * find a matching constructor when using i.e. {@link ApplicationContext#load(de.hshannover.dqgui.framework.api.Manageable, String, Object...) a method to inject a list of dependencies}.
 * 
 * @author myntt
 *
 */
public class ObjectWrapper {
    private final Object obj;
    
    /**
     * Construct a new ObjectWrapper
     * @param obj to wrap
     * @throws NullPointerException if the wrapped object is null
     */
    public ObjectWrapper(Object obj) {
        Objects.requireNonNull(obj, "Wrapped object cannot be null!");
        this.obj = obj;
    }

    /**
     * Unpacks the object via type inference.<br>
     * If you pack wrap a List&lt;String&gt; in here you could unpack it like this: <code>List&lt;String&gt; = obj.unpack();</code><br>
     * This spares you from casting the object yourself but does not protect you from a class cast exception.
     * @param <T> type of the underlying object
     * @return unpacked version of the wrapped object
     */
    @SuppressWarnings("unchecked")
    public <T> T unpack() {
        return (T) obj;
    }
}
