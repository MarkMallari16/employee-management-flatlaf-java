package com.mycompany.firstflatlaf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class Database {

    private final String URL = "jdbc:mysql://localhost:3306/db_employee_management";
    private final String USER = "root";
    private final String PASSWORD = "!M@rkcc16";
    private static HashMap<Integer, Employee> employeesDb = new HashMap<>();
    private static HashMap<Integer, Payroll> payrollDb = new HashMap<>();

    public Database() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
//            Connection conn = DriverManager.getConnection(url, user, password);
//
//            if (conn != null) {
//                System.out.println("Connected");
//                conn.close();
//            } else {
//                System.out.println("MySQL connection Failed.");
//            }

        } catch (ClassNotFoundException ex) {
            System.out.println("MYSQL Driver not found");
            ex.printStackTrace();
        }
    }

    public void addEmployee(Employee employee) {
//        employeesDb.put(id, employee);
//
//        System.out.println("Successfully added!");

        String sql = "INSERT INTO employees (profile, name, age, date_of_birth, gender, status, department, position, location_type, contact_num, email) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, employee.getProfile());
            pstmt.setString(2, employee.getName());
            pstmt.setInt(3, employee.getAge());
            pstmt.setDate(4, java.sql.Date.valueOf(employee.getDateOfBirth()));
            pstmt.setString(5, employee.getGender());
            pstmt.setString(6, employee.getStatus());
            pstmt.setString(7, employee.getDepartment());
            pstmt.setString(8, employee.getPosition());
            pstmt.setString(9, employee.getLocationType());
            pstmt.setString(10, employee.getContactNum());
            pstmt.setString(11, employee.getEmail());

            int rowInserted = pstmt.executeUpdate();

            if (rowInserted > 0) {
                System.out.println("Employee successfully added.");
            } else {
                System.out.println("Failed to add employee.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public HashMap<Integer, Employee> getEmployee() {
        return employeesDb;
    }

    public boolean isEmpIdExists(int id) {
        return employeesDb.containsKey(id);
    }

    public void removeEmployee(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowDeleted = pstmt.executeUpdate();

            if (rowDeleted > 0) {
                System.out.println("Employee successfully deleted");
            } else {
                System.out.println("Employee with id not found");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateEmployee(int id, Employee employee) {
        employeesDb.replace(id, employee);
    }

    public int getTotalEmployees() {
        return employeesDb.size();
    }

    public void addPayroll(int id, Payroll payroll) {
        payrollDb.put(id, payroll);

        System.out.println("Successfully added.");
    }

    public HashMap<Integer, Payroll> getPayroll() {
        return payrollDb;
    }

    public boolean isPayrollExists(int id) {
        return payrollDb.containsKey(id);
    }

    public void updatePayroll(int id, Payroll payroll) {
        payrollDb.replace(id, payroll);
    }

    public void removePayroll(int id) {
        payrollDb.remove(id);
    }

}
