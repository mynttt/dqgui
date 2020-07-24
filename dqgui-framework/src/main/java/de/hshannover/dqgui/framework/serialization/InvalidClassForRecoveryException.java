package de.hshannover.dqgui.framework.serialization;

/**
 * In case the Object recovered does not match the specified class.
 *
 * @author Marc Herschel
 *
 */
public final class InvalidClassForRecoveryException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public InvalidClassForRecoveryException(Throwable t) {
        super(t);
    }

}
