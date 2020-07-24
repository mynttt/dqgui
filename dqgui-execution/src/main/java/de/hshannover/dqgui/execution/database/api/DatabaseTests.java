package de.hshannover.dqgui.execution.database.api;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Some pre-defined database tests and the test result API.
 * @author Marc Herschel
 *
 */
public class DatabaseTests {
    
    /**
     * Database tests must return this.
     *
     */
    public static final class DatabaseTestResult {
        private static final String SUCCESS = "Test passed successfully!";

        private final boolean success;
        private final String message, excpetionName;

        /**
         * Default Constructor: Test passed.
         */
        public DatabaseTestResult() {
            this.success = true;
            this.message = SUCCESS;
            this.excpetionName = "";
        }

        /**
         * String Constructor: Test failed + message.
         * @param error message.
         */
        public DatabaseTestResult(String error) {
            this.success = false;
            this.message = error;
            this.excpetionName = "No exception specified";
        }

        /**
         * Exception Constructor: Test failed + detailed message from exception.
         * @param exception to evaluate.
         */
        public DatabaseTestResult(Exception exception) {
            this.success = false;
            this.excpetionName = exception.getClass().getSimpleName();
            this.message = exception.getMessage();
        }

        /**
         * @return if test succeeded
         */
        public boolean isSuccess() {
            return success;
        }

        /**
         * @return tests message
         */
        public String getMessage() {
            return message;
        }

        /**
         * @return exception of the test
         */
        public String getExceptionName() {
            return excpetionName;
        }
    }
    
    /**
     * All JDBC supporting databases can redirect to this test.<br>
     * Passing in a non-JDBC supporting connection will yield a test fail.
     * @param connection to test for.
     * @return test result.
     */
    public static DatabaseTestResult ofJDBC(DatabaseConnection connection) {
        if(!connection.supportsJDBC())
            return new DatabaseTestResult("Database Connection with Engine " + connection.getEngine() + " does not support JDBC tests.");
        try(Connection conn = DriverManager.getConnection(connection.getDataSourceURL())) {
            return new DatabaseTestResult();
        } catch (Exception e) {
            return new DatabaseTestResult(e);
        }
    }
    
    /**
     * If you don't want to support database tests just redirect to this.
     * @param connection to ignore testing for.
     * @return a negative test result claiming that no tests are implemented for the connection.
     */
    public static DatabaseTestResult reportUnsupported(DatabaseConnection connection) {
        return new DatabaseTestResult(connection.getEngine() + " is not supported by DatabaseTest.");
    }
}
