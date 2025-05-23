package com.mycompany.firstflatlaf;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import java.awt.BorderLayout;
import java.awt.Dimension;
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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.mindrot.jbcrypt.BCrypt;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import java.awt.Desktop;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import javax.swing.JComboBox;

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
    private char[] charArrayConfirmPass;
    private String passString, passConfirmString;
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
        btnExportEmployee.setIcon(new FlatSVGIcon("svg/pdf.svg"));

        btnAddEmpSalary.setIcon(new FlatSVGIcon("svg/salary.svg"));

//        btnCurrentAttendance.setIcon(new FlatSVGIcon("svg/calendar.svg"));
//        btnLate.setIcon(new FlatSVGIcon("svg/late.svg"));
//        btnOvertime.setIcon(new FlatSVGIcon("svg/clock.svg"));
        //search icon and placeholder
        setSearchIconPlaceHolder(txtFieldSearchEmployee);
        setSearchIconPlaceHolder(txtFieldSearchPayroll);
        setSearchIconPlaceHolder(txtFieldSearchAttendance);
        setSearchIconPlaceHolder(txtFieldLeavesSearch);
        //validate salary field
        validateSalaryField();

        //overviews
        txtFieldTotalEmp.setText(String.valueOf(db.getTotalEmployees()));

        //charts
        displayBarChart();

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
        //display employee leaves 
        displayEmpLeaves();

        //display attendance combo box
        String[] items = {"On Time", "Late", "Overtime"};
        displayComboBox(items, cbAttendanceStatus);

        //display payroll combo box
        displayEmpComBox();

        //show/hide password
        txtFieldPassword.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true");
        txtFieldConfirmPassword.putClientProperty(FlatClientProperties.STYLE, "showRevealButton:true");
        //disable update password first
        btnUpdatePassword.setEnabled(false);
        //checking password
        confirmingPass();
    }

    private void confirmingPass() {
        txtFieldConfirmPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                char[] passChar = txtFieldPassword.getPassword();
                char[] confirmPassChar = txtFieldConfirmPassword.getPassword();

                String convertChar = new String(passChar);
                String convertConfirmPassChar = new String(confirmPassChar);

                if (convertChar.equals(convertConfirmPassChar) && convertConfirmPassChar.trim().length() > 4) {
                    btnUpdatePassword.setEnabled(true);
                } else {
                    btnUpdatePassword.setEnabled(false);
                }
            }
        });
    }

    private void displayBarChart() {
        //dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(db.getTotalEmployees(), "Employees", "IT");
        dataset.addValue(24, "Employees", "Marketing");
        dataset.addValue(2, "Employees", "HR");
        dataset.addValue(26, "Employees", "Finance");

        JFreeChart barChart = ChartFactory.createBarChart("Employees by Department",
                "Department", "Number of Employees", dataset);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(580, 490));

        //remove ui first
        panelBarChart1.removeAll();
        //set layout 
        panelBarChart1.setLayout(new BorderLayout());
        //adding bar chart
        panelBarChart1.add(chartPanel, BorderLayout.CENTER);
        //revalidates ui
        panelBarChart1.revalidate();
        panelBarChart1.repaint();

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

        String sql = "SELECT id, profile, name, age, date_of_birth, gender, status"
                + ", department, position, location_type, contact_num, email FROM employees";

        try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery(sql);

            //display all employees
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
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)^" + searchText));
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
        searchField(txtFieldSearchAttendance, tblAttendance, model);

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

    private void displayEmpLeaves() {
        String[] columns = {"ID", "Employee ID", "Employee Name", "Department", "position", "Leave Type", "Start Date", "End Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        searchField(txtFieldLeavesSearch, tblLeaves, model);

        String sql = "SELECT l.leave_id, l.employee_id , e.name, e.department, e.position, l.leave_type, "
                + "l.start_date, l.end_date,l.status "
                + "FROM leaves l INNER JOIN employees e ON l.employee_id = e.id";

        try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("leave_id");
                int empId = rs.getInt("employee_id");
                String empName = rs.getString("name");
                String empDepartment = rs.getString("department");
                String empPosition = rs.getString("position");
                String leaveType = rs.getString("leave_type");
                String startDate = rs.getString("start_date");
                String endDate = rs.getString("end_date");
                String status = rs.getString("status");

                model.addRow(new Object[]{
                    id,
                    empId,
                    empName,
                    empDepartment,
                    empPosition,
                    leaveType,
                    startDate,
                    endDate,
                    status
                });
            }

            tblLeaves.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
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
        txtTime = new javax.swing.JLabel();
        tabContentPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        panelActiveEmp = new javax.swing.JPanel();
        lblText = new javax.swing.JLabel();
        txtFieldTotalEmp4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblActive = new javax.swing.JLabel();
        lblIconActiveEmp = new javax.swing.JLabel();
        panelTotalRep = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtFieldTotalEmp3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblTotalReports = new javax.swing.JLabel();
        lblIconTotalRep = new javax.swing.JLabel();
        panelEmployee = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtFieldTotalEmp5 = new javax.swing.JLabel();
        txtFieldTotalEmp = new javax.swing.JLabel();
        lblEmployeesIcon = new javax.swing.JLabel();
        lblIconTotalEmp = new javax.swing.JLabel();
        panelLine = new javax.swing.JPanel();
        panelBarChart1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblEmployee = new javax.swing.JTable();
        txtFieldSearchEmployee = new javax.swing.JTextField();
        btnExportEmployee = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnAdd = new javax.swing.JButton();
        btnDeleteLink = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAttendance = new javax.swing.JTable();
        txtFieldSearchAttendance = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblAttendance1 = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        cbAttendanceStatus = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblEmployeePayroll = new javax.swing.JTable();
        btnAddEmpSalary = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtFieldSalary = new javax.swing.JTextField();
        txtFieldSearchPayroll = new javax.swing.JTextField();
        cbEmployeeId = new javax.swing.JComboBox<>();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblReports = new javax.swing.JTable();
        btnGenerateReport = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblLeaves = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        txtFieldLeavesSearch = new javax.swing.JTextField();
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
        txtFieldConfirmPassword = new javax.swing.JPasswordField();
        jLabel18 = new javax.swing.JLabel();
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 230, Short.MAX_VALUE)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53))
        );

        txtTime.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        txtTime.setText("jLabel9");

        panelActiveEmp.setBackground(new java.awt.Color(255, 255, 255));
        panelActiveEmp.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(109, 113, 105), 1, true));
        panelActiveEmp.setForeground(new java.awt.Color(0, 0, 0));
        panelActiveEmp.setPreferredSize(new java.awt.Dimension(420, 130));

        lblText.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        lblText.setText("Active Employees");

        txtFieldTotalEmp4.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Poppins", 1, 36)); // NOI18N
        jLabel7.setText("0");

        lblActive.setBackground(new java.awt.Color(102, 153, 255));

        lblIconActiveEmp.setForeground(new java.awt.Color(102, 153, 255));

        javax.swing.GroupLayout panelActiveEmpLayout = new javax.swing.GroupLayout(panelActiveEmp);
        panelActiveEmp.setLayout(panelActiveEmpLayout);
        panelActiveEmpLayout.setHorizontalGroup(
            panelActiveEmpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelActiveEmpLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(panelActiveEmpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelActiveEmpLayout.createSequentialGroup()
                        .addComponent(lblActive)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelActiveEmpLayout.createSequentialGroup()
                        .addGroup(panelActiveEmpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelActiveEmpLayout.createSequentialGroup()
                                .addComponent(lblText)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 165, Short.MAX_VALUE)
                                .addComponent(lblIconActiveEmp))
                            .addGroup(panelActiveEmpLayout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtFieldTotalEmp4)))
                        .addGap(15, 15, 15))))
        );
        panelActiveEmpLayout.setVerticalGroup(
            panelActiveEmpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelActiveEmpLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtFieldTotalEmp4)
                .addGap(25, 25, 25))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelActiveEmpLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(lblActive)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelActiveEmpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblText)
                    .addComponent(lblIconActiveEmp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                .addComponent(jLabel7))
        );

        panelTotalRep.setBackground(new java.awt.Color(255, 255, 255));
        panelTotalRep.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(109, 113, 105), 1, true));
        panelTotalRep.setForeground(new java.awt.Color(255, 255, 255));

        jLabel5.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel5.setText("Total Reports");

        txtFieldTotalEmp3.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Poppins", 1, 36)); // NOI18N
        jLabel6.setText("0");

        lblTotalReports.setBackground(new java.awt.Color(102, 153, 255));

        lblIconTotalRep.setForeground(new java.awt.Color(102, 153, 255));

        javax.swing.GroupLayout panelTotalRepLayout = new javax.swing.GroupLayout(panelTotalRep);
        panelTotalRep.setLayout(panelTotalRepLayout);
        panelTotalRepLayout.setHorizontalGroup(
            panelTotalRepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTotalRepLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panelTotalRepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTotalRepLayout.createSequentialGroup()
                        .addComponent(lblTotalReports)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTotalRepLayout.createSequentialGroup()
                        .addGroup(panelTotalRepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelTotalRepLayout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 221, Short.MAX_VALUE)
                                .addComponent(lblIconTotalRep))
                            .addGroup(panelTotalRepLayout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtFieldTotalEmp3)))
                        .addGap(15, 15, 15))))
        );
        panelTotalRepLayout.setVerticalGroup(
            panelTotalRepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTotalRepLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtFieldTotalEmp3)
                .addGap(25, 25, 25))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTotalRepLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblTotalReports)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelTotalRepLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lblIconTotalRep))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addContainerGap())
        );

        panelEmployee.setBackground(new java.awt.Color(255, 255, 255));
        panelEmployee.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(109, 113, 105), 1, true));
        panelEmployee.setForeground(new java.awt.Color(255, 255, 255));
        panelEmployee.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        panelEmployee.setPreferredSize(new java.awt.Dimension(420, 130));

        jLabel8.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel8.setText("Total Employees");

        txtFieldTotalEmp5.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N

        txtFieldTotalEmp.setFont(new java.awt.Font("Poppins", 1, 36)); // NOI18N
        txtFieldTotalEmp.setText("0");

        lblEmployeesIcon.setBackground(new java.awt.Color(102, 153, 255));

        lblIconTotalEmp.setForeground(new java.awt.Color(102, 153, 255));

        javax.swing.GroupLayout panelEmployeeLayout = new javax.swing.GroupLayout(panelEmployee);
        panelEmployee.setLayout(panelEmployeeLayout);
        panelEmployeeLayout.setHorizontalGroup(
            panelEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEmployeeLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(panelEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelEmployeeLayout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(lblEmployeesIcon)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEmployeeLayout.createSequentialGroup()
                        .addGroup(panelEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelEmployeeLayout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 192, Short.MAX_VALUE)
                                .addComponent(lblIconTotalEmp))
                            .addGroup(panelEmployeeLayout.createSequentialGroup()
                                .addComponent(txtFieldTotalEmp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtFieldTotalEmp5)))
                        .addGap(15, 15, 15))))
        );
        panelEmployeeLayout.setVerticalGroup(
            panelEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEmployeeLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(lblEmployeesIcon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(lblIconTotalEmp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelEmployeeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelEmployeeLayout.createSequentialGroup()
                        .addComponent(txtFieldTotalEmp5)
                        .addGap(25, 25, 25))
                    .addComponent(txtFieldTotalEmp, javax.swing.GroupLayout.Alignment.TRAILING)))
        );

        panelLine.setBackground(new java.awt.Color(255, 255, 255));
        panelLine.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(109, 113, 105), 1, true));
        panelLine.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panelLineLayout = new javax.swing.GroupLayout(panelLine);
        panelLine.setLayout(panelLineLayout);
        panelLineLayout.setHorizontalGroup(
            panelLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 568, Short.MAX_VALUE)
        );
        panelLineLayout.setVerticalGroup(
            panelLineLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 496, Short.MAX_VALUE)
        );

        panelBarChart1.setBackground(new java.awt.Color(255, 255, 255));
        panelBarChart1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(109, 113, 105), 1, true));

        javax.swing.GroupLayout panelBarChart1Layout = new javax.swing.GroupLayout(panelBarChart1);
        panelBarChart1.setLayout(panelBarChart1Layout);
        panelBarChart1Layout.setHorizontalGroup(
            panelBarChart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelBarChart1Layout.setVerticalGroup(
            panelBarChart1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(panelBarChart1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelLine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(panelEmployee, javax.swing.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelTotalRep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelActiveEmp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelActiveEmp, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                    .addComponent(panelTotalRep, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelEmployee, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelLine, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelBarChart1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(664, 664, 664))
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

        btnExportEmployee.setBackground(new java.awt.Color(255, 102, 102));
        btnExportEmployee.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        btnExportEmployee.setText("Export to PDF");
        btnExportEmployee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportEmployeeActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N

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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1258, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnAdd)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDeleteLink)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnExportEmployee))
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtFieldSearchEmployee, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(32, 32, 32)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDeleteLink, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExportEmployee, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFieldSearchEmployee, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 633, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(659, Short.MAX_VALUE))
        );

        tabContentPane.addTab("tab2", jPanel2);

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

        txtFieldSearchAttendance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFieldSearchAttendanceActionPerformed(evt);
            }
        });

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

        cbAttendanceStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbAttendanceStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbAttendanceStatusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(cbAttendanceStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 24, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addGap(0, 12, Short.MAX_VALUE)
                .addComponent(cbAttendanceStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1187, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtFieldSearchAttendance, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1253, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFieldSearchAttendance, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 616, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 112, Short.MAX_VALUE)
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
        btnAddEmpSalary.setText("ADD PAYROLL");
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

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(214, 214, 214)
                                .addComponent(jLabel12))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(cbEmployeeId, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtFieldSalary, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnAddEmpSalary, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtFieldSearchPayroll, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap(26, Short.MAX_VALUE)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 1267, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtFieldSalary, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cbEmployeeId, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFieldSearchPayroll, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddEmpSalary, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 591, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(669, Short.MAX_VALUE))
        );

        tabContentPane.addTab("tab4", jPanel4);

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

        btnGenerateReport.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        btnGenerateReport.setText("Generate Report");
        btnGenerateReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateReportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 1278, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGenerateReport, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(btnGenerateReport, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 632, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(661, Short.MAX_VALUE))
        );

        tabContentPane.addTab("tab5", jPanel5);

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

        jButton1.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jButton1.setText("Button 3");

        jButton2.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jButton2.setText("Button 1");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        jButton3.setText("Button 2");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtFieldLeavesSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 1282, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFieldLeavesSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 623, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(668, Short.MAX_VALUE))
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
        jLabel17.setText("Password");

        txtFieldPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFieldPasswordActionPerformed(evt);
            }
        });

        btnUpdatePassword.setBackground(new java.awt.Color(102, 153, 255));
        btnUpdatePassword.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        btnUpdatePassword.setText("Update");
        btnUpdatePassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdatePasswordActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel18.setText("Confirm Password");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18)
                    .addComponent(txtFieldConfirmPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(btnChangeTheme, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14)
                        .addComponent(jLabel13)
                        .addComponent(jLabel15)
                        .addComponent(jLabel16)
                        .addComponent(jLabel17)
                        .addComponent(txtFieldUsername)
                        .addComponent(txtFieldPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnUpdatePassword, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(1037, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFieldConfirmPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnUpdatePassword, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(923, Short.MAX_VALUE))
        );

        tabContentPane.addTab("tab7", jPanel7);

        jLabel3.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel3.setText("Welcome back, Admin");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(Sidebar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(tabContentPane)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTime)
                        .addGap(15, 15, 15))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTime)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabContentPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1422, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(Sidebar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(1554, 843));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    //display combo box
    private void displayComboBox(String[] items, JComboBox cb) {
        cb.removeAllItems();
        for (String item : items) {
            cb.addItem(item);
        }
    }

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
        charArrayConfirmPass = txtFieldConfirmPassword.getPassword();
        passString = new String(charArrayPass);
        passConfirmString = new String(charArrayConfirmPass);

        //validate password field
        if (passString.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You must enter new password field.", "Error", JOptionPane.ERROR);
            return;
        }
        try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql)) {
            System.out.println(passConfirmString);
            //hashing password;
            String hashedPassword = BCrypt.hashpw(passConfirmString, BCrypt.gensalt());

            //setting password;
            pstmt.setString(1, hashedPassword);
            pstmt.setInt(2, userId);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Password successfully updated.");
            txtFieldPassword.setText("");
            txtFieldConfirmPassword.setText("");
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

    private void btnExportEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportEmployeeActionPerformed
        String[] columns = {"Employee ID", "Name", "Age", "Date of Birth", "Gender",
            "Status", "Contact Number", "Email", "Department", "Position", "Location Type"};

        String filePath = "employees.pdf";
        String logoPath = "C:\\Users\\markm\\OneDrive\\Documents\\NetBeansProjects\\BasicCRUD\\src\\main\\resources\\svg\\logo.png";

        //writer
        PdfWriter writer = null;

        String sql = "SELECT id, name, age, date_of_birth, gender, status"
                + ", department, position, location_type, contact_num, email FROM employees";

        try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql)) {
            writer = new PdfWriter(filePath);
            //pdf doc
            PdfDocument pdf = new PdfDocument(writer);

            //document
            try (Document document = new Document(pdf, PageSize.A4.rotate());) {
                //logo employee management
                ImageData imageData = ImageDataFactory.create(logoPath);
                com.itextpdf.layout.element.Image logo = new com.itextpdf.layout.element.Image(imageData);
                logo.setWidth(80);
                logo.setHeight(60);

                document.add(logo);
                document.add(new Paragraph("Employees")
                        .setMarginTop(20)
                        .setFontSize(18)
                        .setBold());

                float[] columnWidths = new float[columns.length];

                for (int i = 0; i < columns.length; i++) {
                    columnWidths[i] = 2;
                }

                Table table = new Table(columnWidths);

                for (String column : columns) {
                    table.addCell(new Cell().add(new Paragraph(column)));
                }

                ResultSet rs = pstmt.executeQuery();

                //fetching emp datas 
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    int age = rs.getInt("age");
                    Date dateOfBirth = rs.getDate("date_of_birth");
                    String gender = rs.getString("gender");
                    String status = rs.getString("status");
                    String department = rs.getString("department");
                    String position = rs.getString("position");
                    String locationType = rs.getString("location_type");
                    String contactNum = rs.getString("contact_num");
                    String email = rs.getString("email");

                    table.addCell(new Cell().add(new Paragraph(String.valueOf(id))));
                    table.addCell(new Cell().add(new Paragraph(name)));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(age))));
                    table.addCell(new Cell().add(new Paragraph(dateOfBirth.toString())));
                    table.addCell(new Cell().add(new Paragraph(gender)));
                    table.addCell(new Cell().add(new Paragraph(status)));
                    table.addCell(new Cell().add(new Paragraph(contactNum)));
                    table.addCell(new Cell().add(new Paragraph(email)));
                    table.addCell(new Cell().add(new Paragraph(department)));
                    table.addCell(new Cell().add(new Paragraph(position)));
                    table.addCell(new Cell().add(new Paragraph(locationType)));
                }

                //adding to pdf document
                document.add(table);
            }
            File fileExported = new File(filePath);
            if (fileExported.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(fileExported);
                } else {
                    System.out.println("Error");
                }
            } else {
                System.out.println("Pdf file not exists.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnExportEmployeeActionPerformed

    private void txtFieldSearchEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFieldSearchEmployeeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFieldSearchEmployeeActionPerformed

    private void txtFieldSearchAttendanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFieldSearchAttendanceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFieldSearchAttendanceActionPerformed

    private void cbAttendanceStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbAttendanceStatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbAttendanceStatusActionPerformed

    private void txtFieldPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFieldPasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFieldPasswordActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnGenerateReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateReportActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGenerateReportActionPerformed
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
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnDeleteLink;
    private javax.swing.JButton btnEmployee;
    private javax.swing.JButton btnExportEmployee;
    private javax.swing.JButton btnGenerateReport;
    private javax.swing.JButton btnLeaves;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnPayroll;
    private javax.swing.JButton btnReports;
    private javax.swing.JButton btnSettings;
    private javax.swing.JButton btnUpdatePassword;
    private javax.swing.JComboBox<String> cbAttendanceStatus;
    private javax.swing.JComboBox<String> cbEmployeeId;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
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
    private javax.swing.JPanel panelActiveEmp;
    private javax.swing.JPanel panelBarChart1;
    private javax.swing.JPanel panelEmployee;
    private javax.swing.JPanel panelLine;
    private javax.swing.JPanel panelTotalRep;
    private javax.swing.JTabbedPane tabContentPane;
    private javax.swing.JTable tblAttendance;
    private javax.swing.JTable tblAttendance1;
    private javax.swing.JTable tblEmployee;
    private javax.swing.JTable tblEmployeePayroll;
    private javax.swing.JTable tblLeaves;
    private javax.swing.JTable tblReports;
    private javax.swing.JPasswordField txtFieldConfirmPassword;
    private javax.swing.JTextField txtFieldLeavesSearch;
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
