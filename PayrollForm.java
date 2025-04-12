/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.firstflatlaf;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Mallari
 */
public class PayrollForm extends javax.swing.JFrame {

    //temporary database
    private Database db;

    //hold data
    private String selectedStringEmpId, empIdSalary;
    //for link
    private Payroll payroll;
    //for row
    private int rowEmpPayrollId;
    private int rowEmpId;
    private String rowEmpName;
    private double rowEmpSalary;

    //dashboard
    private Dashboard gui;
    //for update form
    private UpdatePayrollForm upf;
    //for searching payroll employee
    private TableRowSorter<DefaultTableModel> rowSorter;

    public PayrollForm() {
        initComponents();

        try {
            db = Database.getInstance();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        displayPayrollTable();

        //placeholder
        txtFieldSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Search");
        //icons
        txtFieldSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("svg/search.svg"));
        btnAddSalary.setIcon(new FlatSVGIcon("svg/salary.svg"));
        btnBackToDashboard.setIcon(new FlatSVGIcon("svg/back.svg"));

        txtFieldSalary.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String text = txtFieldSalary.getText();

                if (Character.isDigit(c)) {
                    return;
                }

                if (c == '.' && !text.contains(".")) {
                    return;
                }

                e.consume();
            }
        });
        
        cbEmployeeId.removeAllItems();

        //display data of employees in combo box
        displayEmpComBox();
    }

    private void displayEmpComBox() {
        //display only no employee payrolls
        String sql = "SELECT e.id FROM employees e LEFT JOIN payroll p ON e.id = p.employee_id WHERE p.salary IS NULL";
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        cbEmployeeId.setModel(model);

        model.addElement("Select Employee ID");

        try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                model.addElement(id + "");
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

    private void displayPayrollTable() {
        String[] columns = {"Payroll ID", "Employee ID", "Employee Name", "Salary"};

        DefaultTableModel model = new DefaultTableModel(columns, 0);

        String sql = "SELECT payroll.id AS payroll_id, payroll.employee_id, employees.name, payroll.salary "
                + "FROM payroll "
                + "INNER JOIN employees ON payroll.employee_id = employees.id";
        try (PreparedStatement pstmt = db.getConnection().prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

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

        searchEmployeePayroll(model);

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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblEmployeePayroll = new javax.swing.JTable();
        btnAddSalary = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtFieldSalary = new javax.swing.JTextField();
        btnBackToDashboard = new javax.swing.JButton();
        txtFieldSearch = new javax.swing.JTextField();
        cbEmployeeId = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Employee Payroll");
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jLabel1.setText("Employee Payroll");

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
        jScrollPane1.setViewportView(tblEmployeePayroll);

        btnAddSalary.setBackground(new java.awt.Color(102, 153, 255));
        btnAddSalary.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        btnAddSalary.setText("ADD EMPLOYEE SALARY");
        btnAddSalary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddSalaryActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel2.setText("Employee ID");

        jLabel3.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jLabel3.setText("Salary");

        txtFieldSalary.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N

        btnBackToDashboard.setFont(new java.awt.Font("Poppins", 1, 12)); // NOI18N
        btnBackToDashboard.setText("Back to Dashboard");
        btnBackToDashboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackToDashboardActionPerformed(evt);
            }
        });

        txtFieldSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFieldSearchActionPerformed(evt);
            }
        });

        cbEmployeeId.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(cbEmployeeId, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtFieldSalary, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnAddSalary, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(9, 9, 9))))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1328, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnBackToDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(61, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(btnBackToDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtFieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAddSalary, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
                            .addComponent(txtFieldSalary, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cbEmployeeId, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(65, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(1472, 791));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddSalaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddSalaryActionPerformed
        empIdSalary = txtFieldSalary.getText();

        //validate textfield 
        if (cbEmployeeId.getSelectedItem().equals("Select Employee ID") || cbEmployeeId.getItemCount() == 0 || empIdSalary.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please input Employee id and salary!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //get selected empId
        selectedStringEmpId = (String) cbEmployeeId.getSelectedItem();
        //convert empId to int
        int empId = Integer.parseInt(selectedStringEmpId);

        //convert to double
        double salary = Double.parseDouble(empIdSalary);

        payroll = new Payroll(empId, salary);

        JOptionPane.showMessageDialog(this,
                "Payroll of Employee Successfully added.", "Success",
                JOptionPane.INFORMATION_MESSAGE);

        db.addPayroll(empId, payroll);

        txtFieldSalary.setText(null);

        displayPayrollTable();


    }//GEN-LAST:event_btnAddSalaryActionPerformed
    private void disposeForm() {
        this.dispose();
    }

    private void searchEmployeePayroll(DefaultTableModel model) {
        rowSorter = new TableRowSorter<>(model);

        tblEmployeePayroll.setRowSorter(rowSorter);

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
    private void btnBackToDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackToDashboardActionPerformed
        if (gui == null || !gui.isDisplayable()) {
            gui = new Dashboard();
            gui.setVisible(true);
            disposeForm();
        }
    }//GEN-LAST:event_btnBackToDashboardActionPerformed

    private void txtFieldSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFieldSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFieldSearchActionPerformed

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
            java.util.logging.Logger.getLogger(PayrollForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PayrollForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PayrollForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PayrollForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PayrollForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddSalary;
    private javax.swing.JButton btnBackToDashboard;
    private javax.swing.JComboBox<String> cbEmployeeId;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblEmployeePayroll;
    private javax.swing.JTextField txtFieldSalary;
    private javax.swing.JTextField txtFieldSearch;
    // End of variables declaration//GEN-END:variables
}
