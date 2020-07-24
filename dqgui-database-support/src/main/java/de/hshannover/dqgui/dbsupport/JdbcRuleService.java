package de.hshannover.dqgui.dbsupport;

import de.hshannover.dqgui.execution.DSLServiceException;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import de.hshannover.dqgui.framework.ErrorUtility;
import de.mvise.iqm4hd.api.RuleService;
import de.mvise.iqm4hd.core.exception.types.Iqm4hdException;

/**
 * JDBC backed rule service
 * 
 * @author Marc Herschel
 *
 */

public class JdbcRuleService implements RuleService {
    private final JdbcDslService service;

    public JdbcRuleService(JdbcDslService jdbcDslService) {
        this.service = jdbcDslService;
    }

    @Override
    public String getActionStatementByName(String arg0) throws Iqm4hdException {
        try {
            return service.read(DSLComponent.of(arg0, DSLComponentType.ACTION, false));
        } catch (DSLServiceException e) {
            throw ErrorUtility.rethrow(e);
        }
    }

    @Override
    public String getCheckStatementByName(String arg0) throws Iqm4hdException {
        try {
            if(service.isGlobalCheck(arg0))
                return service.read(DSLComponent.of(arg0, DSLComponentType.CHECK, true));
            return service.read(DSLComponent.of(arg0, DSLComponentType.CHECK, false));
        } catch (DSLServiceException e) {
            throw ErrorUtility.rethrow(e);
        }
    }

    @Override
    public String getSourceStatementByName(String arg0) throws Iqm4hdException {
        try {
            return service.read(DSLComponent.of(arg0, DSLComponentType.SOURCE, false));
        } catch (DSLServiceException e) {
            throw ErrorUtility.rethrow(e);
        }
    }

}
