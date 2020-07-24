package de.hshannover.dqgui.framework.dialogs;

import de.hshannover.dqgui.framework.dialogs.DialogContext.DialogStyle;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

final class NotificationDialog extends AbstractDialog {
    private final DialogStyle type;
    private final String titlem, msg;
    @FXML ImageView image;
    @FXML Label notification, title;

    NotificationDialog(DialogStyle type, String titlem, String msg) {
        this.type = type;
        this.msg = msg;
        this.titlem = titlem;
    }

    @FXML
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH")
    void initialize() {
        image.setImage(type.getImg());
        setTitle(type.getDesc());
        title.setText(titlem == null ? type.getDesc() : titlem);
        notification.setText(msg);
    }

    @FXML
    void ack() {
        close();
    }

}
