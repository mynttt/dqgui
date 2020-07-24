package de.hshannover.dqgui.execution.model.remote;

/**
 * A RemoteProgressResponse contains the projectGui, jobId and log.<br>
 * 
 * @author myntt
 *
 */
public class RemoteProgressResponse implements RemoteResponse {
    private final String projectGuid, jobId, log;

    public RemoteProgressResponse(String projectGuid, String jobId, String log) {
        this.projectGuid = projectGuid;
        this.jobId = jobId;
        this.log = log;
    }

    public String getProjectGuid() {
        return projectGuid;
    }

    public String getJobId() {
        return jobId;
    }

    public String getLog() {
        return log;
    }
}
