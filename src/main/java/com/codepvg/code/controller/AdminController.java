package com.codepvg.code.controller;

import com.codepvg.code.dto.ProblemCreateDto;
import com.codepvg.code.dto.UserProfile;
import com.codepvg.code.model.Problem;
import com.codepvg.code.model.Submission;
import com.codepvg.code.model.User;
import com.codepvg.code.service.ProblemService;
import com.codepvg.code.service.SubmissionService;
import com.codepvg.code.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private SubmissionService submissionService;

    // User Management APIs
    @GetMapping("/users/pending")
    public ResponseEntity<List<UserProfile>> getPendingUsers() {
        List<UserProfile> pendingUsers = userService.getPendingUsers();
        return ResponseEntity.ok(pendingUsers);
    }

    @PostMapping("/users/{userId}/approve")
    public ResponseEntity<?> approveUser(@PathVariable String userId) {
        try {
            UserProfile user = userService.approveUser(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User approved successfully");
            response.put("user", user);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/users/{userId}/reject")
    public ResponseEntity<?> rejectUser(@PathVariable String userId) {
        try {
            User user = userService.rejectUser(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User rejected successfully");
            response.put("user", user);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Problem Management APIs
    
    // API 1: Create Individual Problem with Full Details
    @PostMapping("/problems/create")
    public ResponseEntity<?> createProblemDetailed(@Valid @RequestBody ProblemCreateDto problemDto, 
                                                 Authentication auth) {
        try {
            String adminUsername = auth.getName();
            Problem createdProblem = problemService.createProblemFromDto(problemDto, adminUsername);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Problem created successfully");
            response.put("problem", createdProblem);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create problem: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // API 2: Import Problems from Excel Sheet
    @PostMapping("/problems/import")
    public ResponseEntity<?> importProblemsFromExcel(@RequestParam("file") MultipartFile file, 
                                                   Authentication auth) {
        try {
            if (file.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Please select a file to upload");
                return ResponseEntity.badRequest().body(error);
            }

            String adminUsername = auth.getName();
            List<Problem> problems = problemService.importProblemsFromExcel(file, adminUsername);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Problems imported successfully from Excel");
            response.put("count", problems.size());
            response.put("problems", problems);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to import problems: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Legacy API for backward compatibility
    @PostMapping("/problems")
    public ResponseEntity<?> createProblem(@RequestBody Problem problem, Authentication auth) {
        try {
            String adminUsername = auth.getName();
            Problem createdProblem = problemService.createProblem(problem, adminUsername);
            return ResponseEntity.ok(createdProblem);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create problem: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/problems/{problemId}")
    public ResponseEntity<?> updateProblem(@PathVariable String problemId, 
                                         @RequestBody Problem problemDetails) {
        try {
            Problem updatedProblem = problemService.updateProblem(problemId, problemDetails);
            return ResponseEntity.ok(updatedProblem);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/problems/{problemId}")
    public ResponseEntity<?> deleteProblem(@PathVariable String problemId) {
        try {
            problemService.deleteProblem(problemId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Problem deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete problem: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // GET APIs for Problem Management
    
    @GetMapping("/problems")
    public ResponseEntity<List<Problem>> getAllProblems() {
        List<Problem> problems = problemService.getAllProblems();
        return ResponseEntity.ok(problems);
    }

    @GetMapping("/problems/{problemId}")
    public ResponseEntity<?> getProblemById(@PathVariable String problemId) {
        try {
            Optional<Problem> problemOpt = problemService.getProblemById(problemId);
            if (problemOpt.isPresent()) {
                return ResponseEntity.ok(problemOpt.get());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Problem not found");
                return ResponseEntity.badRequest().body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get problem: " + e.getMessage());
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
    public ResponseEntity<?> getProblemsByDifficulty(@PathVariable String difficulty) {
        try {
            Problem.Difficulty diff = Problem.Difficulty.valueOf(difficulty.toUpperCase());
            List<Problem> problems = problemService.getProblemsByDifficulty(diff);
            return ResponseEntity.ok(problems);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid difficulty level. Use: EASY, MEDIUM, or HARD");
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/problems/topic/{topic}")
    public ResponseEntity<List<Problem>> getProblemsByTopic(@PathVariable String topic) {
        List<Problem> problems = problemService.getProblemsByTopic(topic);
        return ResponseEntity.ok(problems);
    }

    @GetMapping("/problems/created-by/{username}")
    public ResponseEntity<List<Problem>> getProblemsByCreator(@PathVariable String username) {
        List<Problem> problems = problemService.getProblemsByCreator(username);
        return ResponseEntity.ok(problems);
    }

    // Leaderboard and Student Analytics APIs
    
    @GetMapping("/leaderboard")
    public ResponseEntity<?> getLeaderboard() {
        try {
            List<User> leaderboard = userService.getLeaderboard();
            
            // Filter only students and add additional analytics
            List<Map<String, Object>> studentLeaderboard = new ArrayList<>();
            
            for (int i = 0; i < leaderboard.size(); i++) {
                User user = leaderboard.get(i);
                if (user.getRole() == User.Role.STUDENT && user.getStatus() == User.UserStatus.APPROVED) {
                    Map<String, Object> studentData = new HashMap<>();
                    studentData.put("rank", i + 1);
                    studentData.put("id", user.getId());
                    studentData.put("username", user.getUsername());
                    studentData.put("fullName", user.getFullName());
                    studentData.put("email", user.getEmail());
                    studentData.put("year", user.getYear());
                    studentData.put("branch", user.getBranch());
                    studentData.put("totalSolved", user.getTotalSolved());
                    studentData.put("totalSubmissions", user.getTotalSubmissions());
                    
                    // Calculate success rate
                    double successRate = user.getTotalSubmissions() > 0 
                        ? (double) user.getTotalSolved() / user.getTotalSubmissions() * 100 
                        : 0.0;
                    studentData.put("successRate", Math.round(successRate * 100.0) / 100.0);
                    
                    // Get recent activity
                    List<Submission> recentSubmissions = submissionService.getUserSubmissions(user.getId());
                    studentData.put("recentSubmissions", Math.min(recentSubmissions.size(), 5));
                    
                    // Calculate activity score (submissions in last 7 days)
                    long recentActivity = recentSubmissions.stream()
                        .filter(s -> s.getSubmittedAt().isAfter(java.time.LocalDateTime.now().minusDays(7)))
                        .count();
                    studentData.put("recentActivity", recentActivity);
                    
                    studentData.put("joinedDate", user.getCreatedAt());
                    studentData.put("lastActive", user.getUpdatedAt());
                    
                    studentLeaderboard.add(studentData);
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("leaderboard", studentLeaderboard);
            response.put("totalStudents", studentLeaderboard.size());
            response.put("generatedAt", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get leaderboard: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/leaderboard/detailed")
    public ResponseEntity<?> getDetailedLeaderboard() {
        try {
            List<User> allStudents = userService.getApprovedStudents();
            List<Map<String, Object>> detailedLeaderboard = new ArrayList<>();
            
            for (int i = 0; i < allStudents.size(); i++) {
                User user = allStudents.get(i);
                Map<String, Object> studentData = new HashMap<>();
                
                // Basic info
                studentData.put("rank", i + 1);
                studentData.put("id", user.getId());
                studentData.put("username", user.getUsername());
                studentData.put("fullName", user.getFullName());
                studentData.put("email", user.getEmail());
                studentData.put("totalSolved", user.getTotalSolved());
                studentData.put("totalSubmissions", user.getTotalSubmissions());
                
                // Get all submissions for detailed analysis
                List<Submission> allSubmissions = submissionService.getUserSubmissions(user.getId());
                
                // Calculate detailed statistics
                long acceptedSubmissions = allSubmissions.stream()
                    .filter(s -> s.getStatus() == Submission.SubmissionStatus.ACCEPTED)
                    .count();
                
                long wrongAnswers = allSubmissions.stream()
                    .filter(s -> s.getStatus() == Submission.SubmissionStatus.WRONG_ANSWER)
                    .count();
                
                long timeoutErrors = allSubmissions.stream()
                    .filter(s -> s.getStatus() == Submission.SubmissionStatus.TIME_LIMIT_EXCEEDED)
                    .count();
                
                long runtimeErrors = allSubmissions.stream()
                    .filter(s -> s.getStatus() == Submission.SubmissionStatus.RUNTIME_ERROR)
                    .count();
                
                long compilationErrors = allSubmissions.stream()
                    .filter(s -> s.getStatus() == Submission.SubmissionStatus.COMPILATION_ERROR)
                    .count();
                
                // Calculate average execution time
                double avgExecutionTime = allSubmissions.stream()
                    .filter(s -> s.getExecutionTime() != null && s.getExecutionTime() > 0)
                    .mapToDouble(Submission::getExecutionTime)
                    .average()
                    .orElse(0.0);
                
                // Get solved problems list
                List<String> solvedProblems = user.getSolvedProblems() != null 
                    ? user.getSolvedProblems() 
                    : new ArrayList<>();
                
                // Performance metrics
                studentData.put("acceptedSubmissions", acceptedSubmissions);
                studentData.put("wrongAnswers", wrongAnswers);
                studentData.put("timeoutErrors", timeoutErrors);
                studentData.put("runtimeErrors", runtimeErrors);
                studentData.put("compilationErrors", compilationErrors);
                studentData.put("averageExecutionTime", Math.round(avgExecutionTime * 1000.0) / 1000.0);
                studentData.put("solvedProblemsCount", solvedProblems.size());
                studentData.put("solvedProblems", solvedProblems);
                
                // Success rate
                double successRate = user.getTotalSubmissions() > 0 
                    ? (double) user.getTotalSolved() / user.getTotalSubmissions() * 100 
                    : 0.0;
                studentData.put("successRate", Math.round(successRate * 100.0) / 100.0);
                
                // Activity metrics
                long submissionsLast7Days = allSubmissions.stream()
                    .filter(s -> s.getSubmittedAt().isAfter(java.time.LocalDateTime.now().minusDays(7)))
                    .count();
                
                long submissionsLast30Days = allSubmissions.stream()
                    .filter(s -> s.getSubmittedAt().isAfter(java.time.LocalDateTime.now().minusDays(30)))
                    .count();
                
                studentData.put("submissionsLast7Days", submissionsLast7Days);
                studentData.put("submissionsLast30Days", submissionsLast30Days);
                studentData.put("joinedDate", user.getCreatedAt());
                studentData.put("lastActive", user.getUpdatedAt());
                
                // Get most recent submission
                if (!allSubmissions.isEmpty()) {
                    Submission lastSubmission = allSubmissions.get(0);
                    studentData.put("lastSubmission", Map.of(
                        "problemId", lastSubmission.getProblemId(),
                        "status", lastSubmission.getStatus(),
                        "submittedAt", lastSubmission.getSubmittedAt(),
                        "language", lastSubmission.getLanguage()
                    ));
                }
                
                detailedLeaderboard.add(studentData);
            }
            
            // Sort by total solved (descending), then by total submissions (ascending)
            detailedLeaderboard.sort((a, b) -> {
                int solvedA = (Integer) a.get("totalSolved");
                int solvedB = (Integer) b.get("totalSolved");
                if (solvedA != solvedB) {
                    return Integer.compare(solvedB, solvedA); // Descending
                }
                int submissionsA = (Integer) a.get("totalSubmissions");
                int submissionsB = (Integer) b.get("totalSubmissions");
                return Integer.compare(submissionsA, submissionsB); // Ascending
            });
            
            // Update ranks after sorting
            for (int i = 0; i < detailedLeaderboard.size(); i++) {
                detailedLeaderboard.get(i).put("rank", i + 1);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("leaderboard", detailedLeaderboard);
            response.put("totalStudents", detailedLeaderboard.size());
            response.put("generatedAt", java.time.LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get detailed leaderboard: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/students/{studentId}/performance")
    public ResponseEntity<?> getStudentPerformance(@PathVariable String studentId) {
        try {
            Optional<User> userOpt = userService.getUserById(studentId);
            if (!userOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Student not found");
                return ResponseEntity.badRequest().body(error);
            }
            
            User student = userOpt.get();
            if (student.getRole() != User.Role.STUDENT) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User is not a student");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Get all submissions
            List<Submission> submissions = submissionService.getUserSubmissions(studentId);
            
            // Calculate performance metrics
            Map<String, Object> performance = new HashMap<>();
            performance.put("studentInfo", Map.of(
                "id", student.getId(),
                "username", student.getUsername(),
                "fullName", student.getFullName(),
                "email", student.getEmail(),
                "joinedDate", student.getCreatedAt()
            ));
            
            performance.put("overallStats", Map.of(
                "totalSolved", student.getTotalSolved(),
                "totalSubmissions", student.getTotalSubmissions(),
                "successRate", student.getTotalSubmissions() > 0 
                    ? Math.round((double) student.getTotalSolved() / student.getTotalSubmissions() * 10000.0) / 100.0 
                    : 0.0
            ));
            
            // Submission status breakdown
            Map<String, Long> statusBreakdown = new HashMap<>();
            statusBreakdown.put("accepted", submissions.stream()
                .filter(s -> s.getStatus() == Submission.SubmissionStatus.ACCEPTED).count());
            statusBreakdown.put("wrongAnswer", submissions.stream()
                .filter(s -> s.getStatus() == Submission.SubmissionStatus.WRONG_ANSWER).count());
            statusBreakdown.put("timeLimit", submissions.stream()
                .filter(s -> s.getStatus() == Submission.SubmissionStatus.TIME_LIMIT_EXCEEDED).count());
            statusBreakdown.put("runtimeError", submissions.stream()
                .filter(s -> s.getStatus() == Submission.SubmissionStatus.RUNTIME_ERROR).count());
            statusBreakdown.put("compilationError", submissions.stream()
                .filter(s -> s.getStatus() == Submission.SubmissionStatus.COMPILATION_ERROR).count());
            
            performance.put("statusBreakdown", statusBreakdown);
            
            // Language usage
            Map<String, Long> languageUsage = submissions.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    Submission::getLanguage,
                    java.util.stream.Collectors.counting()
                ));
            performance.put("languageUsage", languageUsage);
            
            // Recent activity (last 30 days)
            List<Submission> recentSubmissions = submissions.stream()
                .filter(s -> s.getSubmittedAt().isAfter(java.time.LocalDateTime.now().minusDays(30)))
                .limit(10)
                .collect(java.util.stream.Collectors.toList());
            
            performance.put("recentActivity", recentSubmissions.stream().map(s -> Map.of(
                "id", s.getId(),
                "problemId", s.getProblemId(),
                "status", s.getStatus(),
                "language", s.getLanguage(),
                "submittedAt", s.getSubmittedAt(),
                "executionTime", s.getExecutionTime() != null ? s.getExecutionTime() : 0.0
            )).collect(java.util.stream.Collectors.toList()));
            
            // Solved problems
            performance.put("solvedProblems", student.getSolvedProblems() != null 
                ? student.getSolvedProblems() 
                : new ArrayList<>());
            
            return ResponseEntity.ok(performance);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get student performance: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Analytics APIs
    @GetMapping("/analytics/dashboard")
    public ResponseEntity<?> getDashboardAnalytics() {
        try {
            Map<String, Object> analytics = new HashMap<>();
            
            List<User> allUsers = userService.getAllUsers();
            List<Problem> allProblems = problemService.getAllProblems();
            
            analytics.put("totalUsers", allUsers.size());
            analytics.put("pendingUsers", userService.getPendingUsers().size());
            analytics.put("totalProblems", allProblems.size());
            analytics.put("totalSubmissions", submissionService.getAllSubmissions().size());
            
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get analytics: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Helper method to generate default code templates
    private Problem.CodeTemplates generateDefaultCodeTemplates(String problemTitle, String functionName) {
        Problem.CodeTemplates templates = new Problem.CodeTemplates();
        
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
            "#include <string>\n" +
            "#include <algorithm>\n" +
            "using namespace std;\n\n" +
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
        
        // Python Template
        templates.setPythonTemplate(
            "# Problem: " + problemTitle + "\n" +
            "# Complete the function below\n\n" +
            "class MySolution:\n" +
            "    def " + functionName + "(self):\n" +
            "        \"\"\"\n" +
            "        Write your solution here\n" +
            "        \"\"\"\n" +
            "        # TODO: Implement your logic\n" +
            "        pass\n\n" +
            "# Test your solution\n" +
            "if __name__ == \"__main__\":\n" +
            "    solution = MySolution()\n" +
            "    # Test your solution here\n" +
            "    pass"
        );
        
        // Java Template
        templates.setJavaTemplate(
            "import java.util.*;\n" +
            "import java.io.*;\n\n" +
            "// Problem: " + problemTitle + "\n" +
            "// Complete the function below\n\n" +
            "class MySolution {\n" +
            "    // Write your solution here\n" +
            "    public int " + functionName + "() {\n" +
            "        // TODO: Implement your logic\n" +
            "        return 0;\n" +
            "    }\n" +
            "}\n\n" +
            "public class Main {\n" +
            "    public static void main(String[] args) {\n" +
            "        MySolution solution = new MySolution();\n" +
            "        // Test your solution\n" +
            "    }\n" +
            "}"
        );
        
        return templates;
    }

    // Helper method to validate target years
    private boolean isValidTargetYear(String year) {
        return year != null && (
            year.equalsIgnoreCase("first") ||
            year.equalsIgnoreCase("second") ||
            year.equalsIgnoreCase("third") ||
            year.equalsIgnoreCase("final")
        );
    }
}