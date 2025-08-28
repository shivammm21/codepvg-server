package com.codepvg.code.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfile {

    private String id;

    private String username;
    private String prnNumber;
    private String mobileNumber;
    private String fullName;
    private String email;
    private String year;
    private String branch;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
