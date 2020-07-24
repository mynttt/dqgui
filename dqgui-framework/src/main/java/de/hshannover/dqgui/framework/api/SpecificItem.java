package de.hshannover.dqgui.framework.api;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tooltip;

/**
 * Allows to have a specific item with a specific String and ContextMenu behavior for example in a TreeMenu or TableView with complex Objects within it.<br>
 *
 * @author Marc Herschel
 *
 */
public interface SpecificItem {

    /**
     * @return a String properly representing the content of this entity.
     */
    String stringConversion();

    /**
     * @return the currently defined ContextMenu for this entity.
     */
    default ContextMenu retrieveSpecifiedContextMenu() {
        return null;
    }

    /**
     * @return a tooltip for the entity.
     */
    default Tooltip retrieveSpecifiedTooltip() {
        return null;
    }
}
