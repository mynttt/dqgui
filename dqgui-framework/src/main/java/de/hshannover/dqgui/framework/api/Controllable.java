package de.hshannover.dqgui.framework.api;

/**
 * Hook methods that can be implemented by a controller.
 *
 * @author Marc Herschel
 *
 */
public interface Controllable {

    /**
     * Implement custom shutdown functionality if needed.<br>
     * Depending on the implementation it might be a good idea to call the super method of this if it is implemented in the super class.
     */
    default void onDestruction() {}


    /**
     * Called when the user wants to close the controller.
     */
    default void onCloseRequest() {}
}
