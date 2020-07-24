package de.mvise.iqm4hd.api;

import de.mvise.iqm4hd.core.exception.types.Iqm4hdException;

public abstract class DatabaseEntryIterator {
	public abstract DatabaseEntry next() throws Iqm4hdException;
}