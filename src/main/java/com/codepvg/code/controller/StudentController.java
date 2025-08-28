package com.codepvg.code.controller;

import com.codepvg.code.dto.SubmissionDto;
import com.codepvg.code.model.Problem;
import com.codepvg.code.model.Submission;
import com.codepvg.code.model.User;
import com.codepvg.code.service.CodeWrapperService;
import com.codepvg.code.service.ProblemService;
import com.codepvg.code.service.SubmissionService;
import com.codepvg.code.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "http://localhost:3000")
public class StudentController {

    @Autowired
    private ProblemService problemService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private CodeWrapperService codeWrapperService;

    // Dashboard API
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(Authentication auth) {
        try {
            String username = auth.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (!userOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.badRequest().body(error);
            }
            
            User user = userOpt.get();
            List<Problem> allProblems = problemService.getAllProblems();
            List<Submission> userSubmissions = submissionService.getUserSubmissions(user.getId());
            
            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "fullName", user.getFullName(),
                "totalSolved", user.getTotalSolved(),
                "totalSubmissions", user.getTotalSubmissions()
            ));
            dashboard.put("totalProblems", allProblems.size());
            dashboard.put("recentSubmissions", userSubmissions.subList(0, Math.min(5, userSubmissions.size())));
            
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get dashboard: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Problems APIs
    @GetMapping("/problems")
    public ResponseEntity<List<Problem>> getAllProblems() {
        List<Problem> problems = problemService.getAllProblems();
        return ResponseEntity.ok(problems);
    }

