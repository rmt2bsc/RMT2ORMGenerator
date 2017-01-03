package com.dbms;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;

import java.util.Properties;

/**
 * This abstract class implementation provides logic that gathers database
 * connection parameters from an input source and connects to the target
 * database.
 * 
 * @author appdev
 * 
 */
class AbstractDbmsProvider {
    /** The connection object used to communicate to the database. */
    protected Connection con;

    /**
     * Default constructor.
     * 
     */
    protected AbstractDbmsProvider() {
        return;
    }

    /**
     * Establishes a connection to a particular database using
     * <i>configFile</i>.
     * 
     * @param configFile
     *            Properties object containing the essential database properties
     *            needed to establish a connection.
     * @throws Exception
     *             When a database communication error occurs, or when database
     *             driver class is not found, or for general errors.
     */
    public void connect(Properties configFile) throws Exception {
        Properties dbParms = new Properties();
        String userid;
        String password;
        String database;
        String dbPropName;
        String driver;
        String url;

        try {
            // Get configuration data
            userid = configFile.getProperty("userid");
            password = configFile.getProperty("password");
            database = configFile.getProperty("database");
            dbPropName = configFile.getProperty("dbPropertyName");
            driver = configFile.getProperty("dbdriver");
            url = configFile.getProperty("dburl");

            // Set Database Parameters
            dbParms.setProperty("user", userid);
            dbParms.setProperty("password", password);
            dbParms.setProperty(dbPropName, database);

            // Connect to database
            Driver dbDriver = (Driver) Class.forName(driver).newInstance();
            this.con = dbDriver.connect(url, dbParms);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.DbmsProvider#close()
     */
    public void close() throws Exception {
        try {
            this.con.close();
            this.con = null;
        } catch (SQLException e) {
            throw new Exception(e);
        }
    }
}
