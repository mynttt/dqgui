package de.hshannover.dqgui.remote;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.tinylog.Logger;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import de.hshannover.dqgui.execution.Rethrow;
import de.hshannover.dqgui.execution.model.remote.RemoteCollectionRequest;
import de.hshannover.dqgui.execution.model.remote.RemoteCollectionResponse;
import de.hshannover.dqgui.execution.model.remote.RemoteJob;
import de.hshannover.dqgui.execution.model.remote.RemoteProgressRequest;
import de.hshannover.dqgui.execution.model.remote.RemoteProgressResponse;
import de.hshannover.dqgui.execution.model.remote.RemoteResult;
import de.hshannover.dqgui.execution.model.remote.RemoteStatusReport;
import de.hshannover.dqgui.execution.model.remote.RemoteStatusReports;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class ExecutionContext {
    private final Gson gson;
    private final JobRunner runner;
    private final ExecutionCompletationListener listener;
    private final ConcurrentMap<String, RemoteStatusReport> running = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, RemoteStatusReport> completedReports = new ConcurrentHashMap<>();
    
    @FunctionalInterface
    public interface ExecutionCompletationHandler {
        void handle(RemoteResult result);
    }
    
    public static class ExecutionCompletationListener {
        private final ExecutionCompletationHandler handler;
        public ExecutionCompletationListener(ExecutionCompletationHandler h) { handler = h; }
        public void complete(RemoteResult result) { handler.handle(result); }
    }

    @SuppressFBWarnings({"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", "REC_CATCH_EXCEPTION"})
    public ExecutionContext(JobRunner runner, ExecutionCompletationHandler handler, Gson gson) {
        this.runner = runner;
        this.listener = new ExecutionCompletationListener(handler);
        this.gson = gson;
        
        try {
            int jobs = 0;
            
            for(int i = 0; i < 16; i++) {
                String s = Integer.toHexString(i);
                Path hex = Main.JOBS.resolve(s);
                for(Path p : Files.list(hex).filter(f -> f.getFileName().toString().endsWith("-status.json")).collect(Collectors.toList())) {
                    try(InputStream is = Files.newInputStream(p)) {
                        RemoteStatusReport r = gson.fromJson(new JsonReader(new InputStreamReader(is, StandardCharsets.UTF_8)), RemoteStatusReport.class);
                        completedReports.put(r.getJobId(), r);
                        jobs++;
                    }
                }
                
                for(Path p : Files.list(hex).filter(f -> !f.getFileName().toString().endsWith("-status.json")).collect(Collectors.toList())) {
                    String name = p.getFileName().toString();
                    if(!completedReports.containsKey(s + name.substring(0, name.length()-5)))
                        Files.deleteIfExists(p);
                }
            }
            
            Logger.info("Loaded {} uncollected jobs.", jobs);
        } catch(Exception e) {
            Rethrow.rethrow(e);
        }
        
        Main.HOUSE_KEEPING.scheduleAtFixedRate(this::prune, 0, Main.config().getPruneUncollectedEveryHours(), TimeUnit.HOURS);
    }
    
    @SuppressFBWarnings("JLM_JSR166_UTILCONCURRENT_MONITORENTER")
    private void prune() {
        List<RemoteStatusReport> collected = new ArrayList<>();
        Iterator<Map.Entry<String, RemoteStatusReport>> it = completedReports.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, RemoteStatusReport> next = it.next();
            if(next.getValue().getSubmitted().isBefore(Instant.now().minus(Main.config().getKeepJobsForDays(), ChronoUnit.DAYS))) {
                Logger.info("Housekeeping: Removing {} after not being collected for 30 days.", next.getValue().getJobId());
                collected.add(next.getValue());
                it.remove();
            }
        }
        
        for(RemoteStatusReport r : collected) {
            try {
                Path root = Main.JOBS.resolve(Character.toString(r.getJobId().charAt(0)));
                Files.deleteIfExists(root.resolve(r.getJobId().substring(1) + ".json"));
                Files.deleteIfExists(root.resolve(r.getJobId().substring(1) + "-status.json"));
            } catch (IOException e) {
                throw Rethrow.rethrow(e);
            }
        }
    }
    
    private void dumpResult(RemoteStatusReport report, RemoteResult result) {
        try {
            Path root = Main.JOBS.resolve(Character.toString(report.getJobId().charAt(0)));
            Files.write(root.resolve(result.getJobId().substring(1) + ".json"), gson.toJson(result).getBytes(StandardCharsets.UTF_8));
            Files.write(root.resolve(result.getJobId().substring(1) + "-status.json"), gson.toJson(report).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            Rethrow.rethrow(e);
        }
    }

    public RemoteStatusReport runJob(RemoteJob job) {
        if(running.containsKey(job.getJobId()) || completedReports.containsKey(job.getJobId()))
            throw new IllegalArgumentException("Already submitted a job with id: " + job.getJobId() + " | IDs are unique to each job.");
        RemoteStatusReport r = new RemoteStatusReport(job.getEnvironment(), job.getAction(), job.getProjectGuid(), job.getJobId());
        runner.submitJob(job, r).thenAccept(result -> {
                dumpResult(r, result);
                completedReports.put(job.getJobId(), running.remove(job.getJobId()));
                listener.complete(result);
            });
        running.put(job.getJobId(), r);
        return r;
    }

    public RemoteStatusReports getAllReports() {
        Set<RemoteStatusReport> l = new HashSet<>();
        l.addAll(running.values());
        l.addAll(completedReports.values());
        return new RemoteStatusReports(l);
    }

    public RemoteProgressResponse progress(RemoteProgressRequest request) {
        RemoteStatusReport r = running.get(request.getJobId());
        if(r == null)
            throw new IllegalArgumentException("Job with id: " + request.getJobId() + " is not running!");
        return new RemoteProgressResponse(r.getProjectGuid(), r.getJobId(), LogInterceptor.request(r.getJobId()));
    }

    public RemoteCollectionResponse collectAll(RemoteCollectionRequest request) {
        List<RemoteStatusReport> collected = new ArrayList<>();
        List<RemoteResult> results = new ArrayList<>();
        
        Iterator<Map.Entry<String, RemoteStatusReport>> it = completedReports.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, RemoteStatusReport> next = it.next();
            if(request.getJobIds().contains(next.getKey())) {
                collected.add(next.getValue());
                it.remove();
            }
        }
        
        for(RemoteStatusReport r : collected) {
            Path root = Main.JOBS.resolve(Character.toString(r.getJobId().charAt(0)));
            try(InputStream i = Files.newInputStream(root.resolve(r.getJobId().substring(1) + ".json"))) {
                results.add(gson.fromJson(new JsonReader(new InputStreamReader(i, StandardCharsets.UTF_8)), RemoteResult.class));
                Files.deleteIfExists(root.resolve(r.getJobId().substring(1) + ".json"));
                Files.deleteIfExists(root.resolve(r.getJobId().substring(1) + "-status.json"));
            } catch (IOException e) {
                throw Rethrow.rethrow(e);
            }
        }
            
        return new RemoteCollectionResponse(results);
    }
    
}
