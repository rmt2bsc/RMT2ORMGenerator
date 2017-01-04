package com.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import com.AbstractClassCreator;
import com.AbstractDataSourceCreator;
import com.DataHelper;
import com.DbmsProvider;
import com.Generator;

import com.commandline.CommandlineBean;
import com.commandline.CommandlineDataSourceView;

import com.dbms.DbmsProviderFactory;

/**
 * @author appdev
 * 
 */
public class GuiTool implements Generator, ActionListener {

    private static final long serialVersionUID = 5336453235300409115L;

    private static final String TITLE = "RMT2 ORM Resource Generator";

    private static final String SUCCESS_MSG = "Generation of ORM resources completed successfully";

    private Properties prop;

    private DbmsProvider dbms;

    private JFrame form;

    private JComboBox cb;

    private JTextField tfDbName;

    private JTextField tfDbDriver;

    private JTextField tfDbUrl;

    private JTextField tfDbOwner;

    private JTextField tfBeanPkg;

    private JTextField tfOutputDir;

    private JTextField tfUserId;

    private JPasswordField pfPassword;

    private JTextArea taConsole;

    private String dbPropName;

    private String databasePrefix;

    private int selectedDbmsCode;

    /**
     * 
     */
    public GuiTool() {
        try {
            this.getStockConfig();
            this.drawForm();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.Generator#close()
     */
    public void close() throws Exception {
        // TODO Auto-generated method stub

    }

    /**
     * Obtain data from cached properties file containing settings from previous
     * session to pre populate form.
     * 
     * @return
     * @throws Exception
     */
    public void getStockConfig() throws Exception {
        // Get stock properties
        Properties stockProps;
        ResourceBundle configFile = ResourceBundle
                .getBundle("com.gui.GuiToolConfig");
        stockProps = DataHelper.convertBundleToProperties(configFile);
        this.prop = stockProps;
    }

    /**
     * Obtain data entered from the data entry form and build Properties
     * collection.
     * 
     * @return
     * @throws Exception
     */
    public Properties getConfig() throws Exception {
        this.validateForm();
        this.prop.setProperty("dbPropertyName", this.dbPropName);
        this.prop.setProperty("database",
                this.databasePrefix + this.tfDbName.getText());
        this.prop.setProperty("dbdriver", this.tfDbDriver.getText());
        this.prop.setProperty("dburl", this.tfDbUrl.getText());
        this.prop.setProperty("db_owner", this.tfDbOwner.getText());
        this.prop.setProperty("userid", this.tfUserId.getText());
        String pw = new String(this.pfPassword.getPassword());
        this.prop.setProperty("password", pw);

        String beanPkg = this.tfBeanPkg.getText();
        this.prop.setProperty("orm_bean_package", beanPkg);
        this.prop.setProperty("orm_bean_package_prefix", beanPkg + ".");
        String dir = this.tfOutputDir.getText();
        this.prop.setProperty("orm_generated_output", dir);

        return null;
    }

    /**
     * 
     * @throws Exception
     */
    private void validateForm() throws Exception {
        String message = null;
        if (this.selectedDbmsCode <= 0 || this.dbPropName == null) {
            message = "A database vendor must be selected";
            throw new Exception(message);
        }
        if (this.tfDbDriver.getText() == null
                || this.tfDbDriver.getText().length() <= 0) {
            message = "Database driver is required";
            throw new Exception(message);
        }
        if (this.tfDbUrl.getText() == null
                || this.tfDbUrl.getText().length() <= 0) {
            message = "Database URL is required";
            throw new Exception(message);
        }
        if (this.tfDbName.getText() == null
                || this.tfDbName.getText().length() <= 0) {
            message = "Database name is required";
            throw new Exception(message);
        }
        if (this.tfUserId.getText() == null
                || this.tfUserId.getText().length() <= 0) {
            message = "Database user id is required";
            throw new Exception(message);
        }
        String pw = new String(this.pfPassword.getPassword());
        if (pw == null || pw.length() <= 0) {
            message = "Database password is required";
            throw new Exception(message);
        }
        if (this.tfBeanPkg.getText() == null
                || this.tfBeanPkg.getText().length() <= 0) {
            message = "Java package is required";
            throw new Exception(message);
        }
        if (this.tfOutputDir.getText() == null
                || this.tfOutputDir.getText().length() <= 0) {
            message = "Output directory is required";
            throw new Exception(message);
        }
    }

    /**
     * 
     * @return
     * @throws Exception
     */
    public DbmsProvider connect() throws Exception {
        try {
            DbmsProvider provider = DbmsProviderFactory
                    .getDbmsApi(this.selectedDbmsCode);
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
            this.getConfig();
            this.dbms = this.connect();
            this.taConsole.setText("");
            this.redirectSystemStreams();
            AbstractClassCreator bean = new CommandlineBean(this.prop,
                    this.dbms);
            bean.produceOrmResource();

            AbstractDataSourceCreator ds = new CommandlineDataSourceView(
                    this.prop, this.dbms);
            ds.produceOrmResource();
            JOptionPane.showMessageDialog(this.form, SUCCESS_MSG, TITLE,
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.form, e.getMessage(), TITLE,
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Redirect System.err and System.out to a JTextArea component. This is
     * useful when you spawn external process from a Swing application and want
     * to see its output in your own text component.
     */
    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                updateTextArea(String.valueOf((char) b));
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                updateTextArea(new String(b, off, len));
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    /**
     * Update the text area component as a separate thread in the background.
     * 
     * @param text
     *            the text to update the component with.
     */
    private void updateTextArea(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                taConsole.append(text);
            }
        });
    }

    /**
     * 
     */
    private void drawForm() {
        this.form = new JFrame(TITLE);
        this.form.setSize(800, 800);

        // Draw form
        JPanel main = new JPanel();
        main.setBackground(Color.LIGHT_GRAY);
        main.setLayout(new FlowLayout(FlowLayout.LEFT));
        JPanel pForm = new JPanel();
        pForm.setBackground(Color.LIGHT_GRAY);
        pForm.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Setup list of DB Vendors
        JLabel lab1 = new JLabel("RDBMS Vendor:");
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 5);
        pForm.add(lab1, gbc);

        VendorComboBoxModel model = new VendorComboBoxModel();
        cb = new JComboBox(model);
        cb.addActionListener(this);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 5);
        pForm.add(cb, gbc);

        // Database Driver Input Text Field
        JLabel lDbDriver = new JLabel("Database Driver:");
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 5);
        pForm.add(lDbDriver, gbc);
        tfDbDriver = new JTextField(30);
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.gridx = 1;
        gbc.gridy = 2;
        pForm.add(tfDbDriver, gbc);

