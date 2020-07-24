package de.hshannover.dqgui.execution.database.api;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import de.hshannover.dqgui.execution.DSLService;
import de.hshannover.dqgui.execution.database.api.Repository.ValidationReport;
import de.hshannover.dqgui.execution.database.util.NamedParameterStatement;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.DSLComponentType;

/**
 *  This class helps with different SQL dialects for relational databases by supplying implementation dependent queries.<br>
 *  RelationalRepositorySupport returns named prepared statements.<br>
 *  That means instead of writing <code>SELECT * FROM t WHERE name = ?</code> you would write <code>SELECT * FROM t WHERE name = :name</code><br>
 *  RelationalRepositorySupport is used by Engines using the JdbcRepository implementation.<br>
 *  Table, column and parameter names are hard coded and must be used in your implementation.<br>
 *  It is recommended to use this in combination with the subclass {@link YamlConfigurableRelationalRepositorySupport}.<br>
 *  
 *  A NoSQL solution would not need to implement this but instead just provide a {@link Repository} and {@link DSLService} implementation.
 *
 * @author Marc Herschel
 *
 */
public interface RelationalRepositorySupport {
    ValidationReport validateRepository(Map<String, Integer> index, Map<String, Integer> repo, Map<String, Integer> results);
    
    String getIndexSchema();
    String getRepositorySchema();
    String getResultsSchema();
    
    public enum KeyType {
        AUTO_INCREMENT,
        SELF_SUPPLIED;
    }
    
    /**
     * A key handle allows to abstract key creation.<br>
     * Some databases allow auto incrementing keys with the query, others require you to supply your own key by querying a series first.<br>
     * The KeyHandle allows to abstract this away so the higher layer always gets the correct primary key.
     */
    public static class KeyHandle {
        public final KeyType type;
        public final NamedParameterStatement statement;
        public final String key;
        
        private KeyHandle(KeyType type, NamedParameterStatement statement, String key) {
            this.type = type;
            this.statement = statement;
            this.key = key;
        }
        
        public static KeyHandle ofAutoIncrementing(NamedParameterStatement statement) {
            return new KeyHandle(KeyType.AUTO_INCREMENT, statement, null);
        }
        
        public static KeyHandle ofSelfSupplied(NamedParameterStatement statement, String key) {
            return new KeyHandle(KeyType.SELF_SUPPLIED, statement, null);
        }
    }
    
    NamedParameterStatement queryAllColumnsNoResults(String tablename, ConnectionHandle<Connection> con);
    NamedParameterStatement queryComponentExists(DSLComponent where, int project, ConnectionHandle<Connection> con);
    NamedParameterStatement queryComponentCreateEmpty(DSLComponent where, int project, ConnectionHandle<Connection> con);
    NamedParameterStatement queryComponentRead(DSLComponent where, int project, ConnectionHandle<Connection> con);
    NamedParameterStatement queryComponentUpdate(DSLComponent where, String content, String extraData, int project, ConnectionHandle<Connection> con);
    NamedParameterStatement queryComponentUpdateExtraDataOnly(DSLComponent where, String extraData, int project, ConnectionHandle<Connection> con);
    NamedParameterStatement queryComponentDelete(DSLComponent where, int project, ConnectionHandle<Connection> con);
    NamedParameterStatement queryComponentMove(DSLComponent from, DSLComponent to, int project, ConnectionHandle<Connection> con);
    NamedParameterStatement queryComponentListAll(int project, ConnectionHandle<Connection> con);
    NamedParameterStatement queryResultListAll(int project, ConnectionHandle<Connection> con);
    NamedParameterStatement queryResultCreate(String hash, String iqm4hdReport, String dqguiMeta, String iqm4hdFeedback, int project, ConnectionHandle<Connection> con);
    NamedParameterStatement queryResultDelete(String hash, int project, ConnectionHandle<Connection> con);
    NamedParameterStatement queryResultRead(String hash, int project, ConnectionHandle<Connection> con);
    NamedParameterStatement queryRequestComponentsForSearch(List<DSLComponentType> types, int project, ConnectionHandle<Connection> con);
    NamedParameterStatement projectCreateGlobalDummyEntry(ConnectionHandle<Connection> con);
    NamedParameterStatement projectListAll(ConnectionHandle<Connection> con);
    KeyHandle projectCreate(String projectName, String guid, String environments, ConnectionHandle<Connection> con);
    NamedParameterStatement projectDeleteRepo(int project, ConnectionHandle<Connection> con);
    NamedParameterStatement projectDeleteResults(int project, ConnectionHandle<Connection> con);
    NamedParameterStatement projectDeleteProjectEntry(int project, ConnectionHandle<Connection> con);
    NamedParameterStatement projectFetchDatabaseEnvironments(int project, ConnectionHandle<Connection> con);
    NamedParameterStatement projectUpdateDatabaseEnvironments(int project, String environments, ConnectionHandle<Connection> con);
    NamedParameterStatement projectExists(int project, ConnectionHandle<Connection> con);
    NamedParameterStatement feedbackRead(int project, String hash, ConnectionHandle<Connection> con);
    NamedParameterStatement feedbackWrite(int project, String hash, String feedback, ConnectionHandle<Connection> con);
    NamedParameterStatement promotionToGlobal(int project, DSLComponent which, ConnectionHandle<Connection> con);
    NamedParameterStatement demotionToLocal(int project, DSLComponent which, ConnectionHandle<Connection> con);
}
