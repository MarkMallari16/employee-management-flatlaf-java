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

    public void removeEmployee(int id) {
        if (employeesDb.containsKey(id)) {
            employeesDb.remove(id);
        }
    }
}
