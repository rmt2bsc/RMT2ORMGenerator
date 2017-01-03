package com;

//import java.util.ResourceBundle;
import java.util.Properties;

/**
 * This abstract class provides a skeletal implementation for establishing a
 * database connection and creating the DBMS Provider api that will be used to
 * generate the ORM java classes and DataSource view resources. This class
 * depends on the existence of an application configuation profile, which is
 * simply a properties file, providing the essential information needed to
 * connect to the database, store generated output files, and identify the
 * specific database vendor.
 * <p>
 * The programmer is required to provide the location of application's
 * configuration profile as a System property identified as,
 * <i>OrmGenProfile</i>.
 * 
 * 
 * @author RTerrell
 * 
 */
public abstract class AbstractOrmResource {
    /**
     * The name of the key that points to the application configuration profile
     */
    public static final String PROFILE = "OrmGenProfile";

    /** The type id of a Sybase Adaptive Server Anywhere DBMS */
    public static final int DBMS_ASA = 1;

    /** The type id of a Sybase Adaptive Server Enerprise DBMS */
    public static final int DBMS_ASE = 2;

    /** The type id of an Oracle DBMS */
    public static final int DBMS_ORACLE = 3;

    /** The type id of a Microsoft SQL Server DBMS */
    public static final int DBMS_SQLSERVER = 4;

    /** The type id of IBM's DB2 DBMS */
    public static final int DBMS_DB2 = 5;

    /** Holds the file path where the output will be stored */
    protected String outputPath;

    /** The type database that is connected */
    protected int dbmsType;

    /** Configuration Properties obtained from some external source */
    protected Properties prop;

    /** A reference to the DBMS ORM Provider. */
    protected DbmsProvider dbms;

    /**
     * The input data needed to generate ORM resources. The value could be null.
     */
    protected Object source;

    /**
     * Creates an AbstractOrmResource instance which is aware of its environment
     * configuration and the database connection needed to generate ORM objects.
     * 
     * @param config
     *            Application's configuration data
     * @param dbms
     *            database connection.
     * @throws Exception
     */
    protected AbstractOrmResource(Properties config, DbmsProvider dbms)
            throws Exception {
        this.prop = config;
        this.dbms = dbms;
        this.outputPath = this.prop.getProperty("orm_generated_output");
    }

    /**
     * Implement this method to drive the process of creating the ORM resource
     * for the current connection of the external data provider in its entirety.
     * 
     */
    public abstract void produceOrmResource();

    /**
     * Get the source of data needed to generate resources.
     * 
     * @return the source
     */
    protected Object getSource() {
        return source;
    }
}
