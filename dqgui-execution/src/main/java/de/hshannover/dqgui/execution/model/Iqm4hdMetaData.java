package de.hshannover.dqgui.execution.model;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import com.google.common.hash.Hashing;
import de.hshannover.dqgui.execution.database.TargetedDatabase;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * General metadata class for storing anything execution relevant.
 *
 * @author Marc Herschel
 *
 */
public final class Iqm4hdMetaData {

    public enum Iqm4hdReturnCode {
        UNDEFINED, PASS, ERROR, ISSUES
    }

    @SuppressFBWarnings("NM_CLASS_NOT_EXCEPTION")
    public static class Iqm4hdRunnerException {
        private final String stacktrace, message, classNameSimpleName;

        private Iqm4hdRunnerException(Throwable t) {
            Throwable cause = t;
            while(cause.getCause() != null)
                cause = cause.getCause();
            
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            
            this.classNameSimpleName = cause.getClass().getSimpleName();
            this.message = cause.getMessage();
            this.stacktrace = sw.toString();
        }

        public String getStacktrace() {
            return stacktrace;
        }

        public String getMessage() {
            return message;
        }

        public String getClassNameSimpleName() {
            return classNameSimpleName;
        }
    }

    public static class HumanReadableMetaData {
        private final String identifier, humanReadable;
        private String contextInformation;

        HumanReadableMetaData(String identifier, String humanReadable, String contextInformation) {
            this.identifier = identifier;
            this.humanReadable = humanReadable;
            this.contextInformation = contextInformation;
        }

        void updateContextInformation(String contextInformation) {
            this.contextInformation = contextInformation;
        }

        public String getIdentifier() {
            return identifier;
        }

        public String getHumanReadable() {
            return humanReadable;
        }

        public String getContextInformation() {
            return contextInformation;
        }
    }

    public Iqm4hdMetaData(String action, String environment, String identifier, boolean optimize, String creator, String executionEnvironment) {
        this.creator = creator;
        this.action = action;
        this.environment = environment;
        this.executionEnvironment = executionEnvironment;
        this.identifier = identifier;
        this.optimize = optimize;
        this.started = Instant.now();
        this.hash = Hashing.sha256().hashString(identifier+creator, StandardCharsets.UTF_8).toString().substring(0, 32);
        humanReadable = new HumanReadableMetaData(identifier,
                String.format("%s @ %s", action, this.environment),
                String.format("Task: %s has not finished yet.", identifier));
    }

    private final List<String> identifiers = new ArrayList<>(),
                               actionValues = new ArrayList<>();
    private final String identifier, environment, action, creator, hash, executionEnvironment;
    private final List<TargetedDatabase> databases = new ArrayList<>();
    private final boolean optimize;
    private HumanReadableMetaData humanReadable;
    private int issues;
    private String message = "Undefined";
    private final Instant started;
    private Instant finished;
    private Duration duration;
    private Iqm4hdReturnCode returnCode = Iqm4hdReturnCode.UNDEFINED;
    private Iqm4hdRunnerException exception = null;

    /*
     * 
     * Must be called before having a valid ended meta data result
     * 
     */

    public void registerReturnCode(Iqm4hdReturnCode result) {
        this.returnCode = result;
        this.finished = Instant.now();
        this.duration = Duration.between(started, finished);
    }

    public void registerHumanReadableFinished() {
        humanReadable.updateContextInformation(
                String.format("Task: %s%nFinished in: %s%nReturned: %s",
                        identifier,
                        duration.toString()
                        .substring(2)
                        .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                        .toLowerCase(),
                        returnCode));
    }

    public void registerMessage(String message) {
        this.message = message;
    }

    public void registerIdentifiers(List<String> extractIdentifier) {
        this.identifiers.addAll(extractIdentifier);
    }
    
    public void registerActionValues(List<String> actionValues) {
        this.actionValues.addAll(actionValues);
    }
    
    public void registerException(Throwable t) {
        this.exception = new Iqm4hdRunnerException(t);
    }

    public void registerCalledDatabases(ArrayList<TargetedDatabase> calledDatabases) {
        databases.addAll(calledDatabases);
    }

    public void registerIssueCount(int issues) {
        this.issues = issues;
    }
    
    /**
     * @return true if execution failed (not if the action itself returned issues!)
     */
    public boolean isError() {
        return returnCode == Iqm4hdReturnCode.ERROR ||returnCode == Iqm4hdReturnCode.UNDEFINED;
    }

    public int getIssues() {
        return issues;
    }

    public String getIdentifier() {
        return identifier;
    }
    
    public String getHash() {
        return hash;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getAction() {
        return action;
    }

    public boolean isOptimize() {
        return optimize;
    }

    public Instant getStarted() {
        return started;
    }

    public Instant getFinished() {
        return finished;
    }

    public Duration getDuration() {
        return duration;
    }

    public List<TargetedDatabase> getDatabases() {
        return databases;
    }
    
    public String getCreator() {
        return creator;
    }

    public String getMessage() {
        return message;
    }

    public Iqm4hdReturnCode getReturnCode() {
        return returnCode;
    }
    
    public String getExecutionEnvironment() {
        return executionEnvironment;
    }

    public Optional<Iqm4hdRunnerException> getException() {
        return Optional.ofNullable(exception);
    }

    public HumanReadableMetaData getHumanReadable() {
        return humanReadable;
    }
    
    public List<String> getIdentifiers() {
        return Collections.unmodifiableList(identifiers);
    }

    public List<String> getActionValues() {
        return actionValues;
    }

    @Override
    public int hashCode() {
        return Objects.hash(action, environment, finished, identifier, message, optimize, returnCode, started);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Iqm4hdMetaData other = (Iqm4hdMetaData) obj;
        return Objects.equals(action, other.action) && Objects.equals(environment, other.environment)
                && Objects.equals(finished, other.finished) && Objects.equals(identifier, other.identifier)
                && Objects.equals(message, other.message) && optimize == other.optimize
                && returnCode == other.returnCode && Objects.equals(started, other.started);
    }
}
