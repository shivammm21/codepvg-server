package com.codepvg.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class ProfileUpdateDto {
    
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;
    
    @Email(message = "Email should be valid")
    private String email;
    
    private String fullName;
    
    @Size(max = 500, message = "Bio cannot exceed 500 characters")
    private String bio;
    
    @Size(max = 100, message = "Headline cannot exceed 100 characters")
    private String headline;
    
    private String linkedinUrl;
    
    private String githubUrl;

    // Constructors
    public ProfileUpdateDto() {}

    public ProfileUpdateDto(String username, String email, String fullName, String bio, 
                           String headline, String linkedinUrl, String githubUrl) {
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.bio = bio;
        this.headline = headline;
        this.linkedinUrl = linkedinUrl;
        this.githubUrl = githubUrl;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }

    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }

    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }
}