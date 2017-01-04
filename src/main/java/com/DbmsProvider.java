package com;

import java.util.List;
import java.util.Properties;

/**
 * An external data source provider for a specific database. The implementation
 * is expected to privide information describing the tables and views regarding
 * a particular database connection.
 * 
 * @author RTerrell
 * 
 */
public interface DbmsProvider {
    /**
     * Extracts all the names of the tables and views belonging to the current
     * connection. The results are returned as a List of objects which can be
     * used to obtain the information needed.
     * 
     * @param criteria
     *            Any special criteria that can be applied to filter the list of
     *            table and view names that are to be returned.
     * @return List of arbitrary objects.
     * @throws Exception
     */
    List getObjectNames(Object criteria) throws Exception;

    /**
     * Extracts all the column names of the tables and views belonging to the
     * current connection. The results are returned as a List of objects which
     * can be used to obtain the information needed.
     * 
     * @param objId
     *            The handle of the table or view to obtain the list column
     *            names.
     * @return List of arbitrary objects.
     * @throws Exception
     */
    List getObjectColumnNames(int objId) throws Exception;

    /**
     * Extracts all the attributes of the tables and views belonging to the
     * current connection. The results are returned as a List of objects which
     * can be used to obtain the information needed.
     * 
     * @param objId
     *            The handle of the table or view to obtain the list column
     *            names.
     * @return List of arbitrary objects.
     * @throws Exception
     */
    List getObjectAttributes(int objId) throws Exception;

    /**
     * Obtains the database type description name for the column represented as
     * <i>colTypeId</i>.
     * 
     * @param colTypeId
     *            The id of the column type to query.
     * @return String
     * @throws Exception
     */
    String getDbColumnTypeName(int colTypeId) throws Exception;

    /**
     * Obtains the java type description name for the column represented as
     * <i>colTypeId</i>.
     * 
     * @param colTypeId
     *            The id of the column type to query.
     * @return String
     * @throws Exception
     */
    String getClassColumnTypeName(int colTypeId) throws Exception;

    /**
     * Establishes a database connection using the <i>props</i>.
     * 
     * @param props
     *            The properties that will be applied to the database connection
     *            URL in order to connect to the database.
     * 
     * @throws Exception
     */
    void connect(Properties props) throws Exception;

    /**
     * Closes the connection;
     * 
     * @throws Exception
     */
    void close() throws Exception;

}
