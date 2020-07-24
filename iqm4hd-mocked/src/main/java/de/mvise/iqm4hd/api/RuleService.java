package de.mvise.iqm4hd.api;

import de.mvise.iqm4hd.core.exception.types.Iqm4hdException;

public interface RuleService {
    String getActionStatementByName(String action) throws Iqm4hdException;
    String getCheckStatementByName(String check) throws Iqm4hdException;
    String getSourceStatementByName(String source) throws Iqm4hdException;
}
