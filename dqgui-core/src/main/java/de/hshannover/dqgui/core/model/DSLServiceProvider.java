package de.hshannover.dqgui.core.model;

import java.util.Objects;
import de.hshannover.dqgui.execution.DSLService;
import de.hshannover.dqgui.execution.DSLService.RepositoryStatus;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;

public class DSLServiceProvider {
    private DSLService service;
    private final ReadOnlyBooleanWrapper validProperty = new ReadOnlyBooleanWrapper(false);

    public ReadOnlyBooleanProperty validRepositoryProperty() {
        return validProperty.getReadOnlyProperty();
    }

    public DSLService getService() {
        Objects.requireNonNull(service);
        return service;
    }
    
    public RepositoryStatus verify() {
        Objects.requireNonNull(service, "service is set to null");
        RepositoryStatus result = service.validateRepository();
        validProperty.set(result.isValid());
        return result;
    }

    /**
     * By replacing a service you're closing the previously installed service.<br>
     * In case the service you try to replace it with is invalid both services will be closed.
     * @param service to replace the old service with
     * @return a {@link RepositoryStatus} informing you if the new service is valid.
     */
    RepositoryStatus replaceService(DSLService service) {
        if(service == null)
            return new RepositoryStatus("No repository defined. Please configure a repository in the DQGUI properties.");
        RepositoryStatus r = service.validateRepository();
        this.service = service;
        validProperty.set(r.isValid());
        return r;
    }

    void unloadService() {
        this.service = null;
    }
}