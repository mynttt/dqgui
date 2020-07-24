package de.hshannover.dqgui.framework.model;

import java.io.PrintWriter;
import java.io.StringWriter;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("NM_CLASS_NOT_EXCEPTION")
public class ReconstructedException {
    private final String classNameSimpleName, message, stacktrace;

    public ReconstructedException(String classNameSimpleName, String message, String stacktrace) {
        this.classNameSimpleName = classNameSimpleName;
        this.message = message;
        this.stacktrace = stacktrace;
    }

    public ReconstructedException(Throwable t) {
        Throwable cause = t;
        while(cause.getCause() != null)
            cause = cause.getCause();
        
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        
        this.classNameSimpleName = cause.getClass().getSimpleName();
        this.message = cause.getMessage();
        this.stacktrace = sw.toString();
    }

    public String getExceptionClassSimpleName() {
        return classNameSimpleName;
    }

    public String getMessage() {
        return message;
    }

    public String getStacktrace() {
        return stacktrace;
    }
}
