package de.hshannover.dqgui.framework;

import static de.hshannover.dqgui.framework.ApplicationContext.NO_ARGS_CACHE;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.tinylog.Logger;
import de.hshannover.dqgui.framework.FXMLLoadingFactory.ComponentContent;
import de.hshannover.dqgui.framework.api.Component;
import de.hshannover.dqgui.framework.api.Controllable;
import de.hshannover.dqgui.framework.api.Manageable;
import de.hshannover.dqgui.framework.dialogs.DialogContext;
import javafx.collections.ObservableMap;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

/**
 * Controller that is able to be managed by the {@link ApplicationContext} that can be mapped with a {@link Manageable}.
 *
 * @author Marc Herschel
 *
 */
public abstract class AbstractWindowController extends AbstractController {
    private Stage stage;
    private Manageable view;
    private ApplicationContext context;
    private DialogContext dialogContext;
    private UnregisterBehavior unregister;
    private List<Runnable> afterRenderHooks = new ArrayList<>(0);
    private List<AbstractComponentController> components = new ArrayList<>();
    private boolean loaded, hooksRun;

    @Override
    final void markLoaded() {
        loaded = true;
    }

    @Override
    final void injectApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    @Override
    final void injectDialogContext(DialogContext context) {
        this.dialogContext = context;
    }

    @Override
    final void injectStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    final void injectOwner(AbstractController owner) {}

    private void loadedCheck() {
        if(!loaded)
            throw new IllegalStateException("Controller has not been fully loaded yet or has been unloaded.");
    }

    final void registerHook(Manageable view, UnregisterBehavior unregister) {
        this.view = view;
        this.unregister = unregister;
        context.registerController(view, this, stage);
        stage.setOnCloseRequest(e -> { onCloseRequest(); e.consume(); });
        keyBindRegisterHook(stage.getScene().getAccelerators());
    }

    final Stage getStage() {
        return stage;
    }

    final void unloadComponent(AbstractComponentController controller) {
        controller.onDestruction();
        components.remove(controller);
    }
    
    final void runAfterRenderHooks() {
        afterRenderHooks.forEach(Runnable::run);
        afterRenderHooks.clear();
        afterRenderHooks = null;
        hooksRun = true;
    }
    
    /**
     * Moves current stage to front of all other stages.
     */
    protected final void toFront() {
        stage.toFront();
    }
    
    /**
     * Allows to register a after render hook which will be called after the stage has been rendered the first time.<br>
     * This method can only be called before the stage is rendered e.g in the initialize method that FXML provides.<br>
     * It exists to mitigate JavaFX render bugs.
     * @param hook to register for being called after rendering.
     */
    protected final void registerAfterRenderHook(Runnable hook) {
        Objects.requireNonNull(hook, "Hook must not be null!");
        if(hooksRun)
            throw new IllegalStateException("After render hooks already ran. You must register a hook before the stage is rendered (e.g in the initialize method).");
        afterRenderHooks.add(hook);
    }

    /**
     * Load a component and register it with the controller.<br>
     * Note: Don't call this in the constructor, call it after the controller has been loaded and linked with the FXML in the initialize() method that FXML provides.
     * @param component to load
     * @param args to give towards the components constructor
     * @return loaded component
     * @throws IllegalStateException if the controller has not been fully loaded yet or is unloaded
     */
    protected final ComponentContent loadComponent(Component component, Object... args) {
        loadedCheck();
        return context.loadComponent(component, this, args);
    }

    /**
     * Load a component with no arguments and register it with the controller.<br>
     * Note: Don't call this in the constructor, call it after the controller has been loaded and linked with the FXML in the initialize() method that FXML provides.
     * @param component to load
     * @return loaded component
     * @throws IllegalStateException if the controller has not been fully loaded yet or is unloaded
     */
    protected final ComponentContent loadComponent(Component component) {
        loadedCheck();
        return loadComponent(component, NO_ARGS_CACHE);
    }

    /**
     * Unload a component.<br>
     * This will call the {@link AbstractComponentController#onDestruction()} method and remove the component from being tracked.<br>
     * After calling this it will not be possible to use the {@link AbstractComponentController#getContext()} and {@link AbstractComponentController#getDialogContext()} methods anymore.
     * @param component to unloaded
     * @throws IllegalStateException if the controller has not been fully loaded yet or is unloaded
     */
    protected final void unloadComponent(ComponentContent component) {
        if(component == null) return;
        loadedCheck();
        unloadComponent(component.getController());
    }

    /**
     * Note: Don't call this in the constructor, call it after the controller has been loaded and linked with the FXML in the initialize() method that FXML provides.
     * @return current application context
     * @throws IllegalStateException if the controller has not been fully loaded yet or is unloaded
     */
    protected final ApplicationContext getContext() {
        loadedCheck();
        return context;
    }

    /**
     * Note: Don't call this in the constructor, call it after the controller has been loaded and linked with the FXML in the initialize() method that FXML provides.
     * @return current dialog context
     * @throws IllegalStateException if the controller has not been fully loaded yet or is unloaded
     */
    protected final DialogContext getDialogContext() {
        loadedCheck();
        return dialogContext;
    }

    /**
     * @param title that the stage shall show
     * @throws IllegalStateException if the controller has not been fully loaded yet or is unloaded
     */
    protected final void setTitle(String title) {
        loadedCheck();
        stage.setTitle(title);
    }

    /**
     * @return view type of controller
     */
    public final Manageable getViewType() {
        return view;
    }

    /**
     * Call this to close the mapped stage and purge all remains of it for eternity.<br>
     * Before closing {@link #onDestruction()} will be called.
     */
    protected final void unregister() {
        Logger.trace("Calling unregister hook for: " + view);
        components.forEach(Controllable::onDestruction);
        components.clear();
        this.dialogContext = null;
        onDestruction();
        unregister.unregister();
        stage.close();
        loaded = false;
    }

    /**
     * Register key bindings here.
     * @param bindings map that will be supplied to you for registration
     */
    protected void keyBindRegisterHook(ObservableMap<KeyCombination, Runnable> bindings) {}

    /**
     * Will be called when a user closes the stage with other means then the controller calling {@link #unregister()}.<br>
     * This allows the controller to decide if it wants to close or not and not the stage.<br>
     * The default behavior redirects to {@link #unregister()}.<br>
     * If you modify this be sure to call {@link #unregister()} at some point or the controller won't be closed properly.
     */
    @Override
    public void onCloseRequest() {
        unregister();
    }

}
