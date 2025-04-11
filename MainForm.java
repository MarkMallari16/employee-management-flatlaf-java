package com.mycompany.firstflatlaf;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Mallari
 */
public class MainForm extends javax.swing.JFrame {

    //Database
    private Database db;
    //links
    private AddingEmployeeForm addingEmployeeForm;
    private DeleteForm df;
    private UpdateEmployeeForm empUpdateForm;
    private Login log;
    //employees data
    //for emp id
    private int rowEmpIdInt;
    private ImageIcon rowEmpProfile;
    private int rowEmpAge;
    private String rowEmpName, rowEmpDateOfBirth, rowEmpGender, rowEmpStatus, rowEmpContactNum,
            rowEmpEmail, rowEmpDepartment, rowEmpPosition, rowEmpLocationType;

    //for profile path
    private String profilePath;
    //Sorter
    private TableRowSorter<DefaultTableModel> rowSorter;

    /**
     * Creates new form MainForm
     */
    public MainForm() {
        initComponents();

        //database setup
        try {
            db = Database.getInstance();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        //sidebar icons
        btnDashboard.setIcon(new FlatSVGIcon("svg/dashboard.svg"));
        btnEmployee.setIcon(new FlatSVGIcon("svg/employee.svg"));
        btnAttendance.setIcon(new FlatSVGIcon("svg/attendance.svg"));
        btnPayroll.setIcon(new FlatSVGIcon("svg/payroll.svg"));

        btnReports.setIcon(new FlatSVGIcon("svg/reports.svg"));
        btnLeaves.setIcon(new FlatSVGIcon("svg/leaves.svg"));
        btnSettings.setIcon(new FlatSVGIcon("svg/settings.svg"));
        btnLogout.setIcon(new FlatSVGIcon("svg/logout.svg"));

        //remove tab headers
        removeTabsHeader();
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

                    if (empUpdateForm == null || !empUpdateForm.isDisplayable()) {
                        empUpdateForm = new UpdateEmployeeForm(rowEmpIdInt, rowEmpProfile, rowEmpName, rowEmpAge, rowEmpDateOfBirth,
                                rowEmpGender, rowEmpStatus, rowEmpContactNum, rowEmpEmail,
                                rowEmpDepartment, rowEmpPosition, rowEmpLocationType, profilePath);
                        empUpdateForm.setVisible(true);
                        disposeForm();
                    }
                }
            }

        });
        tblEmployee.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tblEmployee.setRowHeight(100);
        searchEmployee(model);
    }

    private void searchEmployee(DefaultTableModel model) {
        rowSorter = new TableRowSorter<>(model);
        tblEmployee.setRowSorter(rowSorter);

        txtFieldSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = txtFieldSearch.getText();

                if (searchText.trim().isEmpty()) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
                }
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
        jLabel3 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtFieldTotalEmp3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblTotalReports = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtFieldTotalEmp5 = new javax.swing.JLabel();
        txtFieldTotalEmp = new javax.swing.JLabel();
        lblEmployeesIcon = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblEmployee = new javax.swing.JTable();
        txtFieldSearch = new javax.swing.JTextField();
        btnExportPDF = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnAdd = new javax.swing.JButton();
        btnDeleteLink = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 169, Short.MAX_VALUE)
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

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(318, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFieldTotalEmp4)
                .addGap(15, 15, 15))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblActive)
                    .addComponent(lblText))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(lblText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addContainerGap())
        );

        jLabel3.setFont(new java.awt.Font("Poppins Black", 0, 36)); // NOI18N
        jLabel3.setText("Dashboard");

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setForeground(new java.awt.Color(0, 0, 0));

        jLabel5.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N
        jLabel5.setText("Total Reports");

        txtFieldTotalEmp3.setFont(new java.awt.Font("Poppins", 1, 24)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Poppins", 1, 36)); // NOI18N
        jLabel6.setText("0");

        lblTotalReports.setBackground(new java.awt.Color(102, 153, 255));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(342, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6)
                    .addComponent(txtFieldTotalEmp3))
                .addGap(15, 15, 15))
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotalReports)
                    .addComponent(jLabel5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(jLabel5)
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

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtFieldTotalEmp5)
                    .addComponent(txtFieldTotalEmp))
                .addGap(15, 15, 15))
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(lblEmployeesIcon))
                .addContainerGap(158, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(lblEmployeesIcon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(txtFieldTotalEmp5)
                        .addGap(25, 25, 25))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(txtFieldTotalEmp)
                        .addContainerGap())))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel3)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(45, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(511, Short.MAX_VALUE))
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

        txtFieldSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFieldSearchActionPerformed(evt);
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
                .addGap(30, 30, 30)
                .addComponent(jLabel1)
                .addContainerGap(1075, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(30, 30, 30)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(btnAdd)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnDeleteLink)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(btnExportPDF)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 1179, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(30, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1)
                .addContainerGap(682, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(70, 70, 70)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDeleteLink, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnExportPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGap(18, 18, 18)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 589, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(21, 21, 21)))
        );

        tabContentPane.addTab("tab2", jPanel2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1239, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 735, Short.MAX_VALUE)
        );

        tabContentPane.addTab("tab3", jPanel3);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1239, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 735, Short.MAX_VALUE)
        );

        tabContentPane.addTab("tab4", jPanel4);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1239, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 735, Short.MAX_VALUE)
        );

        tabContentPane.addTab("tab5", jPanel5);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1239, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 735, Short.MAX_VALUE)
        );

        tabContentPane.addTab("tab6", jPanel6);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1239, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 735, Short.MAX_VALUE)
        );

        tabContentPane.addTab("tab7", jPanel7);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(Sidebar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabContentPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(Sidebar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabContentPane)
                .addContainerGap())
        );

        setSize(new java.awt.Dimension(1472, 791));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAttendanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAttendanceActionPerformed
        tabContentPane.setSelectedIndex(2);
    }//GEN-LAST:event_btnAttendanceActionPerformed

    private void btnDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDashboardActionPerformed
        tabContentPane.setSelectedIndex(0);
    }//GEN-LAST:event_btnDashboardActionPerformed

    private void btnEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmployeeActionPerformed
        tabContentPane.setSelectedIndex(1);
    }//GEN-LAST:event_btnEmployeeActionPerformed

    private void btnPayrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPayrollActionPerformed
        tabContentPane.setSelectedIndex(3);
    }//GEN-LAST:event_btnPayrollActionPerformed

    private void btnSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSettingsActionPerformed
        tabContentPane.setSelectedIndex(6);
    }//GEN-LAST:event_btnSettingsActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            goToLogin();
        }
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnReportsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportsActionPerformed
        tabContentPane.setSelectedIndex(4);

    }//GEN-LAST:event_btnReportsActionPerformed

    private void btnLeavesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeavesActionPerformed
        tabContentPane.setSelectedIndex(5);

    }//GEN-LAST:event_btnLeavesActionPerformed

    private void txtFieldSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFieldSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFieldSearchActionPerformed

    private void btnExportPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportPDFActionPerformed

    }//GEN-LAST:event_btnExportPDFActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        if (addingEmployeeForm == null || !addingEmployeeForm.isDisplayable()) {
            addingEmployeeForm = new AddingEmployeeForm();
            disposeForm();
            addingEmployeeForm.setVisible(true);
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnDeleteLinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteLinkActionPerformed
        if (df == null || !df.isDisplayable()) {
            df = new DeleteForm();
            df.setVisible(true);
            disposeForm();
        }
    }//GEN-LAST:event_btnDeleteLinkActionPerformed
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
                new MainForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Sidebar;
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAttendance;
    private javax.swing.JButton btnDashboard;
    private javax.swing.JButton btnDeleteLink;
    private javax.swing.JButton btnEmployee;
    private javax.swing.JButton btnExportPDF;
    private javax.swing.JButton btnLeaves;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnPayroll;
    private javax.swing.JButton btnReports;
    private javax.swing.JButton btnSettings;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
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
    private javax.swing.JLabel lblActive;
    private javax.swing.JLabel lblEmployeesIcon;
    private javax.swing.JLabel lblText;
    private javax.swing.JLabel lblTotalReports;
    private javax.swing.JTabbedPane tabContentPane;
    private javax.swing.JTable tblEmployee;
    private javax.swing.JTextField txtFieldSearch;
    private javax.swing.JLabel txtFieldTotalEmp;
    private javax.swing.JLabel txtFieldTotalEmp3;
    private javax.swing.JLabel txtFieldTotalEmp4;
    private javax.swing.JLabel txtFieldTotalEmp5;
    // End of variables declaration//GEN-END:variables
}
