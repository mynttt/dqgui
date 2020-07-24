package de.hshannover.dqgui.core.wizard;

import java.util.Collections;
import org.tinylog.Logger;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.core.util.ExtraData;
import de.hshannover.dqgui.core.wizard.controllers.SaveComponentAs;
import de.hshannover.dqgui.execution.DSLService;
import de.hshannover.dqgui.execution.DSLServiceException;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.framework.FXMLLoadingFactory;
import de.hshannover.dqgui.framework.FXMLLoadingFactory.WindowContent;
import de.hshannover.dqgui.framework.wizards.AbstractWizard;

public final class SaveComponentAsWizard extends AbstractWizard<DSLComponent> {
    private final DSLService service;
    private final DSLComponent component;
    private final String content;

    public SaveComponentAsWizard(DSLService service, DSLComponent toSave, String content) {
        super(String.format("Save %s as...", toSave.getIdentifier()), Config.APPLICATION_FAVICON);
        this.service = service;
        this.component = toSave;
        this.content = content;
    }

    @Override
    protected void setup() {
        WindowContent obj = FXMLLoadingFactory.load(Config.APPLICATION_PATH_FXMLROOT_WIZARD_COMPONENT+"SaveComponentAs.fxml", Config.APPLICATION_PATHS_CSS);
        SaveComponentAs controller = (SaveComponentAs) obj.getController();
        controller.init(service, component, this);
        setScene(obj.getScene());
    }

    @Override
    protected void finishHook() {
        try {
            service.update(result, content);
            ExtraData.firstLoadProcessing(service, Collections.singletonList(result));
        } catch (DSLServiceException e) {
            Logger.error(e);
            getDialogContext().exceptionDialog(e, "Failed to persist component", String.format("DQGUI failed to persist the component %s.%n%nThe stacktrace below might help the developers to solve the issue.", component.getIdentifier()));
        }
    }

    @Override
    protected void cancelHook() {
        return;
    }

}
