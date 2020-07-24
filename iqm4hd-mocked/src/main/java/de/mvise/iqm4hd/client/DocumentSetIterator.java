package de.mvise.iqm4hd.client;

import java.util.Date;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import de.mvise.iqm4hd.api.DatabaseEntry;
import de.mvise.iqm4hd.api.DatabaseEntryIterator;

public class DocumentSetIterator extends DatabaseEntryIterator {

    private MongoCursor<Document> iter;

    public DocumentSetIterator(MongoIterable<Document> iter) {
        this.iter = iter.iterator();
    }

    @Override
    public DatabaseEntry next() {
        
        if (!iter.hasNext())
            return null;
        
        Document doc = iter.next();

        DatabaseEntryImpl entry = new DatabaseEntryImpl(0);
        for (String key : doc.keySet()) {
            collectEntriesFromValue(entry, key, doc.get(key));
        }
        return entry;
    }

    private void collectEntriesFromValue(DatabaseEntryImpl entry, String key, Object object) {
        key = key.toUpperCase();
        if (object instanceof String) {
            entry.add(key, object);
        } else if (object instanceof ObjectId) {
            entry.add(key, object.toString());
        } else if (object instanceof Number) {
            entry.add(key, object);
        } else if (object instanceof Document) {
            entry.add(key, object);
            Document subdoc = (Document) object;
            for (String subkey : subdoc.keySet()) {
                collectEntriesFromValue(entry, key + "." + subkey, subdoc.get(subkey));
            }   
        } else if (object instanceof Date) {
            entry.add(key, object.toString());
        } else if (object instanceof List) {
            entry.add(key, object.toString());
        } else {
            System.out.println("Unknown database entry type " + object.getClass().getName() + " for key " + key);
        }
    }
}