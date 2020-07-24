package de.hshannover.dqgui.framework.control;

import de.hshannover.dqgui.framework.api.SpecificItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

/**
 * Supports the correct behavior of {@link SpecificItem} implementing TreeCells such as {@link SpecificTreeItem}.
 *
 * @author Marc Herschel
 *
 */
public final class SpecificTreeItemCell extends TreeCell<String> {

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
            setTooltip(null);
            setContextMenu(null);
            return;
        }

        setGraphic(getTreeItem().getGraphic());

        if(getTreeItem() instanceof SpecificItem) {
            SpecificItem i = (SpecificItem) getTreeItem();
            setText(i.stringConversion());
            ContextMenu m = i.retrieveSpecifiedContextMenu();
            Tooltip t = i.retrieveSpecifiedTooltip();
            setContextMenu(m);
            setTooltip(t);
            return;
        }

        setText(getItem() == null ? "" : getItem());
    }

    /**
     * Create a CellFactory for a TreeView that utilizes {@link SpecificTreeItem}.
     * @return factory ready for use
     */
    public static Callback<TreeView<String>, TreeCell<String>> createFactory() {
        return param -> new SpecificTreeItemCell();
    }

}
