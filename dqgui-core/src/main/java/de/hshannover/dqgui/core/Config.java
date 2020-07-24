package de.hshannover.dqgui.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.tinylog.Logger;
import com.google.common.io.CharStreams;
import de.hshannover.dqgui.core.configuration.Components;
import de.hshannover.dqgui.core.configuration.Dialogs;
import de.hshannover.dqgui.core.configuration.Views;
import de.hshannover.dqgui.execution.database.api.EngineManager;
import de.hshannover.dqgui.framework.ErrorUtility;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.scene.image.Image;
import net.harawata.appdirs.AppDirsFactory;

/**
 * Place all globally referenced variables here!
 * Variables must be static!
 */
@SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
public final class Config {

    private Config() {}

    public enum OS {
        WIN, NIX
    }

    /*
     * Application Related Settings
     */
    public static final Charset APPLICATION_CHARSET = StandardCharsets.UTF_8;
    public static final String APPLICATION_NAME = "DQGUI";
    public static final String APPLICATION_VERSION = getVersion();
    public static final Image APPLICATION_FAVICON = new Image(Config.class.getResourceAsStream("/dqgui/core/assets/img/favicon.png"));

    /*
     * Local Path Related Settings
     */
    public static final List<String> APPLICATION_PATHS_CSS = Collections.unmodifiableList(Arrays.asList(
                "/dqgui/core/javafx/style.css"
            ));

    public static final String APPLICATION_PATH_FXMLROOT = "/dqgui/core/javafx/fxml/";
    public static final String APPLICATION_PATH_FXMLROOT_WIZARD_COMPONENT = APPLICATION_PATH_FXMLROOT + "createsave/";
    public static final String APPLICATION_PATH_FXMLROOT_COMPONENT = APPLICATION_PATH_FXMLROOT + "components/";
    public static final String APPLICATION_PATH_FXMLROOT_NOTIFICATIONS = APPLICATION_PATH_FXMLROOT + "notifications/";
    public static final String APPLICATION_PATH_FXMLROOT_DIALOGS_CUSTOM = APPLICATION_PATH_FXMLROOT + "dialogs_custom/";
    private static final String APPLICATION_ASSETS = "/dqgui/core/assets/";
    public static final String APPLICATION_PATH_LANDING_PAGE = APPLICATION_ASSETS + "html/landing.html";
    public static final String APPLICATION_PATH_ERROR_PAGE = APPLICATION_ASSETS + "html/error.html";
    public static final String APPLICATION_PATH_ASSETS_IMAGE_UI = APPLICATION_ASSETS + "img/ui/";
    public static final String APPLICATION_PATH_ASSETS_IMAGE_THREADING = APPLICATION_PATH_ASSETS_IMAGE_UI + "threading/";
    public static final String APPLICATION_PATH_ASSETS_IMAGE_DB_TREE = APPLICATION_ASSETS + "img/dbTreeMenu/";
    
    public static final String DOCU_NOT_FOUND = "<html><body><h1>DOCUMENTATION NOT FOUND - DOCUMENTATION WILL NOT BE INCLUDED WITH :run TASK!</h1></body></html>";
    public static final String APPLICATION_DOCUMENTATION_LASTGEN = "/dqgui/documentation/LASTGEN";
    public static final String APPLICATION_DOCUMENTATION_INDEX = "/dqgui/documentation/projectDocs/index.html";
    public static final String APPLICATION_DOCUMENTATION_JAVADOC = "/dqgui/documentation/javaDocs/index.html";

    /*
     * Path Library Settings
     */
    private static final String APPLICATION_IDENTIFIER = "DQGui";
    private static final String APPLICATION_IDENTIFIER_TEAM = APPLICATION_IDENTIFIER;
    private static final String APPLICATION_IDENTIFIER_VERSION = null;

