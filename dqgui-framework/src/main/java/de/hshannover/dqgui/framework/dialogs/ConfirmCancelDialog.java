package de.hshannover.dqgui.framework.dialogs;

import de.hshannover.dqgui.framework.dialogs.DialogContext.DialogStyle;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

final class ConfirmCancelDialog extends AbstractDialog {
    private final DialogStyle type;
    private final String titlem, questionm;
    @FXML ImageView image;
    @FXML Label question, title;

    ConfirmCancelDialog(DialogStyle type, String titlem, String questionm) {
        this.type = type;
        this.questionm = questionm;
        this.titlem = titlem;
    }

    @FXML
    void initialize() {
        image.setImage(type.getImg());
        question.setText(questionm);
        String t = titlem == null ? "Action required!" : titlem;
        title.setText(t);
    }

    @FXML
    void confirm() {
        setConfirm();
        close();
    }

    @FXML
    void cancel() {
        setCancel();
        close();
    }
    
}
