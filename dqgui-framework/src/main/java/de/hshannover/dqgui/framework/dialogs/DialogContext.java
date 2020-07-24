package de.hshannover.dqgui.framework.dialogs;

import static de.hshannover.dqgui.framework.JavaFXTools.addAccelerator;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import de.hshannover.dqgui.framework.ErrorUtility;
import de.hshannover.dqgui.framework.FXMLLoadingFactory;
import de.hshannover.dqgui.framework.FXMLLoadingFactory.WindowContent;
import de.hshannover.dqgui.framework.ReflectionUtility;
import de.hshannover.dqgui.framework.api.Loadable;
import de.hshannover.dqgui.framework.dialogs.AbstractDialog.Operation;
import de.hshannover.dqgui.framework.model.ReconstructedException;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * Create dialogs that can either show information to the user or collect user input.<p>
 * Dialogs are always blocking which means that the user has to confirm or close the dialog before being allowed to interact with the application again.
 *
 * @author Marc Herschel
 */
public final class DialogContext {
    private static final String ASSETS_IMAGE_UI = "/dqgui/framework/img/",
                                ASSETS_FXML = "/dqgui/framework/fxml/";
    private static final List<String> CSS = Collections.singletonList("/dqgui/framework/style.css");
    private static final Object[] NO_ARGS = {};
    
    private static Image favicon;
    
    public static void setApplicationFavicon(Image favicon) {
        DialogContext.favicon = favicon;
    }
    
    private enum InternalDialogs implements Loadable {
        TEXT_ERROR_DIALOG("TextErrorDialog.fxml");
        ;

        private final String fxml;
        InternalDialogs(String fxml) {
            this.fxml = fxml;
        }
        
        @Override
        public String getFxmlLocation() {
            return ASSETS_FXML + fxml;
        }

        @Override
        public List<String> getStyleSheets() {
            return CSS;
        }
    }

    /**
     * Anonymous context that allows creation of dialogs without an owner.<br>
     * This will fail on multi-monitor setups as the dialog will always be created on the default monitor and not on the one
     * where the anonymous context has been called from.
     */
    public static final DialogContext ANONYMOUS_CONTEXT = new DialogContext(null);

    private enum Dialogs implements Loadable {
        EXCEPTION("ExceptionDialog.fxml"),
        NOTIFICATION("NotificationDialog.fxml"),
        CONFIRM_CANCEL("ConfirmCancelDialog.fxml");

        private String path;

        Dialogs(String s) {
            path = ASSETS_FXML + s;
        }

        @Override
        public String getFxmlLocation() {
            return path;
        }

        @Override
        public List<String> getStyleSheets() {
            return CSS;
        }

        @Override
        public Image getIcon() {
            return favicon;
        }
    }

    /**
     * Image to display within the dialog.
     */
    public enum DialogStyle {
        /**
         * Warning icon + Warning window text
         */
        WARNING("Warning", ASSETS_IMAGE_UI+"warning.png"),
        /**
         * Error icon + Error window text
         */
        ERROR("Error", ASSETS_IMAGE_UI+"error.png"),
        /**
         * Information icon + Information window text
         */
        INFORMATION("Information", ASSETS_IMAGE_UI+"info.png"),
        /**
         * Confirmation icon + Confirmation window text
         */
        CONFIRMATION("Confirmation", ASSETS_IMAGE_UI+"confirm.png");

        private String desc;
        private Image img;

        DialogStyle(String s, String loc) {
            desc = s;
            img = new Image(getClass().getResourceAsStream(loc));
        }

        public String getDesc() {
            return desc;
        }

        public Image getImg() {
            return img;
        }

    }

    /**
     * Owner of dialog
     */
    private final Stage owner;

    public DialogContext(Stage owner) {
        this.owner = owner;
    }

    /**
     * Create a blocking information dialog.
     * @param notification to display in the info box.
     */
    public void information(String notification) {
        dialog(DialogStyle.INFORMATION, null, notification);
    }

    /**
     * Create a blocking information dialog.
     * @param title to display as title and as header bar.
     * @param notification to display in the info box.
     */
    public void information(String title, String notification) {
        dialog(DialogStyle.INFORMATION, title, notification);
    }

    /**
     * Create a blocking warning dialog.
     * @param notification to display in the info box.
     */
    public void warning(String notification) {
        dialog(DialogStyle.WARNING, null, notification);
    }

