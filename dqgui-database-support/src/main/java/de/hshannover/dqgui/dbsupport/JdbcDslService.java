package de.hshannover.dqgui.dbsupport;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.tinylog.Logger;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import de.hshannover.dqgui.execution.ComponentSplitter;
import de.hshannover.dqgui.execution.DSLService;
import de.hshannover.dqgui.execution.DSLServiceException;
import de.hshannover.dqgui.execution.database.api.ConnectionHandle;
import de.hshannover.dqgui.execution.database.api.RelationalRepositorySupport;
import de.hshannover.dqgui.execution.database.api.Repository.ValidationReport;
import de.hshannover.dqgui.execution.database.util.NamedParameterStatement;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import de.mvise.iqm4hd.api.RuleService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.util.Pair;

/**
 * JDBC backed DSL service
 * 
 * @author Marc Herschel
 *
 */

public class JdbcDslService implements DSLService {
    @SuppressWarnings("serial")
    private final Type t = new TypeToken<Map<String,String>>() {}.getType();
    private final Gson gson = new Gson();
    private final JdbcRepository repository;
    private final RelationalRepositorySupport support;
    private final ConnectionHandle<Connection> handle;
    private final int project;

    public JdbcDslService(JdbcRepository jdbcRepository, RelationalRepositorySupport support, ConnectionHandle<Connection> handle, int project) {
        this.repository = jdbcRepository;
        this.support = support;
        this.handle = handle;
        this.project = project;
    }

    @Override
    @SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION")
    public boolean exists(DSLComponent where) {
        synchronized (handle) {
            try(NamedParameterStatement s = support.queryComponentExists(where, where.isGlobal() ? 0 : project, handle)) {
                ResultSet rs = s.executeQuery();
                while(rs.next())
                    return rs.getInt("matches") > 0;
            } catch (Exception e) {
            } finally {
                handle.rollback();
            }
        }
        throw new IllegalStateException("invalid operation: matches not found");
    }
    
    @Override
    public boolean existsInNamespace(DSLComponent where) {
        synchronized (handle) {
            try(NamedParameterStatement s = support.queryComponentExists(where, project, handle)) {
                ResultSet rs = s.executeQuery();
                while(rs.next())
                    return rs.getInt("matches") > 0;
            } catch (Exception e) {
            } finally {
                handle.rollback();
            }
        }
        throw new IllegalStateException("invalid operation: matches not found");
    }
    
    @Override
    public boolean isGlobalCheck(String checkIdentifier) {
        synchronized (handle) {
            try(NamedParameterStatement s = support.queryComponentExists(DSLComponent.of(checkIdentifier, DSLComponentType.CHECK, true), 0, handle)) {
                ResultSet rs = s.executeQuery();
                while(rs.next())
                    return rs.getInt("matches") > 0;
            } catch (Exception e) {
            } finally {
                handle.rollback();
            }
        }
        throw new IllegalStateException("invalid operation: matches not found");
    }

    @Override
    public void create(DSLComponent where) throws DSLServiceException {
        boolean success = false;
        synchronized (handle) {
            try(NamedParameterStatement s = support.queryComponentCreateEmpty(where, where.isGlobal() ? 0 : project, handle)) {
                s.executeUpdate();
                success = true;
            } catch(Exception e) {
                throw new DSLServiceException(e);
            } finally {
                if(success) {
                    handle.commit();
                } else {
                    handle.rollback();
                }
            }
        }
    }

    @Override
    @SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION")
    public String read(DSLComponent where) throws DSLServiceException {
        synchronized(handle) {
            try(NamedParameterStatement s = support.queryComponentRead(where, where.isGlobal() ? 0 : project, handle)) {
                ResultSet rs = s.executeQuery();
                while(rs.next())
                    return rs.getString("content");
            } catch (Exception e) {
                throw new DSLServiceException(e);
            } finally {
                handle.rollback();
            }
            throw new IllegalStateException("invalid operation: " + where.getType() + " " + where.getIdentifier() + " does not exist");
        }
    }

