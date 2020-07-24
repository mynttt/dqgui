package de.hshannover.dqgui.core.configuration;

import java.util.List;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.framework.api.Loadable;
import javafx.scene.image.Image;

public enum Dialogs implements Loadable {
    BATCH_SAVE
        ("BatchSaveDialog.fxml"),
    MULTI_REMOVE
        ("MultiRemoveDialog.fxml")
        ;

    private String path;

    Dialogs(String s) {
        path = Config.APPLICATION_PATH_FXMLROOT_DIALOGS_CUSTOM + s;
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