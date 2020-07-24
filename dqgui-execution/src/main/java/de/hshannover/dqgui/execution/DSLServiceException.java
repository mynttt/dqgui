package de.hshannover.dqgui.execution;

/**
 * In case anything goes wrong with an {@link DSLService} operation.
 *
 * @author Marc Herschel
 *
 */
public final class DSLServiceException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public DSLServiceException(Throwable t) {
        super(t);
    }
    
    public DSLServiceException(String message) {
        super(message);
    }

}