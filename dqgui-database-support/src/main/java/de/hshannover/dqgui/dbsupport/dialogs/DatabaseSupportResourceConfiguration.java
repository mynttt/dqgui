package de.hshannover.dqgui.dbsupport.dialogs;

import java.util.Collections;
import java.util.List;
import javafx.scene.image.Image;

public final class DatabaseSupportResourceConfiguration {
    public static final String ROOT = "/dqgui/dbsupport/";
    public static final Image FAV = new Image(DatabaseSupportResourceConfiguration.class.getResourceAsStream(ROOT+"favicon.png"));
    public static final List<String> CSS = Collections.singletonList(ROOT + "style.css");
}