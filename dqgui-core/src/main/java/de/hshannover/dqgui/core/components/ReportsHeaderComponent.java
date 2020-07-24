package de.hshannover.dqgui.core.components;

import de.hshannover.dqgui.core.Utility;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData;
import de.hshannover.dqgui.framework.AbstractComponentController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

final class ReportsHeaderComponent extends AbstractComponentController {
    
    @FXML Label action, date, time, issues, env;
    private final Iqm4hdMetaData meta;
    
    ReportsHeaderComponent(Iqm4hdMetaData meta) {
        this.meta = meta;
    }
    
    @FXML
    void initialize() {
        action.setText(meta.getAction());
        date.setText(Utility.DATE.format(meta.getFinished()));
        time.setText(Utility.TIME.format(meta.getFinished()));
        issues.setText(Integer.toString(meta.getIssues()));
        env.setText(meta.getEnvironment());
    }
}
