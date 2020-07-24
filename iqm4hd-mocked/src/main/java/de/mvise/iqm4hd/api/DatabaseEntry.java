package de.mvise.iqm4hd.api;

import java.util.Set;

public interface DatabaseEntry {
	Object getValue(String key);
	int getCount();
	Set<String> getKeys();
}