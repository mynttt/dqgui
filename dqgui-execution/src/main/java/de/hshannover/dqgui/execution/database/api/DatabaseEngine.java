package de.hshannover.dqgui.execution.database.api;

import java.util.Objects;
import java.util.Optional;
import de.hshannover.dqgui.execution.database.api.DatabaseTests.DatabaseTestResult;
import de.hshannover.dqgui.execution.database.fetchers.JdbcFetcher;
import de.hshannover.dqgui.execution.database.gui.GuiCapabilities;
import de.hshannover.dqgui.execution.database.gui.GuiConfiguration;

/**
 * All database engines must extend this base class.<br>
 * To have the engine loaded they must be in the package <code>de.hshannover.dqgui.support.database.api</code>.
 * 
 * @author Marc Herschel
 *
 */
public abstract class DatabaseEngine {
    private EngineIdentifier identifier;
    private Integer port = null;
    private RelationalRepositorySupport repositorySupport = null;
    private GuiConfiguration guiConfiguration;
    private EngineLanguage language;
    
    final boolean[] calledInternal = new boolean[5];
    
    /**
     * Runs register driver hook.
     * @throws Exception in case a driver load fails
     */
    protected DatabaseEngine() throws Exception {
        loadDatabaseDriver();
    }
    
    /**
     * Registers a {@link EngineIdentifier} with the engine.<br>
     * Should be called in the subclasses constructor.<br>
     * Can only be called once.<br>
     * Attempting to load two engines with the same identifier will cause issues<br>
     * The identifier will internally be converted to upper-case.<br>
     * So postgres, pOstGres and POSTGRES will all convert to POSTGRES and thus be the same identifier.
     * @param identifier to register for this engine.
     */
    protected final void registerIdentifier(String identifier) {
        if(calledInternal[0])
            throw new RuntimeException("registerIdentifier() has been called already.");
        this.identifier = EngineIdentifier.of(identifier);
        calledInternal[0] = true;
    }
    
    /**
     * Registers a {@linkplain EngineLanguage} with the engine.<br>
     * Should be called in the subclasses constructor.<br>
     * Can only be called once.<br>
     * Language string will be converted to upper-case internally.
     * @param language to register for the engine.
     */
    protected final void registerLanguage(String language) {
        if(calledInternal[1])
            throw new RuntimeException("registerLanguage() has been called already.");
        this.language = EngineLanguage.of(language);
        calledInternal[1] = true;
    }
    
    /**
     * Optionally allows to register a default port.<br>
     * Should be called in the subclasses constructor.<br>
     * Can only be called once.<br>
     * If the database engine does not support networking over TCP or does not have a default port this can be ignored.
     * @param port to register for the engine.
     */
    protected final void registerPort(Integer port) {
        if(calledInternal[2])
            throw new RuntimeException("registerPort() has been called already.");
        this.port = port;
        calledInternal[2] = true;
    }
    
    /**
     * Optionally registers a {@link RelationalRepositorySupport} with the engine.<br>
     * Should be called in the subclasses constructor.<br>
     * Can only be called once.<br>
     * This is required if {@link DatabaseEngine#allowUseForRepository()} returns true.
     * @param repositorySupport to register with the engine.
     */
    protected final void registerRepositorySupport(RelationalRepositorySupport repositorySupport) {
        if(calledInternal[3])
            throw new RuntimeException("registerRepositorySupport() has been called already.");
        if(!allowUseForRepository())
            throw new RuntimeException("Engine does not allow use for repository but an attempt was made to register a RepositorySupport with the engine.");
        Objects.requireNonNull(repositorySupport, "RepositorySupport must not be null.");
        this.repositorySupport = repositorySupport;
        calledInternal[3] = true;
    }
    
