package de.hshannover.dqgui.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.tinylog.Logger;
import de.hshannover.dqgui.framework.DependencyConfiguration.ErrorReport;
import de.hshannover.dqgui.framework.FXMLLoadingFactory.ComponentContent;
import de.hshannover.dqgui.framework.FXMLLoadingFactory.ManagedWindowContent;
import de.hshannover.dqgui.framework.api.Component;
import de.hshannover.dqgui.framework.api.Dependencies;
import de.hshannover.dqgui.framework.api.Manageable;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *  ApplicationContext acts as a window management framework.<p>
 *
 *  It will create and keep track of {@link Manageable} and enforce the {@link WindowInstances}. <br>
 *  If a view already exists and should only exist once it will be pushed into the foreground.<br>
 *  If multiple views of the same type are limited by an amount &gt; 1 it will throw an {@link IllegalArgumentException} once that amount is reached.
 *
 *  @author Marc Herschel
 */
public final class ApplicationContext {
    static final Object[] NO_ARGS_CACHE = new Object[0];

    private final List<AbstractWindowController> openControllers = Collections.synchronizedList(new ArrayList<>());
    private final Map<Manageable, ControllerStage> registeredStages = Collections.synchronizedMap(new HashMap<>());
    private final Map<Manageable, Integer> trackedViews = Collections.synchronizedMap(new HashMap<>());
    private final Map<ManageableIdentifier, ControllerStage> identifierManaged = Collections.synchronizedMap(new HashMap<>());
    private final FXMLLoadingFactory loadingFactory;
    private final AbstractInjector injector;
    private boolean initialized, shutdown;

    /**
     * Create a new ApplicationContext without dependency injection.
     */
    public ApplicationContext() {
        Logger.info("Creating ApplicationContext without dependency injection.");
        injector = new CommonInjector(null, null);
        loadingFactory = new FXMLLoadingFactory(injector);
    }

    /**
     * Create a new ApplicationContext with dependency injection.
     * @param config to load for injection
     * @throws IllegalArgumentException if invalid configuration
     */
    public ApplicationContext(String config) {
        Logger.info("Creating ApplicationContext with dependency injection. Config: {}", config);
        DependencyConfiguration dconfig = DependencyConfiguration.of(config);
        Dependencies container;
        try {
            container = DependencyConfiguration.loadConfiguration(dconfig);
        } catch (Exception e) {
            throw ErrorUtility.rethrow(e);
        }
        if(dconfig.shouldTest) {
            Logger.warn("Configuration validation is enabled in {}. This is an expensive operation, be sure to disable it if passing.", config);
            ErrorReport report = DependencyConfiguration.validate(dconfig, container);
            if(!report.passed()) {
                Logger.error("DependencyInjection is misconfigured. Dumping found errors:");
                report.getErrors().forEach(Logger::error);
                throw new IllegalArgumentException(String.format("Invalid dependency injection configuration. %s", report.getErrorCodes()));
            }
        }
        injector = new DefaultInjector(dconfig, container);
        loadingFactory = new FXMLLoadingFactory(injector);
    }

    /**
     * Create the initial stage that returns a stage object.<br>
     * This method can only be called once.
     * @param main that shall be created as the initial stage
     * @param title to set as title for the initial stage
     * @param configuration configure and show stage here
     * @throws IllegalStateException if shutdown
     * @throws IllegalArgumentException if view is managed by identifier
     */
    public void loadInitial(Manageable main, String title, Consumer<Stage> configuration) {
        loadInitial(main, title, configuration, NO_ARGS_CACHE);
    }

    /**
     * Create the initial stage that returns a stage object.<br>
     * This method can only be called once.
     * @param main that shall be created as the initial stage
     * @param title to set as title for the initial stage
     * @param configuration configure and show stage here
     * @param args arguments for the constructor/callback method of the view  (no generics supported)
     * @throws IllegalStateException if shutdown
     * @throws IllegalArgumentException if view is managed by identifier
     */
    public void loadInitial(Manageable main, String title, Consumer<Stage> configuration, Object... args) {
        preconditionPassed(main, args);
        if(initialized)
            throw new IllegalStateException("Context already initialized...");
        Stage stage = new Stage();
        stage.getIcons().add(main.getIcon());
        stage.setTitle(title);
        ManagedWindowContent loaded = loadingFactory.load(main, stage, args, this);
        stage.setScene(loaded.getScene());
        ((AbstractWindowController) loaded.getController()).registerHook(main, new UnregisterBehaviorDefault(this, (AbstractWindowController) loaded.getController()));
        initialized = true;
        configuration.accept(stage);
        ((AbstractWindowController) loaded.getController()).runAfterRenderHooks();
    }

