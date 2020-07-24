package de.hshannover.dqgui.execution.database.gui;

import java.net.URL;
import java.util.Objects;

/**
 * Defines GUI integration behavior with dqgui-core
 * 
 * @author myntt
 *
 */
public final class GuiConfiguration {
    private static final String FXML_LOCATION = "/dqgui/dbsupport/fxml/", IMG = "/dqgui/dbsupport/img/";
    
    private static final Icon ICON_W_UNKNOWN = Icon.of(GuiConfiguration.class.getResourceAsStream(IMG + "unknown.png"));
    private static final Icon ICON_L_UNKNOWN = Icon.of(GuiConfiguration.class.getResourceAsStream(IMG + "_UNKNOWN.png"));
    private static final String FXML_JDBC_COMMON = FXML_LOCATION + "_JDBC_COMMON.fxml";
    private static final String FXML_FALLBACK = FXML_LOCATION + "_FALLBACK.fxml";
    private static final String CSS_DEFAULT = "/dqgui/dbsupport/style.css";
    
    private static final GuiConfiguration FALLBACK = new GuiConfiguration(GuiCapabilities.FALLBACK, null, null, null, null);
    private static final GuiConfiguration COMMON_JDBC_NO_ICON = new GuiConfiguration(GuiCapabilities.JDBC_COMMON, null, null, null, null);
    
    public final Icon iconWizard, iconList;
    public final String fxmlLocationInClasspath, cssLocation;
    public final GuiCapabilities capabilities;
    
    private GuiConfiguration(GuiCapabilities c, Icon iconWizard, Icon iconList, String fxmlLocationInClasspath, String cssLocation) {
        Objects.requireNonNull(c, "GUICapabilities must not be null in config");
        capabilities = c;
        this.iconWizard = iconWizard == null ? ICON_W_UNKNOWN : iconWizard;
        this.iconList = iconList == null ? ICON_L_UNKNOWN : iconList;
        this.cssLocation = cssLocation == null ? CSS_DEFAULT : cssLocation;
        switch(c) {
        case CUSTOM:
            URL t = GuiConfiguration.class.getResource(fxmlLocationInClasspath);
            if(t == null)
                throw new IllegalArgumentException("Could not find FXML file: '" + fxmlLocationInClasspath + "' in classpath.");
            this.fxmlLocationInClasspath = fxmlLocationInClasspath;
            break;
        case FALLBACK:
            this.fxmlLocationInClasspath = FXML_FALLBACK;
            break;
        case JDBC_COMMON:
            this.fxmlLocationInClasspath = FXML_JDBC_COMMON;
            break;
        default:
            throw new AssertionError();
        }
    }

    /**
     * Request a fallback GUI configuration that can be used when no GUI implementation is supplied.
     * @return fallback GUI configuration
     */
    public static GuiConfiguration ofFallback() {
        return FALLBACK;
    }
    
    /**
     * Request a common JDBC GUI configuration (database, username, password, host, port, jdbc extra parameters) with placeholder icons
     * @return common JDBC configuration with placeholder icons
     */
    public static GuiConfiguration ofJDBCCommon() {
        return COMMON_JDBC_NO_ICON;
    }
    
    /**
     * Request a common JDBC GUI configuration (database, username, password, host, port, jdbc extra parameters) with placeholder icons
     * @param wizardIcon to display in the wizard section
     * @param listIcon to display in the database environment list
     * @return common JDBC configuration with custom icons
     */
    public static GuiConfiguration ofJDBCCommon(Icon wizardIcon, Icon listIcon) {
        return new GuiConfiguration(GuiCapabilities.JDBC_COMMON, Objects.requireNonNull(wizardIcon, "wizardIcon must not be null"), Objects.requireNonNull(listIcon, "listIcon must not be null"), null, null);
    }
    
    /**
     * Request a custom GUI configuration
     * @param listIcon to display in the database environment list
     * @param fxmlLocationInClasspath absolute classpath path to the GUI FXML file
     * @param cssLocation absolute classpath path to the GUI css file
     * @return custom GUI configuration with a custom 
     */
    public static GuiConfiguration ofCustom(Icon listIcon, String fxmlLocationInClasspath, String cssLocation) {
        Objects.requireNonNull(GuiConfiguration.class.getResource(fxmlLocationInClasspath), "fxmlLocationInClasspath must point to a valid fxml file in the classpath");
        Objects.requireNonNull(GuiConfiguration.class.getResource(cssLocation), "cssLocation must point to a valid css file in the classpath");
        return new GuiConfiguration(GuiCapabilities.CUSTOM, null, listIcon, fxmlLocationInClasspath, cssLocation);
    }
}