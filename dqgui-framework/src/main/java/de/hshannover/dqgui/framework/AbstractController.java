package de.hshannover.dqgui.framework;

import de.hshannover.dqgui.framework.api.Controllable;
import de.hshannover.dqgui.framework.dialogs.DialogContext;
import javafx.stage.Stage;

/**
 * Base class for all controllers handled by the {@link ApplicationContext}.<br>
 * Be aware that ApplicationContext injected dependencies won't be available within the constructor of such an object.
 *
 * @author Marc Herschel
 *
 */
public abstract class AbstractController implements Controllable {
    abstract void injectApplicationContext(ApplicationContext context);
    abstract void injectDialogContext(DialogContext context);
    abstract void injectStage(Stage stage);
    abstract void injectOwner(AbstractController owner);
    abstract void markLoaded();
}
