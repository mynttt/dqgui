package de.hshannover.dqgui.framework.api;

import java.util.List;
import de.hshannover.dqgui.framework.FXMLLoadingFactory;
import javafx.scene.image.Image;

/**
 * Contains meta information that allows a key to be loaded by the {@link FXMLLoadingFactory}.
 *
 * @author Marc Herschel
 *
 */
public interface Loadable {

    /**
     * Specifies an absolute location to the associated FXML file starting at the classpath root.
     * @return absolute location of FXML file in the current classpath.
     */
    String getFxmlLocation();

    /**
     * Specifies a set of stylesheets associated with the FXML file starting at the classpath root.
     * @return set of stylesheets associated with the FXML file.
     */
    List<String> getStyleSheets();

    /**
     * Specifies the icon to set as favicon (only required when the Loadable is loaded into a window)
     * @return icon to set as favicon.
     */
    default Image getIcon() {
        return null;
    }

}
