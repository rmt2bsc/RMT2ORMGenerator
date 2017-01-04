package com.commandline;

import java.util.Properties;
import java.util.ResourceBundle;

import com.AbstractClassCreator;
import com.AbstractDataSourceCreator;
import com.AbstractOrmResource;
import com.DataHelper;
import com.DbmsProvider;
import com.Generator;
import com.dbms.DbmsProviderFactory;

/**
 * @author appdev
 * 
 */
public class CommandlineTool implements Generator {

    private Properties prop;

    private DbmsProvider dbms;

    /**
     * @param dataSource
     * @throws Exception
     */
    public CommandlineTool(Object dataSource) throws Exception {
        this.prop = this.getConfig();
        this.dbms = this.connect();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.Generator#getConfig()
     */
    public Properties getConfig() throws Exception {
        // Be sure to include as a System param for command line
        // invocations:
        // -DOrmGenProfile=[class path to application profile for ORM
        // Generation]
        ResourceBundle configFile = ResourceBundle.getBundle(System
                .getProperty(AbstractOrmResource.PROFILE));
        return DataHelper.convertBundleToProperties(configFile);
    }

    /**
     * Performs the initialization needed to establish a viable
     * AbstractOrmResource instance. The tasks that are performed are: indentify
     * and load application configuration profile, identify the output directory
     * and DBMS vendor, create DBMS provider api, and establish the database
     * connection.
     * 
     * @throws Exception
     */
    public DbmsProvider connect() throws Exception {
        String temp;
        int dbmsType;

        try {
            // Get DBMS Vendor type id
            temp = this.prop.getProperty("DBMSVendor");
            try {
                dbmsType = Integer.parseInt(temp);
            } catch (NumberFormatException e) {
                dbmsType = -1;
            }

            DbmsProvider provider = DbmsProviderFactory.getDbmsApi(dbmsType);
            provider.connect(this.prop);
            return provider;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.Generator#generate()
     */
    public void generate() throws Exception {
        try {
            AbstractClassCreator bean = new CommandlineBean(this.prop,
                    this.dbms);
            bean.produceOrmResource();

            AbstractDataSourceCreator ds = new CommandlineDataSourceView(
                    this.prop, this.dbms);
            ds.produceOrmResource();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.Generator#close()
     */
    public void close() throws Exception {
        this.dbms.close();
    }

    /**
     * Entry point from the command line invocation which creates an instance of
     * AbstractClassCreator and starts the ORM java bean class generation
     * process.
     * 
     * @param args
     *            Any command line arguments that may need to be passed to the
     *            creator.
     */
    public static void main(String[] args) {
        try {
            CommandlineTool tool = new CommandlineTool(null);
            tool.generate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
