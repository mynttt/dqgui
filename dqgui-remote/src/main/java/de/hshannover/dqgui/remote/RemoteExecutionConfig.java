package de.hshannover.dqgui.remote;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import com.google.common.hash.Hashing;

public class RemoteExecutionConfig {
    private final boolean rServe, enforceSSL;
    private final int port, executorPoolSize, pruneUncollectedEveryHours, keepJobsForDays;
    private final String key;
    
    public RemoteExecutionConfig() {
        this.pruneUncollectedEveryHours = 24;
        this.keepJobsForDays = 30;
        this.rServe = this.enforceSSL = false;
        this.port = 9999;
        this.executorPoolSize = 8;
        this.key = Hashing.sha256().hashString(UUID.randomUUID().toString(), StandardCharsets.UTF_8).toString();
    }
    
    public int getPruneUncollectedEveryHours() {
        return pruneUncollectedEveryHours;
    }

    public int getKeepJobsForDays() {
        return keepJobsForDays;
    }

    public boolean isrServe() {
        return rServe;
    }
    
    public boolean isEnforceSSL() {
        return enforceSSL;
    }

    public int getPort() {
        return port;
    }
    
    public String getKey() {
        return key;
    }

    public int getExecutorPoolSize() {
        return executorPoolSize;
    }
}