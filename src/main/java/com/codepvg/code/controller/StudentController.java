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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;

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
            // JWT now uses email as subject (consistent with ProfileController)
            String email = auth.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            
            if (!userOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.badRequest().body(error);
            }
            
            User user = userOpt.get();
            List<Submission> userSubmissions = submissionService.getUserSubmissions(user.getId());
            
            // Compute rank among approved students (by totalSolved desc, then totalSubmissions asc)
            List<User> allStudents = userService.getApprovedStudents();
            allStudents.sort((a, b) -> {
                int solvedComparison = Integer.compare(b.getTotalSolved(), a.getTotalSolved());
                if (solvedComparison != 0) return solvedComparison;
                return Integer.compare(a.getTotalSubmissions(), b.getTotalSubmissions());
            });
            int userRank = -1;
            for (int i = 0; i < allStudents.size(); i++) {
                if (allStudents.get(i).getId().equals(user.getId())) {
                    userRank = i + 1;
                    break;
                }
            }
            Map<String, Object> rank = new HashMap<>();
            rank.put("globalRank", userRank);
            rank.put("totalUsers", allStudents.size());
            rank.put("percentile", userRank > 0 ?
                Math.round((1.0 - (double) userRank / Math.max(1, allStudents.size())) * 10000.0) / 100.0 : 0.0);

            // Compute badges
            List<Map<String, Object>> badges = new java.util.ArrayList<>();
            LocalDateTime oneYearAgo = LocalDateTime.now().minusDays(365);
            long uniqueDays = userSubmissions.stream()
                .filter(s -> s.getSubmittedAt() != null && s.getSubmittedAt().isAfter(oneYearAgo))
                .map(s -> s.getSubmittedAt().toLocalDate())
                .distinct()
                .count();
            if (uniqueDays >= 100) {
                Map<String, Object> badge = new HashMap<>();
                badge.put("name", "100 Days Badge 2025");
                badge.put("description", "Submitted solutions on 100+ different days");
                badge.put("icon", "🏆");
                badge.put("earnedDate", LocalDateTime.now());
                badges.add(badge);
            }
            if (user.getTotalSolved() >= 50) {
                Map<String, Object> badge = new HashMap<>();
                badge.put("name", "Problem Solver");
                badge.put("description", "Solved 50+ problems");
                badge.put("icon", "🎯");
                badge.put("earnedDate", LocalDateTime.now());
                badges.add(badge);
            }
            long languageCount = userSubmissions.stream()
                .filter(s -> s.getStatus() == Submission.SubmissionStatus.ACCEPTED)
                .map(Submission::getLanguage)
                .filter(l -> l != null)
                .distinct()
                .count();
            if (languageCount >= 3) {
                Map<String, Object> badge = new HashMap<>();
                badge.put("name", "Polyglot");
                badge.put("description", "Solved problems in 3+ programming languages");
                badge.put("icon", "🌐");
                badge.put("earnedDate", LocalDateTime.now());
                badges.add(badge);
            }

            // Weekly goal progress (current week Monday..Sunday)
            LocalDate today = LocalDate.now();
            LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
            LocalDate endOfWeek = startOfWeek.plusDays(6);
            Set<String> solvedThisWeek = new HashSet<>();
            for (Submission s : userSubmissions) {
                if (s.getSubmittedAt() == null) continue;
                LocalDate d = s.getSubmittedAt().toLocalDate();
                if (!d.isBefore(startOfWeek) && !d.isAfter(endOfWeek)
                    && s.getStatus() == Submission.SubmissionStatus.ACCEPTED) {
                    solvedThisWeek.add(s.getProblemId());
                }
            }
            int weeklyTarget = 7; // default target
            int weeklyCompleted = solvedThisWeek.size();
            double weeklyPercent = weeklyTarget > 0 ? Math.min(100.0, (double) weeklyCompleted / weeklyTarget * 100.0) : 0.0;
            Map<String, Object> weeklyGoal = new HashMap<>();
            weeklyGoal.put("target", weeklyTarget);
            weeklyGoal.put("completed", weeklyCompleted);
            weeklyGoal.put("percentage", Math.round(weeklyPercent * 100.0) / 100.0);
            weeklyGoal.put("weekStart", startOfWeek.toString());
            weeklyGoal.put("weekEnd", endOfWeek.toString());

            // Build response
            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("user", Map.of(
                "fullName", user.getFullName(),
                "branch", user.getBranch(),
                "email", user.getEmail(),
                "mobileNumber", user.getMobileNumber(),
                "year", user.getYear(),
                "prnNumber", user.getPrnNumber(),
                "bio", user.getBio() != null ? user.getBio() : "",
                "headline", user.getHeadline() != null ? user.getHeadline() : "",
                "linkedinUrl", user.getLinkedinUrl() != null ? user.getLinkedinUrl() : "",
                "githubUrl", user.getGithubUrl() != null ? user.getGithubUrl() : ""
            ));
            dashboard.put("totalProblemsSolved", user.getTotalSolved());
            dashboard.put("rank", rank);
            dashboard.put("badges", badges);
            dashboard.put("weeklyGoal", weeklyGoal);

            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get dashboard: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Problems APIs
    @GetMapping("/problems")
    public ResponseEntity<List<Map<String, Object>>> getAllProblems(Authentication auth) {
        List<Problem> problems = problemService.getAllProblems();

        // Determine solved problem ids for current user (if authenticated)
        Set<String> solvedSet = new HashSet<>();
        if (auth != null) {
            String email = auth.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            if (userOpt.isPresent() && userOpt.get().getSolvedProblems() != null) {
                solvedSet.addAll(userOpt.get().getSolvedProblems());
            }
        }
        List<Map<String, Object>> result = new ArrayList<>();
        int number = 1;
        for (Problem p : problems) {
            int submissions = p.getTotalSubmissions();
            int solved = p.getTotalSolved();
            double percentage = submissions > 0 ? (solved * 100.0) / submissions : 0.0;

            Map<String, Object> item = new HashMap<>();
            item.put("number", number++);
            item.put("id", p.getId());
            item.put("title", p.getTitle());
            item.put("difficulty", p.getDifficulty());
            item.put("topics", p.getTopics());
            item.put("tags", p.getTags());
            item.put("totalSubmissions", submissions);
            item.put("totalSolved", solved);
            item.put("targetYears", p.getTargetYears());
            item.put("solveCount", solved);
            item.put("solvePercentage", percentage);
            item.put("submissionCount", submissions);

            boolean isSolved = solvedSet.contains(p.getId());
            item.put("status", isSolved ? "SOLVED" : "UNSOLVED");
            item.put("isSolved", isSolved);
            result.add(item);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/problems/{problemId}")
    public ResponseEntity<?> getProblemById(@PathVariable String problemId, Authentication auth) {
        Optional<Problem> problemOpt = problemService.getProblemById(problemId);
        if (!problemOpt.isPresent()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Problem not found");
            return ResponseEntity.badRequest().body(error);
        }

        Problem p = problemOpt.get();

        // Build allSubmission: users who have ACCEPTED a submission for this problem
        List<Submission> submissions = submissionService.getProblemSubmissions(problemId);
        Map<String, Map<String, Object>> byUser = new HashMap<>();
        for (Submission s : submissions) {
            if (s.getStatus() == Submission.SubmissionStatus.ACCEPTED) {
                String uid = s.getUserId();
                Optional<User> uo = userService.getUserById(uid);
                if (uo.isPresent()) {
                    User u = uo.get();
                    Map<String, Object> row = new HashMap<>();
                    row.put("name", u.getFullName() != null && !u.getFullName().isBlank() ? u.getFullName() : u.getUsername());
                    row.put("email", u.getEmail());
                    row.put("year", u.getYear());
                    row.put("branch", u.getBranch());
                    row.put("language", s.getLanguage());
                    row.put("submissionId", s.getId());
                    byUser.put(uid, row); // keep one per user
                }
            }
        }
        List<Map<String, Object>> allSubmission = new ArrayList<>(byUser.values());

        // Determine if current user has solved this problem
        boolean isSolved = false;
        if (auth != null) {
            String email = auth.getName();
            Optional<User> meOpt = userService.findByEmail(email);
            if (meOpt.isPresent()) {
                User me = meOpt.get();
                List<String> solved = me.getSolvedProblems();
                isSolved = solved != null && solved.contains(problemId);
            }
        }

        // Compose problem response preserving original shape + additional fields
        Map<String, Object> problem = new HashMap<>();
        problem.put("id", p.getId());
        problem.put("title", p.getTitle());
        problem.put("description", p.getDescription());
        problem.put("constraints", p.getConstraints());
        problem.put("difficulty", p.getDifficulty());
        problem.put("topics", p.getTopics());
        problem.put("examples", p.getExamples());
        problem.put("testCases", p.getTestCases());
        problem.put("tags", p.getTags());
        problem.put("createdAt", p.getCreatedAt());
        problem.put("updatedAt", p.getUpdatedAt());
        problem.put("createdBy", p.getCreatedBy());
        problem.put("totalSubmissions", p.getTotalSubmissions());
        problem.put("totalSolved", p.getTotalSolved());
        problem.put("targetYears", p.getTargetYears());
        problem.put("codeTemplates", p.getCodeTemplates());
        problem.put("allSubmission", allSubmission);
        problem.put("status", isSolved ? "SOLVED" : "UNSOLVED");
        problem.put("isSolved", isSolved);

        return ResponseEntity.ok(problem);
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
            String email = auth.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            
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
            String email = auth.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            
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
            String email = auth.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            
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
            String email = auth.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            
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
            String email = auth.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            
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
            
            // Check if the submission belongs to the current user (email-based subject)
            String email = auth.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            
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

    @GetMapping("/submissions/{submissionId}/code")
    public ResponseEntity<?> getSubmissionCode(@PathVariable String submissionId,
                                               Authentication auth) {
        try {
            Optional<Submission> submissionOpt = submissionService.getSubmissionById(submissionId);
            if (!submissionOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Submission not found");
                return ResponseEntity.badRequest().body(error);
            }

            Submission submission = submissionOpt.get();

            // Access check: only owner can view code (email-based subject)
            String email = auth.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            if (!userOpt.isPresent() || !submission.getUserId().equals(userOpt.get().getId())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Access denied");
                return ResponseEntity.badRequest().body(error);
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("submissionId", submission.getId());
            payload.put("problemId", submission.getProblemId());
            payload.put("language", submission.getLanguage());
            payload.put("sourceCode", submission.getSourceCode());
            payload.put("submittedAt", submission.getSubmittedAt());

            return ResponseEntity.ok(payload);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get submission code: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Leaderboard API
    @GetMapping("/leaderboard")
    public ResponseEntity<List<User>> getLeaderboard() {
        List<User> leaderboard = userService.getLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/leaderboard/summary")
    public ResponseEntity<?> getLeaderboardSummary(Authentication auth) {
        try {
            // Fetch approved students and sort by totalSolved desc, then totalSubmissions asc
            List<User> allStudents = userService.getApprovedStudents();
            int totalUsers = allStudents.size();
            allStudents.sort((a, b) -> {
                int solvedComparison = Integer.compare(b.getTotalSolved(), a.getTotalSolved());
                if (solvedComparison != 0) return solvedComparison;
                return Integer.compare(a.getTotalSubmissions(), b.getTotalSubmissions());
            });

            // Resolve current user
            String email = auth != null ? auth.getName() : null;
            Optional<User> meOpt = (email != null) ? userService.findByEmail(email) : Optional.empty();
            User me = meOpt.orElse(null);

            // Compute current user's rank
            int yourRank = -1;
            if (me != null) {
                for (int i = 0; i < allStudents.size(); i++) {
                    if (allStudents.get(i).getId().equals(me.getId())) {
                        yourRank = i + 1;
                        break;
                    }
                }
            }

            // Compute current user's streak
            int currentStreak = 0;
            if (me != null) {
                List<Submission> mySubs = submissionService.getUserSubmissions(me.getId());
                currentStreak = computeCurrentStreak(mySubs);
            }

            // Build performers for ALL users (sorted already)
            List<Map<String, Object>> performers = new ArrayList<>();
            for (User u : allStudents) {
                List<Submission> subs = submissionService.getUserSubmissions(u.getId());
                int streak = computeCurrentStreak(subs);
                Map<String, Object> row = new HashMap<>();
                row.put("userId", u.getId());
                row.put("fullName", u.getFullName());
                row.put("branch", u.getBranch());
                row.put("year", u.getYear());
                row.put("solved", u.getTotalSolved());
                row.put("streak", streak);
                performers.add(row);
            }

            Map<String, Object> header = new HashMap<>();
            header.put("yourRank", yourRank);
            header.put("problemsSolved", me != null ? me.getTotalSolved() : 0);
            header.put("currentStreak", currentStreak);
            header.put("totalUsers", totalUsers);

            // Add daily activity for current user starting from Jan 1, 2025
            List<Map<String, Object>> activity = new ArrayList<>();
            if (me != null) {
                List<Submission> mySubs = submissionService.getUserSubmissions(me.getId());
                java.time.LocalDate today = java.time.LocalDate.now();
                java.time.LocalDate start = java.time.LocalDate.of(2025, 1, 1);
                Map<java.time.LocalDate, Integer> daily = computeDailyAcceptedCountsFrom(start, today, mySubs);
                for (java.time.LocalDate d = start; !d.isAfter(today); d = d.plusDays(1)) {
                    int c = daily.getOrDefault(d, 0);
                    Map<String, Object> day = new HashMap<>();
                    day.put("date", d.toString());
                    day.put("count", c);
                    activity.add(day);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("summary", header);
            response.put("activity", activity);
            response.put("performers", performers);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to load leaderboard: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Compute the current streak in days based on ACCEPTED submissions.
    // Streak counts consecutive days ending today with at least one accepted submission per day.
    private int computeCurrentStreak(List<Submission> submissions) {
        if (submissions == null || submissions.isEmpty()) return 0;
        // Collect unique dates with ACCEPTED submissions
        Set<java.time.LocalDate> days = new HashSet<>();
        for (Submission s : submissions) {
            if (s.getStatus() == Submission.SubmissionStatus.ACCEPTED && s.getSubmittedAt() != null) {
                days.add(s.getSubmittedAt().toLocalDate());
            }
        }
        if (days.isEmpty()) return 0;
        int streak = 0;
        java.time.LocalDate d = java.time.LocalDate.now();
        while (days.contains(d)) {
            streak++;
            d = d.minusDays(1);
        }
        return streak;
    }

    // Compute daily counts of ACCEPTED submissions between [start, end]
    private Map<java.time.LocalDate, Integer> computeDailyAcceptedCountsFrom(java.time.LocalDate start,
                                                                             java.time.LocalDate end,
                                                                             List<Submission> submissions) {
        Map<java.time.LocalDate, Integer> map = new HashMap<>();
        if (submissions == null) return map;
        for (Submission s : submissions) {
            if (s.getStatus() == Submission.SubmissionStatus.ACCEPTED && s.getSubmittedAt() != null) {
                java.time.LocalDate d = s.getSubmittedAt().toLocalDate();
                if (!d.isBefore(start) && !d.isAfter(end)) {
                    map.put(d, map.getOrDefault(d, 0) + 1);
                }
            }
        }
        return map;
    }
}