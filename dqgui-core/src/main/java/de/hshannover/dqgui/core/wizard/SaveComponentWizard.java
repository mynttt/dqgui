package de.hshannover.dqgui.core.wizard;

import org.tinylog.Logger;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.core.wizard.controllers.SaveComponent;
import de.hshannover.dqgui.execution.DSLService;
import de.hshannover.dqgui.execution.DSLServiceException;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.framework.FXMLLoadingFactory;
import de.hshannover.dqgui.framework.FXMLLoadingFactory.WindowContent;
import de.hshannover.dqgui.framework.wizards.AbstractWizard;

public final class SaveComponentWizard extends AbstractWizard<Boolean> {
    private final String content;
    private final DSLService service;
    private final DSLComponent component;

    public SaveComponentWizard(DSLService service, DSLComponent component, String content) {
        super("Do you want to save " + component.getIdentifier() + "?", Config.APPLICATION_FAVICON);
        this.content = content;
        this.component = component;
        this.service = service;
    }

    @Override
    protected void setup() {
        WindowContent obj = FXMLLoadingFactory.load(Config.APPLICATION_PATH_FXMLROOT_WIZARD_COMPONENT+"SaveComponent.fxml", Config.APPLICATION_PATHS_CSS);
        SaveComponent controller = (SaveComponent) obj.getController();
        controller.init(component, this);
        setScene(obj.getScene());
    }

    @Override
    protected void finishHook() {
        if(!result) {
            result = Boolean.TRUE;
            return;
        }
        try {
            service.update(component, content);
        } catch (DSLServiceException e) {
            Logger.error(e);
            result = Boolean.FALSE;
            getDialogContext().exceptionDialog(e, "Failed to persist component", String.format("DQGUI failed to persist the component %s.%n%nThe stacktrace below might help the developers to solve the issue.", component.getIdentifier()));
        }
    }

    @Override
    protected void cancelHook() {
        result = Boolean.FALSE;
    }

}
