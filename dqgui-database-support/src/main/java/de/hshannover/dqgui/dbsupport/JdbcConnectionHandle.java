package de.hshannover.dqgui.dbsupport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.tinylog.Logger;
import de.hshannover.dqgui.execution.database.api.ConnectionHandle;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;
import de.hshannover.dqgui.framework.ErrorUtility;

/**
 * JDBC backed connection handle
 * 
 * @author Marc Herschel
 *
 */

public class JdbcConnectionHandle implements AutoCloseable, ConnectionHandle<Connection> {
    private boolean closed;
    private Connection con;
    private final DatabaseConnection conData;
    
    public JdbcConnectionHandle(DatabaseConnection conData) {
        this.conData = conData;
        createConnection();
    }
    
    private void createConnection() {
        try {
            con = DriverManager.getConnection(conData.getEngine().createDataSourceUrl(conData));
            con.setAutoCommit(false);
        } catch (SQLException e) {
            try {
                if(con != null && !con.isClosed())
                    con.close();
            } catch (SQLException e1) {}
            throw ErrorUtility.rethrow(e);
        }
    }
    
    @Override
    public Connection get() {
        if(closed)
            throw new IllegalStateException("handle has been closed");
        try {
            if(!con.isValid(1))
                createConnection();
        } catch (SQLException e) {
            throw ErrorUtility.rethrow(e);
        }
        return con;
    }

    @Override
    public void close() throws Exception {
        closed = true;
        if(!con.isClosed())
            con.close();
    }

    @Override
    public void commit() {
        try {
            con.commit();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }

    @Override
    public void rollback() {
        try {
            con.rollback();
        } catch (SQLException e) {
            Logger.error(e);
        }
    }
    
}
