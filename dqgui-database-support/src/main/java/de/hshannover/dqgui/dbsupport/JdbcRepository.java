package de.hshannover.dqgui.dbsupport;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.tinylog.Logger;
import de.hshannover.dqgui.execution.DSLService;
import de.hshannover.dqgui.execution.database.api.ConnectionHandle;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;
import de.hshannover.dqgui.execution.database.api.RelationalRepositorySupport;
import de.hshannover.dqgui.execution.database.api.Repository;
import de.hshannover.dqgui.execution.database.api.RelationalRepositorySupport.KeyHandle;
import de.hshannover.dqgui.execution.database.api.RelationalRepositorySupport.KeyType;
import de.hshannover.dqgui.execution.database.util.NamedParameterStatement;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import de.hshannover.dqgui.execution.model.Project;
import de.hshannover.dqgui.execution.model.RepoType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.util.Pair;

/**
 * JDBC backed repository
 * 
 * @author Marc Herschel
 *
 */

public class JdbcRepository implements Repository<JdbcConnectionHandle> {
    private final RelationalRepositorySupport support;
    private final ConnectionHandle<Connection> handle;
    private final DatabaseConnection connection;

    public JdbcRepository(DatabaseConnection con) {
        support = con.getEngine().requestRepositorySupport().orElseThrow(() -> new UnsupportedOperationException("no RelationalRepositorySupport registered for engine: " + con.getEngine().uniqueIdentifier()));
        handle = new JdbcConnectionHandle(con);
        connection = con;
    }
    
    @Override
    public DatabaseConnection getBackingConnection() {
        return connection;
    }

    @Override
    public boolean tableExists(String tableName) throws Exception {
        synchronized (handle) {
            try (ResultSet rs = handle.get().getMetaData().getTables(null, null, tableName, null)) {
                int count = 0;
                while (rs.next())
                    count++;
                if (count == 1)
                    return true;
            } finally {
                handle.rollback();
            }
        }
        return false;
    }
    
    @Override
    public ValidationReport create() throws Exception {
        synchronized (handle) {
            if(!tableExists("dqgui_index"))
                create(support.getIndexSchema());
            if(!tableExists("dqgui_repo"))
                create(support.getRepositorySchema());
            if(!tableExists("dqgui_results"))
                create(support.getResultsSchema());
            if(!projectExists("0")) {
                boolean success = false;
                synchronized(handle) {
                    try(NamedParameterStatement s = support.projectCreateGlobalDummyEntry(handle)) {
                        s.executeUpdate();
                        success = true;
                    } finally {
                        if (success) {
                            handle.commit();
                        } else {
                            handle.rollback();
                        }
                    }
                }
            }
            
            return validate();
        }
    }
   
    @Override
    public void close() throws Exception {
        synchronized (handle) {
            handle.close();
        }
    }

    @Override
    public DSLService createDslService(String project) {
        return new JdbcDslService(this, support, handle, Integer.parseInt(project));
    }

    @Override
    @SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION")
    public ValidationReport validate() {
        synchronized (handle) {
            try {
                ValidationReport report = support.validateRepository(
                        generateValidation("dqgui_index"),
                        generateValidation("dqgui_repo"), 
                        generateValidation("dqgui_results"));

                if (report == null) {
                    Logger.warn(
                            "Verification is not implemented for current repository engine. Smooth operation cannot be guaranteed!");
                    return ValidationReport.success();
                }

                return report;
            } catch (Exception e) {
                Logger.error(e);
                return ValidationReport
                        .error("Exception Encountered: " + e.getClass().getSimpleName() + " | " + e.getMessage());
            } finally {
                handle.rollback();
            }
        }
    }
    
