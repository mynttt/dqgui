package de.hshannover.dqgui.core.components;

import java.io.PrintWriter;
import java.io.StringWriter;
import de.hshannover.dqgui.framework.AbstractComponentController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

final class ReportFailedToLoadComponent extends AbstractComponentController {
    private final Throwable t;

    @FXML
    TextArea stacktrace;
    @FXML
    Label reason, exception;

    public ReportFailedToLoadComponent(Throwable t) {
        this.t = t;
    }

    @FXML
    void initialize() {
        Throwable cause = t;
        while(cause.getCause() != null)
            cause = cause.getCause();
        
        exception.setText(cause.getClass().getSimpleName());
        reason.setText(cause.getMessage() == null ? "No message specified" : t.getMessage());
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        stacktrace.setText(sw.toString());
    }

}
