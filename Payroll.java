package com.mycompany.firstflatlaf;

public class Payroll {
    private int empId;
    private double salary;

    public Payroll(int empId, double salary) {
        this.empId = empId;
        this.salary = salary;
    }

    //getter
    public int getEmpId() {
        return empId;
    }

    //setter
    public void setEmpId(int empId) {
        this.empId = empId;
    }

    //getter
    public double getSalary() {
        return salary;
    }
    
    //setter
    public void setSalary(double salary) {
        this.salary = salary;
    }
}
