package com.codepvg.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdminRegistrationDto {
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "Department is required")
    private String department;
    
    @NotBlank(message = "Admin access code is required")
    private String adminAccessCode;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    // Constructors
    public AdminRegistrationDto() {}

    public AdminRegistrationDto(String firstName, String lastName, String department, 
                               String adminAccessCode, String email, String password, String confirmPassword) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.department = department;
        this.adminAccessCode = adminAccessCode;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAdminAccessCode() {
        return adminAccessCode;
    }

    public void setAdminAccessCode(String adminAccessCode) {
        this.adminAccessCode = adminAccessCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    // Helper method to get full name
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Helper method to generate username from email
    public String generateUsername() {
        return email.split("@")[0];
    }
}
