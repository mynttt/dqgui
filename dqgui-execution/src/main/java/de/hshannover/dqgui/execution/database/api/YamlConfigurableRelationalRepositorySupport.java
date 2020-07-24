package de.hshannover.dqgui.execution.database.api;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.tinylog.Logger;
import org.yaml.snakeyaml.Yaml;
import de.hshannover.dqgui.execution.database.util.NamedParameterStatement;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.DSLComponentType;

/**
 * A {@link RelationalRepositorySupport} that is configurable through YAML and results thus in less boiler plate.
 * 
 * @author Marc Herschel
 *
 */

public abstract class YamlConfigurableRelationalRepositorySupport implements RelationalRepositorySupport {
    protected static final String TABLE = "__TABLE__";
    protected static final String TYPE_TUPEL = "__TYPE_TUPEL__";
    protected static final String PROJECT = "project";
    protected static final String TYPE = "type";
    protected static final String CONTENT = "content";
    protected static final String NAME = "name";
    protected static final String NEW_NAME = "new_name";
    protected static final String OLD_NAME = "old_name";
    protected static final String EXTRA = "extra_data";    
    protected static final String HASH = "hash";
    protected static final String DQM = "dqgui_meta";
    protected static final String IQR = "iqm4hd_report";
    protected static final String IQF = "iqm4hd_feedback";
    protected static final String PROJECT_NAME = "project_name";
    protected static final String ENVIRONMENTS = "environments";
    protected static final String GUID = "guid";
    
    @SuppressWarnings("serial")
    private static final Map<String, String[]> QUERIES = new HashMap<String, String[]>() {{
        put("Q_ALL_COLUMNS_NO_RESULTS", new String[] {TABLE});
        put("Q_COMPONENT_EXISTS", new String[] {TYPE, NAME, PROJECT});
        put("Q_COMPONENT_CREATE_EMPTY", new String[] {PROJECT, TYPE, NAME});
        put("Q_COMPONENT_READ", new String[] {TYPE, NAME, PROJECT});
        put("Q_COMPONENT_UPDATE", new String[] {CONTENT, EXTRA, TYPE, NAME, PROJECT});
        put("Q_COMPONENT_UPDATE_EXTRA_DATA_ONLY", new String[] {EXTRA, TYPE, NAME, PROJECT});
        put("Q_COMPONENT_DELETE", new String[] {TYPE, NAME, PROJECT});
        put("Q_COMPONENT_MOVE", new String[] {NEW_NAME, OLD_NAME, TYPE, PROJECT});
        put("Q_COMPONENT_LIST_ALL", new String[] {PROJECT});
        put("Q_RESULT_LIST_ALL", new String[] {PROJECT});
        put("Q_RESULT_DELETE", new String[] {HASH, PROJECT});
        put("Q_RESULT_READ", new String[] {HASH, PROJECT});
        put("Q_RESULT_CREATE", new String[] {PROJECT, HASH, DQM, IQR, IQF});
        put("Q_REQUEST_COMPONENTS_FOR_SEARCH", new String[] {TYPE_TUPEL, PROJECT});
        put("Q_PROJECT_CREATE_GLOBAL_DUMMY_ENTRY", new String[] {});
        put("Q_PROJECT_LIST_ALL", new String[] {});
        put("Q_PROJECT_CREATE", new String[] {PROJECT_NAME, ENVIRONMENTS, GUID});
        put("Q_PROJECT_DELETE_REPO", new String[] {PROJECT});
        put("Q_PROJECT_DELETE_RESULTS", new String[] {PROJECT});
        put("Q_PROJECT_DELETE_PROJECT_ENTRY", new String[] {PROJECT});
        put("Q_PROJECT_FETCH_DATABASE_ENVIRONMENTS", new String[] {PROJECT});
        put("Q_PROJECT_UPDATE_DATABASE_ENVIRONMENTS", new String[] {PROJECT, ENVIRONMENTS});
        put("Q_PROJECT_EXISTS", new String[] {PROJECT});
        put("Q_FEEDBACK_READ", new String[] {PROJECT, HASH});
        put("Q_FEEDBACK_WRITE", new String[] {PROJECT, HASH, IQF});
        put("Q_PROMOTION_TO_GLOBAL", new String[] {PROJECT, TYPE, NAME});
        put("Q_DEMOTION_TO_LOCAL", new String[] {PROJECT, TYPE, NAME});
    }};
    
