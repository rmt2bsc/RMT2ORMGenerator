package com.dbms;

import com.AbstractOrmResource;
import com.DbmsProvider;

/**
 * Factory class for creating various instances of {@link com.DbmsProvider
 * DbmsProvider}. The following database systems supported are: Adaptive Server
 * Anywhere, Adaptive Server Enerprise, Oracle, DB2, and Microsoft SQL Server.
 * 
 * @author appdev
 * 
 */
public class DbmsProviderFactory {

    /**
     * Creates the appropriate implementation of {@link com.DbmsProvider
     * DbmsProvider} using <i>typeId</i>.
     * 
     * 
     * @param typeId
     *            int value representing the type of DBMS to engage. Valid
     *            values for typeId are:<br>
     *            <ul>
     *            <li>Adaptive Server Anywhere = 1</li>
     *            <li>Adaptive Server Enterprise = 2</li>
     *            <li>Oracle = 3</li>
     *            <li>SQL Server 4</li>
     *            <li>DB2 = 5</li>
     *            </ul>
     * 
     * @return DbmsProvider
     */
    public static final DbmsProvider getDbmsApi(int typeId) {
        DbmsProvider api;

        switch (typeId) {
            case AbstractOrmResource.DBMS_ASA:
                api = new AsaProviderImpl();
                break;

            case AbstractOrmResource.DBMS_ASE:
                api = null;
                break;

            case AbstractOrmResource.DBMS_DB2:
                api = null;
                break;

            case AbstractOrmResource.DBMS_ORACLE:
                api = null;
                break;

            case AbstractOrmResource.DBMS_SQLSERVER:
                api = new SqlServerProviderImpl();
                break;

            default:
                api = null;
        }
        return api;
    }

}
