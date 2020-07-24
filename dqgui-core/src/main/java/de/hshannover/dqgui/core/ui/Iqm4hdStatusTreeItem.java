package de.hshannover.dqgui.core.ui;

import de.hshannover.dqgui.execution.model.Iqm4hdMetaData;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData.HumanReadableMetaData;
import de.hshannover.dqgui.framework.control.SpecificTreeItem;
import javafx.scene.control.Tooltip;

class Iqm4hdStatusTreeItem extends SpecificTreeItem {
    private final String hash;
    private final HumanReadableMetaData humanReadable;
    private final Tooltip tooltip;

    Iqm4hdStatusTreeItem(Iqm4hdMetaData metadata) {
        super(metadata.getHumanReadable().getHumanReadable());
        this.hash = metadata.getHash();
        this.humanReadable = metadata.getHumanReadable();
        tooltip = new Tooltip(humanReadable.getContextInformation());
    }
    
    public String getHash()  {
        return hash;
    }

    public void updateTooltip() {
        tooltip.setText(humanReadable.getContextInformation());
    }

    public String getIdentifier() {
        return humanReadable.getIdentifier();
    }

    @Override
    public String stringConversion() {
        return humanReadable.getHumanReadable();
    }

    @Override
    public Tooltip retrieveSpecifiedTooltip() {
        return tooltip;
    }
}
