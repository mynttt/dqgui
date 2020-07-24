package de.hshannover.dqgui.framework.dialogs;

import de.hshannover.dqgui.framework.model.ReconstructedException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

final class ExceptionDialog extends AbstractDialog {
    private final ReconstructedException ex;
    private final String headerm, infom;
    @FXML Label header, reason, message, info;
    @FXML TextArea stacktrace;

    ExceptionDialog(Throwable t, String headerm, String infom) {
        this.headerm = headerm;
        this.infom = infom;
        this.ex = new ReconstructedException(t);
    }

    public ExceptionDialog(ReconstructedException ex, String headerm, String infom) {
        this.ex = ex;
        this.headerm = headerm;
        this.infom = infom;
    }

    @FXML
    void initialize() {
        setTitle("Oh Noooo! " + ex.getExceptionClassSimpleName() + " :(");
        reason.setText(ex.getExceptionClassSimpleName());
        message.setText(ex.getMessage() == null ? "No message specified" : ex.getMessage());
        stacktrace.setText(ex.getStacktrace());
        this.info.setText(infom);
        this.header.setText(headerm);
    }

    @FXML
    public void cont() {
        close();
    }

}
