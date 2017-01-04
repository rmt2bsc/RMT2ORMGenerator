package com.commandline;

import java.util.Properties;

import com.AbstractClassCreator;
import com.DbmsProvider;

/**
 * Command line implementation of the AbstractClassCreator class which generates
 * java bean classes for the ORM api via the current database connection. This
 * command-line implementation totally relies on the existince of the
 * application configuration profile for its input data.
 * 
 * @author RTerrell
 * 
 */
public class CommandlineBean extends AbstractClassCreator {

    /**
     * Creates an CommandlineBean instance which is aware of its environment
     * configuration and the database connection needed to generate ORM beans.
     * 
     * @param config
     *            Application's configuration data
     * @param dbms
     *            database connection.
     * @throws Exception
     */
    public CommandlineBean(Properties config, DbmsProvider dbms)
            throws Exception {
        super(config, dbms);
    }
}
