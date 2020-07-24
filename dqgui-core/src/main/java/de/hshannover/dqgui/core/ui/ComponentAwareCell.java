package de.hshannover.dqgui.core.ui;

import de.hshannover.dqgui.core.model.DSLExtra;
import de.hshannover.dqgui.core.util.IconFactory;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;

@SuppressWarnings("rawtypes") 
public class ComponentAwareCell extends TableCell {
    private final ContextMenu contextMenu, promoteToGlobal, demoteToLocal;

    public ComponentAwareCell(ContextMenu contextMenu, ContextMenu promoteToGlobal, ContextMenu demoteToLocal) {
        this.contextMenu = contextMenu;
        this.promoteToGlobal = promoteToGlobal;
        this.demoteToLocal = demoteToLocal;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);
        
        if(item == null || empty) {
            setContextMenu(null);
            setGraphic(null);
            setText(null);
            setTooltip(null);
            return;
        }
        
        DSLComponent c = (DSLComponent) item;
        setText(c.getIdentifier());
        setContextMenu(contextMenu);
        
        if(c.isGlobal() && c.getType() == DSLComponentType.CHECK) {
            setGraphic(IconFactory.of(DSLExtra.GLOBAL_CHECK));
            setTooltip(new Tooltip("Component is a global check"));
            setContextMenu(demoteToLocal);
            return;
        }
        
        if(!c.isGlobal() && c.getType() == DSLComponentType.CHECK) {
            setTooltip(new Tooltip("Component is a local check"));
            setGraphic(IconFactory.of(DSLComponentType.CHECK));
            setContextMenu(promoteToGlobal);
            return;
        }
        
        if(c.getExtraData().containsKey("S_IS_CONST_QUERY")) {
            DSLExtra ex = DSLExtra.valueOf(c.getExtraData().get("S_CONST_QUERY"));
            setGraphic(IconFactory.of(ex));
            setTooltip(new Tooltip("Component is " + ex));
            return;
        }
        
        setGraphic(IconFactory.of(c.getType()));
    }
}