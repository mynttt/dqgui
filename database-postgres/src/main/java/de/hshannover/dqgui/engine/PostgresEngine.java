package de.hshannover.dqgui.engine;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import com.google.common.io.CharStreams;
import de.hshannover.dqgui.dbsupport.JdbcRepository;
import de.hshannover.dqgui.engine.postgres.PostgresRepositorySupport;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;
import de.hshannover.dqgui.execution.database.api.DatabaseEngine;
import de.hshannover.dqgui.execution.database.api.DatabaseFetcher;
import de.hshannover.dqgui.execution.database.api.DatabaseSocket.HostFormat;
import de.hshannover.dqgui.execution.database.api.DatabaseTests;
import de.hshannover.dqgui.execution.database.api.DatabaseTests.DatabaseTestResult;
import de.hshannover.dqgui.execution.database.api.Repository;
import de.hshannover.dqgui.execution.database.fetchers.JdbcFetcher;
import de.hshannover.dqgui.execution.database.gui.GuiConfiguration;
import de.hshannover.dqgui.execution.database.gui.Icon;

public class PostgresEngine extends DatabaseEngine {
    private final static String RESOURCES = "/engine/postgres/";

    protected PostgresEngine() throws Exception {
        super();
        registerIdentifier("postgres");
        registerPort(5432);
        registerLanguage("sql");
        
        String schema = CharStreams.toString(new InputStreamReader(PostgresEngine.class.getResourceAsStream(RESOURCES + "postgres_schema.yml"), StandardCharsets.UTF_8));
        String queries = CharStreams.toString(new InputStreamReader(PostgresEngine.class.getResourceAsStream(RESOURCES + "postgres_queries.yml"),StandardCharsets.UTF_8));
                
        registerRepositorySupport(new PostgresRepositorySupport(schema, queries));
        
        registerGuiSupport(
                GuiConfiguration.ofJDBCCommon(
                        Icon.of(RESOURCES + "POSTGRES.png"), 
                        Icon.of(RESOURCES + "postgres_menu.png")
                        )
                );
    }

    @Override
    protected void loadDatabaseDriver() throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
    }

    @Override
    public DatabaseFetcher createFetcher(DatabaseConnection connection) {
        return new JdbcFetcher(connection);
    }

    @Override
    public String name() {
        return "Postgres";
    }

    @Override
    public boolean isRelational() {
        return true;
    }

    @Override
    public boolean allowUseForRepository() {
        return true;
    }

    @Override
    public boolean allowUseForIqm4hd() {
        return true;
    }

    @Override
    public boolean supportsJdbc() {
        return true;
    }

    @Override
    public String createDataSourceUrl(DatabaseConnection connection) {
        StringBuilder sb = new StringBuilder(200);
        String host = connection.getSocket().getFormat() == HostFormat.IPv6 ? String.format("[%s]", connection.getSocket().getHost()) : connection.getSocket().getHost();
        sb.append(String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s",
                host,
                connection.getSocket().getPort(),
                connection.getCredential().getDatabase(),
                connection.getCredential().getUsername(),
                connection.getCredential().getPassword()
        ));
        connection.getCustomParameters().forEach((k, v) -> sb.append(String.format("&%s=%s", k, v)));
        return sb.toString();
    }

    @Override
    public DatabaseTestResult test(DatabaseConnection connection) {
        return DatabaseTests.ofJDBC(connection);
    }

    @Override
    protected Repository<?> createRepositoryForConnection(DatabaseConnection connection) {
        return new JdbcRepository(connection);
    }
}
