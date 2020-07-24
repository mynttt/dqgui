package de.hshannover.dqgui.core.wizard;

import org.tinylog.Logger;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.core.wizard.controllers.CreateComponent;
import de.hshannover.dqgui.execution.DSLService;
import de.hshannover.dqgui.execution.DSLServiceException;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import de.hshannover.dqgui.framework.FXMLLoadingFactory;
import de.hshannover.dqgui.framework.FXMLLoadingFactory.WindowContent;
import de.hshannover.dqgui.framework.wizards.AbstractWizard;

public final class CreateComponentWizard extends AbstractWizard<DSLComponent> {
    private final DSLComponentType shortcut;
    private final DSLService service;
    private final String initialName;

    public CreateComponentWizard(DSLComponentType shortcut, DSLService service) {
        this(shortcut, service, null);
    }
    
    public CreateComponentWizard(DSLComponentType shortcut, DSLService service, String initialName) {
        super("Create a new component", Config.APPLICATION_FAVICON);
        this.shortcut = shortcut;
        this.service = service;
        this.initialName = initialName;
    }

    @Override
    protected void setup() {
        WindowContent obj = FXMLLoadingFactory.load(Config.APPLICATION_PATH_FXMLROOT_WIZARD_COMPONENT+"CreateComponent.fxml", Config.APPLICATION_PATHS_CSS);
        CreateComponent controller = (CreateComponent) obj.getController();
        controller.init(shortcut, initialName, service, this);
        setScene(obj.getScene());
    }

    @Override
    protected void finishHook() {
        try {
            service.create(result);
        } catch (DSLServiceException e) {
            result = null;
            Logger.error(e);
            getDialogContext().exceptionDialog(e, "Failed to create component", String.format("DQGUI failed to create the %s %s.%n%nThe stacktrace below might help the developers to solve the issue.", result.getType(), result.getIdentifier()));
        }
    }

    @Override
    protected void cancelHook() {
        return;
    }

}
