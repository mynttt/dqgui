package de.hshannover.dqgui.core.util;

import static de.hshannover.dqgui.framework.ErrorUtility.rethrow;
import static de.hshannover.dqgui.framework.ErrorUtility.uncheck;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.controlsfx.control.Notifications;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.tinylog.Logger;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.Objects;
import com.google.gson.Gson;
import de.hshannover.dqgui.core.Executors;
import de.hshannover.dqgui.core.model.ProjectHandle;
import de.hshannover.dqgui.execution.DSLService.RepositoryStatus;
import de.hshannover.dqgui.execution.model.Project;
import de.hshannover.dqgui.execution.model.remote.RemoteCollectionRequest;
import de.hshannover.dqgui.execution.model.remote.RemoteCollectionResponse;
import de.hshannover.dqgui.execution.model.remote.RemoteError;
import de.hshannover.dqgui.execution.model.remote.RemoteJob;
import de.hshannover.dqgui.execution.model.remote.RemoteProgressRequest;
import de.hshannover.dqgui.execution.model.remote.RemoteProgressResponse;
import de.hshannover.dqgui.execution.model.remote.RemoteResponse;
import de.hshannover.dqgui.execution.model.remote.RemoteResult;
import de.hshannover.dqgui.execution.model.remote.RemoteSocketNotification;
import de.hshannover.dqgui.execution.model.remote.RemoteStatusReport;
import de.hshannover.dqgui.execution.model.remote.RemoteStatusReport.ExecutionState;
import de.hshannover.dqgui.execution.model.remote.RemoteStatusReports;
import de.hshannover.dqgui.framework.dialogs.DialogContext;
import de.hshannover.dqgui.framework.signal.Signal;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.geometry.Pos;
import javafx.util.Duration;

public class RemoteConnection implements AutoCloseable {
    private static final Gson GSON = new Gson();
    
    private final HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(req -> {
        req.setConnectTimeout(1000);
    });
    
    private final ReadOnlyBooleanWrapper isConnectedWrapper = new ReadOnlyBooleanWrapper(false);
    private final Signal jobsChangedSignal = new Signal();
    private final ProjectHandle handle;
    
    private EventSocket eventSocket = null;
    
    public RemoteConnection(ProjectHandle handle) {
        this.handle = handle;
    }
    
    public ReadOnlyBooleanProperty isConnectedWithEventSocket() {
        return isConnectedWrapper.getReadOnlyProperty();
    }
    
    public Signal getJobsChangedSignal() {
        return jobsChangedSignal;
    }

    public boolean isConnected() {
        return isConnectedWrapper.get(); 
    }
    
    private static class HttpResponseHandler implements AutoCloseable {
        private HttpResponse response;
        
        private HttpResponseHandler(HttpResponse response) {
            this.response = response;
        }
        
        private int code() {
            return response.getStatusCode();
        }
        
        private String parseAsString() {
            try {
                return response.parseAsString();
            } catch (IOException e) {
                throw rethrow(e);
            }
        }

        private <T> T parseAs(Class<T> clazz) {
            try {
                return GSON.fromJson(response.parseAsString(), clazz);
            } catch (IOException e) {
                throw rethrow(e);
            }
        }

        @Override
        public void close() {
            if (response != null) {
                try {
                    response.disconnect();
                } catch (IOException ex) {}
            }
        }
    }
    
    private final ByteArrayContent postContent(Object json) {
        return new ByteArrayContent("application/json", GSON.toJson(json).getBytes(StandardCharsets.UTF_8));
    }
    
    private final void prepareHeaders(HttpRequest r, String key) {
        r.getHeaders().put("X-Key", key);
        r.setThrowExceptionOnExecuteError(false);
    }

    public CompletableFuture<RepositoryStatus> testConnection(String host, String key) {
        return CompletableFuture.supplyAsync(() -> {
            HttpRequest r = uncheck(() -> requestFactory.buildGetRequest(new GenericUrl((host + "/execution/test"))));
            prepareHeaders(r, key);
            return uncheck(() -> {
                try(HttpResponseHandler h = new HttpResponseHandler(r.execute())) {
                    return h.code() == 200 ? 
                            new RepositoryStatus() : 
                            new RepositoryStatus("Error.\nCode: " + h.code() + "\nResponse: " + h.parseAsString());
                }
            });
        }, Executors.SERVICE);
    }
    
