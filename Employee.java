package com.mycompany.firstflatlaf;

public class Employee {

    //employee id
    private int id;
    //personal details
    private String profile;
    private String name;
    private String age;
    private String dateOfBirth;
    private String gender;
    private String status;

    //job details
    private String department;
    private String position;
    private String locationType;

    //contact details
    private String contactNum;
    private String email;

    public Employee(int id, String name, String age, String dateOfBirth, String gender, String status,
            String contactNum, String email, String department, String position, String locationType) {
        //personal info
        this.id = id;
        this.name = name;
        this.age = age;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.status = status;
        this.contactNum = contactNum;
        this.email = email;
        //job details
        this.department = department;
        this.position = position;
        this.locationType = locationType;

    }

    // getter 
    public int getId() {
        return id;
    }

    // setter
    public void setProfile(String profile) {
        this.profile = profile;
    }

    // getter 
    public String getProfile() {
        return profile;
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
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    // setter
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    // getter 

    public String getGender() {
        return gender;
    }

    // setter
    public void setGender(String gender) {
        this.gender = gender;
    }

    // getter 
    public String getStatus() {
        return status;
    }

    // setter
    public void setStatus(String status) {
        this.status = status;
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
    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }
}
