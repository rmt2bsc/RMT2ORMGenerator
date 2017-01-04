package com;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 * Class providing helper methods to format the names of java classes, data
 * source views, and their attributes alike.
 * 
 * @author RTerrell
 * 
 */
public class DataHelper {

    /**
     * Converts a particular String to conform to the naming format of a Java
     * getter/setter method ("XxxxxXxxxx").
     * 
     * @param methodName
     *            The name of the method without the "get" or "set" prefix.
     * @return String as the formatted method name.
     */
    public static String formatClassMethodName(String methodName) {
        String newValue = "";
        String token = "";
        StringTokenizer str = new StringTokenizer(methodName, "_");
        while (str.hasMoreTokens()) {
            token = str.nextToken();
            newValue += DataHelper.wordCap(token.toLowerCase());
        }
        return newValue;
    }

    /**
     * Converts the name of variable to conform to the format of "xxxxXxxxxx".
     * 
     * @param varName
     *            the name of the variable to convert.
     * @return The convert variable name as a String.
     */
    public static String formatVarName(String varName) {

        int wordCount = 0;
        String newValue = "";
        String token = "";

        StringTokenizer str = new StringTokenizer(varName, "_");
        while (str.hasMoreTokens()) {
            token = str.nextToken();
            if (++wordCount == 1) {
                newValue = token.toLowerCase();
            }
            else {
                newValue += DataHelper.wordCap(token.toLowerCase());
            }
        } // end while

        return newValue;
    }

    /**
     * Converts a particular String to conform to the format of the name of a
     * DataSource view, "XxxxxXxxxx".
     * 
     * @param DsName
     *            The name of the DataSource View to convert.
     * @return The converted DataSource view name as a String.
     */
    public static String formatDsName(String dsName) {
        return DataHelper.formatClassMethodName(dsName);
    }

    public static String wordCap(String src) {

        StringBuffer capValue = new StringBuffer(100);

        for (int ndx = 0; ndx < src.length(); ndx++) {
            if (ndx == 0) {
                Character ch = new Character(src.charAt(ndx));
                capValue.append(ch.toString().toUpperCase());
            }
            else {
                Character ch = new Character(src.charAt(ndx));
                capValue.append(ch.toString());
            }
        }
        return capValue.toString();
    }

    /**
     * Copies the data of a ResourceBundle to a Properties collection.
     * 
     * @param source
     *            A valid instance of the targeted ResourceBundle.
     * @return Properties.
     */
    public static Properties convertBundleToProperties(ResourceBundle source) {
        Properties props = new Properties();
        return convertBundleToProperties(source, props);
    }

    /**
     * 
     * @param source
     * @param props
     * @return
     */
    public static Properties convertBundleToProperties(ResourceBundle source,
            Properties props) {
        if (props == null) {
            return null;
        }
        Enumeration iter = source.getKeys();
        while (iter.hasMoreElements()) {
            String key = (String) iter.nextElement();
            String val = source.getString(key);
            props.put(key, val);
        }
        return props;
    }

}
