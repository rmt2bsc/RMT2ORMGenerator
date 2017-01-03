/**
 * 
 */
package com.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 * @author appdev
 * 
 */
public class VendorComboBoxModel implements ComboBoxModel {
    private List<String> dbms;
    private Map<String, Integer> dbmsCodes;
    private String selected;

    public VendorComboBoxModel() {
        this.dbms = new ArrayList<String>();
        this.dbms.add("Adaptive Server Enterprise");
        this.dbms.add("DB2");
        this.dbms.add("MySQL");
        this.dbms.add("Oracle");
        this.dbms.add("SQL Anywhere");
        this.dbms.add("SQL Server");

        this.dbmsCodes = new HashMap<String, Integer>();
        this.dbmsCodes.put("SQL Anywhere", 1);
        this.dbmsCodes.put("Adaptive Server Enterprise", 2);
        this.dbmsCodes.put("Oracle", 3);
        this.dbmsCodes.put("SQL Server", 4);
        this.dbmsCodes.put("DB2", 5);
        this.dbmsCodes.put("MySQL", 6);

        return;
    }

    public int getDbmsCode(String dbmsName) {
        Integer val = this.dbmsCodes.get(dbmsName);
        return val.intValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ComboBoxModel#getSelectedItem()
     */
    public Object getSelectedItem() {
        return this.selected;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
     */
    public void setSelectedItem(Object anItem) {
        this.selected = anItem.toString();

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener
     * )
     */
    public void addListDataListener(ListDataListener l) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index) {
        // TODO Auto-generated method stub
        return this.dbms.get(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize() {
        // TODO Auto-generated method stub
        return this.dbms.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.
     * ListDataListener)
     */
    public void removeListDataListener(ListDataListener l) {
        // TODO Auto-generated method stub

    }

}