    @Override
    public void update(DSLComponent where, String toUpdate) throws DSLServiceException {
        boolean success = false;
        synchronized (handle) {
            try (NamedParameterStatement s = support.queryComponentUpdate(where, toUpdate,
                    gson.toJson(where.getExtraData()), where.isGlobal() ? 0 : project, handle)) {
                s.executeUpdate();
                success = true;
            } catch (Exception e) {
                throw new DSLServiceException(e);
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
    public void delete(DSLComponent where) throws DSLServiceException {
        boolean success = false;
        synchronized (handle) {
            try (NamedParameterStatement s = support.queryComponentDelete(where, where.isGlobal() ? 0 : project, handle)) {
                s.executeUpdate();
                success = true;
            } catch (Exception e) {
                throw new DSLServiceException(e);
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
    public void move(DSLComponent from, DSLComponent to) throws DSLServiceException {
        boolean success = false;
        synchronized (handle) {
            if (exists(to))
                throw new DSLServiceException("Can't move because " + to.getType() + " component already exists: " + to.toString());
            try (NamedParameterStatement s = support.queryComponentMove(from, to, from.isGlobal() ? 0 : project, handle)) {
                s.executeUpdate();
                success = true;
            } catch (Exception e) {
                throw new DSLServiceException(e);
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
    public List<DSLComponent> discover() throws DSLServiceException {
        List<DSLComponent> components = new ArrayList<>();
        synchronized (handle) {
            try (NamedParameterStatement s = support.queryComponentListAll(project, handle)) {
                ResultSet rs = s.executeQuery();
                while (rs.next())
                    components.add(DSLComponent.of(
                            rs.getString("name"), 
                            DSLComponentType.of(rs.getInt("type")),
                            gson.fromJson(rs.getString("extra_data"), t),
                            rs.getInt("project") == 0));
            } catch (Exception e) {
                throw new DSLServiceException(e);
            } finally {
                handle.rollback();
            }
        }
        return components;
    }

    @Override
    public RuleService createRuleService() {
        return new JdbcRuleService(this);
    }

    @Override
    public RepositoryStatus validateRepository() {
        try {
            ValidationReport r = repository.validate();
            if(r.success)
                return new RepositoryStatus();
            return new RepositoryStatus(r.message);
        } catch(Exception e) {
            Logger.error(e);
            return new RepositoryStatus("Exception while testing: " + e.getClass() + " | " + e.getMessage());
        }
    }

    @Override
    public void updateExtraDataOnly(DSLComponent where) throws DSLServiceException {
        boolean success = false;
        synchronized (handle) {
            try (NamedParameterStatement s = support.queryComponentUpdateExtraDataOnly(where,
                    gson.toJson(where.getExtraData()), where.isGlobal() ? 0 : project, handle)) {
                s.executeUpdate();
                success = true;
            } catch (Exception e) {
                throw new DSLServiceException(e);
            } finally {
                if (success) {
                    handle.commit();
                } else {
                    handle.rollback();
                }
            }
        }
    }
    
    private static class JdbcDslSplitter implements ComponentSplitter {
        private final List<DSLComponent> components;
        private final List<String> sources;
        
        private JdbcDslSplitter(JdbcRepository repo, List<DSLComponentType> forTypes, int project) throws DSLServiceException {
            try {
                Pair<List<DSLComponent>, List<String>> r = repo.fetchForSearch(forTypes, Integer.toString(project));
                components = r.getKey();
                sources = r.getValue();
            } catch (Exception e) {
                throw new DSLServiceException(e);
            }
        }

        @Override
        public Iterator<ComponentSourceMapping>[] splitBy(int splitBy) {
            int n = components.size() < 30 ? components.size() : (int) (components.size() / (splitBy * 1.0)) + 1;
            List<List<DSLComponent>> cps = Lists.partition(components, n);
            List<List<String>> strs = Lists.partition(sources, n);
            @SuppressWarnings("unchecked")
            Iterator<ComponentSourceMapping>[] iters = (Iterator<ComponentSourceMapping>[]) new Iterator[cps.size()];
            for(int i = 0; i < cps.size(); i++) {
                final int idx = i;
                Iterator<ComponentSourceMapping> it = new Iterator<ComponentSourceMapping>() {
                    private final Iterator<DSLComponent> icp = cps.get(idx).iterator();
                    private final Iterator<String> src = strs.get(idx).iterator();
                    
                    @Override
                    public ComponentSourceMapping next() {
                        return new ComponentSourceMapping(icp.next(), src.next());
                    }
                    
                    @Override
                    public boolean hasNext() {
                        return icp.hasNext();
                    }
                };
                iters[i] = it;
            }
            return iters;
        }
    }

    @Override
    public ComponentSplitter createComponentSplitter(List<DSLComponentType> forTypes) throws DSLServiceException {
        return new JdbcDslSplitter(repository, forTypes, project);
    }
}
