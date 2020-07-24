package de.hshannover.dqgui.core.rsupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.tinylog.Logger;
import de.hshannover.dqgui.core.Utility;
import de.hshannover.dqgui.framework.ErrorUtility;
import de.hshannover.dqgui.framework.signal.Signal;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;

/**
 * Manages an R process running the RServe library implementation.
 *
 * @author Marc Herschel
 *
 */
public final class RServeInstance {
    private Process instance;
    private List<String> commands;
    private File pwd;
    private RServeInstanceLogUpdate worker;
    private ChangeListener<String> changeListener;
    private StringBuilder sb = new StringBuilder(4000);
    private boolean autostart, updateUi;
    private RMetaInformation meta = new RMetaInformation();
    private final Signal rServeStopSignal = new Signal(),
                         rServeStartSignal = new Signal();

    /**
     * Construct a new instance.
     * @param rPath path of R
     * @param scriptPath path of script to run
     * @param args to supply to R
     * @param autostart if this should autostart
     */
    public RServeInstance(String rPath, String scriptPath, String args, boolean autostart) {
        this.commands = buildCommand(rPath, scriptPath, args);
        this.autostart = autostart;
    }

    /**
     * Start the RServe instance.
     * @throws IOException if script not existing.
     */
    public void startInstance() throws Exception {
        if(alive()) return;
        Logger.info("Attempting RServe instance start.");
        sb.setLength(0);
        worker = new RServeInstanceLogUpdate();
        if(updateUi) {
            worker.messageProperty().addListener(changeListener);
        }
        Thread t = new Thread(worker);
        t.setName("rserve-instance-update-log");
        t.setDaemon(true);
        t.start();
        Logger.info("RServe started.");
        Thread.sleep(100);
        Thread.sleep(100);
        rServeStartSignal.fire();
    }

    /**
     * Stop the RServ instance.
     */
    public void stopInstance() {
        if(!alive()) return;
        kill();
    }

    public boolean alive() {
        return instance != null && instance.isAlive();
    }

    /**
     * @return stdout/err buffer.
     */
    public String getBufferedString() {
        if(!alive())
            throw new IllegalStateException("RInstance must run at least on time to provide a buffered String.");
        return sb.toString();
    }

    /**
     * @return observable string property of rserv output.
     */
    public ReadOnlyStringProperty getStringProperty() {
        if(!alive())
            throw new IllegalStateException("RInstance must run at least on time to provide a StringProperty.");
        return worker.messageProperty();
    }

    /**
     * @param changeListener to update for.
     */
    public void beginUiUpdate(ChangeListener<String> changeListener) {
        this.changeListener = changeListener;
        if(alive()) {
            worker.messageProperty().addListener(changeListener);
        }
        updateUi = true;
    }

    /**
     * Stop updating for listener.
     */
    public void stopUiUpdate() {
        changeListener = null;
        updateUi = false;
    }

    /**
     * Update R variables
     * @param rPath new R path
     * @param scriptPath new Script to run
     * @param args new args to supply
     */
    public void swapValues(String rPath, String scriptPath, String args) {
        commands = buildCommand(rPath, scriptPath, args);
        updateMeta(rPath, scriptPath, args);
        stopInstance();
        if(!this.autostart)
            return;
        try {
            startInstance();
        } catch (Exception e) {
            throw ErrorUtility.rethrow(e);
        }
    }

    /**
     * @param autostart parameter to set
     */
    public void setAutoStart(boolean autostart) {
        this.autostart = autostart;
        if(!this.autostart)
            return;
        try {
            startInstance();
        } catch (Exception e) {
            throw ErrorUtility.rethrow(e);
        }
    }

    /**
     * @return meta information about instance
     */
    public RMetaInformation requestMeta() {
        meta.autostart = autostart;
        return meta;
    }

    public Signal getrServeStopSignal() {
        return rServeStopSignal;
    }

    public Signal getrServeStartSignal() {
        return rServeStartSignal;
    }

    private void kill() {
        Utility.killChildProcessTree();
        instance.destroy();
        try {
            instance.waitFor(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw ErrorUtility.rethrow(e);
        }
    }

    private class RServeInstanceLogUpdate extends Task<Void> {
        private final static String ENDED = "\nProcess has been ended.";
        StringBuilder internalBuffer = new StringBuilder(4000);
        BufferedReader reader;
        String buffer;

        @SuppressFBWarnings({"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", "DM_DEFAULT_ENCODING"})
        private RServeInstanceLogUpdate() throws IOException {
            instance = new ProcessBuilder(commands)
                .directory(pwd)
                .redirectErrorStream(true)
                .start();
            reader = new BufferedReader(new InputStreamReader(instance.getInputStream()));
        }

        @Override
        @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
        protected Void call() throws Exception {
            String s = "PWD: " + pwd.getAbsolutePath();
            sb.append(s);
            updateMessage(s);
            try {
                while ((buffer = reader.readLine()) != null) {
                    internalBuffer.append(buffer).append('\n');
                    if(!reader.ready()) {
                    //In case too many 1 line blocks are being created here we wait 150ms to be sure we don't spam the JavaFX event queue.
                        Thread.sleep(150);
                        sb.append(internalBuffer.toString());
                        updateMessage(internalBuffer.deleteCharAt(internalBuffer.length()-1).toString());
                        internalBuffer.setLength(0);
                    }
                    if(isCancelled())
                        break;
                }
            } catch (IOException e) {
                Logger.info("Stream closed.");
            }
            if(instance.isAlive()) {
                throw new RuntimeException("Why is the process still running?!");
            } else {
                Logger.info("R instance died.");
            //If R fails to start it has some error messages that are still processed in the event queue before this worker here dies.
            //We give 50ms for these messages to update the console before we emit the stop signal and kill the worker.
            //Removing this will sometimes leave incomplete messages for the user.
                Thread.sleep(50);
                Platform.runLater(() -> rServeStopSignal.fire());
            }
            sb.append(ENDED);
            updateMessage(ENDED);
            return null;
        }
    }

    private List<String> buildCommand(String r, String path, String args) {
        if(r == null || path == null) {
            updateMeta(r, path, args);
            return null;
        }
        List<String> command = new ArrayList<>();
        args = args == null ? "" : args;
        command.add(r);
        command.add("-f");
        command.add(path);
        command.addAll(Arrays.asList(args.split("\\s+")));
        updateMeta(r, path, args);
        return command;
   }
    
   @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
   private void updateMeta(String r, String path, String args) {
       Path script = path == null ? null : Paths.get(path);
       meta.R = r == null ? "undefined" : r;
       meta.script = script == null ? "undefined" : script.getFileName().toString();
       meta.PWD = script == null ? "undefined" : script.getParent().toAbsolutePath().toString();
       meta.args = (args == null || args.isEmpty()) ? "none" : args;
       pwd = script == null ? null : script.getParent().toFile();
   }

}
