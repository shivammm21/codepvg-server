package com.codepvg.code.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String username;
    
    @Indexed(unique = true)
    private String email;

    private String prnNumber;
    private String mobileNumber;
    
    private String password;
    private String fullName;
    private String year;        // Student's academic year (e.g., "1st Year", "2nd Year", "3rd Year", "4th Year")
    private String branch;      // Student's branch (e.g., "Computer Science", "Information Technology", "Electronics")
    private Role role; //student,admin
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int totalSolved;
    private int totalSubmissions;
    private List<String> solvedProblems; // List of problem IDs that user has solved
    // Profile fields
    private String bio;           // User's bio/description
    private String headline;      // Professional headline
    private String linkedinUrl;   // LinkedIn profile URL
    private String githubUrl;     // GitHub profile URL

    public enum Role {
        ADMIN, STUDENT
    }

    public enum UserStatus {
        PENDING, APPROVED, REJECTED
    }

    // Constructors
    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = UserStatus.PENDING;
        this.totalSolved = 0;
        this.totalSubmissions = 0;
    }
}