package de.hshannover.dqgui.core.dialogs;

import java.util.List;
import de.hshannover.dqgui.framework.dialogs.AbstractDialog;
import de.hshannover.dqgui.framework.model.ObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

final class MultiRemoveDialog extends AbstractDialog {
    @FXML
    Label title, question;
    @FXML
    ListView<String> listview;
    
    private final String t, q;
    private final ObjectWrapper obj;
    
    MultiRemoveDialog(String title, String question, ObjectWrapper obj) {
        this.t = title;
        this.q = question;
        this.obj = obj;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @FXML
    void initialize() {
        title.setText(t);
        question.setText(q);
        List l = obj.unpack();
        listview.setItems(FXCollections.observableArrayList(l));
    }
    
    @FXML
    void yes() {
        setConfirm();
        close();
    }
    
    @FXML
    void cancel() {
        setCancel();
        close();
    }

    @Override
    protected void keyBindRegisterHook(ObservableMap<KeyCombination, Runnable> bindings) {
        bindings.put(new KeyCodeCombination(KeyCode.ENTER), this::yes);
    }
}
