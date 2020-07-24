package de.hshannover.dqgui.remote;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import org.tinylog.Logger;
import de.hshannover.dqgui.execution.database.DqguiDatabaseService;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData.Iqm4hdReturnCode;
import de.hshannover.dqgui.execution.model.remote.RemoteJob;
import de.hshannover.dqgui.execution.model.remote.RemoteResult;
import de.hshannover.dqgui.execution.model.remote.RemoteStatusReport;
import de.hshannover.dqgui.execution.model.remote.RemoteStatusReport.ExecutionState;
import de.mvise.iqm4hd.api.ExecutionReport;
import de.mvise.iqm4hd.api.Iqm4hdAPI;
import de.mvise.iqm4hd.api.RService;
import de.mvise.iqm4hd.api.RuleService;
import de.mvise.iqm4hd.client.RServiceRservImpl;

public class JobRunner {
    private final RService rService = new RServiceRservImpl();
    private final ExecutorService executor;

    public JobRunner(ExecutorService executor) {
        this.executor = executor;
    }
    
    public CompletableFuture<RemoteResult> submitJob(RemoteJob job, RemoteStatusReport report) {
        return CompletableFuture
                .supplyAsync(() -> {
                    Logger.info("Beginning job: {}", job);
                    report.setState(ExecutionState.RUNNING);
                    String identifier = String.format("%d-%s-%s", System.currentTimeMillis(), job.getAction(), job.getEnvironment());
                    Iqm4hdMetaData metadata = new Iqm4hdMetaData(job.getAction(), job.getEnvironment(), identifier, job.isOptimize(), job.getCreator(), "DQGUI_REMOTE@" + Main.version());
                    LogInterceptor.begin(Thread.currentThread(), job.getJobId());
                    RuleService rules = new RemoteRuleService(job);
                    ExecutionReport iqm4hdReport = null;
                    String log = null;
                    try(DqguiDatabaseService service = new DqguiDatabaseService(job.getConnections())) {
                        String action = rules.getActionStatementByName(job.getAction());
                        iqm4hdReport = Iqm4hdAPI.execute(Iqm4hdAPI.compile(action, rules), service, rService, rules, job.isOptimize());
                        metadata.registerIdentifiers(job.getIdentifiers());
                        metadata.registerActionValues(job.getActionValues());
                        metadata.registerIssueCount(iqm4hdReport.getExecutionResults().size());
                        metadata.registerCalledDatabases(new ArrayList<>(service.getCalledDatabases()));
                        metadata.registerMessage("Task finished successfully.");
                        metadata.registerReturnCode(iqm4hdReport.getExecutionResults().size() > 0 ? Iqm4hdReturnCode.ISSUES : Iqm4hdReturnCode.PASS);
                        log = LogInterceptor.end(Thread.currentThread());
                    } catch(Exception e) {
                        Logger.error(e);
                        metadata.registerMessage("Task finished with exception.");
                        metadata.registerException(e);
                        metadata.registerReturnCode(Iqm4hdReturnCode.ERROR);
                        log = LogInterceptor.end(Thread.currentThread());
                    } finally {
                        metadata.registerHumanReadableFinished();
                    }
                    Logger.info("Completed job: {} -> {}", metadata.getReturnCode(), job);
                    report.setState(ExecutionState.WAIT_FOR_COLLECT);
                    return new RemoteResult(job.getProjectGuid(), job.getJobId(), metadata, iqm4hdReport, log);
                }, executor);
    }
}