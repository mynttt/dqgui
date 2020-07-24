package de.hshannover.dqgui.remote;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.websocket.api.Session;
import org.tinylog.Logger;
import com.google.gson.Gson;
import de.hshannover.dqgui.execution.model.remote.RemoteCollectionRequest;
import de.hshannover.dqgui.execution.model.remote.RemoteJob;
import de.hshannover.dqgui.execution.model.remote.RemoteProgressRequest;
import de.hshannover.dqgui.execution.model.remote.RemoteSocketNotification;
import io.javalin.http.Context;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsHandler;

public class ExecutionController {
    private final ExecutionContext context;
    private final ConcurrentHashMap<Session, WsConnectContext> eventContexts = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<WsConnectContext> eventListeners = new CopyOnWriteArrayList<>();

    public ExecutionController(JobRunner runner, Gson gson) {
        this.context = new ExecutionContext(runner, e -> 
                eventListeners.forEach(l -> 
                        l.send(gson.toJson(new RemoteSocketNotification(e)))), gson);
        
        Main.HOUSE_KEEPING.scheduleAtFixedRate(() -> {
            Iterator<Map.Entry<Session, WsConnectContext>> it = eventContexts.entrySet().iterator();
            while(it.hasNext()) {
                Map.Entry<Session, WsConnectContext> next = it.next();
                if(!next.getKey().isOpen()) {
                    it.remove();
                    eventListeners.remove(next.getValue());
                }
            }
        }, 0, 20, TimeUnit.MINUTES);
    }

    public void getAll(Context ctx) {
        ctx.result(CompletableFuture.supplyAsync(() -> 
            ctx.json(context.getAllReports())));
    }

    public void create(Context ctx) {
        ctx.result(CompletableFuture.supplyAsync(() -> 
            ctx.json(context.runJob(ctx.bodyAsClass(RemoteJob.class).validate()))));
    }
    
    public void progress(Context ctx) {
        ctx.result(CompletableFuture.supplyAsync(() -> 
            ctx.json(context.progress(ctx.bodyAsClass(RemoteProgressRequest.class).validate()))));
    }
    
    public void collectAll(Context ctx) {
        ctx.result(CompletableFuture.supplyAsync(() -> 
            ctx.json(context.collectAll(ctx.bodyAsClass(RemoteCollectionRequest.class).validate()))));
    }

    public void events(WsHandler ctx) {
        ctx.onConnect(x -> {
            eventContexts.put(x.session, x);
            eventListeners.add(x);
            Logger.info("Client connected to event socket: {}", x.session.getRemoteAddress());
        });
        
        ctx.onClose(x -> {
            eventListeners.remove(eventContexts.remove(x.session));
            Logger.info("Client disconnected from event socket: {}", x.session.getRemoteAddress());
        });
    }
}