    /**
     * Create a normal stage for a view.
     * @param view that shall be created as a stage
     * @param title to set as title for the stage
     * @throws IllegalStateException if view is restricted by the set {@link WindowInstances} or shutdown
     * @throws IllegalArgumentException if view is managed by identifier
     */
    public void load(Manageable view, String title) {
        load(view, title, NO_ARGS_CACHE);
    }

    /**
     * Create a normal stage for a view.
     * @param view that shall be created as a stage
     * @param title to set as title for the stage
     * @param args arguments for the constructor/callback method of the view  (no generics supported)
     * @throws IllegalStateException if view is restricted by the set {@link WindowInstances} or shutdown
     * @throws IllegalArgumentException if view is managed by identifier
     */
    public void load(Manageable view, String title, Object... args) {
        if(!preconditionPassed(view, args))
            return;
        Stage stage = new Stage();
        stage.getIcons().add(view.getIcon());
        stage.setTitle(title);
        ManagedWindowContent loaded = loadingFactory.load(view, stage, args, this);
        stage.setScene(loaded.getScene());
        ((AbstractWindowController) loaded.getController()).registerHook(view, new UnregisterBehaviorDefault(this, (AbstractWindowController) loaded.getController()));
        stage.show();
        stage.sizeToScene();
        ((AbstractWindowController) loaded.getController()).runAfterRenderHooks();
    }

    /**
     * Create a normal stage for a view that is managed by an identifier.
     * @param view that shall be created as a stage
     * @param identifier how this instance of the view will identify itself
     * @param title to set as title for the stage
     * @throws IllegalStateException if view is restricted by the set {@link WindowInstances} or shutdown
     * @throws IllegalArgumentException if view is not managed by identifier
     */
    public void loadIdentified(Manageable view, String identifier, String title) {
        loadIdentified(view, identifier, title, NO_ARGS_CACHE);
    }

    /**
     * Create a normal stage for a view that is managed by an identifier.
     * @param view that shall be created as a stage
     * @param identifier how this instance of the view will identify itself
     * @param title to set as title for the stage
     * @param args arguments for the constructor/callback method of the view  (no generics supported)
     * @throws IllegalStateException if view is restricted by the set {@link WindowInstances} or shutdown
     * @throws IllegalArgumentException if view is not managed by identifier
     */
    public void loadIdentified(Manageable view, String identifier, String title, Object... args) {
        if(!preconditionPassedIdentifier(view, identifier, args)) {
            return;
        }
        Stage stage = new Stage();
        stage.getIcons().add(view.getIcon());
        stage.setTitle(title);
        ManagedWindowContent loaded = loadingFactory.load(view, stage, args, this);
        stage.setScene(loaded.getScene());
        ((AbstractWindowController) loaded.getController()).registerHook(view, new UnregisterBehaviorIdentifier(this, (AbstractWindowController) loaded.getController(), identifier));
        identifierManaged.put(ManageableIdentifier.of(view, identifier), new ControllerStage((AbstractWindowController) loaded.getController(), stage));
        stage.show();
        stage.sizeToScene();
        ((AbstractWindowController) loaded.getController()).runAfterRenderHooks();
    }

    /**
     * Create a Blocking Stage (Not able to interact with background until closed).
     * @param view that shall be created as blocking stage
     * @param title to set as title for the blocking stage
     * @param owner controller that is calling
     * @throws IllegalStateException if view is restricted by the set {@link WindowInstances} or shutdown
     * @throws IllegalArgumentException if view is managed by identifier
     */
    public void loadBlocking(Manageable view, String title, AbstractWindowController owner) {
        loadBlocking(view, title, owner, NO_ARGS_CACHE);
    }

    /**
     * Create a Blocking Stage (Not able to interact with background until closed).
     * @param view that shall be created as blocking stage
     * @param title to set as title for the blocking stage
     * @param owner controller that is calling
     * @param args arguments for the constructor/callback method of the view  (no generics supported)
     * @throws IllegalStateException if view is restricted by the set {@link WindowInstances} or shutdown
     * @throws IllegalArgumentException if view is managed by identifier
     */
    public void loadBlocking(Manageable view, String title, AbstractWindowController owner, Object... args) {
        if(!preconditionPassed(view, args))
            return;
        Stage stage = new Stage();
        stage.getIcons().add(view.getIcon());
        stage.setTitle(title);
        ManagedWindowContent loaded = loadingFactory.load(view, stage, args, this);
        stage.initOwner(owner.getStage());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(loaded.getScene());
        ((AbstractWindowController) loaded.getController()).registerHook(view, new UnregisterBehaviorDefault(this, (AbstractWindowController) loaded.getController()));
        stage.show();
        stage.sizeToScene();
        ((AbstractWindowController) loaded.getController()).runAfterRenderHooks();
    }

    /**
     * Report the number of instances of a {@link Manageable}.
     * @param view to query for
     * @return number of open instances
     */
    public int numberOfInstances(Manageable view) {
        if(!trackedViews.containsKey(view)) {
            trackedViews.put(view, 0);
            return 0;
        }
        return trackedViews.get(view);
    }

