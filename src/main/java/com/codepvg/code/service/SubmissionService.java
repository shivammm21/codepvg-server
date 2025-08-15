package com.codepvg.code.service;

import com.codepvg.code.dto.SubmissionDto;
import com.codepvg.code.model.Problem;
import com.codepvg.code.model.Submission;
import com.codepvg.code.model.User;
import com.codepvg.code.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private Judge0Service judge0Service;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private UserService userService;

    @Autowired
    private CodeWrapperService codeWrapperService;

    public Submission submitCode(SubmissionDto submissionDto, String userId) {
        // Create submission record
        Submission submission = new Submission();
        submission.setUserId(userId);
        submission.setProblemId(submissionDto.getProblemId());
        submission.setSourceCode(submissionDto.getSourceCode());
        submission.setLanguage(submissionDto.getLanguage());
        submission.setLanguageId(submissionDto.getLanguageId());
        submission.setStatus(Submission.SubmissionStatus.PENDING);

        // Get problem details
        Optional<Problem> problemOpt = problemService.getProblemById(submissionDto.getProblemId());
        if (!problemOpt.isPresent()) {
            throw new RuntimeException("Problem not found");
        }

        Problem problem = problemOpt.get();
        submission.setTotalTestCases(problem.getTestCases().size());

        // Save initial submission
        submission = submissionRepository.save(submission);

        // Execute code against test cases
        executeSubmission(submission, problem);

        return submission;
    }

    private void executeSubmission(Submission submission, Problem problem) {
        try {
            int passedTestCases = 0;
            boolean allPassed = true;
            StringBuilder outputBuilder = new StringBuilder();
            StringBuilder errorBuilder = new StringBuilder();
            double maxExecutionTime = 0.0;
            int maxMemoryUsage = 0;

            // Get time limit from constraints (default 2 seconds if not specified)
            double timeLimit = extractTimeLimitFromConstraints(problem.getConstraints());
            
            outputBuilder.append("=== Test Case Results ===\n");

            for (int i = 0; i < problem.getTestCases().size(); i++) {
                Problem.TestCase testCase = problem.getTestCases().get(i);
                outputBuilder.append(String.format("Test Case %d:\n", i + 1));
                
                try {
                    // Wrap user code with main function and input/output handling
                    String wrappedCode = codeWrapperService.wrapCodeForProblem(
                        submission.getSourceCode(),
                        submission.getLanguage(),
                        problem.getTitle(),
                        "" // Method signature - can be enhanced later
                    );
                    
                    // Submit wrapped code to Judge0
                    String token = judge0Service.submitCode(
                        wrappedCode,
                        submission.getLanguageId(),
                        testCase.getInput()
                    );

                    // Wait for execution with timeout
                    Thread.sleep(2000);

                    // Get result
                    Map<String, Object> result = judge0Service.getSubmissionResult(token);
                    
                    String status = (String) result.get("status");
                    Integer statusId = (Integer) result.get("statusId");
                    Double executionTime = (Double) result.get("executionTime");
                    Integer memoryUsage = (Integer) result.get("memoryUsage");

                    // Track max execution time and memory
                    if (executionTime != null) {
                        maxExecutionTime = Math.max(maxExecutionTime, executionTime);
                    }
                    if (memoryUsage != null) {
                        maxMemoryUsage = Math.max(maxMemoryUsage, memoryUsage);
                    }

                    // Check for Time Limit Exceeded
                    if (executionTime != null && executionTime > timeLimit) {
                        allPassed = false;
                        submission.setStatus(Submission.SubmissionStatus.TIME_LIMIT_EXCEEDED);
                        outputBuilder.append("❌ Time Limit Exceeded\n");
                        outputBuilder.append(String.format("Execution Time: %.3fs (Limit: %.1fs)\n", executionTime, timeLimit));
                        errorBuilder.append("Time Limit Exceeded: Your code took too long to execute.\n");
                        errorBuilder.append("Consider optimizing your algorithm for better time complexity.\n");
                        break;
                    }

                    // Check for Memory Limit Exceeded (default 256MB)
                    if (memoryUsage != null && memoryUsage > 256000) {
                        allPassed = false;
                        submission.setStatus(Submission.SubmissionStatus.MEMORY_LIMIT_EXCEEDED);
                        outputBuilder.append("❌ Memory Limit Exceeded\n");
                        outputBuilder.append(String.format("Memory Usage: %d KB (Limit: 256000 KB)\n", memoryUsage));
                        errorBuilder.append("Memory Limit Exceeded: Your code used too much memory.\n");
                        break;
                    }

                    // Handle different Judge0 status codes
                    switch (statusId) {
                        case 3: // Accepted
                            String output = (String) result.get("output");
                            String expectedOutput = testCase.getExpectedOutput().trim();
                            String actualOutput = output != null ? output.trim() : "";
                            
                            if (actualOutput.equals(expectedOutput)) {
                                passedTestCases++;
                                outputBuilder.append("✅ Passed\n");
                                outputBuilder.append(String.format("Expected: %s\n", expectedOutput));
                                outputBuilder.append(String.format("Got: %s\n", actualOutput));
                                if (executionTime != null) {
                                    outputBuilder.append(String.format("Time: %.3fs\n", executionTime));
                                }
                            } else {
                                allPassed = false;
                                outputBuilder.append("❌ Wrong Answer\n");
                                outputBuilder.append(String.format("Expected: %s\n", expectedOutput));
                                outputBuilder.append(String.format("Got: %s\n", actualOutput));
                            }
                            break;

                        case 4: // Wrong Answer
                            allPassed = false;
                            submission.setStatus(Submission.SubmissionStatus.WRONG_ANSWER);
                            outputBuilder.append("❌ Wrong Answer\n");
                            String wrongOutput = (String) result.get("output");
                            outputBuilder.append(String.format("Expected: %s\n", testCase.getExpectedOutput()));
                            outputBuilder.append(String.format("Got: %s\n", wrongOutput != null ? wrongOutput : "null"));
                            break;

                        case 5: // Time Limit Exceeded
                            allPassed = false;
                            submission.setStatus(Submission.SubmissionStatus.TIME_LIMIT_EXCEEDED);
                            outputBuilder.append("❌ Time Limit Exceeded\n");
                            errorBuilder.append("Time Limit Exceeded: Your algorithm is too slow.\n");
                            break;

                        case 6: // Compilation Error
                            allPassed = false;
                            submission.setStatus(Submission.SubmissionStatus.COMPILATION_ERROR);
                            String compileError = (String) result.get("compileError");
                            outputBuilder.append("❌ Compilation Error\n");
                            errorBuilder.append("Compilation Error:\n").append(compileError != null ? compileError : "Unknown compilation error");
                            break;

                        case 7: // Runtime Error (SIGSEGV)
                        case 8: // Runtime Error (SIGXFSZ)
                        case 9: // Runtime Error (SIGFPE)
                        case 10: // Runtime Error (SIGABRT)
                        case 11: // Runtime Error (NZEC)
                        case 12: // Runtime Error (Other)
                            allPassed = false;
                            submission.setStatus(Submission.SubmissionStatus.RUNTIME_ERROR);
                            String runtimeError = (String) result.get("error");
                            outputBuilder.append("❌ Runtime Error\n");
                            errorBuilder.append("Runtime Error:\n").append(runtimeError != null ? runtimeError : "Unknown runtime error");
                            break;

                        case 13: // Internal Error
                            allPassed = false;
                            submission.setStatus(Submission.SubmissionStatus.INTERNAL_ERROR);
                            outputBuilder.append("❌ Internal Error\n");
                            errorBuilder.append("Internal Error: Please try again later.");
                            break;

                        default:
                            allPassed = false;
                            outputBuilder.append("❌ Unknown Error\n");
                            outputBuilder.append("Status: ").append(status).append("\n");
                            errorBuilder.append("Unknown Error: ").append(status);
                    }

                    outputBuilder.append("\n");

                } catch (Exception e) {
                    allPassed = false;
                    outputBuilder.append("❌ Execution Error\n");
                    outputBuilder.append("Error: ").append(e.getMessage()).append("\n\n");
                    errorBuilder.append("Execution Error: ").append(e.getMessage()).append("\n");
                }

                // If we hit a critical error, stop processing further test cases
                if (submission.getStatus() == Submission.SubmissionStatus.COMPILATION_ERROR ||
                    submission.getStatus() == Submission.SubmissionStatus.TIME_LIMIT_EXCEEDED ||
                    submission.getStatus() == Submission.SubmissionStatus.MEMORY_LIMIT_EXCEEDED) {
                    break;
                }
            }

            // Update submission with results
            submission.setTestCasesPassed(passedTestCases);
            submission.setOutput(outputBuilder.toString());
            submission.setError(errorBuilder.toString());
            submission.setExecutionTime(maxExecutionTime);
            submission.setMemoryUsage(maxMemoryUsage);

            // Determine final status if not already set by error conditions
            if (submission.getStatus() == Submission.SubmissionStatus.PENDING) {
                if (allPassed && passedTestCases == problem.getTestCases().size()) {
                    submission.setStatus(Submission.SubmissionStatus.ACCEPTED);
                    // Update user and problem stats, add to solved problems
                    updateUserSolvedProblems(submission.getUserId(), submission.getProblemId(), true);
                    problemService.updateProblemStats(submission.getProblemId(), true);
                } else {
                    submission.setStatus(Submission.SubmissionStatus.WRONG_ANSWER);
                    updateUserSolvedProblems(submission.getUserId(), submission.getProblemId(), false);
                    problemService.updateProblemStats(submission.getProblemId(), false);
                }
            } else {
                // Update stats for failed submissions
                updateUserSolvedProblems(submission.getUserId(), submission.getProblemId(), false);
                problemService.updateProblemStats(submission.getProblemId(), false);
            }

            submissionRepository.save(submission);

        } catch (Exception e) {
            submission.setStatus(Submission.SubmissionStatus.INTERNAL_ERROR);
            submission.setError("System error during execution: " + e.getMessage());
            submissionRepository.save(submission);
        }
    }

    private double extractTimeLimitFromConstraints(String constraints) {
        if (constraints == null) return 2.0; // Default 2 seconds
        
        // Try to extract time limit from constraints
        // Look for patterns like "Time: 1s", "1 second", "2000ms", etc.
        String lowerConstraints = constraints.toLowerCase();
        
        if (lowerConstraints.contains("1 second") || lowerConstraints.contains("1s")) return 1.0;
        if (lowerConstraints.contains("2 second") || lowerConstraints.contains("2s")) return 2.0;
        if (lowerConstraints.contains("3 second") || lowerConstraints.contains("3s")) return 3.0;
        if (lowerConstraints.contains("5 second") || lowerConstraints.contains("5s")) return 5.0;
        
        // For large input constraints, set higher time limits
        if (lowerConstraints.contains("10^6") || lowerConstraints.contains("1000000")) return 3.0;
        if (lowerConstraints.contains("10^5") || lowerConstraints.contains("100000")) return 2.0;
        if (lowerConstraints.contains("10^4") || lowerConstraints.contains("10000")) return 1.0;
        
        return 2.0; // Default 2 seconds
    }

    private void updateUserSolvedProblems(String userId, String problemId, boolean solved) {
        try {
            Optional<User> userOpt = userService.getUserById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                
                // Initialize solved problems list if null
                if (user.getSolvedProblems() == null) {
                    user.setSolvedProblems(new ArrayList<>());
                }
                
                // Update total submissions
                user.setTotalSubmissions(user.getTotalSubmissions() + 1);
                
                if (solved) {
                    // Add to solved problems if not already present
                    if (!user.getSolvedProblems().contains(problemId)) {
                        user.getSolvedProblems().add(problemId);
                        user.setTotalSolved(user.getTotalSolved() + 1);
                    }
                }
                
                user.setUpdatedAt(LocalDateTime.now());
                userService.saveUser(user);
            }
        } catch (Exception e) {
            // Log error but don't fail the submission
            System.err.println("Error updating user solved problems: " + e.getMessage());
        }
    }

    public List<Submission> getUserSubmissions(String userId) {
        return submissionRepository.findByUserIdOrderBySubmittedAtDesc(userId);
    }

    public List<Submission> getProblemSubmissions(String problemId) {
        return submissionRepository.findByProblemIdOrderBySubmittedAtDesc(problemId);
    }

    public Optional<Submission> getSubmissionById(String id) {
        return submissionRepository.findById(id);
    }

    public List<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    public Map<String, Object> runCodeAgainstExamples(SubmissionDto submissionDto, String userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Get problem details
            Optional<Problem> problemOpt = problemService.getProblemById(submissionDto.getProblemId());
            if (!problemOpt.isPresent()) {
                result.put("error", "Problem not found");
                return result;
            }

            Problem problem = problemOpt.get();
            
            // Check if problem has examples
            if (problem.getExamples() == null || problem.getExamples().isEmpty()) {
                result.put("error", "No example test cases found for this problem");
                return result;
            }

            StringBuilder outputBuilder = new StringBuilder();
            StringBuilder errorBuilder = new StringBuilder();
            boolean allExamplesPassed = true;
            int passedExamples = 0;
            double maxExecutionTime = 0.0;
            int maxMemoryUsage = 0;

            outputBuilder.append("=== Running Example Test Cases ===\n\n");

            // Run against example test cases only
            for (int i = 0; i < problem.getExamples().size(); i++) {
                Problem.Example example = problem.getExamples().get(i);
                outputBuilder.append(String.format("Example %d:\n", i + 1));
                outputBuilder.append(String.format("Input: %s\n", example.getInput()));
                outputBuilder.append(String.format("Expected Output: %s\n", example.getOutput()));
                
                try {
                    // Convert example input to test case format
                    String testInput = convertExampleInputToTestFormat(example.getInput());
                    
                    // Wrap user code with main function and input/output handling
                    String wrappedCode = codeWrapperService.wrapCodeForProblem(
                        submissionDto.getSourceCode(),
                        submissionDto.getLanguage(),
                        problem.getTitle(),
                        "" // Method signature - can be enhanced later
                    );
                    
                    // Submit wrapped code to Judge0
                    String token = judge0Service.submitCode(
                        wrappedCode,
                        submissionDto.getLanguageId(),
                        testInput
                    );

                    // Wait for execution
                    Thread.sleep(2000);

                    // Get result
                    Map<String, Object> judgeResult = judge0Service.getSubmissionResult(token);
                    
                    String status = (String) judgeResult.get("status");
                    Integer statusId = (Integer) judgeResult.get("statusId");
                    Double executionTime = (Double) judgeResult.get("executionTime");
                    Integer memoryUsage = (Integer) judgeResult.get("memoryUsage");

                    // Track max execution time and memory
                    if (executionTime != null) {
                        maxExecutionTime = Math.max(maxExecutionTime, executionTime);
                    }
                    if (memoryUsage != null) {
                        maxMemoryUsage = Math.max(maxMemoryUsage, memoryUsage);
                    }

                    // Handle different Judge0 status codes
                    switch (statusId) {
                        case 3: // Accepted
                            String actualOutput = (String) judgeResult.get("output");
                            String expectedOutput = example.getOutput().trim();
                            String cleanActualOutput = actualOutput != null ? actualOutput.trim() : "";
                            
                            if (cleanActualOutput.equals(expectedOutput)) {
                                passedExamples++;
                                outputBuilder.append("✅ Passed\n");
                                outputBuilder.append(String.format("Your Output: %s\n", cleanActualOutput));
                                if (executionTime != null) {
                                    outputBuilder.append(String.format("Runtime: %.3fs\n", executionTime));
                                }
                                if (memoryUsage != null) {
                                    outputBuilder.append(String.format("Memory: %d KB\n", memoryUsage));
                                }
                            } else {
                                allExamplesPassed = false;
                                outputBuilder.append("❌ Failed\n");
                                outputBuilder.append(String.format("Your Output: %s\n", cleanActualOutput));
                                outputBuilder.append("Output doesn't match expected result.\n");
                            }
                            break;

                        case 6: // Compilation Error
                            allExamplesPassed = false;
                            String compileError = (String) judgeResult.get("compileError");
                            outputBuilder.append("❌ Compilation Error\n");
                            errorBuilder.append("Compilation Error:\n").append(compileError != null ? compileError : "Unknown compilation error");
                            break;

                        case 7: case 8: case 9: case 10: case 11: case 12: // Runtime Errors
                            allExamplesPassed = false;
                            String runtimeError = (String) judgeResult.get("error");
                            outputBuilder.append("❌ Runtime Error\n");
                            errorBuilder.append("Runtime Error:\n").append(runtimeError != null ? runtimeError : "Unknown runtime error");
                            break;

                        case 5: // Time Limit Exceeded
                            allExamplesPassed = false;
                            outputBuilder.append("❌ Time Limit Exceeded\n");
                            errorBuilder.append("Time Limit Exceeded: Your code is taking too long to execute.\n");
                            break;

                        default:
                            allExamplesPassed = false;
                            outputBuilder.append("❌ Error\n");
                            outputBuilder.append("Status: ").append(status).append("\n");
                            errorBuilder.append("Execution Error: ").append(status);
                    }

                } catch (Exception e) {
                    allExamplesPassed = false;
                    outputBuilder.append("❌ Execution Error\n");
                    outputBuilder.append("Error: ").append(e.getMessage()).append("\n");
                    errorBuilder.append("Execution Error: ").append(e.getMessage()).append("\n");
                }

                outputBuilder.append("\n");

                // If compilation error, stop processing further examples
                if (errorBuilder.toString().contains("Compilation Error")) {
                    break;
                }
            }

            // Prepare result
            result.put("success", true);
            result.put("allExamplesPassed", allExamplesPassed);
            result.put("examplesPassed", passedExamples);
            result.put("totalExamples", problem.getExamples().size());
            result.put("output", outputBuilder.toString());
            result.put("error", errorBuilder.toString());
            result.put("executionTime", maxExecutionTime);
            result.put("memoryUsage", maxMemoryUsage);
            result.put("language", submissionDto.getLanguage());

            // Add appropriate message
            if (allExamplesPassed) {
                result.put("message", "✅ All example test cases passed! You can now submit your solution.");
                result.put("status", "SUCCESS");
            } else if (errorBuilder.toString().contains("Compilation Error")) {
                result.put("message", "🔧 Compilation Error. Please fix syntax errors before running.");
                result.put("status", "COMPILATION_ERROR");
            } else if (errorBuilder.toString().contains("Runtime Error")) {
                result.put("message", "💥 Runtime Error. Check your code for potential issues.");
                result.put("status", "RUNTIME_ERROR");
            } else if (errorBuilder.toString().contains("Time Limit Exceeded")) {
                result.put("message", "⏰ Time Limit Exceeded. Consider optimizing your algorithm.");
                result.put("status", "TIME_LIMIT_EXCEEDED");
            } else {
                result.put("message", String.format("❌ %d/%d example test cases passed. Check your logic.", passedExamples, problem.getExamples().size()));
                result.put("status", "WRONG_ANSWER");
            }

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Failed to run code: " + e.getMessage());
            result.put("message", "❌ Failed to run code. Please try again.");
        }

        return result;
    }

    private String convertExampleInputToTestFormat(String exampleInput) {
        // Convert example input format to actual test input format
        // This method handles different input formats from examples
        
        if (exampleInput == null) return "";
        
        // Handle Two Sum format: "nums = [2,7,11,15], target = 9"
        if (exampleInput.contains("nums") && exampleInput.contains("target")) {
            StringBuilder result = new StringBuilder();
            
            // Extract array part
            int arrayStart = exampleInput.indexOf("[");
            int arrayEnd = exampleInput.indexOf("]");
            if (arrayStart != -1 && arrayEnd != -1) {
                String arrayPart = exampleInput.substring(arrayStart, arrayEnd + 1);
                result.append(arrayPart).append("\n");
            }
            
            // Extract target part
            String[] parts = exampleInput.split(",");
            for (String part : parts) {
                if (part.trim().startsWith("target")) {
                    String targetValue = part.replaceAll("target\\s*=\\s*", "").trim();
                    result.append(targetValue);
                    break;
                }
            }
            
            return result.toString();
        }
        
        // Handle Linked List format: "head = [1,2,3,4,5]"
        if (exampleInput.contains("head") && exampleInput.contains("[")) {
            int arrayStart = exampleInput.indexOf("[");
            int arrayEnd = exampleInput.indexOf("]");
            if (arrayStart != -1 && arrayEnd != -1) {
                return exampleInput.substring(arrayStart, arrayEnd + 1);
            }
        }
        
        // Handle simple array format: "[1,2,3]"
        if (exampleInput.startsWith("[") && exampleInput.endsWith("]")) {
            return exampleInput;
        }
        
        // Handle other formats - try to extract meaningful parts
        String cleanInput = exampleInput
            .replaceAll("nums\\s*=\\s*", "")
            .replaceAll("target\\s*=\\s*", "")
            .replaceAll("head\\s*=\\s*", "")
            .replaceAll("root\\s*=\\s*", "")
            .replaceAll("s\\s*=\\s*", "")
            .replaceAll("\"", "");
        
        // If it contains array notation, preserve it
        if (cleanInput.contains("[") && cleanInput.contains("]")) {
            return cleanInput;
        }
        
        // Handle multiple inputs separated by comma (but not inside arrays)
        if (cleanInput.contains(",") && !cleanInput.matches(".*\\[.*\\].*")) {
            String[] parts = cleanInput.split(",");
            StringBuilder formatted = new StringBuilder();
            for (String part : parts) {
                formatted.append(part.trim()).append("\n");
            }
            return formatted.toString().trim();
        }
        
        return cleanInput.trim();
    }
}