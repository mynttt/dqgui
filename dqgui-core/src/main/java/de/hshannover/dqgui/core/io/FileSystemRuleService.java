package de.hshannover.dqgui.core.io;

import de.hshannover.dqgui.execution.DSLServiceException;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import de.hshannover.dqgui.framework.ErrorUtility;
import de.mvise.iqm4hd.api.RuleService;
import de.mvise.iqm4hd.core.exception.types.Iqm4hdException;

final class FileSystemRuleService implements RuleService {
    private final DSLFileService service;

    FileSystemRuleService(DSLFileService service) {
        this.service = service;
    }

    @Override
    public String getActionStatementByName(String identifier) throws Iqm4hdException {
        try {
            return service.read(DSLComponent.of(identifier, DSLComponentType.ACTION, false));
        } catch (DSLServiceException e) {
            throw ErrorUtility.rethrow(e);
        }
    }

    @Override
    public String getCheckStatementByName(String identifier) throws Iqm4hdException {
        try {
            if(service.isGlobalCheck(identifier))
                return service.read(DSLComponent.of(identifier, DSLComponentType.CHECK, true));
            return service.read(DSLComponent.of(identifier, DSLComponentType.CHECK, false));
        } catch (DSLServiceException e) {
            throw ErrorUtility.rethrow(e);
        }
    }

    @Override
    public String getSourceStatementByName(String identifier) throws Iqm4hdException {
        try {
            return service.read(DSLComponent.of(identifier, DSLComponentType.SOURCE, false));
        } catch (DSLServiceException e) {
            throw ErrorUtility.rethrow(e);
        }
    }

}