    private Map<String, Integer> generateValidation(String table) throws SQLException {
        synchronized (handle) {
            try (NamedParameterStatement s = support.queryAllColumnsNoResults(table, handle)) {
                ResultSet rs = s.executeQuery();
                Map<String, Integer> validation = new HashMap<>();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++)
                    validation.put(rs.getMetaData().getColumnName(i), rs.getMetaData().getColumnType(i));
                return validation;
            } finally {
                handle.rollback();
            }
        }
    }

    @Override
    public void writeResult(String hash, String iqm4hdReport, String dqguiMetadata, String iqm4hdFeedback, String projectIdentifier) throws Exception {
        boolean success = false;
        synchronized (handle) {
            try (NamedParameterStatement s = support.queryResultCreate(hash, iqm4hdReport, dqguiMetadata, iqm4hdFeedback, Integer.parseInt(projectIdentifier), handle)) {
                s.executeUpdate();
                success = true;
            } finally {
                if (success) {
                    handle.commit();
                } else {
                    handle.rollback();
                }
            }
        }
    }

    @Override
    public void removeResult(String hash, String projectIdentifier) throws Exception {
        boolean success = false;
        synchronized (handle) {
            try (NamedParameterStatement s = support.queryResultDelete(hash, Integer.parseInt(projectIdentifier), handle)) {
                s.executeUpdate();
                success = true;
            } finally {
                if (success) {
                    handle.commit();
                } else {
                    handle.rollback();
                }
            }
        }
    }

    @Override
    @SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION")
    public String[] fetchResult(String hash, String projectIdentifier) throws Exception {
        synchronized (handle) {
            try (NamedParameterStatement s = support.queryResultRead(hash, Integer.parseInt(projectIdentifier), handle)) {
                ResultSet rs = s.executeQuery();
                while (rs.next())
                    return new String[] { rs.getString("iqm4hd_report"), rs.getString("dqgui_meta")};
            } finally {
                handle.rollback();
            }
        }
        throw new IllegalStateException("result not found");
    }

    @Override
    @SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION")
    public Set<String> listResults(String projectIdentifier) throws Exception {
        Set<String> hashes = new HashSet<>();
        synchronized (handle) {
            try (NamedParameterStatement s = support.queryResultListAll(Integer.parseInt(projectIdentifier), handle)) {
                ResultSet rs = s.executeQuery();
                while (rs.next())
                    hashes.add(rs.getString("hash"));
            } finally {
                handle.rollback();
            }
        }
        return hashes;
    }
    
    private boolean create(String query) throws Exception {
        boolean success = false;
        synchronized (handle) {
            try (Statement s = handle.get().createStatement()) {
                s.executeUpdate(query);
                success = true;
            } finally {
                if (success) {
                    handle.commit();
                } else {
                    handle.rollback();
                }
            }
        }
        return success;
    }

    @Override
    @SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION")
    public Pair<List<DSLComponent>, List<String>> fetchForSearch(List<DSLComponentType> types, String projectIdentifier) throws Exception {
        List<DSLComponent> c = new ArrayList<>(75);
        List<String> src = new ArrayList<>(75);
        synchronized (handle) {
            try(NamedParameterStatement s = support.queryRequestComponentsForSearch(types, Integer.parseInt(projectIdentifier), handle)) {
                ResultSet rs = s.executeQuery();
                while(rs.next()) {
                    c.add(DSLComponent.of(rs.getString("name"), DSLComponentType.of(rs.getInt("type")), false));
                    src.add(rs.getString("content"));
                }
            } finally {
                handle.rollback();
            }
        }
        return new Pair<>(c, src);
    }

    @Override
    public void updateDatabaseEnvironments(String environments, String projectIdentifier) throws Exception {
        boolean success = false;
        synchronized (handle) {
            try(NamedParameterStatement s = support.projectUpdateDatabaseEnvironments(Integer.parseInt(projectIdentifier), environments, handle)) {
                s.executeUpdate();
                success = true;
            } finally {
                if (success) {
                    handle.commit();
                } else {
                    handle.rollback();
                }
            }
        }
    }

    @Override
    public String fetchDatabaseEnvironments(String projectIdentifier) throws Exception {
        synchronized (handle) {
            try(NamedParameterStatement s = support.projectFetchDatabaseEnvironments(Integer.parseInt(projectIdentifier), handle)) { 
                ResultSet rs = s.executeQuery();
                while(rs.next())
                    return rs.getString("environments");
            } finally {
                handle.rollback();
            }
        }
        throw new IllegalStateException("environments not found");
    }

    @Override
    public Set<Project> listProjects() throws Exception {
        Set<Project> set = new HashSet<>(10);
        synchronized (handle) {
            try(NamedParameterStatement s = support.projectListAll(handle)) {
                ResultSet rs = s.executeQuery();
                while(rs.next())
                    set.add(new Project(Integer.toString(rs.getInt("project")), rs.getString("name"), RepoType.DATABASE, rs.getString("guid")));
            } finally {
                handle.rollback();
            }
        }
        return set;
    }

    @Override
    public String createProject(String projectName, String guid, String databaseEnvironments) throws Exception {
        boolean success = false;
        String key = null;
        synchronized (handle) {
           KeyHandle k = support.projectCreate(projectName, guid, databaseEnvironments, handle);
           try(NamedParameterStatement s = k.statement) {
               if(k.type == KeyType.AUTO_INCREMENT) {
                   ResultSet rs = s.executeUpdateAndGetGeneratedKeys();
                   if(rs.next())
                       key = Integer.toString(rs.getInt("project"));
               } else {
                   s.executeUpdate();
                   key = k.key;
               }
               success = true;
            } finally {
                if (success) {
                    handle.commit();
                } else {
                    handle.rollback();
                }
            }
        }
        return Objects.requireNonNull(key, "generated identifier must not be null!");
    }

    @Override
    public void deleteProject(String projectIdentifier) throws Exception {
        boolean success = false;
        synchronized (handle) {
            try {
                int identifier = Integer.parseInt(projectIdentifier);
                try(NamedParameterStatement s = support.projectDeleteRepo(identifier, handle)) {
                    s.executeUpdate();
                }
                try(NamedParameterStatement s = support.projectDeleteResults(identifier, handle)) {
                    s.executeUpdate();
                }
                try(NamedParameterStatement s = support.projectDeleteProjectEntry(identifier, handle)) {
                    s.executeUpdate();
                }
                success = true;
            } finally {
                if (success) {
                    handle.commit();
                } else {
                    handle.rollback();
                }
            }
        }
    }

    @Override
    public boolean projectExists(String projectIdentifier) throws Exception {
        synchronized (handle) {
            try(NamedParameterStatement s = support.projectExists(Integer.parseInt(projectIdentifier), handle)) {
                ResultSet rs = s.executeQuery();
                while(rs.next())
                    return rs.getInt("matches") > 0;
            } finally {
                handle.rollback();
            }
        }
        throw new IllegalStateException("this should never be executed | check your query PROJECT_EXISTS");
    }

    @Override
    public String readFeedback(String projectIdentifier, String hash) throws Exception {
        synchronized (handle) {
            try(NamedParameterStatement s = support.feedbackRead(Integer.parseInt(projectIdentifier), hash, handle)) {
                ResultSet rs = s.executeQuery();
                while(rs.next())
                    return rs.getString("iqm4hd_feedback");
            } finally {
                handle.rollback();
            }
        }
        throw new IllegalStateException("feedback not found");
    }

    @Override
    public void writeFeedback(String projectIdentifier, String hash, String feedback) throws Exception {
        boolean success = false;
        synchronized (handle) {
            try(NamedParameterStatement s = support.feedbackWrite(Integer.parseInt(projectIdentifier), hash, feedback, handle)) {
                s.executeUpdate();
                success = true;
            } finally {
                if (success) {
                    handle.commit();
                } else {
                    handle.rollback();
                }
            }
        }
    }

    @Override
    public void promoteToGlobal(String projectIdentifier, DSLComponent component) throws Exception {
        boolean success = false;
        synchronized (handle) {
            try(NamedParameterStatement s = support.promotionToGlobal(Integer.parseInt(projectIdentifier), component, handle)) {
                s.executeUpdate();
                success = true;
            } finally {
                if (success) {
                    handle.commit();
                } else {
                    handle.rollback();
                }   
            }
        }
    }

    @Override
    public void demoteToLocal(String projectIdentifier, DSLComponent component) throws Exception {
        boolean success = false;
        synchronized (handle) {
            try(NamedParameterStatement s = support.demotionToLocal(Integer.parseInt(projectIdentifier), component, handle)) {
                s.executeUpdate();
                success = true;
            } finally {
                if (success) {
                    handle.commit();
                } else {
                    handle.rollback();
                }   
            }
        }
    }
}
