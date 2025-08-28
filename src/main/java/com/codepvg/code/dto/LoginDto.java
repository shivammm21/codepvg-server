package com.codepvg.code.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDto {
    
    @NotBlank(message = "Email is required")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;

    // Constructors
    public LoginDto() {}

    public LoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}