package com.codepvg.code.dto;

import com.codepvg.code.model.Problem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class ProblemCreateDto {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotBlank(message = "Constraints are required")
    private String constraints;
    
    @NotNull(message = "Difficulty is required")
    private Problem.Difficulty difficulty;
    
    private List<String> topics;  // ["array", "linkedlist", "tree"]
    
    private List<ExampleDto> examples;  // Example inputs and outputs
    
    private List<TestCaseDto> testCases;  // Test cases for validation
    
    private List<String> tags;  // Additional tags
    
    private List<String> targetYears;  // ["first", "second", "third", "final"]
    
    private CodeTemplatesDto codeTemplates;  // Code templates for different languages

    public static class ExampleDto {
        private String input;
        private String output;
        private String explanation;

        public ExampleDto() {}

        public ExampleDto(String input, String output, String explanation) {
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

    public static class TestCaseDto {
        private String input;
        private String expectedOutput;
        private boolean isHidden;

        public TestCaseDto() {}

        public TestCaseDto(String input, String expectedOutput, boolean isHidden) {
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

    public static class CodeTemplatesDto {
        private String cTemplate;
        private String cppTemplate;
        private String pythonTemplate;
        private String javaTemplate;

        public CodeTemplatesDto() {}

        public CodeTemplatesDto(String cTemplate, String cppTemplate, String pythonTemplate, String javaTemplate) {
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

    // Constructors
    public ProblemCreateDto() {}

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getConstraints() { return constraints; }
    public void setConstraints(String constraints) { this.constraints = constraints; }

    public Problem.Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Problem.Difficulty difficulty) { this.difficulty = difficulty; }

    public List<String> getTopics() { return topics; }
    public void setTopics(List<String> topics) { this.topics = topics; }

    public List<ExampleDto> getExamples() { return examples; }
    public void setExamples(List<ExampleDto> examples) { this.examples = examples; }

    public List<TestCaseDto> getTestCases() { return testCases; }
    public void setTestCases(List<TestCaseDto> testCases) { this.testCases = testCases; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public List<String> getTargetYears() { return targetYears; }
    public void setTargetYears(List<String> targetYears) { this.targetYears = targetYears; }

    public CodeTemplatesDto getCodeTemplates() { return codeTemplates; }
    public void setCodeTemplates(CodeTemplatesDto codeTemplates) { this.codeTemplates = codeTemplates; }
}