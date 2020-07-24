package de.hshannover.dqgui.remote;

import de.hshannover.dqgui.execution.model.DSLComponentType;
import de.hshannover.dqgui.execution.model.remote.RemoteJob;
import de.mvise.iqm4hd.api.RuleService;
import de.mvise.iqm4hd.core.exception.types.Iqm4hdException;

public class RemoteRuleService implements RuleService {
    private final RemoteJob job;

    public RemoteRuleService(RemoteJob job) {
        this.job = job;
    }
    
    private String load(String name, DSLComponentType type) {
        String s = job.getSources().get(type).get(name);
        if(s == null)
            throw new IllegalArgumentException("could not find " + type + ": " + name);
        return s;
    }
    
    @Override
    public String getActionStatementByName(String arg0) throws Iqm4hdException {
        return load(arg0, DSLComponentType.ACTION);
    }

    @Override
    public String getCheckStatementByName(String arg0) throws Iqm4hdException {
        return load(arg0, DSLComponentType.CHECK);
    }

    @Override
    public String getSourceStatementByName(String arg0) throws Iqm4hdException {
        return load(arg0, DSLComponentType.SOURCE);
    }

}