    /**
     * This will close the entire ApplicationContext and make it unusable.
     */
    public void completeShutdown() {
        this.shutdown = true;
        List<AbstractWindowController> shutdown = new ArrayList<>(openControllers);
        shutdown.forEach(AbstractWindowController::unregister);
        openControllers.clear();
        identifierManaged.clear();
        registeredStages.clear();
        trackedViews.clear();
        injector.closeContainer();
        Logger.info("Context has been shutdown.");
    }

    /*
     * Package
     */

    void registerController(Manageable view, AbstractWindowController controller, Stage stage) {
        if(view.getWindowInstances() == WindowInstances.ONCE_AT_A_TIME) {
            registeredStages.put(controller.getViewType(), new ControllerStage(controller, stage));
        }
        int amount = trackedViews.get(controller.getViewType()) + 1;
        trackedViews.put(controller.getViewType(), amount);
        openControllers.add(controller);
    }

    void unregisterController(AbstractWindowController controller) {
        if(registeredStages.containsKey(controller.getViewType())) {
            Logger.trace("Unregistered Controller with ApplicationContext: " + controller.getViewType());
            registeredStages.remove(controller.getViewType());
        }
        unregisterCommon(controller);
    }

    private void unregisterCommon(AbstractWindowController controller) {
        openControllers.remove(controller);
        int amount = trackedViews.get(controller.getViewType()) - 1;
        trackedViews.put(controller.getViewType(), amount);
    }
    
    void unregisterController(AbstractWindowController controller, String identifier) {
        unregisterCommon(controller);
        identifierManaged.remove(ManageableIdentifier.of(controller.getViewType(), identifier));
    }

    private boolean isAllowedToBeCreated(Manageable view) {
        if(!trackedViews.containsKey(view)) {
            trackedViews.put(view, 0);
            return true;
        }
        if(view.getWindowInstances() == WindowInstances.UNLIMITED || view.getWindowInstances() == WindowInstances.UNLIMITED_MANAGED_BY_IDENTIFIER)
            return true;
        return trackedViews.get(view) < view.getWindowInstances().getPossibleInstances();
    }

    ComponentContent loadComponent(Component component, AbstractWindowController owner, Object[] args) {
        return loadingFactory.load(component, args, this, owner.getStage(), owner);
    }

    /*
     * Private
     */

    private boolean preconditionPassed(Manageable view, Object[] args) {
        if(shutdown)
            throw new IllegalStateException("ApplicationContext has been shutdown");
        if(stageRecovered(view, args))
            return false;
        if(view.getWindowInstances().isIdentifierManaged())
            throw new IllegalArgumentException("View must not be identifierManaged");
        if(!isAllowedToBeCreated(view)) {
            throw new IllegalStateException(String.format("View: %s is not allowed to be created. Instances: %d - allowed: %d",
                    view,
                    trackedViews.get(view),
                    view.getWindowInstances().getPossibleInstances()));
        }
        return true;
    }

    private boolean preconditionPassedIdentifier(Manageable view, String identifier, Object[] args) {
        if(shutdown)
            throw new IllegalStateException("ApplicationContext has been shutdown");
        if(identifier == null || identifier.trim().isEmpty())
            throw new IllegalArgumentException("Invalid identifier: " + identifier);
        if(!view.getWindowInstances().isIdentifierManaged())
            throw new IllegalArgumentException("View must be identifierManaged");
        if(stageRecovered(view, identifier, args))
            return false;
        if(!isAllowedToBeCreated(view)) {
            throw new IllegalStateException(String.format("View: %s is not allowed to be created. Instances: %d - allowed: %d",
                    view,
                    trackedViews.get(view),
                    view.getWindowInstances().getPossibleInstances()));
        }
        return true;
    }

    private boolean stageRecovered(Manageable view, String identifier, Object[] args) {
        ManageableIdentifier ident = ManageableIdentifier.of(view, identifier);
        ControllerStage s = identifierManaged.get(ident);
        if(s != null) {
            s.getStage().toFront();
            try {
                injector.handleInjectedCallbacks(s.getController().getClass(), s.getController(), args);
            } catch(Exception e) {
                throw ErrorUtility.rethrow(e);
            }
            return true;
        }
        return false;
    }

    private boolean stageRecovered(Manageable view, Object[] args) {
        ControllerStage s = registeredStages.get(view);
        if(view.getWindowInstances() == WindowInstances.ONCE_AT_A_TIME && s != null) {
            Logger.trace("View: " + view + " is a registrable already loaded within a stage. Moving loaded " + view + " to the foreground...");
            s.getStage().toFront();
            try {
                injector.handleInjectedCallbacks(s.getController().getClass(), s.getController(), args);
            } catch(Exception e) {
                throw ErrorUtility.rethrow(e);
            }
            return true;
        }
        return false;
    }

}
