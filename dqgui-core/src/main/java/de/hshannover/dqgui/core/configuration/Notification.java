package de.hshannover.dqgui.core.configuration;

import java.util.List;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.framework.api.Loadable;
import javafx.scene.image.Image;

public enum Notification implements Loadable {
    TASK_ERROR
        ("TaskError.fxml"),
    TASK_SUCCESS
        ("TaskSuccess.fxml"),
    ERROR
        ("Error.fxml"),
    SUCCESS
        ("Success.fxml"),
    WARNING
        ("Warning.fxml")
    ;

    private String path;

    Notification(String s) {
        path = Config.APPLICATION_PATH_FXMLROOT_NOTIFICATIONS+s;
    }

    @Override
    public String getFxmlLocation() {
        return path;
    }

    @Override
    public List<String> getStyleSheets() {
        return Config.APPLICATION_PATHS_CSS;
    }

    @Override
    public Image getIcon() {
        return Config.APPLICATION_FAVICON;
    }

}
