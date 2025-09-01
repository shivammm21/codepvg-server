package com.codepvg.code.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "problems")
public class Problem {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private Long problemNumber; // sequential unique problem number
    
    private String title;
    private String description;
    private List<String> constraints;
    private Difficulty difficulty;
    private List<String> topics;  // Topics like "array", "linkedlist", "tree", etc.
    private List<Example> examples;  // Example inputs and outputs
    private List<TestCase> testCases;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private int totalSubmissions;
    private int totalSolved;
    private List<String> targetYears; // ["first", "second", "third", "final"]
    private CodeTemplates codeTemplates; // Code templates for different languages

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    public static class CodeTemplates {
        private String cTemplate;
        private String cppTemplate;
        private String pythonTemplate;
        private String javaTemplate;

        public CodeTemplates() {}

        public CodeTemplates(String cTemplate, String cppTemplate, String pythonTemplate, String javaTemplate) {
            this.cTemplate = cTemplate;
            this.cppTemplate = cppTemplate;
            this.pythonTemplate = pythonTemplate;
            this.javaTemplate = javaTemplate;
        }

        // Getters and Setters
        public String getCTemplate() { return cTemplate; }
        public void setCTemplate(String cTemplate) { this.cTemplate = cTemplate; }

        public String getCppTemplate() { return cppTemplate; }
        public void setCppTemplate(String cppTemplate) { this.cppTemplate = cppTemplate; }

        public String getPythonTemplate() { return pythonTemplate; }
        public void setPythonTemplate(String pythonTemplate) { this.pythonTemplate = pythonTemplate; }

        public String getJavaTemplate() { return javaTemplate; }
        public void setJavaTemplate(String javaTemplate) { this.javaTemplate = javaTemplate; }
    }

    public static class Example {
        private String input;
        private String output;
        private String explanation;

        public Example() {}

        public Example(String input, String output, String explanation) {
            this.input = input;
            this.output = output;
            this.explanation = explanation;
        }

        // Getters and Setters
        public String getInput() { return input; }
        public void setInput(String input) { this.input = input; }

        public String getOutput() { return output; }
        public void setOutput(String output) { this.output = output; }

        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
    }

    public static class TestCase {
        private String input;
        private String expectedOutput;
        private boolean isHidden;

        public TestCase() {}

        public TestCase(String input, String expectedOutput, boolean isHidden) {
            this.input = input;
            this.expectedOutput = expectedOutput;
            this.isHidden = isHidden;
        }

        // Getters and Setters
        public String getInput() { return input; }
        public void setInput(String input) { this.input = input; }

        public String getExpectedOutput() { return expectedOutput; }
        public void setExpectedOutput(String expectedOutput) { this.expectedOutput = expectedOutput; }

        public boolean isHidden() { return isHidden; }
        public void setHidden(boolean hidden) { isHidden = hidden; }
    }

    // Constructors
    public Problem() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.totalSubmissions = 0;
        this.totalSolved = 0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getProblemNumber() { return problemNumber; }
    public void setProblemNumber(Long problemNumber) { this.problemNumber = problemNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getConstraints() { return constraints; }
    public void setConstraints(List<String> constraints) { this.constraints = constraints; }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    public List<String> getTopics() { return topics; }
    public void setTopics(List<String> topics) { this.topics = topics; }

    public List<Example> getExamples() { return examples; }
    public void setExamples(List<Example> examples) { this.examples = examples; }

    public List<TestCase> getTestCases() { return testCases; }
    public void setTestCases(List<TestCase> testCases) { this.testCases = testCases; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public int getTotalSubmissions() { return totalSubmissions; }
    public void setTotalSubmissions(int totalSubmissions) { this.totalSubmissions = totalSubmissions; }

    public int getTotalSolved() { return totalSolved; }
    public void setTotalSolved(int totalSolved) { this.totalSolved = totalSolved; }

    public List<String> getTargetYears() { return targetYears; }
    public void setTargetYears(List<String> targetYears) { this.targetYears = targetYears; }

    public CodeTemplates getCodeTemplates() { return codeTemplates; }
    public void setCodeTemplates(CodeTemplates codeTemplates) { this.codeTemplates = codeTemplates; }
}