    static {
        QUERIES.forEach((k, v) -> {
            for(int i = 0; i < v.length; i++) {
                if(v[i].startsWith("__")) continue;
                v[i] = (":" + v[i]).intern();
            }
        });
    }
    
    private static final String[] SCHEMAS = {
            "index",
            "results", 
            "repository"
    };
    
    private final Map<String, String> queries = new HashMap<>(), schemas = new HashMap<>();
    
    /**
     * Construct a new YamlConfigurableRelationalRepositorySupport instance.<br>
     * The constructor will parse the YAML and check if all keys exist within the configuration and that the queries have the correct {@link NamedParameterStatement} dependencies.
     * @param yamlRawdataSchema table schema to load
     * @param yamlRawdataQueries queries to load
     */
    public YamlConfigurableRelationalRepositorySupport(String yamlRawdataSchema, String yamlRawdataQueries) {
        Yaml y = new Yaml();
        queries.putAll(y.load(yamlRawdataQueries));
        schemas.putAll(y.load(yamlRawdataSchema));
        int missing = 0;
        
        for(Map.Entry<String, String[]> k : QUERIES.entrySet()) {
            String q = queries.get(k.getKey());
            if(q == null || q.trim().isEmpty()) {
                missing++;
                Logger.error("Missing YAML query key or blank value: {} => ", k.getKey(), q);
                continue;
            }
            for(String s : k.getValue()) {
                if(!q.contains(s)) {
                    Logger.error("Missing required named parameter: " + s + " in query: " + k.getKey() + " (" + q + ")");
                    missing++;
                }
            }
        }
        
        for(String k : SCHEMAS) {
            if(!schemas.containsKey(k) || schemas.get(k) == null || schemas.get(k).trim().isEmpty()) {
                missing++;
                Logger.error("Missing YAML schema key or blank value: {}", k);
            }
        }
        
        if(missing > 0) {
            Logger.error("Incomplete YAML configuration. Aborting...");
            throw new IllegalArgumentException("Incomplete repository configuration. Missing " + missing + " YAML configuration item(s). Please check log for more details.");
        }
    }
    

    @Override
    public final String getIndexSchema() {
        return getSchema("index");
    }

    @Override
    public final String getRepositorySchema() {
        return getSchema("repository");
    }

    @Override
    public final String getResultsSchema() {
        return getSchema("results");
    } 
    
    public final String getSchema(String key) {
        return Objects.requireNonNull(schemas.get(key), "schema: " + key + "is null");
    }
    
    private String q(String k) {
        return queries.get(k);
    }

    private NamedParameterStatement s(ConnectionHandle<Connection> con, String q) {
        return new NamedParameterStatement(con.get(), q);
    }
    
    @Override
    public NamedParameterStatement queryAllColumnsNoResults(String tablename, ConnectionHandle<Connection> con) {
        return s(con, q("Q_ALL_COLUMNS_NO_RESULTS").replace(TABLE, tablename));
    }

    @Override
    public final NamedParameterStatement queryComponentExists(DSLComponent where, int project,
            ConnectionHandle<Connection> con) {
        return s(con, q("Q_COMPONENT_EXISTS"))
                .setInt(TYPE, where.getType().identifier)
                .setString(NAME, where.getIdentifier())
                .setInt(PROJECT, project);
    }

    @Override
    public final NamedParameterStatement queryComponentCreateEmpty(DSLComponent where, int project,
            ConnectionHandle<Connection> con) {
        return s(con, q("Q_COMPONENT_CREATE_EMPTY"))
                .setInt(PROJECT, project)
                .setInt(TYPE, where.getType().identifier)
                .setString(NAME, where.getIdentifier());
    }

