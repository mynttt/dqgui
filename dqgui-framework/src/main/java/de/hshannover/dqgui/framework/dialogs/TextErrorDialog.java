package de.hshannover.dqgui.framework.dialogs;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public final class TextErrorDialog extends AbstractDialog {
    private final String sError, sHeadline, sHeader;

    @FXML
    TextArea area;
    @FXML
    Label headline, header;

     TextErrorDialog(String header, String headline, String error) {
         this.sHeader = header;
         this.sError = error;
         this.sHeadline = headline;
    }

     @FXML
     void initialize() {
         headline.setText(sHeadline);
         header.setText(sHeader);
         area.setText(sError);
         area.setWrapText(true);
     }

     @FXML
     void okay() {
         close();
     }
}
