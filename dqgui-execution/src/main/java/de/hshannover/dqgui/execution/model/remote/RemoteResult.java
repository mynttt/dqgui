package de.hshannover.dqgui.execution.model.remote;

import de.hshannover.dqgui.execution.model.Iqm4hdMetaData;
import de.mvise.iqm4hd.api.ExecutionReport;

/**
 * A RemoteResult is a completed and ready to be collected result.
 * 
 * @author myntt
 *
 */
public class RemoteResult implements RemoteResponse {
    private final String projectGuid, jobId, log;
    private final Iqm4hdMetaData meta;
    private final ExecutionReport report;
    
    public RemoteResult(String projectGuid, String jobId, Iqm4hdMetaData meta, ExecutionReport report, String log) {
        this.projectGuid = projectGuid;
        this.jobId = jobId;
        this.meta = meta;
        this.report = report;
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

    public Iqm4hdMetaData getMeta() {
        return meta;
    }

    public ExecutionReport getReport() {
        return report;
    }
}
