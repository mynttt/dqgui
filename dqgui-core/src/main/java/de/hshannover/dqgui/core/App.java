package de.hshannover.dqgui.core;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import org.tinylog.Logger;
import de.hshannover.dqgui.core.Config.OS;
import de.hshannover.dqgui.core.concurrency.InterceptablePrintStream;
import de.hshannover.dqgui.core.configuration.Views;
import de.hshannover.dqgui.core.model.ApplicationState;
import de.hshannover.dqgui.framework.ApplicationContext;
import de.hshannover.dqgui.framework.dialogs.DialogContext;
import de.hshannover.dqgui.framework.serialization.Serialization;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.input.DataFormat;
import javafx.stage.Stage;

public class App extends Application {
    private static int exit = 0;

    @Override
    public void init() throws Exception {
        InterceptablePrintStream.intercept();
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            Logger.error(e);
            DialogContext.ANONYMOUS_CONTEXT.exceptionDialog(e);
        });
    }

    @Override
    @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
    public void start(Stage primaryStage) {
        primaryStage.close();
        
        try {
            Class.forName("de.hshannover.dqgui.execution.database.api.EngineManager");
            Config.greeting();
            Serialization.register(Config.CONFIG_APPLICATION_STATE, ApplicationState.class);
            DialogContext.setApplicationFavicon(Config.APPLICATION_FAVICON);
            ApplicationContext context = new ApplicationContext("/dqgui/core/injection.yaml");
            context.loadInitial(Views.MAIN, Config.APPLICATION_NAME, stage -> {
                // Don't run this foreground force hack in *nix as JavaFX bugs out there
                if(Config.PROCESS_HOST != OS.NIX)
                    stage.setAlwaysOnTop(true);
             // Hard coded magic numbers because the split panes have weird behavior when it comes to resizing, they don't respect their boundaries for some reason
             // We leave them commented for the time, if they complain of layout issues when resizing really small we uncomment them again XD
                //primaryStage.setMinHeight(639);
                //primaryStage.setMinWidth(1016);
                stage.show();
                stage.sizeToScene();
                if(Config.PROCESS_HOST != OS.NIX)
                    stage.setAlwaysOnTop(false);
            });
        } catch(Exception e) {
            exit = -1;
            Logger.error(e);
        }
    }

    @Override
    @SuppressFBWarnings("DM_EXIT")
    public void stop() throws Exception {
        if(exit < 0)
            System.exit(exit);
        Platform.exit();
        
        //Hack to keep clipboard content after application shutdown
        String s = (String) javafx.scene.input.Clipboard.getSystemClipboard().getContent(DataFormat.PLAIN_TEXT);
        if(s != null && !s.trim().isEmpty()) {
            StringSelection selection = new StringSelection(s);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        }
        
        System.exit(0);
    }

}
