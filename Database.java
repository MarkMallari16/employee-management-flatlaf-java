package com.mycompany.firstflatlaf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static Database instance;
    private Connection conn;
    private static final String URL = "jdbc:mysql://localhost:3306/db_employee_management";
    private static final String USER = "root";
    private static final String PASSWORD = "!M@rkcc16";

    public Database() {
        try {
            //optional
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException ex) {
            System.out.println("MYSQL Driver not found");
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Connection getConnection() {
        return conn;
    }

    public static synchronized Database getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new Database();
        }
        return instance;
    }

    //insert data (for dynamic and reusability)
//    public void insertData(String query, Objects[] values) {
//        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD); PreparedStatement pstmt = conn.prepareStatement(query)) {
//            for (int i = 0; i < values.length; i++) {
//                pstmt.setObject(i + 1, values[i]);
//            }
//            pstmt.executeUpdate();
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//    }
    //adding employee
    public void addEmployee(Employee employee) {
//        employeesDb.put(id, employee);
//
//        System.out.println("Successfully added!");

        String sql = "INSERT INTO employees (profile, name, age, date_of_birth, gender, status, department, position, location_type, contact_num, email) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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

    public boolean isEmpIdExists(int id) {
        String sql = "SELECT COUNT(*) FROM employees WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    //deleting employee
    public void removeEmployee(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

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
        String sql = "UPDATE employees SET profile = ?, name = ?, age = ?, date_of_birth = ?, gender = ?, status = ?, "
                + "department = ?, position = ?, location_type = ?, contact_num = ?, email = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
            pstmt.setInt(12, id);

            int rowAffected = pstmt.executeUpdate();

            if (rowAffected > 0) {
                System.out.println(rowAffected + " row(s) updated.");
                System.out.println(id);
            } else {
                System.out.println("Failed to update employee");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public int getTotalEmployees() {
        String sql = "SELECT COUNT(id) FROM employees";
        int totalEmployees = 0;

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                totalEmployees = rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return totalEmployees;
    }

    public void addPayroll(int id, Payroll payroll) {
        String sql = "INSERT INTO payroll (employee_id, salary) VALUES (?,?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setDouble(2, payroll.getSalary());

            int rowAffected = pstmt.executeUpdate();

            if (rowAffected > 0) {
                System.out.println("Successfully added.");
            } else {
                System.out.println("Failed to add employee salary.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        System.out.println("Successfully added.");
    }

    public void updatePayroll(int id, Payroll payroll) {
        String sql = "UPDATE payroll SET employee_id = ?, salary = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, payroll.getEmpId());
            pstmt.setDouble(2, payroll.getSalary());
            pstmt.setInt(3, id);

            int rowAffected = pstmt.executeUpdate();

            if (rowAffected > 0) {
                System.out.println("Employee Salary updated successfully.");
            } else {
                System.out.println("Failed to update employee salary.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void removePayroll(int id) {
        String sql = "DELETE FROM payroll WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int rowAffected = pstmt.executeUpdate();

            if (rowAffected > 0) {
                System.out.println("Employee Payroll successfully deleted.");
            } else {
                System.out.println("Failed to delete employee payroll.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
