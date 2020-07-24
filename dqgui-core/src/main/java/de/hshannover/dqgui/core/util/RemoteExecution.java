package de.hshannover.dqgui.core.util;

import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.dbsupport.api.DatabaseEnvironment;
import de.hshannover.dqgui.execution.model.Project;
import de.hshannover.dqgui.execution.model.remote.RemoteJob;
import de.hshannover.dqgui.framework.ErrorUtility;
import de.mvise.iqm4hd.api.RuleService;
import de.mvise.iqm4hd.core.exception.types.Iqm4hdException;

public class RemoteExecution {

    public enum ExecutionStrategy {
        LOCAL, REMOTE;
    }

    public static RemoteJob createJob(String actionName, boolean optimize, RuleService service, DatabaseEnvironment env, Project project) {
        try {
            String actionSource = service.getActionStatementByName(actionName);
            ParserTools.extractActionValues(actionSource);
            return new RemoteJob(project, ParserTools.extractDependencies(actionName, actionSource, service), env.getConnections(), actionName, Config.USER_IDENTIFIER,
                    env.getIdentifier(), optimize, ParserTools.extractIdentifier(actionSource, service), ParserTools.extractActionValues(actionSource));
        } catch (Iqm4hdException e) {
            throw ErrorUtility.rethrow(e);
        }
    }
}