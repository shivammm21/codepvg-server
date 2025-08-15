package com.codepvg.code.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SubmissionDto {
    
    @NotBlank(message = "Problem ID is required")
    private String problemId;
    
    @NotBlank(message = "Source code is required")
    private String sourceCode;
    
    @NotBlank(message = "Language is required")
    private String language;
    
    @NotNull(message = "Language ID is required")
    private Integer languageId;

    // Constructors
    public SubmissionDto() {}

    public SubmissionDto(String problemId, String sourceCode, String language, Integer languageId) {
        this.problemId = problemId;
        this.sourceCode = sourceCode;
        this.language = language;
        this.languageId = languageId;
    }

    // Getters and Setters
    public String getProblemId() { return problemId; }
    public void setProblemId(String problemId) { this.problemId = problemId; }

    public String getSourceCode() { return sourceCode; }
    public void setSourceCode(String sourceCode) { this.sourceCode = sourceCode; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public Integer getLanguageId() { return languageId; }
    public void setLanguageId(Integer languageId) { this.languageId = languageId; }
}