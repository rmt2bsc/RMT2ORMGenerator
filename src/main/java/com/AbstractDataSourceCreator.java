package com;

import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.api.util.RMT2File;
import com.api.xml.RMT2XmlUtility;

/**
 * Proivides the necessary logic to generate an ORM DataSource view file as XML
 * which contains the SQL select list, table, and detailed column attributes.
 * 
 * @author appdev
 * 
 */
public abstract class AbstractDataSourceCreator extends AbstractOrmResource {

    private Document doc;

    private String tableName;

    private int tableId;

    private String formattedTableName;

    /**
     * Creates an AbstractDataSourceCreator instance which is aware of its
     * environment configuration and the database connection needed to generate
     * ORM objects.
     * 
     * @param config
     *            Application's configuration data
     * @param dbms
     *            database connection.
     * @throws Exception
     */
    public AbstractDataSourceCreator(Properties config, DbmsProvider dbms)
            throws Exception {
        super(config, dbms);
    }

    /**
     * Cycles through each required database object and its attributes and build
     * the select list, the source table, and a detailed list of column
     * attributes for the DataSource view resource as XML. Once all the above
     * information is gathered, output the XML to a file designated by the
     * system property key, <i.orm_generated_output</i>.
     * 
     */
    public void produceOrmResource() {

        try {
            List list = this.dbms.getObjectNames(this.getSource());
            for (Object data : list) {
                tableName = ((OrmObjectData) data).getObjectName().trim()
                        .toLowerCase();
                formattedTableName = DataHelper.formatDsName(tableName);
                tableId = ((OrmObjectData) data).getObjectId();

                DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = f.newDocumentBuilder();
                doc = builder.newDocument();

                // Create root element
                Element root = doc.createElement("DataSource");
                root.setAttribute("Name", formattedTableName + "View");

                // Create Select Element
                String selectList = this.buildSelectList(tableId);
                Element selectItem = doc.createElement("Sql");
                selectItem.setAttribute("Select", selectList);
                selectItem.setAttribute("From", tableName);
                selectItem.setAttribute("Where", "");
                selectItem.setAttribute("GroupBy", "");
                selectItem.setAttribute("Having", "");
                selectItem.setAttribute("OrderBy", "");
                root.appendChild(selectItem);

                // Create Table Usage Element
                Element tableItem = doc.createElement("TableUsage");
                tableItem.setAttribute("Name", formattedTableName + "Table");
                tableItem
                        .setAttribute("DBName", tableName.trim().toLowerCase());
                tableItem.setAttribute("Updateable", "true");
                root.appendChild(tableItem);

                // Create Column Attributes
                this.buildColAttribute(tableId, root);

                doc.appendChild(root);
                // doc.setXmlVersion("1.0");
                String fileContent = RMT2XmlUtility.prettyPrint(doc, true, false);
                String fileName = outputPath + formattedTableName + "View.xml";
                RMT2File.createFile(fileContent, fileName);

                System.out.println(formattedTableName
                        + "View - created successfully!");
            }
            System.out.println("DataSource Creation Process Completed!!!!!!!");
        } catch (Throwable e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Build the SQL select list based on the column values found for
     * <i>tableId</i>.
     * 
     * @param tableId
     *            The handle of the table that is to be managed.
     * @return THe select list as a String.
     * @throws Exception
     *             Problem obtaining the table attributes.
     */
    protected String buildSelectList(int tableId) throws Exception {
        List list = this.dbms.getObjectAttributes(tableId);
        StringBuffer selectList = new StringBuffer(200);
        int colCount = 0;
        for (Object data : list) {
            String colName = ((OrmColumnData) data).getColName().trim()
                    .toLowerCase();
            selectList.append((++colCount > 1 ? ", \n\t\t"
                    + colName.toLowerCase() : colName.toLowerCase()));
        }
        return selectList.toString();
    }

    /**
     * Builds a list of attributes as a XML node for each column belonging to
     * <i>tableId</i>. The attributes identified are: DBName, SqlType, IsNull,
     * JavaType, PrimaryKey, TableName, Updatable, DisplayHeader, Computed, and
     * DataValue.
     * 
     * @param tableId
     *            THe handle of the table that is to be processed.
     * @param root
     *            The root XML element to expand.
     * @return The total number of columns processed.
     * @throws Exception
     *             Problem obtaining the table attributes.
     */
    protected int buildColAttribute(int tableId, Element root) throws Exception {
        List list = this.dbms.getObjectAttributes(tableId);
        int count = 0;
        for (Object obj : list) {
            OrmColumnData col = (OrmColumnData) obj;

            Element item = doc.createElement("ColAttr");
            if (col.getPKey().trim().equalsIgnoreCase("true")) {
                item.setAttribute("Name",
                        DataHelper.formatDsName(col.getColName().trim()));
            }
            else {
                item.setAttribute("Name",
                        DataHelper.formatDsName(col.getColName().trim()));
            }
            item.setAttribute("DBName", col.getColName().toLowerCase().trim());
            item.setAttribute("SqlType", col.getDataTypeName().trim());
            item.setAttribute("IsNull", col.getNullable().trim());
            item.setAttribute("JavaType", col.getJavaType().trim());
            item.setAttribute("PrimaryKey", col.getPKey().trim());
            item.setAttribute("TableName", formattedTableName + "Table");
            item.setAttribute("Updateable", "true");
            item.setAttribute("DisplayHeader", "");
            item.setAttribute("Computed", "false");
            item.setAttribute("DataValue", "");
            root.appendChild(item);
            count++;
        } // end for

        return count;

    }

}
