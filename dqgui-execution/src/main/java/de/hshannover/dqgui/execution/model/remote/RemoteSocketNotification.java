package de.hshannover.dqgui.execution.model.remote;

/**
 * A RemoteSocketNotification will be transfered to event socket listeners.<br>
 * It signals that a job has been completed.
 * 
 * @author myntt
 *
 */
public class RemoteSocketNotification {
    private final String jobId, projectGuid;
    
    public RemoteSocketNotification(RemoteResult r) {
        this.jobId = r.getJobId();
        this.projectGuid = r.getProjectGuid();
    }

    public String getJobId() {
        return jobId;
    }

    public String getProjectGuid() {
        return projectGuid;
    }
}
