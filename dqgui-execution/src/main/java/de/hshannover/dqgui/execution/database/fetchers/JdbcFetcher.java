package de.hshannover.dqgui.execution.database.fetchers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import de.hshannover.dqgui.execution.Rethrow;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;
import de.hshannover.dqgui.execution.database.api.DatabaseFetcher;
import de.mvise.iqm4hd.api.DatabaseEntryIterator;
import de.mvise.iqm4hd.client.ResultSetIterator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Fetcher for JDBC supported engines.
 *
 * @author Marc Herschel
 *
 */
public final class JdbcFetcher extends DatabaseFetcher {
    private Connection connection;

    public JdbcFetcher(DatabaseConnection connection) {
        super(connection);
    }

    @Override
    public void initiate() {
        try {
            connection = DriverManager.getConnection(connectionData.getDataSourceURL());
        } catch (SQLException e) {
            throw Rethrow.rethrow(e);
        }
    }

    /*
     * Statement is not closed here to realize lazy loading.
     * Statement will definitely closed on the DatabaseServices close() operation.
     */
    @Override
    @SuppressFBWarnings({"ODR_OPEN_DATABASE_RESOURCE", "OBL_UNSATISFIED_OBLIGATION"})
    public DatabaseEntryIterator fetch(String query) {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            return new ResultSetIterator(rs);
        } catch(SQLException e) {
            throw Rethrow.rethrow(e);
        }
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
