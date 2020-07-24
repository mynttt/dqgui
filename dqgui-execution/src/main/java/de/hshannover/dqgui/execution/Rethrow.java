package de.hshannover.dqgui.execution;

/**
 * Simple error utility to deal with checked exceptions
 * @author myntt
 *
 */
public final class Rethrow {

    private Rethrow() {}

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> RuntimeException rethrow(Throwable t) throws T {
        throw (T) t;
    }

}
