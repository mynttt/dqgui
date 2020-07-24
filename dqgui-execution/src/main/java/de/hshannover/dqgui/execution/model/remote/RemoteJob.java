package de.hshannover.dqgui.execution.model.remote;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import com.google.common.hash.Hashing;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import de.hshannover.dqgui.execution.model.Project;

/**
 * A remote job can be send to a remote execution server to be enqued within the work queue.<br>
 * The job id is assigned automatically in the constructor.
 * 
 * @author myntt
 *
 */
public class RemoteJob implements Validatable<RemoteJob> {
    private final Map<DSLComponentType, Map<String, String>> sources;
    private final Set<DatabaseConnection> connections;
    private final String action, creator, environment, projectGuid;
    private final boolean optimize;
    private final List<String> identifiers, actionValues;
    private final String jobId;
    
    public RemoteJob(Project project, Map<DSLComponentType, Map<String, String>> sources, Set<DatabaseConnection> connections,
            String action, String creator, String environment, boolean optimize, List<String> identifiers, List<String> actionValues) {
        this.projectGuid = project.getGuid();
        this.sources = sources;
        this.connections = connections;
        this.action = action;
        this.creator = creator;
        this.environment = environment;
        this.actionValues = actionValues;
        this.optimize = optimize;
        this.identifiers = identifiers;
        this.jobId = Hashing.sha256().hashString(project.getGuid() + action + System.nanoTime(), StandardCharsets.UTF_8).toString();
    }

    public Map<DSLComponentType, Map<String, String>> getSources() {
        return sources;
    }

    public Set<DatabaseConnection> getConnections() {
        return connections;
    }

    public String getAction() {
        return action;
    }

    public String getCreator() {
        return creator;
    }

    public String getEnvironment() {
        return environment;
    }

    public boolean isOptimize() {
        return optimize;
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }
    
    public List<String> getActionValues() {
        return actionValues;
    }

    public String getProjectGuid() {
        return projectGuid;
    }
    
    public String getJobId() {
        return jobId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(jobId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RemoteJob other = (RemoteJob) obj;
        return Objects.equals(jobId, other.jobId);
    }

    @Override
    public RemoteJob validate() {
        notNullOrBlank(jobId, "jobId");
        notNull(identifiers, "identifiers");
        notNullOrBlank(jobId, "jobId");
        identifiers.forEach(i -> notNullOrBlank(i, "identifiers entry"));
        notNullOrBlank(action, "action");
        notNullOrBlank(creator, "creator");
        notNullOrBlank(environment, "environment");
        notNullOrBlank(projectGuid, "projectGuid");
        notNull(connections, "null connections present");
        connections.forEach(i -> notNull(i, "connections entry"));
        notNull(sources, "sources");
        sources.entrySet().forEach(i -> notNull(i, "sources entry"));
        for(DatabaseConnection con : connections) {
            if(!con.engineIdentifierIsRegistered())
                throw new IllegalArgumentException("Connection with guid " + con.getGuid() + " is missing engine implementation [" + con.requiredEngineImplementation() + "].");
        }
        return this;
    }
    
    @Override
    public String toString() {
        return String.format("%s [action=%s, env=%s, creator=%s, project=%s]", jobId, action, environment, creator, projectGuid);
    }
}
