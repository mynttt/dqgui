package de.hshannover.dqgui.framework.api;

import de.hshannover.dqgui.framework.AbstractWindowController;

/**
 * A component is a sub element of a window that is connected with its own controller.<br>
 * It is not managed as window but instead it is managed as a part of the creating {@link AbstractWindowController}.
 *
 * @author Marc Herschel
 *
 */
public interface Component extends Loadable {}
