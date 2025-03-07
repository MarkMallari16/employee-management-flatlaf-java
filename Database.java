package com.mycompany.firstflatlaf;

import java.util.HashMap;

public class Database {

    private static HashMap<Integer, String[]> employeesDb = new HashMap<>();
    
    public void addEmployee(int id, String[] data) {
        employeesDb.put(id, data);

        System.out.println("Successfully added!");
    }

    public HashMap<Integer, String[]> getEmployee() {
        return employeesDb;
    }

    public boolean isEmpIdExists(int id) {
        return employeesDb.containsKey(id);
    }

    public void removeEmployee(int id) {
        employeesDb.remove(id);
    }

    public void updateEmployee(int id, String[] datas) {
        employeesDb.replace(id, datas);
    }

    public int getTotalEmployees() {
        return employeesDb.size();
    }

}
