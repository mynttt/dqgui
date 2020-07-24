package de.hshannover.dqgui.core.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.core.concurrency.TaskHandler;
import de.hshannover.dqgui.core.model.ApplicationProperties;
import de.hshannover.dqgui.core.model.ApplicationState;
import de.hshannover.dqgui.core.model.ProjectHandle;
import de.hshannover.dqgui.core.rsupport.RServeInstance;
import de.hshannover.dqgui.core.util.ExceptionRecoveryTips;
import de.hshannover.dqgui.core.util.RemoteConnection;
import de.hshannover.dqgui.framework.api.Dependencies;
import de.hshannover.dqgui.framework.dialogs.DialogContext;
import de.hshannover.dqgui.framework.serialization.Serialization;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class DependencyContainer implements Dependencies {
    ApplicationProperties properties;
    ProjectHandle projectHandle;
    TaskHandler taskHandler;
    RServeInstance rServeInstance;
    ApplicationState applicationState;
    RemoteConnection remoteConnection;

    @SuppressFBWarnings("URF_UNREAD_FIELD")
    @Override
    public void loadEager() {
        this.properties = Serialization.recover(ApplicationProperties.class);
        this.applicationState = Serialization.recover(ApplicationState.class); 
        this.rServeInstance = new RServeInstance(properties.getrPath(), properties.getrScriptPath(), properties.getrArgs(), properties.isRServeAutoStart());
        this.projectHandle = new ProjectHandle(properties);
        this.taskHandler = projectHandle.getTaskHandler();
        this.remoteConnection = new RemoteConnection(projectHandle);
        
        if(properties.isRServeAutoStart()) {
            try {
                rServeInstance.startInstance();
            } catch (Exception e) {
                properties.setRServeAutoStart(false);
                DialogContext.ANONYMOUS_CONTEXT.exceptionDialog(e, "Failed to start RServe instance!", ExceptionRecoveryTips.RSERVE_ON_STARTUP.getTip());
            }
        }
        
        if(properties.serverSetProperty().get()) {
            remoteConnection.connectEventSocket(properties.getRemoteHost(), properties.getRemoteKey());
            projectHandle.getSelectedProject()
                .safeGet()
                .ifPresent(p -> remoteConnection.collectAll(p, properties.getRemoteHost(), properties.getRemoteKey()));
        }
        
        Runtime.getRuntime().addShutdownHook(new Thread(() ->  {
            System.out.println("\nRunning shutdown hooks...\n");
            @SuppressWarnings("serial")
            Map<String, Callable<Void>> hooks = new HashMap<String, Callable<Void>>() {{
                put("REMOTE_CONECTION", () -> { remoteConnection.close(); return null; });
                put("RSERVE_INSTANCE", () -> { rServeInstance.stopInstance(); return null; });
                put("PROJECT_HANDLE", () -> {projectHandle.close(); return null;});
            }};
            
            for(Map.Entry<String, Callable<Void>> e : hooks.entrySet()) {
                try {
                    e.getValue().call();
                    System.out.println("PASS: " +e.getKey());
                } catch (Exception ex) {
                    System.err.println("FAIL: " +e.getKey());
                }
            }
            
            System.out.println("\nShutdown completed.");
         }));
        

    }

    @Override
    public String serializationRoot() {
        return Config.APPLICATION_CONFIG;
    }

    @Override
    public void onClose() {}
}
