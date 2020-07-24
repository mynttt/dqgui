package de.hshannover.dqgui.engine.mongodb;

import java.util.List;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;
import de.hshannover.dqgui.execution.database.api.DatabaseFetcher;
import de.hshannover.dqgui.execution.database.api.DatabaseSocket.HostFormat;
import de.mvise.iqm4hd.api.DatabaseEntryIterator;
import de.mvise.iqm4hd.client.DocumentSetIterator;

/**
 * Fetcher for MongoDB databases.
 *
 * @author Marc Herschel
 *
 */
public final class MongoDbFetcher extends DatabaseFetcher {
    private MongoClient client;

    public MongoDbFetcher(DatabaseConnection connection) {
        super(connection);
    }

    @Override
    public void initiate() {
        String host = connectionData.getSocket().getFormat() == HostFormat.IPv6 ? String.format("[%s]", connectionData.getSocket().getHost()) : connectionData.getSocket().getHost();
        if(connectionData.getCredential().getUsername() == null && connectionData.getCredential().getPassword() == null) {
            client = new MongoClient(new ServerAddress(host, connectionData.getSocket().getPort()), MongoClientOptions.builder().serverSelectionTimeout(10000).build());
        } else {
            MongoCredential credential = MongoCredential.createCredential(connectionData.getCredential().getUsername(), connectionData.getCredential().getDatabase(), connectionData.getCredential().getPassword().toCharArray());
            client = new MongoClient(new ServerAddress(host, connectionData.getSocket().getPort()), credential, MongoClientOptions.builder().serverSelectionTimeout(10000).build());
        }
    }

    /**
     * This is from the DatabaseServiceTestImpl.java from the IQM4HD package.
     * Created by burikovs on 14.08.2017.
     */
    @Override
    public DatabaseEntryIterator fetch(String query) {
        int firstDot = query.indexOf('.');
        int secondDot = query.indexOf('.', firstDot+1);
        int open = query.indexOf('(');
        int close = query.lastIndexOf(')');
        String collection = query.substring(firstDot+1, secondDot);
        String command = query.substring(secondDot+1, open);
        String innerQuery = query.substring(open+1, close);

        MongoDatabase db = client.getDatabase(connectionData.getCredential().getDatabase());
        MongoCollection<Document> coll = db.getCollection(collection);
        MongoIterable<Document> res;

        if (command.equals("find")) {
            Document queryDoc = Document.parse(innerQuery);
            res = coll.find(queryDoc);
        } else {
            innerQuery = "{ val: " + innerQuery + "}";
            Document queryDoc = Document.parse(innerQuery);
            @SuppressWarnings("unchecked")
            List<Document> pipeline = (List<Document>) queryDoc.get("val");
            res = coll.aggregate(pipeline);
        }

        return new DocumentSetIterator(res);
    }

    @Override
    public void close() throws Exception {
        client.close();
    }
}