    /**
     * Create a blocking warning dialog.
     * @param title to display as title and as header bar.
     * @param notification to display in the info box.
     */
    public void warning(String title, String notification) {
        dialog(DialogStyle.WARNING, title, notification);
    }

    /**
     * Create a blocking error dialog.
     * @param notification to display in the info box.
     */
    public void error(String notification) {
        dialog(DialogStyle.ERROR, null, notification);
    }

    /**
     * Create a blocking error dialog.
     * @param title to display as title and as header bar.
     * @param notification to display in the info box.
     */
    public void error(String title, String notification) {
        dialog(DialogStyle.ERROR, title, notification);
    }

    private void dialog(DialogStyle type, String title, String notification) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        WindowContent obj = FXMLLoadingFactory.load(
                Dialogs.NOTIFICATION,
                (dialog) -> {
                    AbstractDialog d = new NotificationDialog(type, title, notification);
                    d.injectStage(stage, favicon);
                    d.injectContext(this);
                    return d;
                }
            );
        stage.setScene(obj.getScene());
        registerEscapeClose(obj);
        addAccelerator(obj.getScene(), KeyCode.ENTER, () -> ((NotificationDialog) obj.getController()).ack());
        stage.showAndWait();
    }

    /**
     * Ask a blocking question, get a boolean!
     * @param type image to show
     * @param question to ask
     * @return confirm/cancel &lt;=&gt; true/false
     */
    public boolean confirmCancelDialog(DialogStyle type, String question) {
        return confirmCancelDialog(type, null, question);
    }

    /**
     * Ask a blocking question, get a boolean!
     * @param type image to show
     * @param title to show
     * @param question to ask
     * @return confirm/cancel &lt;=&gt; true/false
     */
    public boolean confirmCancelDialog(DialogStyle type, String title, String question) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        WindowContent obj = FXMLLoadingFactory.load(
                Dialogs.CONFIRM_CANCEL,
                (dialog) -> {
                    AbstractDialog d = new ConfirmCancelDialog(type, title, question);
                    d.injectStage(stage, favicon);
                    d.injectContext(this);
                    return d;
                }
            );
        ConfirmCancelDialog dialog = (ConfirmCancelDialog) obj.getController();
        stage.setScene(obj.getScene());
        registerEscapeClose(obj);
        addAccelerator(obj.getScene(), KeyCode.ENTER, () -> ((ConfirmCancelDialog) obj.getController()).confirm());
        stage.showAndWait();
        return dialog.isConfirmed();
    }

    /**
     * Show an exception dialog!
     * @param t throwable to show
     */
    public void exceptionDialog(Throwable t) {
        exceptionDialog(t, "Ups... This should not have happened", "No information specified.");
    }

    /**
     * Show an exception dialog with a reconstructed exception
     * @param ex reconstructed exception to show
     * @param header header to show
     * @param notification to show
     */
    public void exceptionDialog(ReconstructedException ex, String header, String notification) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        WindowContent obj = FXMLLoadingFactory.load(
                Dialogs.EXCEPTION,
                (dialog) -> {
                    AbstractDialog d = new ExceptionDialog(ex, header, notification);
                    d.injectStage(stage, favicon);
                    d.injectContext(this);
                    return d;
                }
            );
        stage.setScene(obj.getScene());
        registerEscapeClose(obj);
        addAccelerator(obj.getScene(), KeyCode.ENTER, () -> ((ExceptionDialog) obj.getController()).cont());
        stage.showAndWait();
    }
    
    /**
     * Show an exception dialog!
     * @param t throwable to show
     * @param header header to show
     * @param notification to show
     */
    public void exceptionDialog(Throwable t, String header, String notification) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        WindowContent obj = FXMLLoadingFactory.load(
                Dialogs.EXCEPTION,
                (dialog) -> {
                    AbstractDialog d = new ExceptionDialog(t, header, notification);
                    d.injectStage(stage, favicon);
                    d.injectContext(this);
                    return d;
                }
            );
        stage.setScene(obj.getScene());
        registerEscapeClose(obj);
        addAccelerator(obj.getScene(), KeyCode.ENTER, () -> ((ExceptionDialog) obj.getController()).cont());
        stage.showAndWait();
    }

    /**
     * Spawn a directory chooser.
     * @param title of the chooser
     * @return chosen dir (null if none)
     * @throws IllegalArgumentException if opened with anonymous context
     */
    public File directoryDialog(String title) {
        return directoryDialog(title, null);
    }
    
    /**
     * Spawn an error dialog with a text box.
     * @param title of the dialog
     * @param headline outlined via red color
     * @param error to display in the text box
     */
    public void textErrorDialog(String title, String headline, String error) {
        customDialog(InternalDialogs.TEXT_ERROR_DIALOG, title, title, headline, error);
    }

    /**
     * Spawn a directory chooser.
     * @param title of the chooser
     * @param initial folder of the chooser
     * @return chosen dir (null if none)
     * @throws IllegalArgumentException if opened with anonymous context
     */
    public File directoryDialog(String title, String initial) {
        if(owner == null)
            throw new IllegalStateException("Anonymous context is not able to spawn a dir chooser");
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(title);
        File initialFile = fileExists(string2file(initial));
        if(initialFile != null)
            chooser.setInitialDirectory(initialFile.isDirectory() ? initialFile : initialFile.getParentFile());
        return chooser.showDialog(owner);
    }

    /**
     * Spawn a file chooser.
     * @param title of the chooser
     * @return chosen file (null if none)
     * @throws IllegalArgumentException if opened with anonymous context
     */
    public File fileDialog(String title) {
        return fileDialog(title, null);
    }

    /**
     * Spawn a file chooser.
     * @param title of the chooser
     * @param initial folder of the chooser
     * @return chosen file (null if none)
     * @throws IllegalArgumentException if opened with anonymous context
     */
    public File fileDialog(String title, String initial) {
        return fileDialog(title, initial, new ExtensionFilter[0]);
    }

    /**
     * Spawn a file chooser.
     * @param title of the chooser
     * @param initial folder of the chooser
     * @param filter filters to apply to dialog
     * @return chosen file (null if none)
     * @throws IllegalArgumentException if opened with anonymous context
     */
    public File fileDialog(String title, String initial, ExtensionFilter...filter) {
        if(owner == null)
            throw new IllegalStateException("Anonymous context is not able to spawn a file chooser");
        FileChooser chooser = new FileChooser();
        chooser.setTitle(title);
        for(ExtensionFilter f : filter)
            chooser.getExtensionFilters().add(f);
        File initialFile = fileExists(string2file(initial));
        if(initialFile != null)
            chooser.setInitialDirectory(initialFile.isDirectory() ? initialFile : initialFile.getParentFile());
        return chooser.showOpenDialog(owner);
    }

    /**
     * Spawn a customized dialog with no title and arguments.
     * @param load loadable of the dialog
     * @return the dialogs registered operation
     */
    public Pair<Operation, Object> customDialog(Loadable load) {
        return customDialog(load, "", NO_ARGS);
    }
    
    /**
     * Spawn a customized dialog with no title.
     * @param load loadable of the dialog
     * @param args to send to the dialogs constructor
     * @return the dialogs registered operation
     */
    public Pair<Operation, Object> customDialogNoTitle(Loadable load, Object...args) {
        return customDialog(load, "", args);
    }

    /**
     * Spawn a customized dialog.
     * @param load loadable of the dialog
     * @param title of the dialog window
     * @param args to send to the dialogs constructor
     * @return the dialogs registered operation
     */
    public Pair<Operation, Object> customDialog(Loadable load, String title, Object... args) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        WindowContent obj = FXMLLoadingFactory.load(
                load,
                (dialog) -> {
                    if(!AbstractDialog.class.isAssignableFrom(dialog))
                        throw new IllegalArgumentException("Dialogs must extend AbstractDialog.");
                    try {
                        AbstractDialog d = (AbstractDialog) ReflectionUtility.createInstance(dialog, args);
                        d.injectStage(stage, favicon);
                        d.injectContext(this);
                        return d;
                    } catch (Exception e) {
                        throw ErrorUtility.rethrow(e);
                    }
                }
            );
        AbstractDialog dialog = (AbstractDialog) obj.getController();
        stage.setScene(obj.getScene());
        registerEscapeClose(obj);
        ((AbstractDialog) obj.getController()).keyBindRegisterHook(obj.getScene().getAccelerators());
        stage.showAndWait();
        return dialog.collect();
    }

    private void registerEscapeClose(WindowContent obj) {
        obj.getScene().setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ESCAPE)
                ((AbstractDialog) obj.getController()).close();
        });
    }
    
    private static File fileExists(File f) {
        return f != null && f.exists() ? f : null;
    }
    
    private static File string2file(String s) {
        File f = null;
        if(s != null) {
            Path p = Paths.get(s);
            if(Files.exists(p))
                f = p.toFile();
        }
        return f;
    }
}