    public CompletableFuture<RemoteResponse> submitJob(RemoteJob job, String host, String key) {
        return CompletableFuture.supplyAsync(() -> {
            HttpRequest r = uncheck(() -> requestFactory.buildPostRequest(new GenericUrl(host + "/execution"), postContent(job)));
            prepareHeaders(r, key);
            return uncheck(() -> {
                try(HttpResponseHandler h = new HttpResponseHandler(r.execute())) {
                    return h.code() == 200 
                            ? h.parseAs(RemoteStatusReport.class)
                            : h.parseAs(RemoteError.class);
                }
            });
        }, Executors.SERVICE);
    }
    
    public CompletableFuture<RemoteResponse> listRemoteJobs(String host, String key) {
        return CompletableFuture.supplyAsync(() -> {
            HttpRequest r = uncheck(() -> requestFactory.buildGetRequest(new GenericUrl((host + "/execution"))));
            prepareHeaders(r, key);
            return uncheck(() ->  {
                try(HttpResponseHandler h = new HttpResponseHandler(r.execute())) {
                    return h.code() == 200
                            ? h.parseAs(RemoteStatusReports.class)
                            : h.parseAs(RemoteError.class);
                }
            });
        }, Executors.SERVICE);
    }
    
    public CompletableFuture<RemoteResponse> requestProgress (String projectGuid, String jobId, String host, String key) {
        return CompletableFuture.supplyAsync(() -> {
            RemoteProgressRequest req = new RemoteProgressRequest(projectGuid, jobId);
            HttpRequest r = uncheck(() -> requestFactory.buildPostRequest(new GenericUrl(host + "/execution/progress"), postContent(req)));
            prepareHeaders(r, key);
            return uncheck(() -> {
               try(HttpResponseHandler h = new HttpResponseHandler(r.execute())) {
                   return h.code() == 200
                           ? h.parseAs(RemoteProgressResponse.class)
                           : h.parseAs(RemoteError.class);
               }
            });
        }, Executors.SERVICE);
    }
    
    public CompletableFuture<RemoteResponse> collect(Set<String> jobIds, String host, String key) {
        return CompletableFuture.supplyAsync(() -> {
            if(jobIds.isEmpty()) return new RemoteCollectionResponse(Collections.emptyList());
            RemoteCollectionRequest req = new RemoteCollectionRequest(jobIds);
            HttpRequest r = uncheck(() -> requestFactory.buildPostRequest(new GenericUrl(host + "/execution/collect"), postContent(req)));
            prepareHeaders(r, key);
            return uncheck(() -> {
                try(HttpResponseHandler h = new HttpResponseHandler(r.execute())) {
                    return h.code() == 200
                            ? h.parseAs(RemoteCollectionResponse.class)
                            : h.parseAs(RemoteError.class);
                }
            });
        }, Executors.SERVICE);
    }
    
    public CompletableFuture<Void> collectAll(Project p, String host, String key) {
        return listRemoteJobs(host, key)
            .thenApply(r -> {
                if(r instanceof RemoteError) {
                    handleCollectionError((RemoteError) r);
                    Set<String> s = Collections.emptySet();
                    return s;
                }
                if(r instanceof RemoteStatusReports) {
                    return ((RemoteStatusReports) r).getResults().stream()
                            .filter(rp -> rp.getState() == ExecutionState.WAIT_FOR_COLLECT)
                            .filter(rp -> Objects.equal(p.getGuid(), rp.getProjectGuid()))
                            .map(RemoteStatusReport::getJobId)
                            .collect(Collectors.toSet());
                }
                throw new AssertionError();
            })
            .thenCompose(s -> collect(s, host, key))
            .thenAccept(r -> {
                if(r instanceof RemoteError) {
                    handleCollectionError((RemoteError) r);
                    return;
                }
                if(r instanceof RemoteCollectionResponse) {
                    jobsChangedSignal.fire();
                    List<RemoteResult> results = ((RemoteCollectionResponse) r).getResults();
                    if(results.isEmpty()) return;
                    results.forEach(handle::addRemoteResult);
                    Logger.info("Collected {} results from {}", results.size(), host);
                    return;
                }
                throw new AssertionError();
            }).exceptionally(e -> {
                handleCollectionException(e);
                return null;
            });
    }
    
