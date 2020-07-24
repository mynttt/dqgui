package de.hshannover.dqgui.framework.dialogs;

import javafx.collections.ObservableMap;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * Extend this to create your own dialog.
 *
 * @author Marc Herschel
 *
 */
public abstract class AbstractDialog {

    /**
     * A dialog controller will offer this user interaction for collection.
     */
    public enum Operation {
        /**
         * Closed with no user interaction made.
         */
        UNDEFINED,
        /**
         * User confirmed the dialog.
         */
        CONFIRM,
        /**
         * User canceled the dialog.
         */
        CANCEL
    }

    private DialogContext context;
    private Stage stage;
    private Operation operation = Operation.UNDEFINED;
    private Object payload;

    final void injectStage(Stage stage, Image favicon) {
        this.stage = stage;
        stage.setOnCloseRequest(e -> close());
        if(favicon != null)
            stage.getIcons().add(favicon);
    }

    final void injectContext(DialogContext context) {
        this.context = context;
    }

    /**
     * @return context that created this dialog for further dialogs
     */
    protected final DialogContext getDialogContext() {
        return context;
    }

    /**
     * @param title to set as stage title
     */
    protected final void setTitle(String title) {
        stage.setTitle(title);
    }

    /**
     * Close the stage, call the {@link #onClose()}
     */
    protected final void close() {
        onClose();
        stage.close();
    }

    /**
     * Set operation to {@link Operation#CONFIRM}
     */
    protected final void setConfirm() {
        operation = Operation.CONFIRM;
    }

    /**
     * Set operation to {@link Operation#CANCEL}
     */
    protected final void setCancel() {
        operation = Operation.CANCEL;
    }
    
    /**
     * Set custom payload
     * @param payload to set
     */
    protected final void setPayload(Object payload) {
        this.payload = payload;
    }

    /**
     * @return dialog operation and payload
     */
    public final Pair<Operation, Object> collect() {
        return new Pair<>(operation, payload);
    }

    /**
     * @return true if the operation equals {@link Operation#CONFIRM}
     */
    public final boolean isConfirmed() {
        return operation == Operation.CONFIRM;
    }

    /**
     * On close hook called before closing dialog.
     */
    protected void onClose() {}
    
    /**
     * Register key bindings here.
     * @param bindings map that will be supplied to you for registration
     */
    protected void keyBindRegisterHook(ObservableMap<KeyCombination, Runnable> bindings) {}

}
