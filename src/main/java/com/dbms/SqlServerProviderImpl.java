package com.dbms;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.DbmsProvider;
import com.OrmColumnData;
import com.OrmObjectData;

/**
 * This is the Microsoft SQL Servere implementation of {@link com.DbmsProvider
 * DbmsProvider} interface.
 * 
 * @author RTerrell
 * 
 */
class SqlServerProviderImpl extends AbstractDbmsProvider implements
        DbmsProvider {

    /**
     * The default constructor
     */
    public SqlServerProviderImpl() {
        super();
    }

    /**
     * Obtains the java.sql.Types value for the column represented as
     * <i>colTypeId</i>.
     * 
     * @param colTypeId
     *            The id of the column type to query.
     * @return String
     * @throws Exception
     */
    public String getDbColumnTypeName(int colTypeId) throws Exception {
        String javaType;

        switch (colTypeId) {
            case 5:
            case 6:
            case 7:
            case 13:
            case 16:
                javaType = "java.sql.Types.INTEGER";
                break;

            case 8:
            case 10:
            case 11:
            case 14:
            case 17:
            case 21:
            case 23:
            case 24:
            case 25:
            case 26:
                javaType = "java.sql.Types.DOUBLE";
                break;

            case 1:
            case 2:
            case 3:
            case 4:
            case 19:
            case 20:
                javaType = "java.sql.Types.VARCHAR";
                break;

            case 12:
            case 15:
                javaType = "java.sql.Types.DATE";
                break;

            default:
                javaType = "";
                break;
        } // end switch
        return javaType;
    }

    /**
     * Uses the Microsoft SQL Server system tables/views to extract column
     * attributes for the table/view identified as <i>objId</i>
     * 
     * @param objId
     *            The handle of the table or view to obtain the list column
     *            names.
     * @return List of {@link com.OrmColumnData OrmColumnData} objects.
     * @throws Exception
     *             For general SQL errors.
     */
    public List getObjectAttributes(int objId) throws Exception {
        String sql = "select * from syscolumns where id = " + objId;
        ResultSet rsCols = null;
        ResultSet rsKey = null;
        ResultSet rsType = null;
        CallableStatement call;
        String colName = null;
        String pKey = null;
        String nullable = null;
        String dataTypeName = null;
        String javaType = null;
        String sqlType = null;
        int dataType = 0;
        String proc;
        String tableName;
        List list = new ArrayList();

        List tableList = this.getObjectNames(" id = " + objId);
        if (tableList == null || tableList.size() <= 0) {
            throw new Exception("Table could not be found using object Id: "
                    + objId);
        }
        OrmObjectData objData = (OrmObjectData) tableList.get(0);
        tableName = objData.getObjectName();

        try {
            // TODO: Get table name using objId parameter
            proc = "sp_pkeys " + tableName;
            call = this.con.prepareCall(proc);
            rsKey = call.executeQuery();
            rsCols = this.con.createStatement().executeQuery(sql);
            while (rsCols.next()) {
                colName = rsCols.getString("name").trim();
                pKey = (this.isPrimaryKey(rsKey, colName) ? "true" : "false");
                nullable = (rsCols.getInt("status") == 8 ? "true" : "false");
                dataType = rsCols.getInt("usertype");
                rsType = this.con.createStatement().executeQuery(
                        "select name from systypes where usertype = "
                                + dataType);
                rsType.next();
                dataTypeName = rsType.getString("name");
                javaType = this.getClassColumnTypeName(dataType);
                sqlType = this.getDbColumnTypeName(dataType);

                OrmColumnData colData = new OrmColumnData();
                colData.setColName(colName);
                colData.setPKey(pKey);
                colData.setNullable(nullable);
                colData.setDataTypeName(dataTypeName);
                colData.setJavaType(javaType);
                colData.setDataType(dataType);
                colData.setSqlType(sqlType);
                list.add(colData);
            }
            return list;
        } catch (SQLException e) {
            throw new Exception(e);
        }
    }

    /**
     * Determines if <i>colname</i> is part of the primary key of a given table.
     * 
     * @param rs
     *            Is a ResultSet of primary key column names related to a
     *            particular table.
     * @param colname
     *            The name of the target column.
     * @return true when <i>colname</i> is the primary key and false when
     *         otherwise.
     * @throws SQLException
     *             Access error to <i>rs</i>
     */
    private boolean isPrimaryKey(ResultSet rs, String colname)
            throws SQLException {
        String ndx;
        try {
            while (rs.next()) {
                ndx = rs.getString("column_name");
                if (ndx.equalsIgnoreCase(colname)) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Obtains the java data type for the column represented as
     * <i>colTypeId</i>.
     * 
     * @param colTypeId
     *            The id of the column type to query.
     * @return String
     * @throws Exception
     */
    public String getClassColumnTypeName(int colTypeId) throws Exception {
        String dataTypeName;

        switch (colTypeId) {
            case 5:
            case 6:
            case 7:
            case 13:
            case 16:
                dataTypeName = "int";
                break;

            case 8:
            case 10:
            case 11:
            case 14:
            case 17:
            case 21:
            case 23:
            case 24:
            case 25:
            case 26:
                dataTypeName = "double";
                break;

            case 1:
            case 2:
            case 3:
            case 4:
            case 19:
            case 20:
                dataTypeName = "String";
                break;

            case 12:
            case 15:
                dataTypeName = "java.util.Date";
                break;

            default:
                dataTypeName = "";
                break;
        } // end switch
        return dataTypeName;
    }

    /**
     * Obtains all the column names of a table/view belonging to the current
     * connection. The results are returned as a List of objects which can be
     * used to obtain the information needed.
     * 
     * @param objId
     *            The handle of the table or view to obtain the list column
     *            names.
     * @return List of {@link com.OrmObjectData OrmObjectData} objects.
     * @throws Exception
     *             General SQL errors
     */
    public List getObjectColumnNames(int objId) throws Exception {
        String sql = "select * from syscolumn where table_id = " + objId;
        try {
            // Get column names that belong to source
            Statement stmt = this.con.createStatement();
            ResultSet rsSysTable = stmt.executeQuery(sql);

            List list = new ArrayList();
            while (rsSysTable.next()) {
                OrmObjectData data = new OrmObjectData();
                String colName = rsSysTable.getString("name").trim();
                int dataType = rsSysTable.getInt("usertype");
                data.setObjectId(dataType);
                data.setObjectName(colName);
                list.add(data);
            }
            return list;
        } catch (SQLException e) {
            throw new Exception(e);
        }

    }

    /**
     * Obtains all table/view names belonging to the current connection. The
     * results are returned as a List of objects which can be used to obtain the
     * information needed.
     * 
     * @param criteria
     *            The selection criteria to be applied to the query.
     * @return List of {@link com.OrmObjectData OrmObjectData} objects.
     * @throws Exception
     *             General SQL errors
     */
    public List getObjectNames(Object criteria) throws Exception {
        String sql = "select name \"table_name\", id \"table_id\", type \"object_type\" from sysobjects where type in ('U', 'V')";
        ResultSet rsSysTable;

        if (criteria != null && criteria instanceof String) {
            sql += " and " + criteria.toString();
        }

        // Get table names to create datasources
        try {
            Statement stmt = this.con.createStatement();
            rsSysTable = stmt.executeQuery(sql);

            List list = new ArrayList();
            while (rsSysTable.next()) {
                OrmObjectData data = new OrmObjectData();
                String tableName = rsSysTable.getString("table_name").trim()
                        .toLowerCase();
                int tableId = rsSysTable.getInt("table_id");
                String objTypeId = rsSysTable.getString("object_type");
                data.setObjectId(tableId);
                data.setObjectName(tableName);
                if (objTypeId.trim().equalsIgnoreCase("U")) {
                    data.setObjType("T");
                }
                else if (objTypeId.trim().equalsIgnoreCase("V")) {
                    data.setObjType("V");
                }
                else {
                    continue;
                }
                list.add(data);
            }
            return list;

        } catch (SQLException e) {
            throw new Exception(e);
        }
    }

}