    /*
     * RServe Instance Process Management
     */
    public static final OS PROCESS_HOST = (System.getProperty("os.name").toLowerCase().contains("win")) ? OS.WIN : OS.NIX;
    static final long PROCESS_OWN_PID = Utility.getJvmPid();
    static final boolean PROCESS_HAVE_PID = PROCESS_OWN_PID > 0;
    static final String[] PROCESS_KILL_FALLBACK_WINDOWS = { "R.exe", "RTerm.exe" };
    static final String[] PROCESS_KILL_FALLBACK_LINUX = { "R" };
    
    /*
     * User Path Related
     */
    public static final String APPLICATION_CONFIG = PROCESS_HOST == OS.WIN ? 
            
            //*win resolvement
            Paths.get(
            AppDirsFactory.getInstance().getUserConfigDir(
                    APPLICATION_IDENTIFIER, 
                    APPLICATION_IDENTIFIER_VERSION, 
                    APPLICATION_IDENTIFIER_TEAM, true))
            .getParent()
            .toAbsolutePath()
            .toString()
            
            //*nix resolvement
            : Paths.get(AppDirsFactory.getInstance().getUserConfigDir(
                    APPLICATION_IDENTIFIER, 
                    APPLICATION_IDENTIFIER_VERSION, 
                    APPLICATION_IDENTIFIER_TEAM, true))
            .toAbsolutePath()
            .toString();
    
    public static Path configPathFor(String file) { return Paths.get(APPLICATION_CONFIG, file); }

    private static String getVersion() {
        try {
            return CharStreams.toString(new InputStreamReader(
                    Config.class.getResourceAsStream("/dqgui/core/VERSION"), APPLICATION_CHARSET));
        } catch (IOException e) {
            throw ErrorUtility.rethrow(e);
        }
    }

    public static final String USER_IDENTIFIER = Utility.generateUserIdentifier();
    
    /*
     * Config Paths
     */
    public static final Path DATABASE_REPO_CACHE = configPathFor("database-cache/");
    static final Path CONFIG_APPLICATION_STATE = configPathFor("state.json");
    private static final Path CONFIG_IQM4HD_LOGS = configPathFor("logs-iqm4hd/");
    private static final Path CONFIG_REPORT = configPathFor("reports/");

    /**
     * Initializations and greeting.
     * Test paths on your system.
     */
    static void greeting() {

        StringBuilder sb = new StringBuilder(2000);
        
        try(BufferedReader r = new BufferedReader(new InputStreamReader(Config.class.getResourceAsStream("/dqgui/core/greeting"), APPLICATION_CHARSET))) {
            String s;
            while((s = r.readLine()) != null)
                sb.append(s).append('\n');
            sb.append((String.format("%nUser:       %s%nOS:         %s%nPID:        %s%nTime:       %s%nVersion:    %s%nConfig:     %s%nViews:      %s%nComponents: %s%nDialogs:    %s%n%n=== Database Abstraction Layer ===%nEngines loaded: %s%nEngines IQM4HD: %s%nEngines Repo  : %s%n",
                    USER_IDENTIFIER,
                    PROCESS_HOST,
                    PROCESS_HAVE_PID ? PROCESS_OWN_PID : "Unknown. Fallback handler ready.",
                    Instant.now(),
                    APPLICATION_VERSION,
                    APPLICATION_CONFIG,
                    Arrays.toString(Views.values()),
                    Arrays.toString(Components.values()),
                    Arrays.toString(Dialogs.values()),
                    EngineManager.enginesAll(),
                    EngineManager.enginesIqm4hd(),
                    EngineManager.enginesRepository())));
        } catch(IOException e) {
            throw ErrorUtility.rethrow(e);
        }
        
        Logger.info(sb.toString());

        try {
            if(!Files.exists(CONFIG_IQM4HD_LOGS))
                Files.createDirectories(CONFIG_IQM4HD_LOGS);
            if(!Files.exists(CONFIG_REPORT))
                Files.createDirectories(CONFIG_REPORT);
        } catch(IOException e) {
            Logger.error("Failed to create logs folder...");
            throw ErrorUtility.rethrow(e);
        }
    }
}
