package de.hshannover.dqgui.framework.control;

import de.hshannover.dqgui.framework.api.SpecificItem;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;

/**
 * Extend this to support the {@link SpecificItem} behavior for TreeViews.
 *
 * @author Marc Herschel
 *
 */
public class SpecificTreeItem extends TreeItem<String> implements SpecificItem {
    private ContextMenu menu;
    
    public SpecificTreeItem() {
        super();
    }

    public SpecificTreeItem(String value, Node graphic) {
        super(value, graphic);
    }

    public SpecificTreeItem(String value) {
        super(value);
    }

    @Override
    public final ContextMenu retrieveSpecifiedContextMenu() {
        return menu;
    }

    /**
     * @param menu to set
     * @return SpecificTreeItem instance for chaining
     */
    public final SpecificTreeItem setContextMenu(ContextMenu menu) {
        this.menu = menu;
        return this;
    }

    @Override
    public String stringConversion() {
        return getValue();
    }
}
