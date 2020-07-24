package de.hshannover.dqgui.framework.api;

import de.hshannover.dqgui.framework.ApplicationContext;

/**
 * Marker interface combining {@link Loadable} and {@link Registrable}.<p>
 * All keys that map to a view that shall be managed by the {@link ApplicationContext} must implement this.<p>
 *
 * A Manageable must implement a working {@link Object#hashCode()} and {@link Object#equals(Object)}.
 *
 * @author Marc Herschel
 *
 */
public interface Manageable extends Loadable, Registrable {}