    /**
     * Registers GUI capabilities with the engine.<br>
     * Should be called in the subclasses constructor.<br>
     * Can only be called once.<br>
     * @param config GUI configuration
     */
    protected final void registerGuiSupport(GuiConfiguration config) {
        Objects.requireNonNull(config, "GUIConfiguration must not be null");
        if(calledInternal[4])
            throw new RuntimeException("registerGuiSupport() has been called already.");
        if(config.capabilities == GuiCapabilities.JDBC_COMMON && !supportsJdbc())
            throw new RuntimeException("JDBC_COMMON support set but engine does not support JDBC.");
        this.guiConfiguration = config;
        calledInternal[4] = true;
    }
    
    /**
     * @return unique engine identifier
     */
    public final EngineIdentifier uniqueIdentifier() {
        return identifier;
    }
    
    /**
     * @return supported database language
     */
    public final EngineLanguage language() {
        return language;
    }
    
    /**
     * @return an {@link Optional} with the default port
     */
    public final Optional<Integer> defaultPort() {
        return Optional.ofNullable(port);
    }
    
    /**
     * 
     * @return GUI configuration of engine
     */
    public final GuiConfiguration guiConfiguration() {
        return guiConfiguration;
    }
    
    /**
     * @return identifier as string
     */
    @Override
    public final String toString() {
        return identifier.toString();
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DatabaseEngine other = (DatabaseEngine) obj;
        return Objects.equals(identifier, other.identifier);
    }

    /**
     * If you need to load database drivers, here is your chance.<br>
     * Some JDBC drivers need to be loaded with Class.forName() to register with the JDBC driver manager.
     * @throws Exception in case driver loading fails
     */
    protected abstract void loadDatabaseDriver() throws Exception;
    
    /**
     * Returns a fetcher implementation for the database engine.<br>
     * JDBC supported engines can redirect to the {@link JdbcFetcher}.
     * @param connection to create fetcher for.
     * @return fetcher that can fetch for the engine.
     */
    public abstract DatabaseFetcher createFetcher(DatabaseConnection connection);
    
    /**
     * @return how the engine should be displayed in the UI. This will affect alphabetic sorting.
     */
    public abstract String name();
    
    /**
     * Runs a test for the supplied connection.<br>
     * JDBC supported engines can redirect to the 
     * @param connection to test for
     * @return a {@link DatabaseTestResult} informing about the outcome
     */
    public abstract DatabaseTests.DatabaseTestResult test(DatabaseConnection connection);
    
    /**
     * @return true if the engine is relational
     */
    public abstract boolean isRelational();
    
    /**
     * @return true if this engine should be used for the repository
     */
    public abstract boolean allowUseForRepository();
    
    /**
     * @return true if this engine should be used for iqm4hd
     */
    public abstract boolean allowUseForIqm4hd();
    
    /**
     * @return true if JDBC is supported
     */
    public abstract boolean supportsJdbc();
    
    /**
     * Creates a JDBC data source URL that is used to create the JDBC connection.<br>
     * If the engine does not support JDBC this should return null.
     * @param connection to create the data source URL for
     * @return a data source URL ready to use with the JDBC driver manager
     */
    public abstract String createDataSourceUrl(DatabaseConnection connection);
   
    /**
     * If your database supports a database backed repository create the repository object here.
     * @param connection to create repo for
     * @return a repository if supported or null
     */
    protected abstract Repository<?> createRepositoryForConnection(DatabaseConnection connection);
    
    /**
     * Creates a repository for a given connection.
     * @param connection to create repository for
     * @return created repository
     */
    public final Repository<?> createRepository(DatabaseConnection connection) {
        if(!allowUseForRepository())
            throw new IllegalArgumentException("Engine: " + name() + " does not support database backed repositories.");
        Repository<?> r = createRepositoryForConnection(connection);
        return Objects.requireNonNull(r, "Supplied repository must not be null for engine: " + name());
    }
    
    /**
     * @return the repository support instance associated with the engine or null if no repository is supported
     */
    public final Optional<RelationalRepositorySupport> requestRepositorySupport() {
        if(!allowUseForRepository())
            throw new IllegalArgumentException("Engine: " + name() + " does not support database backed repositories.");
        return Optional.of(repositorySupport);
    }
}