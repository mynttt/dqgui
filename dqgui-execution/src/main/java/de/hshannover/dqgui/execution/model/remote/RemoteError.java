package de.hshannover.dqgui.execution.model.remote;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A remote error will always contain a message and errorName.<br>
 * It is the servers decision if it wants to send a stacktracke.
 * 
 * @author myntt
 *
 */
public class RemoteError implements RemoteResponse {
    private final String stackTrace, errorName, message;
    
    public RemoteError(String exceptionName, String message) {
        this.errorName = exceptionName;
        this.message = message;
        this.stackTrace = "undefind";
    }
    
    public RemoteError(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        this.stackTrace = sw.toString();
        Throwable t = e;
        while(e.getCause() != null)
            t = e.getCause();
        this.errorName = t.getClass().getSimpleName();
        this.message = t.getMessage();
    }
    
    public String getStackTrace() {
        return stackTrace;
    }
    
    public String getExceptionName() {
        return errorName;
    }
    
    public String getMessage() {
        return message;
    }
}