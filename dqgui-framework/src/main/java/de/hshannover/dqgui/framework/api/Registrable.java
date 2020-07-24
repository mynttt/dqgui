package de.hshannover.dqgui.framework.api;

import de.hshannover.dqgui.framework.ApplicationContext;
import de.hshannover.dqgui.framework.WindowInstances;

/**
 * Allow a key to be registered with the {@link ApplicationContext} and be treated as a managed window.
 *
 * @author Marc Herschel
 *
 */
public interface Registrable {

    /**
     * Specifies the amount of window instances possible.<br>
     * Be aware that writing 0 here is not a good idea.<p>
     *
     * A controller is only registrable if the amount of window instances is 1.
     *
     * @return possible window instances
     */
    WindowInstances getWindowInstances();
}
