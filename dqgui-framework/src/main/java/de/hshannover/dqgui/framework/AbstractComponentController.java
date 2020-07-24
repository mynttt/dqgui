package de.hshannover.dqgui.framework;

import static de.hshannover.dqgui.framework.ApplicationContext.NO_ARGS_CACHE;
import de.hshannover.dqgui.framework.FXMLLoadingFactory.ComponentContent;
import de.hshannover.dqgui.framework.api.Component;
import de.hshannover.dqgui.framework.dialogs.DialogContext;
import javafx.stage.Stage;

public class AbstractComponentController extends AbstractController {
    private ApplicationContext context;
    private DialogContext dialogContext;
    private AbstractWindowController owner;
    private boolean loaded;

    @Override
    final void injectApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    @Override
    final void injectDialogContext(DialogContext context) {
        this.dialogContext = context;
    }

    @Override
    final void injectStage(Stage stage) {}

    @Override
    final void injectOwner(AbstractController owner) {
        this.owner = (AbstractWindowController) owner;
    }

    @Override
    final void markLoaded() {
        loaded = true;
    }

    /**
     * This method would never be called for this controller. Thus it is marked final.
     * @throws UnsupportedOperationException if you decide to call it
     */
    @Override
    public final void onCloseRequest() {
        throw new UnsupportedOperationException("No close request could ever reach this");
    }

    /**
     * Unregisters the component from the owning {@link AbstractWindowController}
     * @throws IllegalStateException if the component is already unregistered
     */
    protected final void unregister() {
        if(!loaded)
            throw new IllegalStateException("Component already unloaded");
        loaded = false;
        context = null;
        dialogContext = null;
        onDestruction();
        owner.unloadComponent(this);
        owner = null;
    }

    /**
     * Load a component with no arguments and register it with the owning {@link AbstractWindowController}.<br>
     * Note: Don't call this in the constructor, call it after the controller has been loaded and linked with the FXML in the initialize() method that FXML provides.
     * @param component to load
     * @return loaded component
     * @throws IllegalStateException if the controller has not been fully loaded yet or is unloaded
     */
    protected final ComponentContent loadComponent(Component component) {
        loadedCheck();
        return owner.loadComponent(component, NO_ARGS_CACHE);
    }

    /**
     * Load a component and register it with the owning {@link AbstractWindowController}.<br>
     * Note: Don't call this in the constructor, call it after the controller has been loaded and linked with the FXML in the initialize() method that FXML provides.
     * @param component to load
     * @param args to give towards the components constructor
     * @return loaded component
     * @throws IllegalStateException if the controller has not been fully loaded yet or is unloaded
     */
    protected final ComponentContent loadComponent(Component component, Object... args) {
        loadedCheck();
        return owner.loadComponent(component, args);
    }

    /**
     * Note: Don't call this in the constructor, call it after the controller has been loaded and linked with the FXML in the initialize() method that FXML provides.
     * @return current application context
     * @throws IllegalStateException if the component has not been fully loaded or is unloaded
     */
    protected final ApplicationContext getContext() {
        loadedCheck();
        return context;
    }

    /**
     * Note: Don't call this in the constructor, call it after the controller has been loaded and linked with the FXML in the initialize() method that FXML provides.
     * @return current dialog context
     * @throws IllegalStateException if the component has not been fully loaded or is unloaded
     */
    protected final DialogContext getDialogContext() {
        loadedCheck();
        return dialogContext;
    }

    private void loadedCheck() {
        if(!loaded)
            throw new IllegalStateException("Component has not been fully loaded yet or has been unloaded.");
    }
}
