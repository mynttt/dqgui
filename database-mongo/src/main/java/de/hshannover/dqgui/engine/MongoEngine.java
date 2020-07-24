package de.hshannover.dqgui.engine;

import java.util.Map;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import de.hshannover.dqgui.engine.mongodb.MongoDbFetcher;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;
import de.hshannover.dqgui.execution.database.api.DatabaseEngine;
import de.hshannover.dqgui.execution.database.api.DatabaseFetcher;
import de.hshannover.dqgui.execution.database.api.DatabaseSocket.HostFormat;
import de.hshannover.dqgui.execution.database.api.DatabaseTests.DatabaseTestResult;
import de.hshannover.dqgui.execution.database.api.Repository;
import de.hshannover.dqgui.execution.database.gui.GuiConfiguration;
import de.hshannover.dqgui.execution.database.gui.Icon;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class MongoEngine extends DatabaseEngine {
    private static final String RESOURCES = "/engine/mongo/";
    
    protected MongoEngine() throws Exception {
        super();
        registerIdentifier("mongo");
        registerLanguage("mongo");
        registerPort(27017);
        registerGuiSupport(GuiConfiguration.ofCustom(
                Icon.of(MongoEngine.class.getResourceAsStream(RESOURCES + "MONGO.png")), 
                RESOURCES + "MONGO.fxml",
                RESOURCES + "style.css"));
    }

    @Override
    protected void loadDatabaseDriver() throws ClassNotFoundException {}
    
    @Override
    public DatabaseFetcher createFetcher(DatabaseConnection connection) {
        return new MongoDbFetcher(connection);
    }
    
    @Override
    public String name() {
        return "MongoDB";
    }
    
    @Override
    public boolean isRelational() {
        return false;
    }
    
    @Override
    public boolean allowUseForRepository() {
        return false;
    }
    
    @Override
    public boolean allowUseForIqm4hd() {
        return true;
    }
    
    @Override
    public boolean supportsJdbc() {
        return false;
    }
    
    @Override
    public String createDataSourceUrl(DatabaseConnection connection) {
        return null;
    }

    @Override
    @SuppressWarnings("rawtypes")
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    public DatabaseTestResult test(DatabaseConnection connection) {
        String host = connection.getSocket().getFormat() == HostFormat.IPv6 ? String.format("[%s]", connection.getSocket().getHost()) : connection.getSocket().getHost();
        if(connection.getCredential().getUsername() == null && connection.getCredential().getPassword() == null) {
            try(MongoClient m = new MongoClient(new ServerAddress(host, connection.getSocket().getPort()), MongoClientOptions.builder().serverSelectionTimeout(500).build())) {
                MongoDatabase database = m.getDatabase("admin");
                Document serverStatus = database.runCommand(new Document("serverStatus", 1));
                Map connections = (Map) serverStatus.get("connections");
                connections.get("current");
                return new DatabaseTestResult();
            } catch(Exception e) {
                return new DatabaseTestResult(e);
            }
        } else {
            MongoCredential credential = MongoCredential.createCredential(connection.getCredential().getUsername(), connection.getCredential().getDatabase(), connection.getCredential().getPassword().toCharArray());
            try(MongoClient m = new MongoClient(new ServerAddress(host, connection.getSocket().getPort()), credential, MongoClientOptions.builder().serverSelectionTimeout(500).build())) {
                MongoDatabase database = m.getDatabase("admin");
                Document serverStatus = database.runCommand(new Document("serverStatus", 1));
                Map connections = (Map) serverStatus.get("connections");
                connections.get("current");
                return new DatabaseTestResult();
            } catch(Exception e) {
                return new DatabaseTestResult(e);
            }
        }
    }

    @Override
    protected Repository<?> createRepositoryForConnection(DatabaseConnection connection) {
        return null;
    }
}
