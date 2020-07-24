package de.hshannover.dqgui.core.dialogs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.hshannover.dqgui.core.ui.CodeEditor;
import de.hshannover.dqgui.execution.DSLService;
import de.hshannover.dqgui.execution.DSLServiceException;
import de.hshannover.dqgui.framework.dialogs.AbstractDialog;
import de.hshannover.dqgui.framework.model.ObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

final class BatchSaveDialog extends AbstractDialog {

    private class BatchSaveCell extends ListCell<CodeEditor> {
        private final CheckBox box = new CheckBox();

        {
            box.setMnemonicParsing(false);
            box.setSelected(true);
        }

        @Override
        protected void updateItem(CodeEditor item, boolean empty) {
            super.updateItem(item, empty);

            if(item == null || empty) {
                setGraphic(null);
                return;
            }

            map.put(item, box);
            box.setText(item.getComponent().getIdentifier());
            setGraphic(box);
        }

    }

    @FXML Label unsaved;
    @FXML ListView<CodeEditor> items;

    private final Map<CodeEditor, CheckBox> map = new HashMap<>();
    private final ObjectWrapper data;
    private final DSLService service;

    BatchSaveDialog(ObjectWrapper data, DSLService service) {
        this.data = data;
        this.service = service;
    }

    @FXML
    void initialize() {
        List<CodeEditor> tabs = data.unpack();
        tabs.sort((t1, t2) -> t1.getComponent().getIdentifier().compareToIgnoreCase(t2.getComponent().getIdentifier()));
        unsaved.setText(tabs.size() + " component" + (tabs.size() == 1 ? " has" : "s have") + " not been saved yet!");
        items.setItems(FXCollections.observableArrayList(tabs));
        items.setCellFactory(c -> new BatchSaveCell());
        setCancel();
    }

    @FXML
    void cancel() {
        close();
    }

    @FXML
    void ignoreAll() {
        setConfirm();
        close();
    }

    @FXML
    void saveSelected() {
        setConfirm();
        for(Map.Entry<CodeEditor, CheckBox> entry : map.entrySet()) {
            if(!entry.getValue().isSelected())
                continue;
            try {
                service.update(entry.getKey().getComponent(), entry.getKey().getEditorContent());
            } catch (DSLServiceException e) {
                getDialogContext().exceptionDialog(e);
                close();
                break;
            }
        }
        close();
    }

}
