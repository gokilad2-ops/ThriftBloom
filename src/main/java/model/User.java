package model;

import java.sql.Timestamp;

public class User {
    private int id;
    private String name;
    private String registrationNumber;
    private String email;
    private String passwordHash;
    private String department;
    private String yearOfStudy;
    private String phone;
    private Timestamp createdAt;

    public User() {}

    public User(int id, String name, String registrationNumber, String email, String passwordHash,
                String department, String yearOfStudy, String phone, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.email = email;
        this.passwordHash = passwordHash;
        this.department = department;
        this.yearOfStudy = yearOfStudy;
        this.phone = phone;
        this.createdAt = createdAt;
    }

    public User(String name, String registrationNumber, String email, String passwordHash,
                String department, String yearOfStudy, String phone) {
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.email = email;
        this.passwordHash = passwordHash;
        this.department = department;
        this.yearOfStudy = yearOfStudy;
        this.phone = phone;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(String yearOfStudy) { this.yearOfStudy = yearOfStudy; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return name + " (" + registrationNumber + ")";
    }
}
