package de.hshannover.dqgui.framework.wizards;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import de.hshannover.dqgui.framework.dialogs.DialogContext;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Superclass for wizards.
 *
 * @author Marc Herschel
 * @param <T> Type of wizard result.
 */
public abstract class AbstractWizard<T> {
    private final List<Runnable> closeHooks = new ArrayList<>();
    private final Stage wizardStage;
    private final DialogContext dialogContext;
    protected T result;

    /**
     * @param wizardTitle initial wizard title
     */
    public AbstractWizard(String wizardTitle) {
        this(wizardTitle, null);
    }

    /**
     * @param wizardTitle initial wizard title
     * @param wizardIcon initial wizard icon
     */
    public AbstractWizard(String wizardTitle, Image wizardIcon) {
        wizardStage = new Stage();
        dialogContext = new DialogContext(wizardStage);
        wizardStage.setTitle(wizardTitle);
        wizardStage.initModality(Modality.APPLICATION_MODAL);
        wizardStage.setOnCloseRequest(e -> this.cancel());
        if(wizardIcon != null)
            wizardStage.getIcons().add(wizardIcon);
    }

    /**
     * @param scene to set in the wizard stage
     */
    protected final void setScene(Scene scene) {
        wizardStage.setScene(scene);
        wizardStage.sizeToScene();
    }

    /**
     * @return a context that allows for creation of dialogs
     */
    public DialogContext getDialogContext() {
        return dialogContext;
    }

    /**
     * Show and begin the wizard operation.
     * @return result of the wizard
     */
    public final Optional<T> begin() {
        setup();
        wizardStage.sizeToScene();
        wizardStage.showAndWait();
        return Optional.ofNullable(result);
    }

    /**
     * Close the wizard and set the final result.
     * @param result to finish with
     */
    public final void finish(T result) {
        this.result = result;
        finishHook();
        wizardStage.close();
        closeHooks.forEach(Runnable::run);
        closeHooks.clear();
    }

    /**
     * Cancel the wizard (no changes made).
     */
    public final void cancel() {
        cancelHook();
        wizardStage.close();
        closeHooks.forEach(Runnable::run);
        closeHooks.clear();
    }
    
    /**
     * Register a close hook for the wizard that will be executed before closing
     * @param hook to register
     */
    public final void registerCloseHook(Runnable hook) {
        closeHooks.add(hook);
    }

    /**
     * Hooks for required setup steps (setting a scene to the stage, loading fxml, ...)
     */
    protected abstract void setup();

    /**
     * Hooks for finishing.
     */
    protected abstract void finishHook();

    /**
     * Hooks for canceling.
     */
    protected abstract void cancelHook();

}