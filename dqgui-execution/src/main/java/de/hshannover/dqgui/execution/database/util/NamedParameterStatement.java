package de.hshannover.dqgui.execution.database.util;

import static de.hshannover.dqgui.execution.Rethrow.rethrow;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.tinylog.Logger;

/**
 * This class wraps around a {@link PreparedStatement} and allows the programmer
 * to set parameters by name instead of by index. This eliminates any confusion
 * as to which parameter index represents what. This also means that rearranging
 * the SQL statement or adding a parameter doesn't involve renumbering your
 * indices. Code such as this:
 *
 * 
 * Connection con=getConnection(); String query="select * from my_table where
 * name=? or address=?"; PreparedStatement p=con.prepareStatement(query);
 * p.setString(1, "bob"); p.setString(2, "123 terrace ct"); ResultSet
 * rs=p.executeQuery();
 *
 * 
 * can be replaced with:
 *
 * 
 * Connection con=getConnection(); String query="select * from my_table where
 * name=:name or address=:address"; NamedParameterStatement p=new
 * NamedParameterStatement(con, query); p.setString("name", "bob");
 * p.setString("address", "123 terrace ct"); ResultSet rs=p.executeQuery();
 *
 * @author adam_crume
 * @author myntt (Modified for DQGUI)
 */
public class NamedParameterStatement implements AutoCloseable {
    /** The statement this object is wrapping. */
    private final PreparedStatement statement;
    private final boolean supportsKeyReturn;
    
    /**
     * Maps parameter names to arrays of ints which are the parameter indices.
     */
    private final Map<String, List<Integer>> indexMap;

    /**
     * Creates a NamedParameterStatement. Wraps a call to
     * c.{@link Connection#prepareStatement(java.lang.String) prepareStatement}.
     * 
     * @param connection the database connection
     * @param query      the parameterized query
     */
    public NamedParameterStatement(Connection connection, String query) {
        Logger.debug("QUERY Entered: {}", query.trim());
        supportsKeyReturn = false;
        indexMap = new HashMap<>();
        String parsedQuery = parse(query, indexMap);
        try {
            statement = connection.prepareStatement(parsedQuery);
        } catch (SQLException e) {
            throw rethrow(e);
        }
    }
    
    /**
     * Creates a NamedParameterStatement. Wraps a call to
     * c.{@link Connection#prepareStatement(java.lang.String) prepareStatement}.
     * 
     * @param connection the database connection
     * @param query      the parameterized query
     * @param primaryKeyColumnName if primary key should be provided
     */
    public NamedParameterStatement(Connection connection, String query, String primaryKeyColumnName) {
        Logger.debug("QUERY Entered: {}", query.trim());
        supportsKeyReturn = true;
        indexMap = new HashMap<>();
        String parsedQuery = parse(query, indexMap);
        try {
            statement = connection.prepareStatement(parsedQuery, new String[] { primaryKeyColumnName });
        } catch (SQLException e) {
            throw rethrow(e);
        }
    }