    @Override
    public final NamedParameterStatement queryComponentRead(DSLComponent where, int project,
            ConnectionHandle<Connection> con) {
        return s(con, q("Q_COMPONENT_READ"))
                .setInt(PROJECT, project)
                .setInt(TYPE, where.getType().identifier)
                .setString(NAME, where.getIdentifier());
    }

    @Override
    public final NamedParameterStatement queryComponentUpdate(DSLComponent where, String content, String extraData, int project,
            ConnectionHandle<Connection> con) {
        return s(con, q("Q_COMPONENT_UPDATE"))
                .setInt(PROJECT, project)
                .setInt(TYPE, where.getType().identifier)
                .setString(EXTRA, extraData)
                .setString(CONTENT, content)
                .setString(NAME, where.getIdentifier());
    }

    @Override
    public final NamedParameterStatement queryComponentUpdateExtraDataOnly(DSLComponent where, String extraData, int project,
            ConnectionHandle<Connection> con) {
        return s(con, q("Q_COMPONENT_UPDATE_EXTRA_DATA_ONLY"))
                .setString(EXTRA, extraData)
                .setInt(TYPE, where.getType().identifier)
                .setInt(PROJECT, project)
                .setString(NAME, where.getIdentifier());
    }

    @Override
    public final NamedParameterStatement queryComponentDelete(DSLComponent where, int project,
            ConnectionHandle<Connection> con) {
        return s(con, q("Q_COMPONENT_DELETE"))
                .setInt(TYPE, where.getType().identifier)
                .setString(NAME, where.getIdentifier())
                .setInt(PROJECT, project);
    }

    @Override
    public final NamedParameterStatement queryComponentMove(DSLComponent from, DSLComponent to, int project,
            ConnectionHandle<Connection> con) {
        return s(con, q("Q_COMPONENT_MOVE"))
                .setString(NEW_NAME, to.getIdentifier())
                .setString(OLD_NAME, from.getIdentifier())
                .setInt(TYPE, from.getType().identifier)
                .setInt(PROJECT, project);
    }

    @Override
    public final NamedParameterStatement queryComponentListAll(int project, ConnectionHandle<Connection> con) {
        return s(con, q("Q_COMPONENT_LIST_ALL"))
                .setInt(PROJECT, project);
    }

    @Override
    public final NamedParameterStatement queryResultListAll(int project, ConnectionHandle<Connection> con) {
        return s(con, q("Q_RESULT_LIST_ALL"))
                .setInt(PROJECT, project);
    }

    @Override
    public final NamedParameterStatement queryResultCreate(String hash, String iqm4hdReport, String dqguiMeta, String iqm4hdFeedback, int project,
            ConnectionHandle<Connection> con) {
        return s(con, q("Q_RESULT_CREATE"))
                .setInt(PROJECT, project)
                .setString(HASH, hash)
                .setString(DQM, dqguiMeta)
                .setString(IQR, iqm4hdReport)
                .setString(IQF, iqm4hdFeedback);
    }

    @Override
    public final NamedParameterStatement queryResultDelete(String hash, int project, ConnectionHandle<Connection> con) {
        return s(con, q("Q_RESULT_DELETE"))
                .setString(HASH, hash)
                .setInt(PROJECT, project);
    }

    @Override
    public final NamedParameterStatement queryResultRead(String hash, int project, ConnectionHandle<Connection> con) {
        return s(con, q("Q_RESULT_READ"))
                .setString(HASH, hash)
                .setInt(PROJECT, project);
    }

    @Override
    public final NamedParameterStatement queryRequestComponentsForSearch(List<DSLComponentType> types, int project,
            ConnectionHandle<Connection> con) {
        return s(con, q("Q_REQUEST_COMPONENTS_FOR_SEARCH").replace(TYPE_TUPEL, "(" + String.join(",", 
                types.stream().map(c -> c.identifier).map(i -> Integer.toString(i)).collect(Collectors.toList())) +")"))
                .setInt(PROJECT, project);
    }

