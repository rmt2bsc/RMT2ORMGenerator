package com;

/**
 * A java bean class that is capable of managing basic key attributes pertaining
 * to a database column such as: column name, primary key flag, is nullable
 * flag, data type name, java type name, SQL type name and general data type
 * name.
 * 
 * @author appdev
 * 
 */
public class OrmColumnData {
    private String colName;

    private String pKey;

    private String nullable;

    private String dataTypeName;

    private String javaType;

    private String sqlType;

    private int dataType;

    /**
     * @return the colName
     */
    public String getColName() {
        return colName;
    }

    /**
     * @param colName
     *            the colName to set
     */
    public void setColName(String colName) {
        this.colName = colName;
    }

    /**
     * @return the dataType
     */
    public int getDataType() {
        return dataType;
    }

    /**
     * @param dataType
     *            the dataType to set
     */
    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the dataTypeName
     */
    public String getDataTypeName() {
        return dataTypeName;
    }

    /**
     * @param dataTypeName
     *            the dataTypeName to set
     */
    public void setDataTypeName(String dataTypeName) {
        this.dataTypeName = dataTypeName;
    }

    /**
     * @return the javaType
     */
    public String getJavaType() {
        return javaType;
    }

    /**
     * @param javaType
     *            the javaType to set
     */
    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    /**
     * @return the nullable
     */
    public String getNullable() {
        return nullable;
    }

    /**
     * @param nullable
     *            the nullable to set
     */
    public void setNullable(String nullable) {
        this.nullable = nullable;
    }

    /**
     * @return the pKey
     */
    public String getPKey() {
        return pKey;
    }

    /**
     * @param key
     *            the pKey to set
     */
    public void setPKey(String key) {
        pKey = key;
    }

    /**
     * @return the sqlType
     */
    public String getSqlType() {
        return sqlType;
    }

    /**
     * @param sqlType
     *            the sqlType to set
     */
    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

}
