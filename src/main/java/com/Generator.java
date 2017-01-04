package com;

import java.util.Properties;

/**
 * @author appdev
 * 
 */
public interface Generator {

    /**
     * 
     * @return
     * @throws Exception
     */
    DbmsProvider connect() throws Exception;

    /**
     * 
     * @return
     * @throws Exception
     */
    Properties getConfig() throws Exception;

    /**
     * 
     * @throws Exception
     */
    void generate() throws Exception;

    /**
     * 
     * @throws Exception
     */
    void close() throws Exception;
}
