package com.mycompany.firstflatlaf;

import java.util.HashMap;

public class Database {

    private static HashMap<Integer, Employee> employeesDb = new HashMap<>();

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

}
