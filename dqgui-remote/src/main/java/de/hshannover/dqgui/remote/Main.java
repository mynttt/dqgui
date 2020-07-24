package de.hshannover.dqgui.remote;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.ws;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.rosuda.REngine.Rserve.RserveException;
import org.tinylog.Logger;
import com.google.common.base.Objects;
import com.google.common.io.CharStreams;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.hshannover.dqgui.execution.model.remote.RemoteError;
import de.mvise.iqm4hd.client.RConn;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJson;

public class Main {
    public static final ScheduledExecutorService HOUSE_KEEPING = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setDaemon(true).build());
    
    public static final Path PWD = Paths.get(".");
    public static final Path JOBS = PWD.resolve("jobs");
    
    private static RemoteExecutionConfig CONFIG;
    private static String VERSION;
    
    public static String version() { return VERSION; }
    public static RemoteExecutionConfig config() { return CONFIG; }
    
    static {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            Logger.error("===== Uncaught: " + e.getClass().getSimpleName() + " =====");
            Logger.error(e);
        });
    }
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if(!Files.exists(PWD.resolve("config.json"))) {
            Files.write(PWD.resolve("config.json"), new GsonBuilder().setPrettyPrinting().create().toJson(new RemoteExecutionConfig()).getBytes(StandardCharsets.UTF_8));
            Logger.error("No default configuration found.");
            Logger.error("Default configuration has been created. Please review before starting DQGUI remote execution server.");
            return;
        }
        
        Gson gson = new GsonBuilder().serializeNulls().create();
        CONFIG = gson.fromJson(new InputStreamReader(Files.newInputStream(PWD.resolve("config.json")), StandardCharsets.UTF_8), RemoteExecutionConfig.class);
        
        VERSION = CharStreams.toString(new InputStreamReader(Main.class.getResourceAsStream("/dqgui/remote/VERSION"), StandardCharsets.UTF_8));
        
        Logger.info("===== Starting DQGUI Remote Execution Server =====");
        Logger.info("Version: " + VERSION);
        Logger.info("Loaded configuration:\n" + CharStreams.toString(new InputStreamReader(Files.newInputStream(PWD.resolve("config.json")), StandardCharsets.UTF_8)));
        
        Logger.info("Creating executor with {} threads...", CONFIG.getExecutorPoolSize());
        ExecutorService executor = Executors.newFixedThreadPool(CONFIG.getExecutorPoolSize(), new ThreadFactoryBuilder().setDaemon(true).build());
        
        if(CONFIG.isrServe()) {
            Logger.info("Testing RServe connection...");
            try {
                if(!RConn.getConnection().isConnected()) {
                    Logger.error("Failed to connect to RServe instance. Shutting down...");
                    return;
                } else {
                    Logger.info("Connected to RServe instance. Server version: {}", RConn.getConnection().getServerVersion());
                }
            } catch (RserveException e) {
                Logger.error(e);
                Logger.error("Failed to interact with REngine. Shutting down...");
                return;
            }
        }
        
        if(!Files.isDirectory(JOBS))
            Files.createDirectories(JOBS);
        
        for(int i = 0; i < 16; i++) {
            String h = Integer.toHexString(i);
            if(!Files.isDirectory(JOBS.resolve(h)))
                Files.createDirectories(JOBS.resolve(h));
        }

        ExecutionController c = new ExecutionController(new JobRunner(executor), gson);
        
    // Swap std::out with a modified std::out that allows to filter on thread basis
        LogInterceptor.init();
        
    // Call static initializer to discover registered engines
        Class.forName("de.hshannover.dqgui.execution.database.api.EngineManager");
        
        Logger.info("Starting Javalin...");
        
        JavalinJson.setFromJsonMapper(gson::fromJson);
        JavalinJson.setToJsonMapper(gson::toJson);
        
        Javalin app = Javalin.create(config -> {
            config.defaultContentType = "application/json";
            config.enableCorsForAllOrigins();
            config.showJavalinBanner = true;
            config.enforceSsl = CONFIG.isEnforceSSL();
            config.accessManager((handler, ctx, roles) -> {
                if(Objects.equal(ctx.header("X-Key"), CONFIG.getKey())) { 
                    handler.handle(ctx); return; 
                 }
                ctx.status(401).result("{\"error\": \"You are not authorized to connect with system. Set header X-Key to a valid API key.\"}");
            });
        }).routes(() -> {
            path("execution", () -> {
                get(c::getAll);
                post(c::create);
                path("progress", () -> {
                   post(c::progress);
                });
                path("test", () -> {
                    get(ctx -> ctx.result("passed"));
                });
                path("collect", () -> {
                   post(c::collectAll); 
                });
            });
            ws("events", c::events);
        })
        .exception(IllegalArgumentException.class, (e, ctx) -> {
            ctx.status(500).json(new RemoteError(e.getClass().getName(), e.getMessage()));
        })
        .exception(Exception.class, (e, ctx) -> {
            ctx.status(500).json(new RemoteError(e));
            Logger.error(e);
        })
        .start(CONFIG.getPort());
    
        Runtime.getRuntime().addShutdownHook(new Thread(() -> app.stop()));
    }
}
