package com.codepvg.code.service;

import com.codepvg.code.dto.ProblemCreateDto;
import com.codepvg.code.model.Problem;
import com.codepvg.code.repository.ProblemRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProblemService {

    @Autowired
    private ProblemRepository problemRepository;

    public List<Problem> getAllProblems() {
        return problemRepository.findAllOrderByCreatedAtDesc();
    }

    public Optional<Problem> getProblemById(String id) {
        return problemRepository.findById(id);
    }

    public Problem createProblem(Problem problem, String createdBy) {
        problem.setCreatedBy(createdBy);
        problem.setCreatedAt(LocalDateTime.now());
        problem.setUpdatedAt(LocalDateTime.now());
        return problemRepository.save(problem);
    }

    public Problem updateProblem(String id, Problem problemDetails) {
        Optional<Problem> problemOpt = problemRepository.findById(id);
        if (problemOpt.isPresent()) {
            Problem problem = problemOpt.get();
            problem.setTitle(problemDetails.getTitle());
            problem.setDescription(problemDetails.getDescription());
            problem.setConstraints(problemDetails.getConstraints());
            problem.setDifficulty(problemDetails.getDifficulty());
            problem.setTestCases(problemDetails.getTestCases());
            problem.setTags(problemDetails.getTags());
            problem.setUpdatedAt(LocalDateTime.now());
            return problemRepository.save(problem);
        }
        throw new RuntimeException("Problem not found");
    }

    public void deleteProblem(String id) {
        problemRepository.deleteById(id);
    }

    public List<Problem> importProblemsFromExcel(MultipartFile file, String createdBy) throws IOException {
        List<Problem> problems = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // Skip header row
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Problem problem = new Problem();
                    
                    // Title (Column A)
                    Cell titleCell = row.getCell(0);
                    if (titleCell != null) {
                        problem.setTitle(getCellValueAsString(titleCell));
                    }
                    
                    // Description (Column B)
                    Cell descCell = row.getCell(1);
                    if (descCell != null) {
                        problem.setDescription(getCellValueAsString(descCell));
                    }
                    
                    // Difficulty (Column C) - EASY/MEDIUM/HARD
                    Cell difficultyCell = row.getCell(2);
                    if (difficultyCell != null) {
                        String difficultyStr = getCellValueAsString(difficultyCell).toUpperCase();
                        try {
                            problem.setDifficulty(Problem.Difficulty.valueOf(difficultyStr));
                        } catch (IllegalArgumentException e) {
                            problem.setDifficulty(Problem.Difficulty.MEDIUM); // Default
                        }
                    } else {
                        problem.setDifficulty(Problem.Difficulty.MEDIUM);
                    }
                    
                    // Topics (Column D) - comma separated: "array,linkedlist,tree"
                    Cell topicsCell = row.getCell(3);
                    if (topicsCell != null) {
                        String topicsStr = getCellValueAsString(topicsCell);
                        List<String> topics = Arrays.asList(topicsStr.split(","));
                        problem.setTopics(topics.stream().map(String::trim).collect(ArrayList::new, ArrayList::add, ArrayList::addAll));
                    }
                    
                    // Example Input (Column E)
                    Cell exampleInputCell = row.getCell(4);
                    String exampleInput = exampleInputCell != null ? getCellValueAsString(exampleInputCell) : "";
                    
                    // Example Output (Column F)
                    Cell exampleOutputCell = row.getCell(5);
                    String exampleOutput = exampleOutputCell != null ? getCellValueAsString(exampleOutputCell) : "";
                    
                    // Create example
                    if (!exampleInput.isEmpty() && !exampleOutput.isEmpty()) {
                        List<Problem.Example> examples = new ArrayList<>();
                        examples.add(new Problem.Example(exampleInput, exampleOutput, ""));
                        problem.setExamples(examples);
                    }
                    
                    // Test Case Input (Column G)
                    Cell testInputCell = row.getCell(6);
                    String testInput = testInputCell != null ? getCellValueAsString(testInputCell) : exampleInput;
                    
                    // Test Case Output (Column H)
                    Cell testOutputCell = row.getCell(7);
                    String testOutput = testOutputCell != null ? getCellValueAsString(testOutputCell) : exampleOutput;
                    
                    // Constraints (Column I)
                    Cell constraintCell = row.getCell(8);
                    if (constraintCell != null) {
                        problem.setConstraints(getCellValueAsString(constraintCell));
                    }
                    
                    // Create test case
                    List<Problem.TestCase> testCases = new ArrayList<>();
                    testCases.add(new Problem.TestCase(testInput, testOutput, false));
                    problem.setTestCases(testCases);
                    
                    // Set default values
                    problem.setTags(problem.getTopics() != null ? problem.getTopics() : Arrays.asList("general"));
                    problem.setCreatedBy(createdBy);
                    
                    problems.add(problem);
                }
            }
        }
        
        return problemRepository.saveAll(problems);
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    public Problem createProblemFromDto(ProblemCreateDto dto, String createdBy) {
        Problem problem = new Problem();
        problem.setTitle(dto.getTitle());
        problem.setDescription(dto.getDescription());
        problem.setConstraints(dto.getConstraints());
        problem.setDifficulty(dto.getDifficulty());
        problem.setTopics(dto.getTopics());
        problem.setTags(dto.getTags());
        problem.setCreatedBy(createdBy);

        // Set target years
        problem.setTargetYears(dto.getTargetYears());

        // Convert examples
        if (dto.getExamples() != null) {
            List<Problem.Example> examples = new ArrayList<>();
            for (ProblemCreateDto.ExampleDto exampleDto : dto.getExamples()) {
                examples.add(new Problem.Example(
                    exampleDto.getInput(),
                    exampleDto.getOutput(),
                    exampleDto.getExplanation()
                ));
            }
            problem.setExamples(examples);
        }

        // Convert test cases
        if (dto.getTestCases() != null) {
            List<Problem.TestCase> testCases = new ArrayList<>();
            for (ProblemCreateDto.TestCaseDto testCaseDto : dto.getTestCases()) {
                testCases.add(new Problem.TestCase(
                    testCaseDto.getInput(),
                    testCaseDto.getExpectedOutput(),
                    testCaseDto.isHidden()
                ));
            }
            problem.setTestCases(testCases);
        }

        // Convert code templates
        if (dto.getCodeTemplates() != null) {
            Problem.CodeTemplates templates = new Problem.CodeTemplates(
                dto.getCodeTemplates().getCTemplate(),
                dto.getCodeTemplates().getCppTemplate(),
                dto.getCodeTemplates().getPythonTemplate(),
                dto.getCodeTemplates().getJavaTemplate()
            );
            problem.setCodeTemplates(templates);
        } else {
            // Generate default templates if not provided
            problem.setCodeTemplates(generateDefaultCodeTemplates(dto.getTitle()));
        }

        return problemRepository.save(problem);
    }

    // Helper method to generate default code templates
    private Problem.CodeTemplates generateDefaultCodeTemplates(String problemTitle) {
        Problem.CodeTemplates templates = new Problem.CodeTemplates();
        String functionName = "solve";
        
        // C Template
        templates.setCTemplate(
            "#include <stdio.h>\n" +
            "#include <stdlib.h>\n" +
            "#include <string.h>\n\n" +
            "// Problem: " + problemTitle + "\n" +
            "// Complete the function below\n\n" +
            "class MySolution {\n" +
            "public:\n" +
            "    // Write your solution here\n" +
            "    int " + functionName + "() {\n" +
            "        // TODO: Implement your logic\n" +
            "        return 0;\n" +
            "    }\n" +
            "};\n\n" +
            "int main() {\n" +
            "    MySolution solution;\n" +
            "    // Test your solution\n" +
            "    return 0;\n" +
            "}"
        );
        
        // C++ Template
        templates.setCppTemplate(
            "#include <iostream>\n" +
            "#include <vector>\n" +
            "using namespace std;\n\n" +
            "class Solution {\n" +
            "public:\n" +
            "    int " + functionName + "() {\n" +
            "        // TODO: Implement your logic here\n" +
            "        return 0;\n" +
            "    }\n" +
            "};\n\n" +
            "int main() {\n" +
            "    // Read input here\n" +
            "    \n" +
            "    Solution solution;\n" +
            "    // Call your solution method and print result\n" +
            "    \n" +
            "    return 0;\n" +
            "}"
        );
        
        // Python Template
        templates.setPythonTemplate(
            "class Solution:\n" +
            "    def " + functionName + "(self):\n" +
            "        \"\"\"\n" +
            "        TODO: Implement your logic here\n" +
            "        \"\"\"\n" +
            "        pass\n\n" +
            "if __name__ == \"__main__\":\n" +
            "    # Read input here\n" +
            "    \n" +
            "    solution = Solution()\n" +
            "    # Call your solution method and print result\n"
        );
        
        // Java Template
        templates.setJavaTemplate(
            "import java.util.Scanner;\n\n" +
            "public class Solution {\n" +
            "    public int " + functionName + "() {\n" +
            "        // TODO: Implement your logic here\n" +
            "        return 0;\n" +
            "    }\n" +
            "    \n" +
            "    public static void main(String[] args) {\n" +
            "        Scanner sc = new Scanner(System.in);\n" +
            "        // Read input here\n" +
            "        \n" +
            "        Solution solution = new Solution();\n" +
            "        // Call your solution method and print result\n" +
            "        \n" +
            "        sc.close();\n" +
            "    }\n" +
            "}"
        );
        
        return templates;
    }

    public List<Problem> searchProblems(String query) {
        return problemRepository.findByTitleContainingIgnoreCase(query);
    }

    public List<Problem> getProblemsByDifficulty(Problem.Difficulty difficulty) {
        return problemRepository.findByDifficulty(difficulty);
    }

    public Optional<Problem> getProblemByTitle(String title) {
        return problemRepository.findByTitle(title);
    }

    public Problem updateProblemStats(String problemId, boolean solved) {
        Optional<Problem> problemOpt = problemRepository.findById(problemId);
        if (problemOpt.isPresent()) {
            Problem problem = problemOpt.get();
            problem.setTotalSubmissions(problem.getTotalSubmissions() + 1);
            if (solved) {
                problem.setTotalSolved(problem.getTotalSolved() + 1);
            }
            problem.setUpdatedAt(LocalDateTime.now());
            return problemRepository.save(problem);
        }
        throw new RuntimeException("Problem not found");
    }

    public List<Problem> getProblemsByTopic(String topic) {
        return problemRepository.findByTopicsContaining(topic);
    }

    public List<Problem> getProblemsByCreator(String username) {
        return problemRepository.findByCreatedBy(username);
    }
}