        // Database URL Input Text Field
        JLabel lDbUrl = new JLabel("Database URL:");
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 10, 5);
        pForm.add(lDbUrl, gbc);
        tfDbUrl = new JTextField(40);
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.gridx = 1;
        gbc.gridy = 3;
        pForm.add(tfDbUrl, gbc);

        // Database Owner Input Text Field
        JLabel lDbOwner = new JLabel("Database Owner:");
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 10, 5);
        pForm.add(lDbOwner, gbc);
        tfDbOwner = new JTextField(10);
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.gridx = 1;
        gbc.gridy = 4;
        pForm.add(tfDbOwner, gbc);

        // Database name Input Text Field
        JLabel lDbName = new JLabel("Database Name:");
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 10, 5);
        pForm.add(lDbName, gbc);
        tfDbName = new JTextField(20);
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.gridx = 1;
        gbc.gridy = 5;
        pForm.add(tfDbName, gbc);

        // Database user id Input Text Field
        JLabel lUserId = new JLabel("User Id:");
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 10, 5);
        pForm.add(lUserId, gbc);
        tfUserId = new JTextField(20);
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.gridx = 1;
        gbc.gridy = 6;
        pForm.add(tfUserId, gbc);

        // Database password Input Text Field
        JLabel lPassword = new JLabel("Password:");
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 10, 5);
        pForm.add(lPassword, gbc);
        pfPassword = new JPasswordField(20);
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.gridx = 1;
        gbc.gridy = 7;
        pForm.add(pfPassword, gbc);

        // Bean package Input Text Field
        JLabel lBeanPkg = new JLabel("Java package:");
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 10, 5);
        pForm.add(lBeanPkg, gbc);
        tfBeanPkg = new JTextField(20);
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.gridx = 1;
        gbc.gridy = 8;
        pForm.add(tfBeanPkg, gbc);

        // Output Directory Input Text Field
        JLabel lOutputDir = new JLabel("Output directory:");
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 10, 5);
        pForm.add(lOutputDir, gbc);
        tfOutputDir = new JTextField(40);
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.gridx = 1;
        gbc.gridy = 9;
        pForm.add(tfOutputDir, gbc);
        JButton bChoose = new JButton("Choose...");
        bChoose.addActionListener(this);
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.gridx = 1;
        gbc.gridy = 9;
        pForm.add(bChoose, gbc);

        // Output Console Input Text Area Field
        taConsole = new JTextArea(8, 50);
        taConsole.setLineWrap(true);
        taConsole.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(taConsole);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.gridx = 1;
        gbc.gridy = 10;
        pForm.add(scroll, gbc);

        // Create button panel
        JPanel buttonPan = new JPanel();
        buttonPan.setLayout(new GridLayout(1, 2, 3, 1));
        JButton bGen = new JButton("Generate");
        JButton bClose = new JButton("Close");
        bClose.addActionListener(this);
        bGen.addActionListener(this);
        buttonPan.add(bGen);
        buttonPan.add(bClose);

        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.gridx = 0;
        gbc.gridy = 15;
        gbc.insets = new Insets(50, 0, 0, 0);
        pForm.add(buttonPan, gbc);

        main.add(pForm);

        this.form.setContentPane(main);

        // Center form
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        this.form.setSize(screenWidth / 2, screenHeight / 2);
        this.form.setLocation(screenWidth / 4, screenHeight / 4);

        // Show form
        this.form.pack();
        this.form.setVisible(true);
    }

    /**
     * 
     * @param dbmsCode
     */
    private void refreshDbFields(int dbmsCode) {
        // VendorComboBoxModel model = (VendorComboBoxModel) this.cb.getModel();
        this.selectedDbmsCode = dbmsCode;
        this.dbPropName = this.prop.getProperty("dbms." + dbmsCode
                + ".dbPropertyName");
        this.databasePrefix = this.prop.getProperty("dbms." + dbmsCode
                + ".database");
        String dbDriver = this.prop.getProperty("dbms." + dbmsCode
                + ".dbdriver");
        String dbUrl = this.prop.getProperty("dbms." + dbmsCode + ".dburl");
        String dbOwner = this.prop
                .getProperty("dbms." + dbmsCode + ".db_owner");
        String beanPkg = this.prop.getProperty("orm_bean_package");

        this.tfDbDriver.setText(dbDriver);
        this.tfDbUrl.setText(dbUrl);
        this.tfDbOwner.setText(dbOwner);
        this.tfBeanPkg.setText(beanPkg);
        return;
    }

    /**
     * 
     */
    private void chooseOutputDir() {
        FileDialog d = new FileDialog(this.form, "Choose Output Directory");
        d.setVisible(true);
        String dir = d.getDirectory();
        this.tfOutputDir.setText(dir);
        return;
    }

    /**
     * 
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JButton) {
            if (e.getActionCommand().equalsIgnoreCase("close")) {
                this.form.dispose();
            }
            if (e.getActionCommand().equalsIgnoreCase("choose...")) {
                this.chooseOutputDir();
            }
            if (e.getActionCommand().equalsIgnoreCase("Generate")) {
                try {
                    this.generate();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (e.getSource() instanceof JComboBox) {
            String dbmsName = ((JComboBox) e.getSource()).getSelectedItem()
                    .toString();
            VendorComboBoxModel model = (VendorComboBoxModel) ((JComboBox) e
                    .getSource()).getModel();
            int code = model.getDbmsCode(dbmsName);
            try {
                this.refreshDbFields(code);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 
     * @param args
     */
    public static void main(String args[]) {
        GuiTool tool = new GuiTool();

    }
}