    /**
     * Parses a query with named parameters.  The parameter-index mappings are 
put into the map, and the
     * parsed query is returned.  DO NOT CALL FROM CLIENT CODE.  This 
method is non-private so JUnit code can
     * test it.
     * @param query    query to parse
     * @param paramMap map to hold parameter-index mappings
     * @return the parsed query
     */
    static final String parse(String query, Map<String, List<Integer>> paramMap) {
        // I was originally using regular expressions, but they didn't work well 
        // for ignoring
        // parameter-like strings inside quotes.
        int length=query.length();
        StringBuffer parsedQuery=new StringBuffer(length);
        boolean inSingleQuote=false;
        boolean inDoubleQuote=false;
        int index=1;

        for(int i=0;i<length;i++) {
            char c=query.charAt(i);
            if(inSingleQuote) {
                if(c=='\'') {
                    inSingleQuote=false;
                }
            } else if(inDoubleQuote) {
                if(c=='"') {
                    inDoubleQuote=false;
                }
            } else {
                if(c=='\'') {
                    inSingleQuote=true;
                } else if(c=='"') {
                    inDoubleQuote=true;
                } else if(c==':' && i+1<length &&
                        Character.isJavaIdentifierStart(query.charAt(i+1)) &&
                        query.charAt(i-1) != ':') {
                    int j=i+2;
                    while(j<length && Character.isJavaIdentifierPart(query.charAt(j))) {
                        j++;
                    }
                    String name=query.substring(i+1,j);
                    c='?'; // replace the parameter with a question mark
                    i+=name.length(); // skip past the end if the parameter

                    List<Integer> indexList=paramMap.get(name);
                    if(indexList==null) {
                        indexList=new LinkedList<>();
                        paramMap.put(name, indexList);
                    }
                    indexList.add(index);

                    index++;
                }
            }
            parsedQuery.append(c);
        }

        // replace the lists of Integer objects with arrays of ints
        for(Iterator<Map.Entry<String, List<Integer>>> itr=paramMap.entrySet().iterator(); itr.hasNext();) {
            Map.Entry<String, List<Integer>> entry=itr.next();
            List<Integer> list=entry.getValue();
            int[] indexes=new int[list.size()];
            int i=0;
            for(Iterator<Integer> itr2=list.iterator(); itr2.hasNext();) {
                Integer x=itr2.next();
                indexes[i++]=x.intValue();
            }
            list.clear();
            for(int in : indexes) {
                list.add(in);
            }
            entry.setValue(list);
        }

        return parsedQuery.toString();
    }

    /**
     * Returns the indexes for a parameter.
     * 
     * @param name parameter name
     * @return parameter indexes
     * @throws IllegalArgumentException if the parameter does not exist
     */
    private List<Integer> getIndexes(String name) {
        List<Integer> indexes = indexMap.get(name);
        if (indexes == null) {
            throw new IllegalArgumentException("Parameter not found: " + name);
        }
        return indexes;
    }

    /**
     * Sets a parameter.
     * 
     * @param name  parameter name
     * @param value parameter value
     * @see PreparedStatement#setObject(int, java.lang.Object)
     * @return this for fluent api
     */
    public NamedParameterStatement setObject(String name, Object value) {
        List<Integer> indexes = getIndexes(name);
        try {
            for (int i = 0; i < indexes.size(); i++)
                statement.setObject(indexes.get(i), value);
        } catch (SQLException e) {
            throw rethrow(e);
        }
        return this;
    }

    /**
     * Sets a parameter.
     * 
     * @param name  parameter name
     * @param value parameter value
     * @return this for fluent api
     * @see PreparedStatement#setString(int, java.lang.String)
     */
    public NamedParameterStatement setString(String name, String value) {
        List<Integer> indexes = getIndexes(name);
        try {
            for (int i = 0; i < indexes.size(); i++) {
                statement.setString(indexes.get(i), value);
            }
        } catch (SQLException e) {
            throw rethrow(e);
        }
        return this;
    }

    /**
     * Sets a parameter.
     * 
     * @param name  parameter name
     * @param value parameter value
     * @return this for fluent api
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setInt(int, int)
     */
    public NamedParameterStatement setInt(String name, int value) {
        List<Integer> indexes = getIndexes(name);
        try {
            for (int i = 0; i < indexes.size(); i++)
                statement.setInt(indexes.get(i), value);
        } catch (SQLException e) {
            throw rethrow(e);
        }
        return this;
    }

    /**
     * Sets a parameter.
     * 
     * @param name  parameter name
     * @param value parameter value
     * @return this for fluent api
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setInt(int, int)
     */
    public NamedParameterStatement setLong(String name, long value) {
        List<Integer> indexes = getIndexes(name);
        try {
            for (int i = 0; i < indexes.size(); i++)
                statement.setLong(indexes.get(i), value);
        } catch (SQLException e) {
            throw rethrow(e);
        }
        return this;
    }

