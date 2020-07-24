package de.mvise.iqm4hd.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExecutionReport {
    private List<ExecutionIssue> executionResults = new ArrayList<>();

    public List<ExecutionIssue> getExecutionResults() {
        return executionResults;
    }

    public void setExecutionResults(List<ExecutionIssue> executionResults) {
        this.executionResults = executionResults;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(executionResults);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ExecutionReport other = (ExecutionReport) obj;
        return Objects.equals(executionResults, other.executionResults);
    }
}
