package de.hshannover.dqgui.core.concurrency;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import org.tinylog.Logger;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.core.model.ProjectHandle;
import de.hshannover.dqgui.core.util.ParserTools;
import de.hshannover.dqgui.dbsupport.api.DatabaseEnvironment;
import de.hshannover.dqgui.execution.DSLService;
import de.hshannover.dqgui.execution.database.DqguiDatabaseService;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData;
import de.mvise.iqm4hd.api.ExecutionReport;
import de.mvise.iqm4hd.api.Iqm4hdAPI;
import de.mvise.iqm4hd.api.RService;
import de.mvise.iqm4hd.api.RuleService;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Represents an IQM4HD task.
 *
 * @author Marc Herschel
 *
 */
public final class Iqm4hdTask {

    public enum State {
        AWAIT, RUN, FINISH
    }

    private final class Iqm4hdRunner implements Runnable {
        private final RService rService;

        Iqm4hdRunner(RService rService) {
            this.rService = rService;
        }

        @Override
        public void run() {
            Platform.runLater(() -> stateProperty.set(State.RUN));
            Thread.currentThread().setName(threadName);
            Logger.info("Task {} starting.", identifier);
            InterceptablePrintStream.register(Thread.currentThread());
            tasks.getLogIdentifiers().put(identifier, Thread.currentThread());
            try(DqguiDatabaseService service = new DqguiDatabaseService(connections)) {
                String action = ruleService.getActionStatementByName(metadata.getAction());
                String compiled = Iqm4hdAPI.compile(action, ruleService);
                ExecutionReport report = Iqm4hdAPI.execute(compiled, service, rService, ruleService, metadata.isOptimize());
                metadata.registerIssueCount(report.getExecutionResults().size());
                metadata.registerIdentifiers(ParserTools.extractIdentifier(action, ruleService));
                metadata.registerActionValues(ParserTools.extractActionValues(action));
                unregister();
                completed = true;
                tasks.complete(getTask(), report, new ArrayList<>(service.getCalledDatabases()));
            } catch(Exception e) {
                Logger.error(e);
                unregister();
                completed = true;
                tasks.complete(getTask(), e);
            }
            Platform.runLater(() -> stateProperty.set(State.FINISH));
            Logger.info("Task {} finished.", identifier);
        }

    }

    private final SimpleObjectProperty<State> stateProperty;
    private final Iqm4hdMetaData metadata;
    private final String identifier, threadName;
    private final Set<DatabaseConnection> connections;
    private final RuleService ruleService;
    private final ProjectHandle handle;
    private boolean completed;
    private ActiveTasks tasks;
    private Iqm4hdRunner runner;

    Iqm4hdTask(String action, ProjectHandle handle, DatabaseEnvironment environment, DSLService service, RService rService, ActiveTasks tasks, boolean optimize) {
        this.threadName = String.format("worker-%d", Sequence.VALUE.addAndGet(1));
        this.identifier = String.format("%d-%s-%s-%s", System.currentTimeMillis(), action, environment.getIdentifier(), threadName);
        this.metadata = new Iqm4hdMetaData(action, environment.getIdentifier(), identifier, optimize, Config.USER_IDENTIFIER, "DQGUI_LOCAL@" + Config.APPLICATION_VERSION);
        this.connections = environment.getConnections();
        this.ruleService = service.createRuleService();
        this.tasks = tasks;
        this.handle = handle;
        this.runner = new Iqm4hdRunner(rService);
        this.stateProperty = new SimpleObjectProperty<>(State.AWAIT);
        Logger.info(String.format("Task %s @ %s created. Status: %s | Id: %s", action, metadata.getEnvironment(), State.AWAIT, identifier));
    }

    private void unregister() {
        tasks.getLogIdentifiers().remove(identifier);
        InterceptablePrintStream.unregister(Thread.currentThread(), handle, metadata);
    }

    private Iqm4hdTask getTask() {
        return this;
    }

    Iqm4hdRunner getRunner() {
        return runner;
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean isCompleted() {
        return completed;
    }

    public Iqm4hdMetaData getMetadata() {
        return metadata;
    }

    public SimpleObjectProperty<State> getStateProperty() {
        return stateProperty;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, metadata, threadName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Iqm4hdTask other = (Iqm4hdTask) obj;
        if (identifier == null) {
            if (other.identifier != null)
                return false;
        } else if (!identifier.equals(other.identifier))
            return false;
        if (metadata == null) {
            if (other.metadata != null)
                return false;
        } else if (!metadata.equals(other.metadata))
            return false;
        if (threadName == null) {
            if (other.threadName != null)
                return false;
        } else if (!threadName.equals(other.threadName))
            return false;
        return true;
    }

}
