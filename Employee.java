package com.mycompany.firstflatlaf;

public class Employee {

    private int id;
    private String name;
    private String age;
    private String department;
    private String position;
    private String contactNum;
    private String email;

    public Employee(int id, String name, String age, String department, String position, String contactNum, String email) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.department = department;
        this.position = position;
        this.contactNum = contactNum;
        this.email = email;
    }

    // getter 
    public int getId() {
        return id;
    }

    // setter
    public void setId(int id) {
        this.id = id;
    }

    // getter 
    public String getName() {
        return name;
    }

    // setter
    public void setName(String name) {
        this.name = name;
    }

    // getter 
    public String getAge() {
        return age;
    }

    // setter
    public void setAge(String age) {
        this.age = age;
    }

    // getter 
    public String getDepartment() {
        return department;
    }

    // setter
    public void setDepartment(String department) {
        this.department = department;
    }

    // getter 
    public String getPosition() {
        return position;
    }

    // setter
    public void setPosition(String position) {
        this.position = position;
    }

    // getter 
    public String getContactNum() {
        return contactNum;
    }

    // setter
    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }

    // getter 
    public String getEmail() {
        return email;
    }

    // setter
    public void setEmail(String email) {
        this.email = email;
    }
}
