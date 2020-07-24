package de.hshannover.dqgui.core.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.hshannover.dqgui.framework.api.Recoverable;

public class Iqm4hdFeedback implements Recoverable {
    
    public enum FeedbackState {
        NOT_EVALUATED("Feedback not evaluated for identifier"), 
        CORRECT("Identifier marked as correctly evaluated"), 
        FALSE_POSITIVE("Identifier marked as false positive")
        ;
        
        private final String msg;
        
        FeedbackState(String msg) {
            this.msg = msg;
        }
        
        public String getMessage() { return msg; }
    }
   
    private final Map<List<String>, FeedbackState> feedbackMapping = new HashMap<>();
    
    public void setFeedback(List<String> primaryKeys, FeedbackState feedback) {
        if(feedback == FeedbackState.NOT_EVALUATED) {
            feedbackMapping.remove(primaryKeys);
        } else {
            feedbackMapping.put(primaryKeys, feedback);
        }
    }
    
    public FeedbackState getFeedback(List<String> primaryKeys) {
        return feedbackMapping.getOrDefault(primaryKeys, FeedbackState.NOT_EVALUATED);
    }
}