    private void handleCollectionException(Throwable e) {
        Notifications nf = NotificationTools.error("Failed to collect results", "Exception occured", "Click to view stacktrace")
                .position(Pos.TOP_RIGHT)
                .hideAfter(Duration.millis(3000))
                .onAction(ev -> Platform.runLater(() -> DialogContext.ANONYMOUS_CONTEXT.exceptionDialog(e, "Failed to collect result", ExceptionRecoveryTips.REMOTE_ERROR.getTip())));
        Platform.runLater(nf::show);
    }
    
    private void handleCollectionError(RemoteError err) {
        Notifications nf = NotificationTools.error("Failed to collect results", "Server error occured", "Click to view server response")
                .position(Pos.TOP_RIGHT)
                .hideAfter(Duration.millis(3000))
                .onAction(ev -> Platform.runLater(() -> DialogContext.ANONYMOUS_CONTEXT.textErrorDialog("Failed to collect result", "Server error occured", err.getMessage())));
        Platform.runLater(nf::show);
    }
    
    @Override
    public void close() throws Exception {
        if(eventSocket == null) return;
        eventSocket.closeConnection(1000, "CLOSED_BY_CLIENT");
        eventSocket = null;
    }

    public void disconnectEventSocket() {
        if(eventSocket == null) return;
        eventSocket.close();
        eventSocket = null;
    }

    public void connectEventSocket(String remoteHost, String remoteKey) {
        try {
            if(eventSocket != null)
                eventSocket.closeWithoutNotification();
            eventSocket = new EventSocket(remoteHost, remoteKey);
            eventSocket.connectBlocking();
        } catch (Exception e) {
            throw rethrow(e);
        }
    }
    
    @SuppressFBWarnings("SE_BAD_FIELD")
    private class EventSocket extends WebSocketClient {
        private final String remoteHost, key;
        private volatile boolean notification = true;
        private boolean open = false;

        @SuppressWarnings("serial")
        public EventSocket(String remoteHost, String key) throws URISyntaxException {
            super(new URI(remoteHost + "/events"), new HashMap<String, String>() {{ put("X-Key", key); }});
            this.remoteHost = remoteHost;
            this.key = key;
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Logger.info("Connected to event socket");
            open = true;
            Platform.runLater(() -> {
                isConnectedWrapper.set(true);
                NotificationTools.success("Connected to event socket", "Connection established", "Remote job notification + auto collect enabled")
                    .position(Pos.TOP_RIGHT)
                    .hideAfter(Duration.millis(3000))
                    .show();
            });
        }

        @Override
        public void onMessage(String message) {
            RemoteSocketNotification n = GSON.fromJson(message, RemoteSocketNotification.class);
            if(handle.isValidProject() && handle.getSelectedProject().unsafeGet().getGuid().equals(n.getProjectGuid())) {
                collect(Collections.singleton(n.getJobId()), remoteHost, key)
                    .thenAccept(r -> {
                        if(r instanceof RemoteError) {
                            handleCollectionError((RemoteError) r);
                            return;
                        }
                        if(r instanceof RemoteCollectionResponse) {
                            ((RemoteCollectionResponse) r).getResults().forEach(handle::addRemoteResult);
                            return;
                        }
                        throw new AssertionError();
                    })
                    .exceptionally(e -> {
                        handleCollectionException(e);
                        return null;
                    });
            }
            jobsChangedSignal.fire();
        }
        
        public void closeWithoutNotification() {
            notification = false;
            this.close();
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Logger.info("Disconnected from event socket => Code {} ", code, reason);
            if(!open) return;
            open = false;
            Platform.runLater(() -> { 
                isConnectedWrapper.set(false);
                if(!notification) return;
                NotificationTools.warning("Disconnected from event socket", "Connection closed", "Remote job notification + auto collect disabled")
                    .position(Pos.TOP_RIGHT)
                    .hideAfter(Duration.millis(3000))
                    .show();
            });
        }

        @Override
        public void onError(Exception ex) {
            Logger.error("Event socket encountered {}", ex.getClass().getSimpleName());
            Logger.error(ex);
            Platform.runLater(() -> {
                NotificationTools
                .error("Event socket error", "Socket encountered " + ex.getClass().getSimpleName(), "Disabled remote job notification + auto collection.")
                .position(Pos.TOP_RIGHT)
                .hideAfter(Duration.millis(3000))
                .onAction(e -> DialogContext.ANONYMOUS_CONTEXT.exceptionDialog(ex, "Event socket error", ExceptionRecoveryTips.REMOTE_ERROR.getTip()))
                .show();
            });
        }
    }
}
