package de.hshannover.dqgui.core.configuration;

import java.util.List;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.framework.api.Component;

public enum Components implements Component {
    NO_REPORTS_AVAILABLE
        ("NoReportsAvailable.fxml"),
    REPORT_DATA_COMPONENT
        ("ReportData.fxml"),
    REPORT_FAILED_TO_LOAD
        ("ReportFailedToLoad.fxml"),
    PROPERTIES_FS_REPO
        ("PropertiesRepoFilesystemService.fxml"),
    PROPERTIES_DB_REPO
        ("PropertiesRepoDatabaseService.fxml"),
    REPORT_HEADER
        ("ReportHeader.fxml"),
    REPORT_IDENTIFIER_OVERVIEW
        ("ReportIdentifierOverview.fxml"), 
    REPORT_HEADER_FAILED_TO_LOAD
        ("ReportHeaderFailedToLoad.fxml")
    ;

    Components(String fxmlLocation) {
        this.fxmlLocation = Config.APPLICATION_PATH_FXMLROOT_COMPONENT + fxmlLocation;
    }

    private final String fxmlLocation;

    @Override
    public String getFxmlLocation() {
        return fxmlLocation;
    }

    @Override
    public List<String> getStyleSheets() {
        return Config.APPLICATION_PATHS_CSS;
    }
}
