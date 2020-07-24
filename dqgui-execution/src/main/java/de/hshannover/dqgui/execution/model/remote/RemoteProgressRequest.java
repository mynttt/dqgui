package de.hshannover.dqgui.execution.model.remote;

/**
 * A RemoteProgressRequest contains a projectGuid and jobId and requests the current remote progress.
 * 
 * @author myntt
 *
 */
public class RemoteProgressRequest implements Validatable<RemoteProgressRequest> {
    private final String projectGuid, jobId;

    public RemoteProgressRequest(String projectGuid, String jobId) {
        this.projectGuid = projectGuid;
        this.jobId = jobId;
    }

    public String getProjectGuid() {
        return projectGuid;
    }

    public String getJobId() {
        return jobId;
    }

    @Override
    public RemoteProgressRequest validate() {
        notNullOrBlank(projectGuid, "projectGuid");
        notNullOrBlank(jobId, "jobId");
        return this;
    }
}