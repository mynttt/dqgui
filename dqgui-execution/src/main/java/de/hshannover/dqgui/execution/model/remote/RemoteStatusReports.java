package de.hshannover.dqgui.execution.model.remote;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A RemoteStatusReports contains a set of {@link RemoteStatusReport}s
 * 
 * @author myntt
 *
 */
public class RemoteStatusReports implements RemoteResponse {
    private final Set<RemoteStatusReport> results = new HashSet<>();
    
    public RemoteStatusReports(Collection<RemoteStatusReport> results) {
        this.results.addAll(results);
    }

    public Set<RemoteStatusReport> getResults() {
        return results;
    }
}
