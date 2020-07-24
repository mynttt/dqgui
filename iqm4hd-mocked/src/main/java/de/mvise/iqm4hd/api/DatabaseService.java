package de.mvise.iqm4hd.api;

import de.mvise.iqm4hd.core.exception.types.Iqm4hdException;

public interface DatabaseService {
    DatabaseEntryIterator getEntryListOfSource(String identifier, String query) throws Iqm4hdException;
    String getQueryLanguage(String identifier);
}