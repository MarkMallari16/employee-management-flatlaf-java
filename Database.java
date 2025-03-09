package com.mycompany.firstflatlaf;

import java.util.HashMap;

public class Database {

    private static HashMap<Integer, Employee> employeesDb = new HashMap<>();
    private static HashMap<Integer, Payroll> payrollDb = new HashMap<>();

    public void addEmployee(int id, Employee employee) {
        employeesDb.put(id, employee);

        System.out.println("Successfully added!");
    }

    public HashMap<Integer, Employee> getEmployee() {
        return employeesDb;
    }

    public boolean isEmpIdExists(int id) {
        return employeesDb.containsKey(id);
    }

    public void removeEmployee(int id) {
        employeesDb.remove(id);
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