    /**
     * Sets a parameter.
     * 
     * @param name  parameter name
     * @param value parameter value
     * @return this for fluent api
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setTimestamp(int, java.sql.Timestamp)
     */
    public NamedParameterStatement setTimestamp(String name, Timestamp value) {
        List<Integer> indexes = getIndexes(name);
        try {
            for (int i = 0; i < indexes.size(); i++)
                statement.setTimestamp(indexes.get(i), value);
        } catch (SQLException e) {
            throw rethrow(e);
        }
        return this;
    }
    
    /**
     * Sets a parameter if it exists.
     * 
     * @param name  parameter name
     * @param value parameter value
     * @return this for fluent api
     */
    public NamedParameterStatement setOptionally(String name, Object value) {
        if(!indexMap.containsKey(name)) return this;
        if(value instanceof Integer)
            return setInt(name, (Integer) value);
        if(value instanceof Long)
            return setLong(name, (Long) value);
        if(value instanceof String)
            return setString(name, (String) value);
        if(value instanceof Timestamp)
            return setTimestamp(name, (Timestamp) value);
        return setObject(name, value);
    }

    /**
     * Returns the underlying statement.
     * 
     * @return the statement
     */
    public PreparedStatement getStatement() {
        return statement;
    }

    /**
     * Executes the statement.
     * 
     * @return true if the first result is a {@link ResultSet}
     * @see PreparedStatement#execute()
     */
    public boolean execute() {
        try  {
            return statement.execute();
        } catch (SQLException e) {
            throw rethrow(e);
        }
    }

    /**
     * Executes the statement, which must be a query.
     * 
     * @return the query results
     * @see PreparedStatement#executeQuery()
     */
    public ResultSet executeQuery() {
        try {
            return statement.executeQuery();
        } catch (SQLException e) {
            throw rethrow(e);
        }
    }

    /**
     * Executes the statement, which must be an SQL INSERT, UPDATE or DELETE
     * statement; or an SQL statement that returns nothing, such as a DDL statement.
     * 
     * @return number of rows affected
     * @see PreparedStatement#executeUpdate()
     */
    public int executeUpdate() {
        try {
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw rethrow(e);
        }
    }
    
    /**
     * Executes the statement, which must be an SQL INSERT, UPDATE or DELETE
     * statement; or an SQL statement that returns nothing, such as a DDL statement.
     * 
     * Additionally returns a ResultSet containing the generated keys.
     * 
     * @return generated keys
     * @throws IllegalArgumentException if called without setting a primary key column within the constructor first
     * @see PreparedStatement#executeUpdate()
     */
    public ResultSet executeUpdateAndGetGeneratedKeys() {
        if(!supportsKeyReturn)
            throw new IllegalArgumentException("must use constructor with primary key column name for this to work");
        try {
            statement.executeUpdate();
            return statement.getGeneratedKeys();
        } catch(SQLException e) {
            throw rethrow(e);
        }
    }

    /**
     * Closes the statement.
     * 
     * @see Statement#close()
     */
    public void close() {
        try {
            statement.close();
        } catch (SQLException e) {
            throw rethrow(e);
        }
    }

    /**
     * Adds the current set of parameters as a batch entry.
     * @return this for fluent api
     */
    public NamedParameterStatement addBatch() {
        try {
            statement.addBatch();
            return this;
        } catch (SQLException e) {
            throw rethrow(e);
        }
    }

    /**
     * Executes all of the batched statements.
     * 
     * See {@link Statement#executeBatch()} for details.
     * 
     * @return update counts for each statement
     */
    public int[] executeBatch() {
        try {
            return statement.executeBatch();
        } catch (SQLException e) {
            throw rethrow(e);
        }
    }
}
