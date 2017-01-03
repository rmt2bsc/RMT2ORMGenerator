package com;

import java.util.List;
import java.util.Properties;

import java.io.PrintWriter;
import java.io.FileWriter;

/**
 * Proivides the necessary logic to generate an ORM java bean class file
 * containing the properties and accessor methods that corresponds to the source
 * database object.
 * 
 * @author appdev
 * 
 */
public abstract class AbstractClassCreator extends AbstractOrmResource {

    private String tableName;

    private int tableId;

    private String formBeanName;

    private StringBuffer header;

    private StringBuffer declaration;

    private StringBuffer methods;

    private StringBuffer constructor;

    private StringBuffer staticColNames;

    /**
     * Creates an AbstractClassCreator instance which is aware of its
     * environment configuration and the database connection needed to generate
     * ORM objects.
     * 
     * @param config
     *            Application's configuration data
     * @param dbms
     *            database connection.
     * @throws Exception
     */
    protected AbstractClassCreator(Properties config, DbmsProvider dbms)
            throws Exception {
        super(config, dbms);
    }

    /**
     * Drives the process of creating an ORM java bean class file for every
     * database object belonging to a particular connection.
     * 
     */
    public void produceOrmResource() {
        String content;

        try {
            List list = this.dbms.getObjectNames(this.getSource());
            for (Object data : list) {
                tableName = ((OrmObjectData) data).getObjectName().trim()
                        .toLowerCase();
                formBeanName = DataHelper.formatClassMethodName(tableName);
                tableId = ((OrmObjectData) data).getObjectId();

                // Instantiate buffers
                this.header = new StringBuffer();
                this.declaration = new StringBuffer();
                this.methods = new StringBuffer();
                this.constructor = new StringBuffer();
                this.staticColNames = new StringBuffer();

                // Create class package statement
                String packageLoc = this.prop.getProperty("orm_bean_package");
                this.header.append("package " + packageLoc + ";\n\n\n");
                this.header.append("import java.util.Date;\n");
                this.header.append("import java.io.*;\n");
                this.header
                        .append("import com.api.persistence.db.orm.OrmBean;\n");
                this.header.append("import com.SystemException;\n\n\n");

                // Add class description javadoc
                this.header.append("/**\n");
                this.header.append(" * Peer object that maps to the "
                        + tableName + " database table/view.\n");
                this.header.append(" *\n");
                this.header.append(" * @author auto generated.\n");
                this.header.append(" */\n");

                this.header.append("public class ");
                this.header.append(formBeanName);
                this.header.append(" extends OrmBean {\n\n");

                // Add javadoc for default constructor.
                this.constructor.append("/**\n");
                this.constructor.append(" * Default constructor.\n");
                // Commented out to remedy javadoc generation errors
                // this.constructor.append(" *\n");
                // this.constructor.append(" * @author auto generated.\n");
                this.constructor.append(" */\n");
                this.constructor.append("  public ");
                this.constructor.append(formBeanName);
                this.constructor
                        .append("() throws SystemException {\n\tsuper();\n }\n");

                // Create Class data memeber declarations
                this.identifyProperties(tableId);

                // Build last method
                this.methods.append("/**\n");
                this.methods
                        .append(" * Stubbed initialization method designed to implemented by developer.\n\n");
                // Commented out to remedy javadoc generation errors
                // this.methods.append(" *\n");
                // this.methods.append(" * @author auto generated.\n");
                this.methods.append(" */\n");
                this.methods
                        .append("  public void initBean() throws SystemException {}\n}");

                // Assemble the javabean parts into a file.
                content = this.header.toString()
                        + "\n\n\n\t// Property name constants that belong to respective DataSource, "
                        + formBeanName + "View\n\n"
                        + this.staticColNames.toString() + "\n\n\n\t"
                        + this.declaration.toString()
                        + "\n\n\n\t// Getter/Setter Methods\n\n"
                        + this.constructor.toString() + this.methods.toString();
                // this.methods.toString() +
                // "  public void initBean() throws SystemException {}\n}";
                FileWriter file = new FileWriter(outputPath + formBeanName
                        + ".java");
                PrintWriter pw = (new PrintWriter(file));
                pw.print(content);
                pw.flush();
                pw.close();
                System.out.println(formBeanName
                        + "Bean - created successfully!");
            }
            System.out.println("Bean Creation Process Completed!!!!!!!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Identifies each database column that belongs to table, <i>tableId</i>,
     * and invokes the method, buildDataMemeber to produce class properties and
     * methods.
     * 
     * @param tableId
     *            The handle of the database table or view that is used to fetch
     *            all relative columns.
     * @return The total number of columns processed as an int value.
     * @throws Exception
     */
    protected int identifyProperties(int tableId) throws Exception {
        List list = this.dbms.getObjectAttributes(tableId);
        int count = 0;
        for (Object data : list) {
            String colName = ((OrmColumnData) data).getColName().trim()
                    .toLowerCase();
            int dataType = ((OrmColumnData) data).getDataType();
            count += this.buildPropertyDeclarations(colName, dataType);
        }
        return count;
    }

    /**
     * Builds the property declaration, property name constant, and the
     * getter/setter methods for <i>colName</i>.
     * 
     * @param colName
     *            The name of the column to process.
     * @param dataType
     *            An integer value representing the data type.
     * @return returns 1.
     * @throws Exception
     *             When the column type description is unobtainable.
     */
    protected int buildPropertyDeclarations(String colName, int dataType)
            throws Exception {

        String varName = DataHelper.formatVarName(colName);
        String methodName = DataHelper.formatClassMethodName(colName);
        String dataTypeName = null;
        dataTypeName = this.dbms.getClassColumnTypeName(dataType);

        // Create Class Data Member and its javadoc
        this.declaration
                .append("/** The javabean property equivalent of database column "
                        + this.tableName + "." + colName + " */\n");
        this.declaration.append("  private ");
        this.declaration.append(dataTypeName);
        this.declaration.append(" ");
        this.declaration.append(varName);
        this.declaration.append(";\n");

        // Create DataSource property name constants and their javadoc
        this.staticColNames
                .append("/** The property name constant equivalent to property, "
                        + methodName + ", of respective DataSource view. */\n");
        this.staticColNames.append("  public static final String PROP_");
        this.staticColNames.append(methodName.toUpperCase());
        this.staticColNames.append(" = \"");
        this.staticColNames.append(methodName);
        this.staticColNames.append("\";\n");

        // Create Class setter Method and its javadoc
        this.methods.append("/**\n");
        this.methods.append(" * Sets the value of member variable " + varName
                + "\n");
        // Commented out to remedy javadoc generation errors
        // this.methods.append(" *\n");
        // this.methods.append(" * @author auto generated.\n");
        this.methods.append(" */\n");
        this.methods.append("  public void set");
        this.methods.append(methodName);
        this.methods.append("(");
        this.methods.append(dataTypeName);
        this.methods.append(" value) {\n");
        this.methods.append("    this.");
        this.methods.append(varName);
        this.methods.append(" = value;\n");
        this.methods.append("  }\n");

        // Create Class getter Method
        this.methods.append("/**\n");
        this.methods.append(" * Gets the value of member variable " + varName
                + "\n");
        // Commented out to remedy javadoc generation errors
        // this.methods.append(" *\n");
        // this.methods.append(" * @author atuo generated.\n");
        this.methods.append(" */\n");
        this.methods.append("  public ");
        this.methods.append(dataTypeName);
        this.methods.append(" get");
        this.methods.append(methodName);
        this.methods.append("() {\n");
        this.methods.append("    return this.");
        this.methods.append(varName);
        this.methods.append(";\n");
        this.methods.append("  }\n");

        return 1;
    }
}
