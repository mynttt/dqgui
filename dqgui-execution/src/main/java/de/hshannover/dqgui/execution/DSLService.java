package de.hshannover.dqgui.execution;

import java.util.List;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import de.mvise.iqm4hd.api.RuleService;

/**
 * DSLService is a CRUD (Create, Read, Update, Delete) interface for the DQGUI application.<br>
 * It can be implemented for various repository data sources.<p>
 *
 * The interface should fail hard and quick. Thus all operations should rethrow checked exceptions as {@link DSLServiceException}
 * so someone higher up the chain can deal with them.<p>
 *
 * It also features methods to move a component and discover all existing valid components within the repository and can detect if it is malformed.
 *
 * @author Marc Herschel
 */
public interface DSLService {
    
    /**
     * Reports the status of the set repository (does it exist, is it malformed?)
     */
    public class RepositoryStatus {
        private boolean isValid;
        private String message;

        /**
         * Default constructor : valid repository
         */
        public RepositoryStatus() { this.isValid = true; this.message = "Repository is valid."; }

        /**
         * Invalid repository
         * @param error to display the user.
         */
        public RepositoryStatus(String error) { this.isValid = false; this.message = error; }

        /**
         * @return true if the repository is validated.
         */
        public boolean isValid() { return isValid; }

        /**
         * @return validation message.
         */
        public String message() { return message; }
    }

    /**
     * Check if a component exists (factoring the global / local status).
     * @param where it should exists.
     * @return true if it actually exists.
     */
    boolean exists(DSLComponent where);
    
    /**
     * Check if a component identifier exists in the joined global / local component space.
     * @param where it should exists
     * @return true if it actually exists.
     */
    boolean existsInNamespace(DSLComponent where);
    
    /**
     * Check if a check exists as global check
     * @param checkIdentifier of the global check
     * @return true only if a global check with this identifier exists
     */
    boolean isGlobalCheck(String checkIdentifier);

    /**
     * Create a new component.
     * @param where to create it.
     * @throws DSLServiceException if creation fails.
     */
    void create(DSLComponent where) throws DSLServiceException;

    /**
     * Read a components content.
     * @param where to read from it.
     * @return result as String.
     * @throws DSLServiceException if reading fails.
     */
    String read(DSLComponent where) throws DSLServiceException;

    /**
     * Update a component.
     * @param where to update the component.
     * @param toUpdate what to update it with.
     * @throws DSLServiceException if updating fails.
     */
    void update(DSLComponent where, String toUpdate) throws DSLServiceException;
    
    /**
     * Update only extra data of component
     * @param where to update the components extra data
     * @throws DSLServiceException if updating fails.
     */
    void updateExtraDataOnly(DSLComponent where) throws DSLServiceException;

    /**
     * Delete a component.
     * @param where to delete the component.
     * @throws DSLServiceException if removal fails.
     */
    void delete(DSLComponent where) throws DSLServiceException;

    /**
     * Move a component.
     * @param from which component to move.
     * @param to where it should be moved to.
     * @throws DSLServiceException if something goes wrong or the component already exists in the new location.
     */
    void move(DSLComponent from, DSLComponent to) throws DSLServiceException;

    /**
     * Discover all components within the repository.
     * @return A list of all indexed components within the entire repository.
     * @throws DSLServiceException if anything goes wrong here.
     */
    List<DSLComponent> discover() throws DSLServiceException;

    /**
     * Create a fitting RuleService for the implementation.
     * @return fitting RuleService.
     */
    RuleService createRuleService();

    /**
     * Check if the repository exists, is malformed or otherwise configured wrongly.
     * @return a {@link RepositoryStatus} object containing the results of the test.
     */
    RepositoryStatus validateRepository();
    
    /**
     * Create a ComponentSplitter for the global repository search
     * @param forTypes which types should be requested by the splitter
     * @return a splitter ready to be used by the threaded search
     * @throws DSLServiceException if anything goes wrong
     */
    ComponentSplitter createComponentSplitter(List<DSLComponentType> forTypes) throws DSLServiceException;
}
