package com.mycompany.firstflatlaf;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Mallari
 */
public class MainForm extends javax.swing.JFrame {

    //Database
    private Database db;
    //links
    private AddingEmployeeForm adf;
    private DeleteEmployeeForm df;
    private UpdateEmployeeForm euf;
    private Payroll payroll;
    private UpdatePayrollForm upf;
    private Login log;

    //employees datas
    //for emp id
    private int rowEmpIdInt;
    private ImageIcon rowEmpProfile;
    private int rowEmpAge;
    private String rowEmpName, rowEmpDateOfBirth, rowEmpGender, rowEmpStatus, rowEmpContactNum,
            rowEmpEmail, rowEmpDepartment, rowEmpPosition, rowEmpLocationType;

    //payroll datas
    //hold data
    private String selectedStringEmpId, empIdSalary;
    //for row
    private int rowEmpPayrollId;
    private int rowEmpId;
    private double rowEmpSalary;

    //for profile path
    private String profilePath;

    //settings datas
    private String username;
    private int userId;
    private char[] charArrayPass;
    private String passString;
    //for toggling theme
    private boolean isDarkMode = ThemeManager.isDarkMode();

    //Sorter
    /**
     * Creates new form MainForm
     *
     * @param tabIndex
     */
    public MainForm(int tabIndex) {
        initComponents();

        //database setup
        try {
            db = Database.getInstance();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        //set time icon
        txtTime.setIcon(new FlatSVGIcon("svg/timer.svg"));
        //align time in vertical center
        txtTime.setVerticalTextPosition(SwingConstants.CENTER);

        //dashboard overviews icons
        lblIconTotalEmp.setIcon(new FlatSVGIcon("svg/total_emp.svg"));
        lblIconTotalRep.setIcon(new FlatSVGIcon("svg/report_list.svg"));
        lblIconActiveEmp.setIcon(new FlatSVGIcon("svg/active_emp.svg"));

        //default index
        tabContentPane.setSelectedIndex(tabIndex);

        //default active links
        setActiveBtn(btnDashboard);

        //remove tab border
        tabContentPane.setBorder(BorderFactory.createEmptyBorder());
        //btn theme icon
        btnChangeTheme.setIcon(new FlatSVGIcon("svg/night.svg"));
        //sidebar icons
        btnDashboard.setIcon(new FlatSVGIcon("svg/dashboard.svg"));
        btnEmployee.setIcon(new FlatSVGIcon("svg/employee.svg"));
        btnAttendance.setIcon(new FlatSVGIcon("svg/attendance.svg"));
        btnPayroll.setIcon(new FlatSVGIcon("svg/payroll.svg"));

        btnReports.setIcon(new FlatSVGIcon("svg/reports.svg"));
        btnLeaves.setIcon(new FlatSVGIcon("svg/leaves.svg"));
        btnSettings.setIcon(new FlatSVGIcon("svg/settings.svg"));
        btnLogout.setIcon(new FlatSVGIcon("svg/logout.svg"));

        btnAdd.setIcon(new FlatSVGIcon("svg/add.svg"));
        btnDeleteLink.setIcon(new FlatSVGIcon("svg/delete.svg"));
        btnExportPDF.setIcon(new FlatSVGIcon("svg/pdf.svg"));

        btnAddEmpSalary.setIcon(new FlatSVGIcon("svg/salary.svg"));

        btnCurrentAttendance.setIcon(new FlatSVGIcon("svg/calendar.svg"));
        btnLate.setIcon(new FlatSVGIcon("svg/late.svg"));
        btnOvertime.setIcon(new FlatSVGIcon("svg/clock.svg"));

        //search icon and placeholder
        setSearchIconPlaceHolder(txtFieldSearchEmployee);
        setSearchIconPlaceHolder(txtFieldSearchPayroll);
        setSearchIconPlaceHolder(txtFieldSearchAttendance);
        //validate salary field
        validateSalaryField();

        //overviews
        txtFieldTotalEmp.setText(String.valueOf(db.getTotalEmployees()));

        //remove tab headers
        removeTabsHeader();

        //this will display current time
        displayTime();

        //display employees table
        displayEmpTable();
        //display payrolls table
        displayPayrollTable();
        //display attendance
        displayAttendanceTable();

        //display payroll combo box
        displayEmpComBox();
        //disable update password first
        btnUpdatePassword.setEnabled(false);
        txtFieldPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                char[] passChar = txtFieldPassword.getPassword();
                String convertChar = new String(passChar);
                if (convertChar.trim().length() > 4) {
                    btnUpdatePassword.setEnabled(true);
                } else {
                    btnUpdatePassword.setEnabled(false);
                }
            }
        });

    }

    private void setSearchIconPlaceHolder(JTextField textField) {
        textField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search");
        textField.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("svg/search.svg"));
    }

    private void removeTabsHeader() {
        tabContentPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int tabPlacement, int horizonCount, int maxTabHeight) {
                return 0;
            }

            @Override
            protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {

            }

            @Override
            protected void installListeners() {
                // Don't install listeners - disables interaction
            }

            @Override
            protected void installKeyboardActions() {
                // Don't install keyboard actions - disables tab switching with keyboard
            }
        });
    }

    private void displayEmpTable() {
        String[] columns = {"Employee ID", "Profile", "Name", "Age", "Date of Birth", "Gender",
            "Status", "Contact Number", "Email", "Department", "Position", "Location Type", "Profile Path"};

        //setup default table model
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) {
                    return ImageIcon.class;
                }
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try (Statement stmt = db.getConnection().createStatement()) {
            String sql = "SELECT id, profile, name, age, date_of_birth, gender, status"
                    + ", department, position, location_type, contact_num, email FROM employees";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {

                int empId = rs.getInt("id");
                String empProfile = rs.getString("profile");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String dateOfBirth = rs.getDate("date_of_birth").toString();
                String gender = rs.getString("gender");
                String status = rs.getString("status");
                String contactNum = rs.getString("contact_num");
                String email = rs.getString("email");
                String department = rs.getString("department");
                String position = rs.getString("position");
                String locationType = rs.getString("location_type");

                ImageIcon profileImage = new ImageIcon("svg/default_profile.svg");

                if (empProfile != null) {
                    File profile = new File(empProfile);
                    if (profile.exists()) {
                        try {
                            BufferedImage originalImage = ImageIO.read(profile);
                            Image resizedImage = originalImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);

                            profileImage = new ImageIcon(resizedImage);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                model.addRow(new Object[]{
                    empId,
                    profileImage,
                    name,
                    age,
                    dateOfBirth,
                    gender,
                    status,
                    contactNum,
                    email,
                    department,
                    position,
                    locationType,
                    empProfile
                });
            }
        } catch (SQLException ex) {
            System.out.println("Something went wrong.");
            ex.printStackTrace();
        }

        tblEmployee.setModel(model);

        tblEmployee.getColumnModel().getColumn(12).setMinWidth(0);
        tblEmployee.getColumnModel().getColumn(12).setMaxWidth(0);
        tblEmployee.getColumnModel().getColumn(12).setWidth(0);
        tblEmployee.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        //clicking row
        tblEmployee.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblEmployee.getSelectedRow();

                if (row != -1) {
                    rowEmpIdInt = (Integer) tblEmployee.getValueAt(row, 0);
                    rowEmpProfile = (ImageIcon) tblEmployee.getValueAt(row, 1);
                    rowEmpName = (String) tblEmployee.getValueAt(row, 2);
                    rowEmpAge = (Integer) tblEmployee.getValueAt(row, 3);
                    rowEmpDateOfBirth = (String) tblEmployee.getValueAt(row, 4);
                    rowEmpGender = (String) tblEmployee.getValueAt(row, 5);
                    rowEmpStatus = (String) tblEmployee.getValueAt(row, 6);
                    rowEmpContactNum = (String) tblEmployee.getValueAt(row, 7);
                    rowEmpEmail = (String) tblEmployee.getValueAt(row, 8);
                    rowEmpDepartment = (String) tblEmployee.getValueAt(row, 9);
                    rowEmpPosition = (String) tblEmployee.getValueAt(row, 10);
                    rowEmpLocationType = (String) tblEmployee.getValueAt(row, 11);
                    //get the value of profile path
                    profilePath = (String) tblEmployee.getValueAt(row, 12);

                    if (euf == null || !euf.isDisplayable()) {
                        euf = new UpdateEmployeeForm(rowEmpIdInt, rowEmpProfile, rowEmpName, rowEmpAge, rowEmpDateOfBirth,
                                rowEmpGender, rowEmpStatus, rowEmpContactNum, rowEmpEmail,
                                rowEmpDepartment, rowEmpPosition, rowEmpLocationType, profilePath);
                        euf.setVisible(true);
                        disposeForm();
                    }
                }
            }

        });
        //for employee
        tblEmployee.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblEmployee.setRowHeight(100);
        searchField(txtFieldSearchEmployee, tblEmployee, model);
    }

    private void searchField(JTextField textField, JTable table, DefaultTableModel model) {
        TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<>(model);

        table.setRowSorter(rowSorter);

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = textField.getText() != null ? textField.getText() : "";

                if (searchText.trim().isEmpty()) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
                }
            }
        });
    }

    private void displayPayrollTable() {
        String[] columns = {"Payroll ID", "Employee ID", "Employee Name", "Salary"};

        DefaultTableModel model = new DefaultTableModel(columns, 0);

        String sql = "SELECT payroll.id AS payroll_id, payroll.employee_id, employees.name, payroll.salary "
                + "FROM payroll "
                + "INNER JOIN employees ON payroll.employee_id = employees.id";
        try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql); ResultSet rs = pstmt.executeQuery();) {

            while (rs.next()) {
                int payrollId = rs.getInt("payroll_id");
                int employeeId = rs.getInt("employee_id");
                String empName = rs.getString("name");
                double empSalary = rs.getDouble("salary");

                model.addRow(new Object[]{
                    payrollId,
                    employeeId,
                    empName,
                    empSalary
                });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        tblEmployeePayroll.setModel(model);
        tblEmployeePayroll.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        //for payroll
        searchField(txtFieldSearchPayroll, tblEmployeePayroll, model);

        tblEmployeePayroll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblEmployeePayroll.getSelectedRow();

                if (row != -1) {
                    rowEmpPayrollId = (Integer) tblEmployeePayroll.getValueAt(row, 0);
                    rowEmpId = (Integer) tblEmployeePayroll.getValueAt(row, 1);
                    rowEmpName = (String) tblEmployeePayroll.getValueAt(row, 2);
                    rowEmpSalary = (Double) tblEmployeePayroll.getValueAt(row, 3);

                    if (upf == null || !upf.isDisplayable()) {
                        upf = new UpdatePayrollForm(rowEmpPayrollId, rowEmpId, rowEmpName, rowEmpSalary);
                        upf.setVisible(true);
                        disposeForm();
                    }
                }
            }
        });
    }

    private void displayAttendanceTable() {
        String[] columns = {"ID", "Employee ID", "Date", "Check In", "Check Out", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        String sql = "SELECT * FROM attendance";

        try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                int employeeId = rs.getInt("employee_id");
                String date = rs.getString("date");
                Time checkIn = rs.getTime("check_in");
                Time checkOut = rs.getTime("check_out");
                String status = rs.getString("status");

                model.addRow(new Object[]{
                    id,
                    employeeId,
                    date,
                    checkIn,
                    checkOut,
                    status
                });

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        tblAttendance.setModel(model);
    }

    private void displayEmpComBox() {
        String sql = "SELECT e.id, e.name FROM employees e LEFT JOIN payroll p ON e.id = p.employee_id WHERE p.salary IS NULL";
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        cbEmployeeId.setModel(model);

        model.addElement("Select Employee ID");

        try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                model.addElement(+id + " - " + name);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        cbEmployeeId.setRenderer(new DefaultListCellRenderer() {
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                java.awt.Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (index == 0) {
                    comp.setEnabled(false);
                } else {
                    comp.setEnabled(true);
                }

                return comp;
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Sidebar = new javax.swing.JPanel();
        btnAttendance = new javax.swing.JButton();
        btnDashboard = new javax.swing.JButton();
        btnEmployee = new javax.swing.JButton();
        btnPayroll = new javax.swing.JButton();
        btnReports = new javax.swing.JButton();
        btnSettings = new javax.swing.JButton();
        btnLeaves = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        tabContentPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        lblText = new javax.swing.JLabel();
        txtFieldTotalEmp4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblActive = new javax.swing.JLabel();
        lblIconActiveEmp = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtFieldTotalEmp3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblTotalReports = new javax.swing.JLabel();
        lblIconTotalRep = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtFieldTotalEmp5 = new javax.swing.JLabel();
        txtFieldTotalEmp = new javax.swing.JLabel();
        lblEmployeesIcon = new javax.swing.JLabel();
        lblIconTotalEmp = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblEmployee = new javax.swing.JTable();
        txtFieldSearchEmployee = new javax.swing.JTextField();
        btnExportPDF = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnAdd = new javax.swing.JButton();
        btnDeleteLink = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        btnCurrentAttendance = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAttendance = new javax.swing.JTable();
        btnLate = new javax.swing.JButton();
        btnOvertime = new javax.swing.JButton();
        txtFieldSearchAttendance = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblAttendance1 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblEmployeePayroll = new javax.swing.JTable();
        btnAddEmpSalary = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtFieldSalary = new javax.swing.JTextField();
        txtFieldSearchPayroll = new javax.swing.JTextField();
        cbEmployeeId = new javax.swing.JComboBox<>();
        jLabel21 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblReports = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblLeaves = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        btnChangeTheme = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        txtFieldUsername = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtFieldPassword = new javax.swing.JPasswordField();
        btnUpdatePassword = new javax.swing.JButton();
        txtTime = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Employee Management System");

        Sidebar.setBackground(new java.awt.Color(102, 153, 255));
        Sidebar.setForeground(new java.awt.Color(0, 0, 0));

        btnAttendance.setBackground(new java.awt.Color(102, 153, 255));
        btnAttendance.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        btnAttendance.setForeground(java.awt.Color.white);
        btnAttendance.setText("Attendance");
        btnAttendance.setBorder(null);
        btnAttendance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAttendanceActionPerformed(evt);
            }
        });

        btnDashboard.setBackground(new java.awt.Color(102, 153, 255));
        btnDashboard.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        btnDashboard.setForeground(java.awt.Color.white);
        btnDashboard.setText("Dashboard");
        btnDashboard.setBorder(null);
        btnDashboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDashboardActionPerformed(evt);
            }
        });

        btnEmployee.setBackground(new java.awt.Color(102, 153, 255));
        btnEmployee.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        btnEmployee.setForeground(java.awt.Color.white);
        btnEmployee.setText("Employee");
        btnEmployee.setBorder(null);
        btnEmployee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmployeeActionPerformed(evt);
            }
        });

        btnPayroll.setBackground(new java.awt.Color(102, 153, 255));
        btnPayroll.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        btnPayroll.setForeground(java.awt.Color.white);
        btnPayroll.setText("Payroll");
        btnPayroll.setBorder(null);
        btnPayroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPayrollActionPerformed(evt);
            }
        });

        btnReports.setBackground(new java.awt.Color(102, 153, 255));
        btnReports.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        btnReports.setForeground(java.awt.Color.white);
        btnReports.setText("Reports");
        btnReports.setBorder(null);
        btnReports.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportsActionPerformed(evt);
            }
        });

        btnSettings.setBackground(new java.awt.Color(102, 153, 255));
        btnSettings.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        btnSettings.setForeground(java.awt.Color.white);
        btnSettings.setText("Settings");
        btnSettings.setBorder(null);
        btnSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSettingsActionPerformed(evt);
            }
        });

        btnLeaves.setBackground(new java.awt.Color(102, 153, 255));
        btnLeaves.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        btnLeaves.setForeground(java.awt.Color.white);
        btnLeaves.setText("Leaves");
        btnLeaves.setBorder(null);
        btnLeaves.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeavesActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("EMPLOYEE MANAGEMENT");

        jLabel4.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("SYSTEM");

        btnLogout.setBackground(new java.awt.Color(255, 102, 102));
        btnLogout.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setText("Logout");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout SidebarLayout = new javax.swing.GroupLayout(Sidebar);
        Sidebar.setLayout(SidebarLayout);
        SidebarLayout.setHorizontalGroup(
            SidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnDashboard, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnEmployee, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnAttendance, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnPayroll, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnReports, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnLeaves, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnSettings, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(SidebarLayout.createSequentialGroup()
                .addGroup(SidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SidebarLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(SidebarLayout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(jLabel4)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(SidebarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        SidebarLayout.setVerticalGroup(
            SidebarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SidebarLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(77, 77, 77)
                .addComponent(btnDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnEmployee, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnAttendance, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnPayroll, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnReports, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnLeaves, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 183, Short.MAX_VALUE)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53))
        );

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setForeground(new java.awt.Color(0, 0, 0));

        lblText.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        lblText.setText("Active Employees");

        txtFieldTotalEmp4.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Poppins", 1, 36)); // NOI18N
        jLabel7.setText("0");

        lblActive.setBackground(new java.awt.Color(102, 153, 255));

        lblIconActiveEmp.setForeground(new java.awt.Color(102, 153, 255));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(lblActive)
                        .addContainerGap(343, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(lblText)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblIconActiveEmp))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtFieldTotalEmp4)))
                        .addGap(15, 15, 15))))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtFieldTotalEmp4)
                .addGap(25, 25, 25))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblActive)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblText)
                    .addComponent(lblIconActiveEmp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addComponent(jLabel7))
        );

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setForeground(new java.awt.Color(0, 0, 0));

        jLabel5.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel5.setText("Total Reports");

        txtFieldTotalEmp3.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Poppins", 1, 36)); // NOI18N
        jLabel6.setText("0");

        lblTotalReports.setBackground(new java.awt.Color(102, 153, 255));

        lblIconTotalRep.setForeground(new java.awt.Color(102, 153, 255));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(lblTotalReports)
                        .addContainerGap(360, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblIconTotalRep))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtFieldTotalEmp3)))
                        .addGap(15, 15, 15))))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtFieldTotalEmp3)
                .addGap(25, 25, 25))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblTotalReports)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lblIconTotalRep))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addContainerGap())
        );

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setForeground(new java.awt.Color(0, 0, 0));

        jLabel8.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel8.setText("Total Employees");

        txtFieldTotalEmp5.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N

        txtFieldTotalEmp.setFont(new java.awt.Font("Poppins", 1, 36)); // NOI18N
        txtFieldTotalEmp.setText("0");

        lblEmployeesIcon.setBackground(new java.awt.Color(102, 153, 255));

        lblIconTotalEmp.setForeground(new java.awt.Color(102, 153, 255));

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(lblEmployeesIcon)
                        .addContainerGap(359, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblIconTotalEmp))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(txtFieldTotalEmp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtFieldTotalEmp5)))
                        .addGap(15, 15, 15))))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(lblEmployeesIcon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(lblIconTotalEmp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(txtFieldTotalEmp5)
                        .addGap(25, 25, 25))
                    .addComponent(txtFieldTotalEmp, javax.swing.GroupLayout.Alignment.TRAILING)))
        );

        jLabel18.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel18.setText("Dashboard");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(102, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(1187, Short.MAX_VALUE))
        );

        tabContentPane.addTab("tab1", jPanel1);

        tblEmployee.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        tblEmployee.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblEmployee);

        txtFieldSearchEmployee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFieldSearchEmployeeActionPerformed(evt);
            }
        });

        btnExportPDF.setBackground(new java.awt.Color(255, 102, 102));
        btnExportPDF.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        btnExportPDF.setText("Export to PDF");
        btnExportPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportPDFActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel1.setText("Employees");

        btnAdd.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnDeleteLink.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        btnDeleteLink.setText("Delete");
        btnDeleteLink.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteLinkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1179, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnAdd)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDeleteLink)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnExportPDF))
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtFieldSearchEmployee, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(84, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtFieldSearchEmployee)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnDeleteLink, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnExportPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 589, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        tabContentPane.addTab("tab2", jPanel2);

        jLabel9.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel9.setText("Attendance");

        btnCurrentAttendance.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        btnCurrentAttendance.setText("Current Attendance");
        btnCurrentAttendance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCurrentAttendanceActionPerformed(evt);
            }
        });

        tblAttendance.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tblAttendance);

        btnLate.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        btnLate.setText("Late");

        btnOvertime.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        btnOvertime.setText("Overtime");

        tblAttendance1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tblAttendance1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1187, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(btnCurrentAttendance, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnLate, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnOvertime, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(384, 384, 384)
                                .addComponent(txtFieldSearchAttendance, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(1040, 1040, 1040))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1187, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(75, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addGap(26, 26, 26)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCurrentAttendance, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLate, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOvertime, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFieldSearchAttendance, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE))
                .addGap(27, 27, 27)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 549, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 133, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 482, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(79, 79, 79))
        );

        tabContentPane.addTab("tab3", jPanel3);

        tblEmployeePayroll.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(tblEmployeePayroll);

        btnAddEmpSalary.setBackground(new java.awt.Color(102, 153, 255));
        btnAddEmpSalary.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        btnAddEmpSalary.setText("ADD EMPLOYEE SALARY");
        btnAddEmpSalary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddEmpSalaryActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel11.setText("Employee ID");

        jLabel12.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel12.setText("Salary");

        txtFieldSalary.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        txtFieldSearchPayroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFieldSearchPayrollActionPerformed(evt);
            }
        });

        cbEmployeeId.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbEmployeeId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbEmployeeIdActionPerformed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel21.setText("Payroll");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel11)
                                .addComponent(cbEmployeeId, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel12)
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addComponent(txtFieldSalary, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(btnAddEmpSalary, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtFieldSearchPayroll, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 1188, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(80, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel21)
                .addGap(3, 3, 3)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtFieldSalary, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cbEmployeeId, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtFieldSearchPayroll, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAddEmpSalary, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 559, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 695, Short.MAX_VALUE))
        );

        tabContentPane.addTab("tab4", jPanel4);

        jLabel20.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel20.setText("Reports");

        tblReports.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane5.setViewportView(tblReports);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 1206, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20)
                .addGap(40, 40, 40)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 595, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(703, Short.MAX_VALUE))
        );

        tabContentPane.addTab("tab5", jPanel5);

        jLabel19.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel19.setText("Leaves");

        tblLeaves.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane6.setViewportView(tblLeaves);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 1204, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(68, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel19)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 630, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(690, Short.MAX_VALUE))
        );

        tabContentPane.addTab("tab6", jPanel6);

        jLabel13.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel13.setText("Settings");

        jLabel14.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jLabel14.setText("Manage Account");

        btnChangeTheme.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        btnChangeTheme.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeThemeActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel15.setText("Enable Dark Mode");

        txtFieldUsername.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        txtFieldUsername.setForeground(new java.awt.Color(255, 255, 255));
        txtFieldUsername.setEnabled(false);

        jLabel16.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel16.setText("Username");

        jLabel17.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel17.setText("Change Password");

        btnUpdatePassword.setBackground(new java.awt.Color(102, 153, 255));
        btnUpdatePassword.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        btnUpdatePassword.setText("Update");
        btnUpdatePassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdatePasswordActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnChangeTheme, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel13)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17)
                    .addComponent(txtFieldUsername)
                    .addComponent(txtFieldPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdatePassword, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(1010, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addGap(33, 33, 33)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnChangeTheme, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFieldUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFieldPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnUpdatePassword, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(989, Short.MAX_VALUE))
        );

        tabContentPane.addTab("tab7", jPanel7);

        txtTime.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        txtTime.setText("jLabel9");

        jLabel3.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel3.setText("Welcome back, Admin");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(Sidebar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tabContentPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 1294, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTime)
                        .addGap(61, 61, 61))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTime)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabContentPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1416, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(Sidebar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(1472, 800));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    //display time
    private void displayTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        txtTime.setText(sdf.format(new Date()));

        Timer timer = new Timer(1000, (e) -> {
            txtTime.setText(sdf.format(new Date()));
        });

        timer.start();
    }

    private void setActiveBtn(JButton currentBtn) {

        btnDashboard.setEnabled(true);
        btnAttendance.setEnabled(true);
        btnEmployee.setEnabled(true);
        btnPayroll.setEnabled(true);
        btnReports.setEnabled(true);
        btnLeaves.setEnabled(true);
        btnSettings.setEnabled(true);

        currentBtn.setEnabled(false);
    }
    private void btnAttendanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAttendanceActionPerformed
        tabContentPane.setSelectedIndex(2);
        setActiveBtn(btnAttendance);
    }//GEN-LAST:event_btnAttendanceActionPerformed

    private void btnDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDashboardActionPerformed
        tabContentPane.setSelectedIndex(0);
        setActiveBtn(btnDashboard);
    }//GEN-LAST:event_btnDashboardActionPerformed

    private void btnEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmployeeActionPerformed
        tabContentPane.setSelectedIndex(1);
        setActiveBtn(btnEmployee);
    }//GEN-LAST:event_btnEmployeeActionPerformed

    private void btnPayrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPayrollActionPerformed
        tabContentPane.setSelectedIndex(3);
        setActiveBtn(btnPayroll);
    }//GEN-LAST:event_btnPayrollActionPerformed

    private void btnSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSettingsActionPerformed
        tabContentPane.setSelectedIndex(6);
        setActiveBtn(btnSettings);
        //get the admin info
        setAdminInfo();
    }//GEN-LAST:event_btnSettingsActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            goToLogin();
        }
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnReportsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportsActionPerformed
        tabContentPane.setSelectedIndex(4);
        setActiveBtn(btnReports);

    }//GEN-LAST:event_btnReportsActionPerformed

    private void btnLeavesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeavesActionPerformed
        tabContentPane.setSelectedIndex(5);
        setActiveBtn(btnLeaves);
    }//GEN-LAST:event_btnLeavesActionPerformed

    private void btnUpdatePasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdatePasswordActionPerformed
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        charArrayPass = txtFieldPassword.getPassword();
        passString = new String(charArrayPass);

        //validate password field
        if (passString.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You must enter new password field.", "Error", JOptionPane.ERROR);
            return;
        }
        try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql)) {
            System.out.println(passString);
            String hashedPassword = BCrypt.hashpw(passString, BCrypt.gensalt());
            pstmt.setString(1, hashedPassword);
            pstmt.setInt(2, userId);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Password successfully updated.");
            txtFieldPassword.setText("");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnUpdatePasswordActionPerformed

    private void btnChangeThemeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeThemeActionPerformed
        toggleTheme();
    }//GEN-LAST:event_btnChangeThemeActionPerformed

    private void cbEmployeeIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbEmployeeIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbEmployeeIdActionPerformed

    private void txtFieldSearchPayrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFieldSearchPayrollActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFieldSearchPayrollActionPerformed

    private void btnAddEmpSalaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddEmpSalaryActionPerformed
        empIdSalary = txtFieldSalary.getText();

        //validate textfield
        if (cbEmployeeId.getSelectedItem().equals("Select Employee ID") || cbEmployeeId.getItemCount() == 0 || empIdSalary.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please input Employee id and salary!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //get selected empId
        selectedStringEmpId = (String) cbEmployeeId.getSelectedItem();
        String[] parts = selectedStringEmpId.split(" - ");
        //convert empId to int
        int empId = Integer.parseInt(parts[0]);

        //convert to double
        double salary = Double.parseDouble(empIdSalary);

        payroll = new Payroll(empId, salary);

        JOptionPane.showMessageDialog(this,
                "Payroll of Employee Successfully added.", "Success",
                JOptionPane.INFORMATION_MESSAGE);

        db.addPayroll(empId, payroll);

        cbEmployeeId.setSelectedIndex(0);

        txtFieldSalary.setText(null);
        displayEmpComBox();
        displayPayrollTable();
    }//GEN-LAST:event_btnAddEmpSalaryActionPerformed

    private void btnCurrentAttendanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCurrentAttendanceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCurrentAttendanceActionPerformed

    private void btnDeleteLinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteLinkActionPerformed
        if (df == null || !df.isDisplayable()) {
            df = new DeleteEmployeeForm();
            df.setVisible(true);
            disposeForm();
        }
    }//GEN-LAST:event_btnDeleteLinkActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        if (adf == null || !adf.isDisplayable()) {
            adf = new AddingEmployeeForm();
            disposeForm();
            adf.setVisible(true);
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnExportPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportPDFActionPerformed

    }//GEN-LAST:event_btnExportPDFActionPerformed

    private void txtFieldSearchEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFieldSearchEmployeeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFieldSearchEmployeeActionPerformed
    private void toggleTheme() {
        isDarkMode = ThemeManager.isDarkMode();
        isDarkMode = !isDarkMode;
        ThemeManager.setDarkMode(isDarkMode);
        try {
            if (isDarkMode) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
                btnChangeTheme.setIcon(new FlatSVGIcon("svg/light.svg"));
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
                btnChangeTheme.setIcon(new FlatSVGIcon("svg/night.svg"));
            }
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void validateSalaryField() {
        txtFieldSalary.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char letter = e.getKeyChar();
                String text = txtFieldSalary.getText();

                if (Character.isDigit(letter)) {
                    return;
                }

                if (letter == '.' && !text.contains(".")) {
                    return;
                }

                e.consume();

            }
        });

    }

    private void setAdminInfo() {
        String sql = "SELECT id,username FROM users WHERE id = 1000";

        try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                userId = rs.getInt("id");
                System.out.println(userId);
                username = rs.getString("username");
            }
            txtFieldUsername.setText(username);
            txtFieldPassword.setText("");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }    //logout 

    private void goToLogin() {
        if (log == null || !log.isDisplayable()) {
            log = new Login();
            log.setVisible(true);
            disposeForm();
        }
    }

    private void disposeForm() {
        this.dispose();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                new MainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Sidebar;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAddEmpSalary;
    private javax.swing.JButton btnAttendance;
    private javax.swing.JButton btnChangeTheme;
    private javax.swing.JButton btnCurrentAttendance;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnDeleteLink;
    private javax.swing.JButton btnEmployee;
    private javax.swing.JButton btnExportPDF;
    private javax.swing.JButton btnLate;
    private javax.swing.JButton btnLeaves;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnOvertime;
    private javax.swing.JButton btnPayroll;
    private javax.swing.JButton btnReports;
    private javax.swing.JButton btnSettings;
    private javax.swing.JButton btnUpdatePassword;
    private javax.swing.JComboBox<String> cbEmployeeId;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JLabel lblActive;
    private javax.swing.JLabel lblEmployeesIcon;
    private javax.swing.JLabel lblIconActiveEmp;
    private javax.swing.JLabel lblIconTotalEmp;
    private javax.swing.JLabel lblIconTotalRep;
    private javax.swing.JLabel lblText;
    private javax.swing.JLabel lblTotalReports;
    private javax.swing.JTabbedPane tabContentPane;
    private javax.swing.JTable tblAttendance;
    private javax.swing.JTable tblAttendance1;
    private javax.swing.JTable tblEmployee;
    private javax.swing.JTable tblEmployeePayroll;
    private javax.swing.JTable tblLeaves;
    private javax.swing.JTable tblReports;
    private javax.swing.JPasswordField txtFieldPassword;
    private javax.swing.JTextField txtFieldSalary;
    private javax.swing.JTextField txtFieldSearchAttendance;
    private javax.swing.JTextField txtFieldSearchEmployee;
    private javax.swing.JTextField txtFieldSearchPayroll;
    private javax.swing.JLabel txtFieldTotalEmp;
    private javax.swing.JLabel txtFieldTotalEmp3;
    private javax.swing.JLabel txtFieldTotalEmp4;
    private javax.swing.JLabel txtFieldTotalEmp5;
    private javax.swing.JTextField txtFieldUsername;
    private javax.swing.JLabel txtTime;
    // End of variables declaration//GEN-END:variables
}
