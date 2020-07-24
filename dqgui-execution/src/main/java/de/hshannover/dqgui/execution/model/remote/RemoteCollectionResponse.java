package de.hshannover.dqgui.execution.model.remote;

import java.util.ArrayList;
import java.util.List;

/**
 * A RemoteCollectionResponse contains a list of collected {@link RemoteResult}s.<br>
 * If nothing was collected the list will be empty.
 * 
 * @author myntt
 *
 */
public class RemoteCollectionResponse implements RemoteResponse {
    private final List<RemoteResult> results = new ArrayList<>();
    
    public RemoteCollectionResponse(List<RemoteResult> results) {
        this.results.addAll(results);
    }

    public List<RemoteResult> getResults() {
        return results;
    }
}