    @Override
    public final NamedParameterStatement projectCreateGlobalDummyEntry(ConnectionHandle<Connection> con) {
        return s(con, q("Q_PROJECT_CREATE_GLOBAL_DUMMY_ENTRY"));
    }

    @Override
    public final NamedParameterStatement projectListAll(ConnectionHandle<Connection> con) {
        return s(con, q("Q_PROJECT_LIST_ALL"));
    }

    @Override
    @SuppressWarnings("resource")
    public KeyHandle projectCreate(String projectName, String guid, String environments, ConnectionHandle<Connection> con) {
        return KeyHandle.ofAutoIncrementing(new NamedParameterStatement(con.get(), q("Q_PROJECT_CREATE"), "project")
                .setString(ENVIRONMENTS, environments)
                .setString(PROJECT_NAME, projectName)
                .setString(GUID, guid));
    }

    @Override
    public final NamedParameterStatement projectDeleteRepo(int project, ConnectionHandle<Connection> con) {
        return s(con, q("Q_PROJECT_DELETE_REPO"))
                .setInt(PROJECT, project);
    }

    @Override
    public final NamedParameterStatement projectDeleteResults(int project, ConnectionHandle<Connection> con) {
        return s(con, q("Q_PROJECT_DELETE_RESULTS"))
                .setInt(PROJECT, project);
    }

    @Override
    public final NamedParameterStatement projectDeleteProjectEntry(int project, ConnectionHandle<Connection> con) {
        return s(con, q("Q_PROJECT_DELETE_PROJECT_ENTRY"))
                .setInt(PROJECT, project);
    }

    @Override
    public final NamedParameterStatement projectFetchDatabaseEnvironments(int project, ConnectionHandle<Connection> con) {
        return s(con, q("Q_PROJECT_FETCH_DATABASE_ENVIRONMENTS"))
                .setInt(PROJECT, project);
    }

    @Override
    public final NamedParameterStatement projectUpdateDatabaseEnvironments(int project, String environments,
            ConnectionHandle<Connection> con) {
        return s(con, q("Q_PROJECT_UPDATE_DATABASE_ENVIRONMENTS"))
                .setInt(PROJECT, project)
                .setString(ENVIRONMENTS, environments);
    }

    @Override
    public final NamedParameterStatement projectExists(int project, ConnectionHandle<Connection> con) {
        return s(con, q("Q_PROJECT_EXISTS"))
                .setInt(PROJECT, project);
    }

    @Override
    public final NamedParameterStatement feedbackRead(int project, String hash, ConnectionHandle<Connection> con) {
        return s(con, q("Q_FEEDBACK_READ"))
                .setInt(PROJECT, project)
                .setString(HASH, hash);
    }

    @Override
    public final NamedParameterStatement feedbackWrite(int project, String hash, String feedback,
            ConnectionHandle<Connection> con) {
        return s(con, q("Q_FEEDBACK_WRITE"))
                .setInt(PROJECT, project)
                .setString(HASH, hash)
                .setString(IQF, feedback);
    }

    @Override
    public NamedParameterStatement promotionToGlobal(int project, DSLComponent which,
            ConnectionHandle<Connection> con) {
        return s(con, q("Q_PROMOTION_TO_GLOBAL"))
                .setInt(PROJECT, project)
                .setInt(TYPE, which.getType().identifier)
                .setString(NAME, which.getIdentifier());
    }

    @Override
    public NamedParameterStatement demotionToLocal(int project, DSLComponent which, ConnectionHandle<Connection> con) {
        return s(con, q("Q_DEMOTION_TO_LOCAL"))
                .setInt(PROJECT, project)
                .setInt(TYPE, which.getType().identifier)
                .setString(NAME, which.getIdentifier());
    }
}
