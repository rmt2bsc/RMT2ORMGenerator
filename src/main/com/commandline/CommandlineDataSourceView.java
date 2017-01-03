package com.commandline;

import java.util.Properties;

import com.AbstractDataSourceCreator;
import com.DbmsProvider;

/**
 * Command line implementation of the AbstractDataSourceCreator class which
 * generates XML files that represent the ORM DataSource views for the current
 * database connection. This command-line implementation totally relies on the
 * existince of the application configuration profile for its input data.
 * 
 * @author RTerrell
 * 
 */
public class CommandlineDataSourceView extends AbstractDataSourceCreator {

    /**
     * Creates an CommandlineDataSourceView instance which is aware of its
     * environment configuration and the database connection needed to generate
     * ORM datasource views.
     * 
     * @param config
     *            Application's configuration data
     * @param dbms
     *            database connection.
     * @throws Exception
     */
    public CommandlineDataSourceView(Properties config, DbmsProvider dbms)
            throws Exception {
        super(config, dbms);
    }
}
