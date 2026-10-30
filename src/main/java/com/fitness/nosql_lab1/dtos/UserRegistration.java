package com.fitness.nosql_lab1.dtos;

public class UserRegistration {
    private String username;
    private String password;
    private String role;
    private String secreteCode;

    public UserRegistration() {}

    public UserRegistration(String username, String password, String role, String secreteCode) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.secreteCode = secreteCode;
    }

    public UserRegistration(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSecreteCode() {
        return secreteCode;
    }

    public void setSecreteCode(String secreteCode) {
        this.secreteCode = secreteCode;
    }
}