    @GetMapping("/problems/{problemId}")
    public ResponseEntity<?> getProblemById(@PathVariable String problemId) {
        Optional<Problem> problemOpt = problemService.getProblemById(problemId);
        if (problemOpt.isPresent()) {
            return ResponseEntity.ok(problemOpt.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Problem not found");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/problems/title/{title}")
    public ResponseEntity<?> getProblemByTitle(@PathVariable String title) {
        try {
            // Decode URL-encoded title (replace %20 with spaces, etc.)
            String decodedTitle = java.net.URLDecoder.decode(title, "UTF-8");
            Optional<Problem> problemOpt = problemService.getProblemByTitle(decodedTitle);
            
            if (problemOpt.isPresent()) {
                return ResponseEntity.ok(problemOpt.get());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Problem not found with title: " + decodedTitle);
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get problem: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/problems/search")
    public ResponseEntity<List<Problem>> searchProblems(@RequestParam String query) {
        List<Problem> problems = problemService.searchProblems(query);
        return ResponseEntity.ok(problems);
    }

    @GetMapping("/problems/difficulty/{difficulty}")
    public ResponseEntity<List<Problem>> getProblemsByDifficulty(@PathVariable String difficulty) {
        try {
            Problem.Difficulty diff = Problem.Difficulty.valueOf(difficulty.toUpperCase());
            List<Problem> problems = problemService.getProblemsByDifficulty(diff);
            return ResponseEntity.ok(problems);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Submission APIs
    @PostMapping("/submissions")
    public ResponseEntity<?> submitCode(@Valid @RequestBody SubmissionDto submissionDto, 
                                      Authentication auth) {
        try {
            String username = auth.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (!userOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.badRequest().body(error);
            }
            
            User user = userOpt.get();
            Submission submission = submissionService.submitCode(submissionDto, user.getId());
            
            return ResponseEntity.ok(submission);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to submit code: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/submissions/debug")
    public ResponseEntity<?> debugCode(@Valid @RequestBody SubmissionDto submissionDto, 
                                     Authentication auth) {
        try {
            String username = auth.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (!userOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Get problem details for debugging
            Optional<Problem> problemOpt = problemService.getProblemById(submissionDto.getProblemId());
            if (!problemOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Problem not found");
                return ResponseEntity.badRequest().body(error);
            }
            
            Problem problem = problemOpt.get();
            
            // Show what we're working with
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("problemTitle", problem.getTitle());
            debugInfo.put("examples", problem.getExamples());
            debugInfo.put("userCode", submissionDto.getSourceCode());
            
            // Show wrapped code
            String wrappedCode = codeWrapperService.wrapCodeForProblem(
                submissionDto.getSourceCode(),
                submissionDto.getLanguage(),
                problem.getTitle(),
                ""
            );
            debugInfo.put("wrappedCode", wrappedCode);
            
            return ResponseEntity.ok(debugInfo);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Debug failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/submissions/run")
    public ResponseEntity<?> runCode(@Valid @RequestBody SubmissionDto submissionDto, 
                                   Authentication auth) {
        try {
            String username = auth.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (!userOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.badRequest().body(error);
            }
            
            User user = userOpt.get();
            
            // Run code against example test cases only (no submission record)
            Map<String, Object> result = submissionService.runCodeAgainstExamples(submissionDto, user.getId());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to run code: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/submissions/execute")
    public ResponseEntity<?> executeCode(@Valid @RequestBody SubmissionDto submissionDto, 
                                       Authentication auth) {
        try {
            String username = auth.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (!userOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.badRequest().body(error);
            }
            
            User user = userOpt.get();
            
            // Execute code and get comprehensive results
            Submission submission = submissionService.submitCode(submissionDto, user.getId());
            
            // Wait for execution to complete
            int maxWaitTime = 30; // 30 seconds max wait
            int waitTime = 0;
            
            while (submission.getStatus() == Submission.SubmissionStatus.PENDING && waitTime < maxWaitTime) {
                Thread.sleep(1000);
                waitTime++;
                Optional<Submission> updatedSubmission = submissionService.getSubmissionById(submission.getId());
                if (updatedSubmission.isPresent()) {
                    submission = updatedSubmission.get();
                }
            }
            
            // Prepare comprehensive response
            Map<String, Object> response = new HashMap<>();
            response.put("submissionId", submission.getId());
            response.put("status", submission.getStatus());
            response.put("testCasesPassed", submission.getTestCasesPassed());
            response.put("totalTestCases", submission.getTotalTestCases());
            response.put("executionTime", submission.getExecutionTime());
            response.put("memoryUsage", submission.getMemoryUsage());
            response.put("output", submission.getOutput());
            response.put("error", submission.getError());
            response.put("language", submission.getLanguage());
            response.put("submittedAt", submission.getSubmittedAt());
            
            // Add success message if all test cases passed
            if (submission.getStatus() == Submission.SubmissionStatus.ACCEPTED) {
                response.put("message", "🎉 Congratulations! All test cases passed!");
                response.put("problemSolved", true);
                
                // Get updated user stats
                Optional<User> updatedUser = userService.getUserById(user.getId());
                if (updatedUser.isPresent()) {
                    response.put("userStats", Map.of(
                        "totalSolved", updatedUser.get().getTotalSolved(),
                        "totalSubmissions", updatedUser.get().getTotalSubmissions()
                    ));
                }
            } else {
                response.put("problemSolved", false);
                
                // Add helpful error messages based on status
                switch (submission.getStatus()) {
                    case TIME_LIMIT_EXCEEDED:
                        response.put("message", "⏰ Time Limit Exceeded! Try optimizing your algorithm.");
                        response.put("hint", "Consider using more efficient data structures or algorithms with better time complexity.");
                        break;
                    case MEMORY_LIMIT_EXCEEDED:
                        response.put("message", "💾 Memory Limit Exceeded! Your solution uses too much memory.");
                        response.put("hint", "Try to optimize memory usage by using more efficient data structures.");
                        break;
                    case WRONG_ANSWER:
                        response.put("message", "❌ Wrong Answer. Some test cases failed.");
                        response.put("hint", "Check your logic and edge cases. Review the problem constraints.");
                        break;
                    case COMPILATION_ERROR:
                        response.put("message", "🔧 Compilation Error. Please fix syntax errors.");
                        response.put("hint", "Check for syntax errors, missing semicolons, or incorrect variable declarations.");
                        break;
                    case RUNTIME_ERROR:
                        response.put("message", "💥 Runtime Error occurred during execution.");
                        response.put("hint", "Check for array bounds, null pointer exceptions, or division by zero.");
                        break;
                    default:
                        response.put("message", "❓ Submission completed with status: " + submission.getStatus());
                }
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to execute code: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/submissions")
    public ResponseEntity<?> getMySubmissions(Authentication auth) {
        try {
            String username = auth.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (!userOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.badRequest().body(error);
            }
            
            User user = userOpt.get();
            List<Submission> submissions = submissionService.getUserSubmissions(user.getId());
            
            return ResponseEntity.ok(submissions);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get submissions: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/submissions/{submissionId}")
    public ResponseEntity<?> getSubmissionById(@PathVariable String submissionId, 
                                             Authentication auth) {
        try {
            Optional<Submission> submissionOpt = submissionService.getSubmissionById(submissionId);
            
            if (!submissionOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Submission not found");
                return ResponseEntity.badRequest().body(error);
            }
            
            Submission submission = submissionOpt.get();
            
            // Check if the submission belongs to the current user
            String username = auth.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (!userOpt.isPresent() || !submission.getUserId().equals(userOpt.get().getId())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Access denied");
                return ResponseEntity.badRequest().body(error);
            }
            
            return ResponseEntity.ok(submission);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get submission: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Leaderboard API
    @GetMapping("/leaderboard")
    public ResponseEntity<List<User>> getLeaderboard() {
        List<User> leaderboard = userService.getLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }
}