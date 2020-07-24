package de.hshannover.dqgui.execution.model.remote;

import java.time.Instant;
import java.util.Objects;

/**
 * A RemoteStatusReport allows to query the curren job status.
 * 
 * @author myntt
 *
 */
public class RemoteStatusReport implements RemoteResponse {
    
    /**
     * The ExecutionState signals the stage the job is in.
     */
    public enum ExecutionState {
        QUEUED, RUNNING, WAIT_FOR_COLLECT;
    }
    
    private final String environment, action, projectGuid, jobId;
    private final Instant submitted;
    private ExecutionState state = ExecutionState.QUEUED;
    
    public RemoteStatusReport(String environment, String action, String projectGuid, String jobId) {
        this.environment = environment;
        this.action = action;
        this.projectGuid = projectGuid;
        this.jobId = jobId;
        this.submitted = Instant.now();
    }

    public ExecutionState getState() {
        return state;
    }

    public void setState(ExecutionState state) {
        this.state = state;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getAction() {
        return action;
    }

    public String getProjectGuid() {
        return projectGuid;
    }

    public String getJobId() {
        return jobId;
    }

    public Instant getSubmitted() {
        return submitted;
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RemoteStatusReport other = (RemoteStatusReport) obj;
        return Objects.equals(jobId, other.jobId);
    }

    @Override
    public String toString() {
        return "RemoteStatusReport [environment=" + environment + ", action=" + action + ", projectGuid=" + projectGuid
                + ", jobId=" + jobId + ", submitted=" + submitted + ", state=" + state + "]";
    }
}
