package de.mvise.iqm4hd.client;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import de.mvise.iqm4hd.api.DatabaseEntry;
import de.mvise.iqm4hd.api.DatabaseEntryIterator;

public class ResultSetIterator extends DatabaseEntryIterator {
	private ResultSet rs;
	private int columnCount;
	private ResultSetMetaData rsmd;
	

	public ResultSetIterator(ResultSet rs) {
		try {
			this.rs = rs;
			rsmd = rs.getMetaData();
			columnCount = rsmd.getColumnCount();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public DatabaseEntry next() {
		try {
			if (!rs.next())
				return null;
			
			DatabaseEntryImpl entry = new DatabaseEntryImpl(columnCount);
			
			for (int col = 1; col <= columnCount; col++) {
				entry.add(rsmd.getColumnName(col), rs.getObject(col));
			}
			
			return entry;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
