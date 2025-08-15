package com.codepvg.code.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "submissions")
public class Submission {
    @Id
    private String id;
    
    private String userId;
    private String problemId;
    private String sourceCode;
    private String language;
    private int languageId;
    private SubmissionStatus status;
    private String output;
    private String error;
    private Double executionTime;
    private Integer memoryUsage;
    private LocalDateTime submittedAt;
    private String judge0Token;
    private int testCasesPassed;
    private int totalTestCases;

    public enum SubmissionStatus {
        PENDING, ACCEPTED, WRONG_ANSWER, TIME_LIMIT_EXCEEDED, 
        MEMORY_LIMIT_EXCEEDED, RUNTIME_ERROR, COMPILATION_ERROR, INTERNAL_ERROR
    }

    // Constructors
    public Submission() {
        this.submittedAt = LocalDateTime.now();
        this.status = SubmissionStatus.PENDING;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getProblemId() { return problemId; }
    public void setProblemId(String problemId) { this.problemId = problemId; }

    public String getSourceCode() { return sourceCode; }
    public void setSourceCode(String sourceCode) { this.sourceCode = sourceCode; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public int getLanguageId() { return languageId; }
    public void setLanguageId(int languageId) { this.languageId = languageId; }

    public SubmissionStatus getStatus() { return status; }
    public void setStatus(SubmissionStatus status) { this.status = status; }

    public String getOutput() { return output; }
    public void setOutput(String output) { this.output = output; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public Double getExecutionTime() { return executionTime; }
    public void setExecutionTime(Double executionTime) { this.executionTime = executionTime; }

    public Integer getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(Integer memoryUsage) { this.memoryUsage = memoryUsage; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public String getJudge0Token() { return judge0Token; }
    public void setJudge0Token(String judge0Token) { this.judge0Token = judge0Token; }

    public int getTestCasesPassed() { return testCasesPassed; }
    public void setTestCasesPassed(int testCasesPassed) { this.testCasesPassed = testCasesPassed; }

    public int getTotalTestCases() { return totalTestCases; }
    public void setTotalTestCases(int totalTestCases) { this.totalTestCases = totalTestCases; }
}