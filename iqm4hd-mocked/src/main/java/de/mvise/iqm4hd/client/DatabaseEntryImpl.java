package de.mvise.iqm4hd.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.mvise.iqm4hd.api.DatabaseEntry;

public class DatabaseEntryImpl implements DatabaseEntry {
	Map<String, Object> values;
	
	public DatabaseEntryImpl(int columns) {
		values = new HashMap<>(columns);
	}
	
	public void add(String key, Object value) {
		values.put(key.toUpperCase(), value);
	}

	@Override
	public Set<String> getKeys() {
		return values.keySet();
	}

	@Override
	public int getCount() {
		return values.size();
	}

	@Override
	public Object getValue(String key) {
		return values.get(key);
	}
}
