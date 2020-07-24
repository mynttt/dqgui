package de.hshannover.dqgui.framework.api;

import de.hshannover.dqgui.framework.ApplicationContext;

/**
 * An implementation of this interface will allow to create and inject dependencies via the {@link ApplicationContext}.<br>
 * An implementation should have a default constructor that will be called to create the implementation.<br>
 * The fields can be public or private as the container is created by the {@link ApplicationContext} and will never be exposed.<p>
 *
 * An implementing class must not have static variables and should only have the members that are going to be injected.
 * All members must be created in the loadEager() method.<p>
 *
 * Lazy loading is currently not supported because we don't need it.
 *
 * @author Marc Herschel
 *
 */
public interface Dependencies {

    /**
     * Initialize all eager dependencies here.
     */
    void loadEager();

    /**
     * When the ApplicationContext shuts down this will be called.
     */
    void onClose();

    /**
     * If you plan to use serialization implement this and return a path to where the serialization should save the JSON files.
     * @return string representing a path where to store serialized objects.
     */
    default String serializationRoot() {
        return null;
    }

}
