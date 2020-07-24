package de.hshannover.dqgui.execution.database.api;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import de.hshannover.dqgui.execution.DSLService;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import de.hshannover.dqgui.execution.model.Project;
import javafx.util.Pair;

/**
 * Base type of all database backed repositories.
 * @author Marc Herschel
 *
 * @param <T> type of underlying connection
 */

public interface Repository<T> extends AutoCloseable {
    
    /**
     *  Reports if the repository fulfills the required schema.
     */
    final class ValidationReport {
        private static final ValidationReport SUCCESS = new ValidationReport(true, "Validation succeeded.");
        
        public final boolean success;
        public final String message;
        
        private ValidationReport(boolean success, String message) {
            Objects.requireNonNull(message, "message must not be null");
            this.success = success;
            this.message = message;
        }
        
        /**
         * @return a successful report
         */
        public static ValidationReport success() { return SUCCESS; }

        /**
         * @param message to report
         * @return a failed report with a message attached
         */
        public static ValidationReport error(String message) { return new ValidationReport(false, message); }
    }
    
    DatabaseConnection getBackingConnection();
    
    /**
     * Checks if a table with the name already exists.
     * @param tableName to check for
     * @return true if the table exists
     * @throws Exception on failure
     */
    boolean tableExists(String tableName) throws Exception;
    
    /**
     * Creates the repository in the database.
     * If the repository already exists in the database it should validate it and report the result.
     * @return a report if the repository has been created successfully.
     * @throws Exception on failure
     */
    ValidationReport create() throws Exception;
    
    /**
     * Validates the supplied tables if the schema is correctly implemented.
     * @return a {@link ValidationReport}
     */
    ValidationReport validate();
    
    /**
     * It is advised to use the same handle and connection everywhere to ensure the close calls in DQGUI reach every resource.
     * @param projectIdentifier of requested project
     * @return DLSService implementation for repository.
     */
    DSLService createDslService(String projectIdentifier);
    
    /**
     * Writes a result
     * @param hash of result
     * @param iqm4hdReport serialized as json
     * @param dqguiReport serialized as json
     * @param iqm4hdFeedback serialized as json
     * @param projectIdentifier of project
     * @throws Exception on failure
     */
    void writeResult(String hash, String iqm4hdReport, String dqguiReport, String iqm4hdFeedback, String projectIdentifier) throws Exception;
    
    /**
     * Removes a result
     * @param hash to remove
     * @param projectIdentifier of project
     * @throws Exception on failure
     */
    void removeResult(String hash, String projectIdentifier) throws Exception;
    
    /**
     * Fetches a result.<br>
     * Index 0: iqm4hd report<br>
     * Index 1: dqgui metadata<br>
     * @param hash to fetch for
     * @param projectIdentifier of project
     * @throws Exception on failure
     * @return string array with index values as descibed above
     */
    String[] fetchResult(String hash, String projectIdentifier) throws Exception;
    
    /**
     * Lists all results by hash
     * @param projectIdentifier for project
     * @return list all existing result hashes
     * @throws Exception on failure
     */
    Set<String> listResults(String projectIdentifier) throws Exception;
    
    /**
     * Fetch components for global search
     * @param types to filter for
     * @param projectIdentifier identifier of project
     * @return map of all components and their sources
     * @throws Exception on failure
     */
    Pair<List<DSLComponent>, List<String>> fetchForSearch(List<DSLComponentType> types, String projectIdentifier) throws Exception;

    /**
     * Update the database environments of the selected project
     * @param environments to update
     * @param projectIdentifier of project
     * @throws Exception on failure
     */
    void updateDatabaseEnvironments(String environments, String projectIdentifier) throws Exception;

    /**
     * Fetches database environments of selected project
     * @param projectIdentifier of project
     * @return json of serialized database environments
     * @throws Exception on failure
     */
    String fetchDatabaseEnvironments(String projectIdentifier) throws Exception;
    
    /**
     * List all projects within the repository excluding the global dummy project
     * @return set of found projects
     * @throws Exception on failure
     */
    Set<Project> listProjects() throws Exception;
    
    /**
     * Create a new project with the initial name and database environments json string
     * @param projectName of project
     * @param guid of project
     * @param databaseEnvironments serialized initial database environments
     * @return the identifier of the project
     * @throws Exception on failure
     */
    String createProject(String projectName, String guid, String databaseEnvironments) throws Exception;
    
    /**
     * Delete the project, all repo entries and results from the database
     * @param projectIdentifier to delete
     * @throws Exception on failure
     */
    void deleteProject(String projectIdentifier) throws Exception;
    
    /**
     * Check if a project exists
     * @param projectIdentifier to check for
     * @return true if the project exists
     * @throws Exception on failure
     */
    boolean projectExists(String projectIdentifier) throws Exception;
    
    /**
     * Read feedback as serialized json
     * @param projectIdentifier of the feedback
     * @param hash of the feedback
     * @return feedback json
     * @throws Exception on failure
     */
    String readFeedback(String projectIdentifier, String hash) throws Exception;
    
    /**
     * Write feedback as serialized json
     * @param projectIdentifier of the feedback
     * @param hash of the feedback
     * @param feedback to write
     * @throws Exception on failure
     */
    void writeFeedback(String projectIdentifier, String hash, String feedback) throws Exception;
    
    /**
     * Promotes a given component to global status
     * @param projectIdentifier of project
     * @param component to promote
     * @throws Exception on failure
     */
    void promoteToGlobal(String projectIdentifier, DSLComponent component) throws Exception;
    
    /**
     * Demotes a given component to local status
     * @param projectIdentifier of project
     * @param component to demote
     * @throws Exception on failure
     */
    void demoteToLocal(String projectIdentifier, DSLComponent component) throws Exception;
}