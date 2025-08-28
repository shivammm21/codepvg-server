package com.codepvg.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDto {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    @NotBlank(message = "Year is required")
    private String year;
    
    @NotBlank(message = "Branch is required")
    private String branch;

    @NotBlank(message = "PRN number is required")
    private String prnNumber;

    @NotBlank(message = "Mobile number is required")
    @Size(min = 10, max = 15, message = "Mobile number must be between 10 and 15 digits")
    private String mobileNumber;

    @NotBlank(message = "Role is required")
    private String role;


    // Constructors
    public UserRegistrationDto() {}

    public UserRegistrationDto(String email, String password, String fullName, String year, String branch) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.year = year;
        this.branch = branch;
    }

}