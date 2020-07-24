package de.hshannover.dqgui.framework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.tinylog.Logger;
import de.hshannover.dqgui.framework.api.Component;
import de.hshannover.dqgui.framework.api.Loadable;
import de.hshannover.dqgui.framework.api.Manageable;
import de.hshannover.dqgui.framework.dialogs.DialogContext;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Helps loading FXML files.
 *
 * @author Marc Herschel
 *
 */
public final class FXMLLoadingFactory {

    /**
     * A pair of a scene containing the FXML root node and the mapped {@link AbstractController}.
     */
    static class ManagedWindowContent {
        private final Scene scene;
        private final AbstractController controller;

        ManagedWindowContent(Scene scene, AbstractController controller) {
            this.scene = scene;
            this.controller = controller;
        }

        public Scene getScene() { return scene; }
        public AbstractController getController() { return controller; }

        @Override
        public int hashCode() {
            return Objects.hash(scene, controller);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof ManagedWindowContent) {
                ManagedWindowContent other = (ManagedWindowContent) obj;
                return Objects.equals(scene, other.scene) && Objects.equals(controller, other.controller);
            }
            return false;
        }
    }

    /**
     * A pair of a FXML root node and the mapped {@link AbstractComponentController}.
     */
    public static class ComponentContent {
        private final Parent parent;
        private final AbstractComponentController controller;

        ComponentContent(Parent parent, AbstractComponentController controller) {
            this.parent = parent;
            this.controller = controller;
        }

        public Parent getParent() { return parent; }
        public AbstractComponentController getController() { return controller; }

        @Override
        public int hashCode() {
            return Objects.hash(parent, controller);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof ComponentContent) {
                ComponentContent other = (ComponentContent) obj;
                return Objects.equals(parent, other.parent) && Objects.equals(controller, other.controller);
            }
            return false;
        }
    }

    /**
     * A pair of a scene containing the FXML root node and the mapped controller.
     */
    public static class WindowContent {
        private final Scene scene;
        private final Object controller;

        WindowContent(Scene scene, Object controller) {
            this.scene = scene;
            this.controller = controller;
        }

        public Scene getScene() { return scene; }
        public Object getController() { return controller; }

        @Override
        public int hashCode() {
            return Objects.hash(scene, controller);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof WindowContent) {
                WindowContent other = (WindowContent) obj;
                return Objects.equals(scene, other.scene) && Objects.equals(controller, other.controller);
            }
            return false;
        }
    }

    private final AbstractInjector injector;

    FXMLLoadingFactory(AbstractInjector injector) {
        this.injector = injector;
    }

    ComponentContent load(Component component, Object[] args, ApplicationContext context, Stage stage, AbstractWindowController owner) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(component.getFxmlLocation()));
        loader.setControllerFactory(createLoaderFactoryForArgs(args, context, stage, owner));
        Parent p = null;
        try {
            p = loader.load();
        } catch (IOException e) {
            throw ErrorUtility.rethrow(e);
        }
        for(String css : component.getStyleSheets())
            p.getStylesheets().add(getClass().getResource(css).toExternalForm());
        p.requestFocus();
        return new ComponentContent(p, loader.getController());
    }

    ManagedWindowContent load(Manageable view, Stage stage, Object[] args, ApplicationContext context) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(view.getFxmlLocation()));
        loader.setControllerFactory(createLoaderFactoryForArgs(args, context, stage, null));
        Scene tmp = null;
        try {
            tmp = new Scene(loader.load());
        } catch (Exception e) {
            throw ErrorUtility.rethrow(e);
        }
        for(String css : view.getStyleSheets())
            tmp.getStylesheets().add(getClass().getResource(css).toExternalForm());
        tmp.getRoot().requestFocus();
        return new ManagedWindowContent(tmp, loader.getController());
    }

    private Callback<Class<?>, Object> createLoaderFactoryForArgs(Object[] args, ApplicationContext context, Stage stage, AbstractController owner) {
        return (controller) -> {
            if(!AbstractController.class.isAssignableFrom(controller))
                throw new IllegalArgumentException("Controllers must extend AbstractController.");
            try {
                AbstractController obj = (AbstractController) this.injector.inject(controller, args);
                DialogContext dialogContext = null;
                if(stage != null)
                    dialogContext = new DialogContext(stage);
                obj.injectStage(stage);
                obj.injectApplicationContext(context);
                obj.injectDialogContext(dialogContext);
                obj.injectOwner(owner);
                obj.markLoaded();
                return obj;
            } catch (Exception e) {
                Logger.error("Injector failed to create controller instance because of {}", e.getClass().getName());
                throw ErrorUtility.rethrow(e);
            }
        };
    }

    public static <K extends Loadable> WindowContent load(K view) {
        return load(view, null);
    }

    public static <K extends Loadable> WindowContent load(String path, String cssPath) {
        List<String> l = new ArrayList<>(); l.add(cssPath);
        return load(path, l, null);
    }

    public static <K extends Loadable> WindowContent load(String path, List<String> cssPaths) {
        return load(path, cssPaths, null);
    }

    public static <K extends Loadable> WindowContent load(K view, Callback<Class<?>, Object> controllerFactory) {
        return load(view.getFxmlLocation(), view.getStyleSheets(), controllerFactory);
    }

    public static WindowContent load(String path, List<String> cssPath, Callback<Class<?>, Object> controllerFactory) {
        FXMLLoader loader = new FXMLLoader(FXMLLoadingFactory.class.getResource(path));
        if(controllerFactory != null)
            loader.setControllerFactory(controllerFactory);
        Scene tmp = null;
        try {
            tmp = new Scene(loader.load());
        } catch (IOException e) {
            throw ErrorUtility.rethrow(e);
        }
        for(String css : cssPath)
            tmp.getStylesheets().add(FXMLLoadingFactory.class.getResource(css).toExternalForm());
        tmp.getRoot().requestFocus();
        return new WindowContent(tmp, loader.getController());
    }
}
