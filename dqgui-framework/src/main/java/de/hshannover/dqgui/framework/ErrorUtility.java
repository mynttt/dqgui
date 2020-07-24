package de.hshannover.dqgui.framework;

import java.util.concurrent.Callable;

public final class ErrorUtility {

    private ErrorUtility() {}

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> RuntimeException rethrow(Throwable t) throws T {
        throw (T) t;
    }

    public static <T> T uncheck(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw ErrorUtility.rethrow(e);
        }
    }
}
