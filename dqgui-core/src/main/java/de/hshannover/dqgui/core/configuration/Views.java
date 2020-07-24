package de.hshannover.dqgui.core.configuration;

import java.util.List;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.framework.WindowInstances;
import de.hshannover.dqgui.framework.api.Manageable;
import javafx.scene.image.Image;

public enum Views implements Manageable {

    MAIN
        ("Main.fxml", WindowInstances.ONCE_AT_A_TIME),
    ABOUT
        ("About.fxml", WindowInstances.ONCE_AT_A_TIME),
    PROPERTIES
        ("Properties.fxml", WindowInstances.ONCE_AT_A_TIME),
    RSERVE
        ("RServe.fxml", WindowInstances.ONCE_AT_A_TIME),
    LIVELOG
        ("Livelog.fxml", WindowInstances.UNLIMITED_MANAGED_BY_IDENTIFIER),
    REPORTS
        ("Reports.fxml", WindowInstances.ONCE_AT_A_TIME),
    REPOSITORY_SEARCH
        ("RepositorySearch.fxml", WindowInstances.ONCE_AT_A_TIME),
    LICENSES
        ("Licenses.fxml", WindowInstances.ONCE_AT_A_TIME),
    REPO_IMPORT
        ("RepoImport.fxml", WindowInstances.ONCE_AT_A_TIME),
    REPO_EXPORT
        ("RepoExport.fxml", WindowInstances.ONCE_AT_A_TIME),
    PROJECTS
        ("Projects.fxml", WindowInstances.ONCE_AT_A_TIME),
    REMOTE
        ("Remote.fxml", WindowInstances.ONCE_AT_A_TIME),
    HELP
        ("Help.fxml", WindowInstances.ONCE_AT_A_TIME),
    DB_ENV_COMPARE
        ("ConnectionCompare.fxml", WindowInstances.ONCE_AT_A_TIME)
    ;

    private String path;
    private WindowInstances instances;

    Views(String path, WindowInstances instances) {
        this.path =  Config.APPLICATION_PATH_FXMLROOT + path;
        this.instances = instances;
    }

    @Override
    public WindowInstances getWindowInstances() {
        return instances;
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
