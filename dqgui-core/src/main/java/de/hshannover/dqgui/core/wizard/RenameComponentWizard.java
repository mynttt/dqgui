package de.hshannover.dqgui.core.wizard;

import org.tinylog.Logger;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.core.wizard.controllers.RenameComponent;
import de.hshannover.dqgui.execution.DSLService;
import de.hshannover.dqgui.execution.DSLServiceException;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.framework.FXMLLoadingFactory;
import de.hshannover.dqgui.framework.FXMLLoadingFactory.WindowContent;
import de.hshannover.dqgui.framework.wizards.AbstractWizard;

public class RenameComponentWizard extends AbstractWizard<DSLComponent> {
    private final DSLComponent from;
    private final DSLService service;

    public RenameComponentWizard(DSLComponent from, DSLService service) {
        super("Rename " + from.getIdentifier(), Config.APPLICATION_FAVICON);
        this.from = from;
        this.service = service;
    }

    @Override
    protected void setup() {
        WindowContent obj = FXMLLoadingFactory.load(Config.APPLICATION_PATH_FXMLROOT_WIZARD_COMPONENT+"RenameComponent.fxml", Config.APPLICATION_PATHS_CSS);
        RenameComponent controller = (RenameComponent) obj.getController();
        controller.init(from, service, this);
        setScene(obj.getScene());
    }

    @Override
    protected void finishHook() {
        if(result == null)
            return;
        try {
            service.move(from, result);
        } catch (DSLServiceException e) {
            Logger.error(e);
            getDialogContext().exceptionDialog(e);
        }
        return;
    }

    @Override
    protected void cancelHook() {
        return;
    }

}
