package de.hshannover.dqgui.execution.model.remote;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A RemoteCollectionRequest contains a set of job ids to collect from the server.
 * 
 * @author myntt
 *
 */
public class RemoteCollectionRequest implements Validatable<RemoteCollectionRequest> {
    private final Set<String> jobIds = new HashSet<>();
    
    public RemoteCollectionRequest(Set<String> jobIds) {
        this.jobIds.addAll(jobIds);
    }
    
    public Set<String> getJobIds() {
        return Collections.unmodifiableSet(jobIds);
    }

    @Override
    public RemoteCollectionRequest validate() {
        notNullOrEmpty(jobIds, "jobIds");
        jobIds.forEach(t -> notNullOrBlank(t, "jobIds"));
        return this;
    }
}
