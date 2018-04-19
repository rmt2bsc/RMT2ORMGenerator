package com.dbms;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.DbmsProvider;
import com.OrmColumnData;
import com.OrmObjectData;

/**
 * This is the Adaptive Server Anywhere implementation of
 * {@link com.DbmsProvider DbmsProvider} interface.
 * 
 * @author RTerrell
 * 
 */
class AsaProviderImpl extends AbstractDbmsProvider implements DbmsProvider {

    /**
     * Default constructor.
     */
    public AsaProviderImpl() {
        super();
    }

    /**
     * Uses the Adaptive Server Anywhere system tables/views to extract column
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
        String sql = "select * from syscolumn where table_id = " + objId;
        ResultSet rsCols = null;
        ResultSet rsType = null;
        String colName = null;
        String pKey = null;
        String nullable = null;
        String dataTypeName = null;
        String javaType = null;
        String sqlType = null;
        int dataType = 0;
        List list = new ArrayList();

        rsCols = this.con.createStatement().executeQuery(sql);
        try {
            while (rsCols.next()) {
                colName = rsCols.getString("column_name").trim();
                pKey = (rsCols.getString("pkey").equalsIgnoreCase("Y") ? "true"
                        : "false");
                nullable = (rsCols.getString("nulls").equalsIgnoreCase("Y") ? "true"
                        : "false");
                dataType = rsCols.getInt("domain_id");
                rsType = this.con.createStatement()
                        .executeQuery(
                                "select * from sysdomain where domain_id = "
                                        + dataType);
                rsType.next();
                dataTypeName = rsType.getString("domain_name");
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
     * Obtains the java.sql.Types value for the column represented as
     * <i>colTypeId</i>.
     * 
     * @param colTypeId
     *            The id of the column type to query.
     * @return String
     * @throws Exception
     */
    public String getDbColumnTypeName(int dataType) throws Exception {
        String javaType;

        switch (dataType) {
            case 1:
            case 2:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
                javaType = "java.sql.Types.INTEGER";
                break;

            case 3:
            case 4:
            case 5:
            case 27:
                javaType = "java.sql.Types.DOUBLE";
                break;
            case 7:
            case 8:
            case 33:
            case 34:
                javaType = "java.sql.Types.CHAR";
                break;

            case 9:
            case 10:
            case 11:
                // case 12:
            case 28:
            case 32:
            case 35:
            case 36:
                javaType = "java.sql.Types.VARCHAR";
                break;

            case 12:
                javaType = "java.sql.Types.LONGVARBINARY";
                break;

            case 6:
            case 13:
            case 14:
                javaType = "java.sql.Types.DATE";
                break;

            default:
                javaType = "";
                break;
        } // end switch
        return javaType;
    }

    /**
     * Obtains the java data type for the column represented as
     * <i>colTypeId</i>.
     * 
     * @param colTypeId
     *            The id of the column type to query. This value is mapped to he
     *            column, domain_id, of Sybase SQL Anywhere's system stored
     *            procedure, SYS.SYSDOMAIN.
     * @return String
     * @throws Exception
     */
    public String getClassColumnTypeName(int colTypeId) throws Exception {
        String dataTypeName;

        switch (colTypeId) {
            case 1:
            case 2:
            case 19:
            case 21:
            case 22:
            case 24:
                dataTypeName = "int";
                break;

            case 20:
            case 23:
                dataTypeName = "long";
                break;

            case 3:
            case 4:
            case 5:
            case 27:
                dataTypeName = "double";
                break;

            case 7:
            case 8:
            case 33:
            case 34:
                dataTypeName = "char";
                break;

            case 9:
            case 10:
                // case 11:
                // case 12:
            case 28:
            case 32:
            case 35:
            case 36:
                dataTypeName = "String";
                break;

            case 11:
            case 12:
                // binary and long Binary (mutilmedia files)
                dataTypeName = "byte[]";
                break;

            case 6:
            case 13:
            case 14:
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
        String sql = "select * from systabcol where table_id = " + objId;
        try {
            // Get column names that belong to source
            Statement stmt = this.con.createStatement();
            ResultSet rsSysTable = stmt.executeQuery(sql);

            List list = new ArrayList();
            while (rsSysTable.next()) {
                OrmObjectData data = new OrmObjectData();
                String colName = rsSysTable.getString("column_name").trim();
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
     *            The selection criteria that is to be applied to the query
     * @return List of {@link com.OrmObjectData OrmObjectData} objects.
     * @throws Exception
     *             General SQL errors
     */
    public List getObjectNames(Object criteria) throws Exception {
        String sql = "select table_name, st.table_id, so.object_type from sysobject so, systable st where so.object_id = st.object_id and so.object_type in (1, 2) and st.creator in (select user_id from sysuser where user_name = 'dba')";
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
                int objTypeId = rsSysTable.getInt("object_type");
                data.setObjectId(tableId);
                data.setObjectName(tableName);
                switch (objTypeId) {
                    case 1:
                        data.setObjType("T");
                        break;
                    case 2:
                        data.setObjType("V");
                        break;
                    default:
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
