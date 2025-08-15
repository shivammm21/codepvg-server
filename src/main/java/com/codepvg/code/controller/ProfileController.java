package com.codepvg.code.controller;

import com.codepvg.code.dto.ProfileUpdateDto;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private ProblemService problemService;

    // Get current user's profile
    @GetMapping
    public ResponseEntity<?> getProfile(Authentication auth) {
        try {
            String username = auth.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (!userOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.badRequest().body(error);
            }
            
            User user = userOpt.get();
            
            // Get comprehensive profile data
            Map<String, Object> profile = getComprehensiveProfile(user);
            
            return ResponseEntity.ok(profile);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get profile: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Update user profile
    @PutMapping
    public ResponseEntity<?> updateProfile(@Valid @RequestBody ProfileUpdateDto profileDto, 
                                         Authentication auth) {
        try {
            String currentUsername = auth.getName();
            Optional<User> userOpt = userService.findByUsername(currentUsername);
            
            if (!userOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.badRequest().body(error);
            }
            
            User user = userOpt.get();
            
            // Check if username is being changed and if it's unique
            if (profileDto.getUsername() != null && 
                !profileDto.getUsername().equals(user.getUsername())) {
                
                if (userService.existsByUsername(profileDto.getUsername())) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Username already exists");
                    return ResponseEntity.badRequest().body(error);
                }
                user.setUsername(profileDto.getUsername());
            }
            
            // Check if email is being changed and if it's unique
            if (profileDto.getEmail() != null && 
                !profileDto.getEmail().equals(user.getEmail())) {
                
                if (userService.existsByEmail(profileDto.getEmail())) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Email already exists");
                    return ResponseEntity.badRequest().body(error);
                }
                user.setEmail(profileDto.getEmail());
            }
            
            // Update other profile fields
            if (profileDto.getFullName() != null) {
                user.setFullName(profileDto.getFullName());
            }
            
            if (profileDto.getBio() != null) {
                user.setBio(profileDto.getBio());
            }
            
            if (profileDto.getHeadline() != null) {
                user.setHeadline(profileDto.getHeadline());
            }
            
            if (profileDto.getLinkedinUrl() != null) {
                // Validate LinkedIn URL format
                if (!profileDto.getLinkedinUrl().isEmpty() && 
                    !isValidLinkedInUrl(profileDto.getLinkedinUrl())) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Invalid LinkedIn URL format");
                    return ResponseEntity.badRequest().body(error);
                }
                user.setLinkedinUrl(profileDto.getLinkedinUrl());
            }
            
            if (profileDto.getGithubUrl() != null) {
                // Validate GitHub URL format
                if (!profileDto.getGithubUrl().isEmpty() && 
                    !isValidGitHubUrl(profileDto.getGithubUrl())) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Invalid GitHub URL format");
                    return ResponseEntity.badRequest().body(error);
                }
                user.setGithubUrl(profileDto.getGithubUrl());
            }
            
            // Update timestamp
            user.setUpdatedAt(java.time.LocalDateTime.now());
            
            // Save updated user
            User updatedUser = userService.saveUser(user);
            
            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Profile updated successfully");
            response.put("profile", Map.of(
                "id", updatedUser.getId(),
                "username", updatedUser.getUsername(),
                "email", updatedUser.getEmail(),
                "fullName", updatedUser.getFullName(),
                "bio", updatedUser.getBio() != null ? updatedUser.getBio() : "",
                "headline", updatedUser.getHeadline() != null ? updatedUser.getHeadline() : "",
                "linkedinUrl", updatedUser.getLinkedinUrl() != null ? updatedUser.getLinkedinUrl() : "",
                "githubUrl", updatedUser.getGithubUrl() != null ? updatedUser.getGithubUrl() : "",
                "updatedAt", updatedUser.getUpdatedAt()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update profile: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get public profile by username
    @GetMapping("/public/{username}")
    public ResponseEntity<?> getPublicProfile(@PathVariable String username) {
        try {
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (!userOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.badRequest().body(error);
            }
            
            User user = userOpt.get();
            
            // Get comprehensive profile data (same as private profile but exclude sensitive info)
            Map<String, Object> publicProfile = getComprehensiveProfile(user);
            
            // Remove sensitive information for public access
            publicProfile.remove("id");
            publicProfile.remove("email");
            
            return ResponseEntity.ok(publicProfile);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get public profile: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get comprehensive profile data with detailed statistics
    private Map<String, Object> getComprehensiveProfile(User user) {
        Map<String, Object> profile = new HashMap<>();
        
        // Basic profile information
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("email", user.getEmail());
        profile.put("fullName", user.getFullName());
        profile.put("year", user.getYear());
        profile.put("branch", user.getBranch());
        profile.put("bio", user.getBio() != null ? user.getBio() : "");
        profile.put("headline", user.getHeadline() != null ? user.getHeadline() : "");
        profile.put("linkedinUrl", user.getLinkedinUrl() != null ? user.getLinkedinUrl() : "");
        profile.put("githubUrl", user.getGithubUrl() != null ? user.getGithubUrl() : "");
        profile.put("role", user.getRole());
        profile.put("joinedDate", user.getCreatedAt());
        profile.put("lastActive", user.getUpdatedAt());
        
        // Get all user submissions for detailed analysis
        List<Submission> allSubmissions = submissionService.getUserSubmissions(user.getId());
        List<Problem> allProblems = problemService.getAllProblems();
        
        // Basic statistics
        profile.put("totalSolved", user.getTotalSolved());
        profile.put("totalSubmissions", user.getTotalSubmissions());
        
        // Calculate acceptance rate
        double acceptanceRate = user.getTotalSubmissions() > 0 
            ? (double) user.getTotalSolved() / user.getTotalSubmissions() * 100 
            : 0.0;
        profile.put("acceptanceRate", Math.round(acceptanceRate * 100.0) / 100.0);
        
        // Get solved problems by difficulty
        Map<String, Object> solvedByDifficulty = getSolvedProblemsByDifficulty(user, allProblems);
        profile.put("solvedByDifficulty", solvedByDifficulty);
        
        // Get submission statistics
        Map<String, Object> submissionStats = getSubmissionStatistics(allSubmissions);
        profile.put("submissionStats", submissionStats);
        
        // Get recent submissions (last 10)
        List<Map<String, Object>> recentSubmissions = getRecentSubmissions(allSubmissions, 10);
        profile.put("recentSubmissions", recentSubmissions);
        
        // Get solved problems timeline (last 365 days)
        Map<String, Object> solvedProblemsTimeline = getSolvedProblemsTimeline(allSubmissions, allProblems);
        profile.put("solvedProblemsTimeline", solvedProblemsTimeline);
        
        // Get language statistics
        Map<String, Object> languageStats = getLanguageStatistics(allSubmissions);
        profile.put("languageStats", languageStats);
        
        // Get ranking information
        Map<String, Object> ranking = getUserRanking(user);
        profile.put("ranking", ranking);
        
        // Get solved problems list with details
        List<Map<String, Object>> solvedProblems = getSolvedProblemsDetails(user, allProblems);
        profile.put("solvedProblems", solvedProblems);
        
        // Get badges/achievements
        List<Map<String, Object>> badges = getUserBadges(user, allSubmissions);
        profile.put("badges", badges);
        
        return profile;
    }
    
    private Map<String, Object> getSolvedProblemsByDifficulty(User user, List<Problem> allProblems) {
        Map<String, Object> result = new HashMap<>();
        
        // Get solved problem IDs
        List<String> solvedProblemIds = user.getSolvedProblems() != null 
            ? user.getSolvedProblems() 
            : new ArrayList<>();
        
        // Count total problems by difficulty
        Map<String, Integer> totalByDifficulty = allProblems.stream()
            .collect(Collectors.groupingBy(
                p -> p.getDifficulty().toString(),
                Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
            ));
        
        // Count solved problems by difficulty
        Map<String, Integer> solvedByDifficulty = allProblems.stream()
            .filter(p -> solvedProblemIds.contains(p.getId()))
            .collect(Collectors.groupingBy(
                p -> p.getDifficulty().toString(),
                Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
            ));
        
        // Create difficulty breakdown
        Map<String, Object> easy = new HashMap<>();
        easy.put("solved", solvedByDifficulty.getOrDefault("EASY", 0));
        easy.put("total", totalByDifficulty.getOrDefault("EASY", 0));
        easy.put("percentage", calculatePercentage(solvedByDifficulty.getOrDefault("EASY", 0), 
                                                 totalByDifficulty.getOrDefault("EASY", 0)));
        
        Map<String, Object> medium = new HashMap<>();
        medium.put("solved", solvedByDifficulty.getOrDefault("MEDIUM", 0));
        medium.put("total", totalByDifficulty.getOrDefault("MEDIUM", 0));
        medium.put("percentage", calculatePercentage(solvedByDifficulty.getOrDefault("MEDIUM", 0), 
                                                   totalByDifficulty.getOrDefault("MEDIUM", 0)));
        
        Map<String, Object> hard = new HashMap<>();
        hard.put("solved", solvedByDifficulty.getOrDefault("HARD", 0));
        hard.put("total", totalByDifficulty.getOrDefault("HARD", 0));
        hard.put("percentage", calculatePercentage(solvedByDifficulty.getOrDefault("HARD", 0), 
                                                 totalByDifficulty.getOrDefault("HARD", 0)));
        
        result.put("easy", easy);
        result.put("medium", medium);
        result.put("hard", hard);
        result.put("totalSolved", user.getTotalSolved());
        result.put("totalProblems", allProblems.size());
        
        return result;
    }
    
    private Map<String, Object> getSubmissionStatistics(List<Submission> submissions) {
        Map<String, Object> stats = new HashMap<>();
        
        // Count by status
        Map<String, Long> statusCounts = submissions.stream()
            .collect(Collectors.groupingBy(
                s -> s.getStatus().toString(),
                Collectors.counting()
            ));
        
        stats.put("total", submissions.size());
        stats.put("accepted", statusCounts.getOrDefault("ACCEPTED", 0L));
        stats.put("wrongAnswer", statusCounts.getOrDefault("WRONG_ANSWER", 0L));
        stats.put("timeLimitExceeded", statusCounts.getOrDefault("TIME_LIMIT_EXCEEDED", 0L));
        stats.put("runtimeError", statusCounts.getOrDefault("RUNTIME_ERROR", 0L));
        stats.put("compilationError", statusCounts.getOrDefault("COMPILATION_ERROR", 0L));
        stats.put("memoryLimitExceeded", statusCounts.getOrDefault("MEMORY_LIMIT_EXCEEDED", 0L));
        
        // Calculate average execution time
        double avgExecutionTime = submissions.stream()
            .filter(s -> s.getExecutionTime() != null && s.getExecutionTime() > 0)
            .mapToDouble(Submission::getExecutionTime)
            .average()
            .orElse(0.0);
        stats.put("averageExecutionTime", Math.round(avgExecutionTime * 1000.0) / 1000.0);
        
        // Calculate average memory usage
        double avgMemoryUsage = submissions.stream()
            .filter(s -> s.getMemoryUsage() != null && s.getMemoryUsage() > 0)
            .mapToDouble(Submission::getMemoryUsage)
            .average()
            .orElse(0.0);
        stats.put("averageMemoryUsage", Math.round(avgMemoryUsage * 100.0) / 100.0);
        
        return stats;
    }
    
    private List<Map<String, Object>> getRecentSubmissions(List<Submission> submissions, int limit) {
        return submissions.stream()
            .limit(limit)
            .map(submission -> {
                Map<String, Object> submissionData = new HashMap<>();
                submissionData.put("id", submission.getId());
                submissionData.put("problemId", submission.getProblemId());
                submissionData.put("status", submission.getStatus());
                submissionData.put("language", submission.getLanguage());
                submissionData.put("submittedAt", submission.getSubmittedAt());
                submissionData.put("executionTime", submission.getExecutionTime());
                submissionData.put("memoryUsage", submission.getMemoryUsage());
                submissionData.put("testCasesPassed", submission.getTestCasesPassed());
                submissionData.put("totalTestCases", submission.getTotalTestCases());
                return submissionData;
            })
            .collect(Collectors.toList());
    }
    
    private Map<String, Object> getSolvedProblemsTimeline(List<Submission> submissions, List<Problem> allProblems) {
        Map<String, Object> timeline = new HashMap<>();
        
        // Get only accepted submissions from last 365 days
        LocalDateTime oneYearAgo = LocalDateTime.now().minusDays(365);
        LocalDateTime now = LocalDateTime.now();
        
        // Filter to only accepted submissions (solved problems) from last year
        List<Submission> solvedSubmissions = submissions.stream()
            .filter(s -> s.getStatus() == Submission.SubmissionStatus.ACCEPTED)
            .filter(s -> s.getSubmittedAt().isAfter(oneYearAgo))
            .collect(Collectors.toList());
        
        // Group solved problems by date (only first solve counts)
        Map<String, List<Map<String, Object>>> dailySolvedProblems = new LinkedHashMap<>();
        
        // Track which problems have been solved to avoid duplicates
        Set<String> alreadySolved = new HashSet<>();
        
        // Sort submissions by date to get first solve
        solvedSubmissions.sort((a, b) -> a.getSubmittedAt().compareTo(b.getSubmittedAt()));
        
        for (Submission submission : solvedSubmissions) {
            String problemId = submission.getProblemId();
            
            // Only count first time solving each problem
            if (!alreadySolved.contains(problemId)) {
                alreadySolved.add(problemId);
                
                String dateKey = submission.getSubmittedAt().toLocalDate().toString();
                
                // Find problem details
                Problem problem = allProblems.stream()
                    .filter(p -> p.getId().equals(problemId))
                    .findFirst()
                    .orElse(null);
                
                if (problem != null) {
                    Map<String, Object> solvedProblem = new HashMap<>();
                    solvedProblem.put("problemId", problem.getId());
                    solvedProblem.put("title", problem.getTitle());
                    solvedProblem.put("difficulty", problem.getDifficulty());
                    solvedProblem.put("language", submission.getLanguage());
                    solvedProblem.put("solvedAt", submission.getSubmittedAt());
                    
                    dailySolvedProblems.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(solvedProblem);
                }
            }
        }
        
        // Create timeline structure for calendar visualization
        Map<String, Object> calendarData = createSolvedProblemsCalendar(dailySolvedProblems, oneYearAgo, now);
        
        // Calculate statistics
        long totalProblemsSolved = alreadySolved.size();
        long activeDays = dailySolvedProblems.size();
        long maxStreak = calculateSolvingStreak(dailySolvedProblems);
        long currentStreak = calculateCurrentSolvingStreak(dailySolvedProblems);
        
        timeline.put("calendar", calendarData);
        timeline.put("dailySolvedProblems", dailySolvedProblems);
        timeline.put("totalProblemsSolved", totalProblemsSolved);
        timeline.put("activeDays", activeDays);
        timeline.put("maxStreak", maxStreak);
        timeline.put("currentStreak", currentStreak);
        
        // Add summary text
        timeline.put("summaryText", totalProblemsSolved + " problems solved in the past one year");
        timeline.put("streakText", "Total active days: " + activeDays + "    Max streak: " + maxStreak);
        
        return timeline;
    }
    
    private Map<String, Object> createSolvedProblemsCalendar(Map<String, List<Map<String, Object>>> dailySolvedProblems,
                                                           LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> calendar = new HashMap<>();
        
        // Create a list of only days when problems were solved
        List<Map<String, Object>> solvedDays = new ArrayList<>();
        
        // Sort dates to maintain chronological order
        List<String> sortedDates = dailySolvedProblems.keySet().stream()
            .sorted()
            .collect(Collectors.toList());
        
        for (String dateKey : sortedDates) {
            List<Map<String, Object>> solvedProblems = dailySolvedProblems.get(dateKey);
            
            // Only include days where problems were actually solved
            if (!solvedProblems.isEmpty()) {
                Map<String, Object> dayData = new HashMap<>();
                dayData.put("date", dateKey);
                dayData.put("problemsSolved", solvedProblems);
                dayData.put("count", solvedProblems.size());
                dayData.put("level", getSolvingLevel(solvedProblems.size()));
                
                // Parse date to get day info
                LocalDateTime dayDate = LocalDateTime.parse(dateKey + "T00:00:00");
                dayData.put("dayOfWeek", dayDate.getDayOfWeek().getValue());
                dayData.put("dayName", dayDate.getDayOfWeek().toString());
                dayData.put("monthName", dayDate.getMonth().toString());
                dayData.put("formattedDate", dayDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
                
                // Add tooltip information with problem titles
                List<String> problemTitles = solvedProblems.stream()
                    .map(p -> (String) p.get("title"))
                    .collect(Collectors.toList());
                dayData.put("tooltip", String.join(", ", problemTitles));
                
                solvedDays.add(dayData);
            }
        }
        
        // Create month-wise grouping for better organization
        Map<String, List<Map<String, Object>>> monthlyData = solvedDays.stream()
            .collect(Collectors.groupingBy(
                day -> {
                    LocalDateTime dayDate = LocalDateTime.parse((String) day.get("date") + "T00:00:00");
                    return dayDate.format(DateTimeFormatter.ofPattern("MMM yyyy"));
                },
                LinkedHashMap::new,
                Collectors.toList()
            ));
        
        // Create timeline entries for easy frontend consumption
        List<Map<String, Object>> timelineEntries = new ArrayList<>();
        for (Map<String, Object> day : solvedDays) {
            List<Map<String, Object>> problemsSolved = (List<Map<String, Object>>) day.get("problemsSolved");
            
            for (Map<String, Object> problem : problemsSolved) {
                Map<String, Object> entry = new HashMap<>();
                entry.put("date", day.get("date"));
                entry.put("formattedDate", day.get("formattedDate"));
                entry.put("problemId", problem.get("problemId"));
                entry.put("title", problem.get("title"));
                entry.put("difficulty", problem.get("difficulty"));
                entry.put("language", problem.get("language"));
                entry.put("solvedAt", problem.get("solvedAt"));
                
                timelineEntries.add(entry);
            }
        }
        
        calendar.put("solvedDays", solvedDays);
        calendar.put("monthlyData", monthlyData);
        calendar.put("timelineEntries", timelineEntries);
        calendar.put("startDate", startDate.toLocalDate().toString());
        calendar.put("endDate", endDate.toLocalDate().toString());
        
        return calendar;
    }
    
    private int getSolvingLevel(int problemsSolved) {
        if (problemsSolved == 0) return 0;
        if (problemsSolved == 1) return 1;
        if (problemsSolved == 2) return 2;
        if (problemsSolved <= 4) return 3;
        return 4; // 5+ problems solved in one day
    }
    
    private long calculateSolvingStreak(Map<String, List<Map<String, Object>>> dailySolvedProblems) {
        if (dailySolvedProblems.isEmpty()) return 0;
        
        List<String> sortedDates = dailySolvedProblems.keySet().stream()
            .sorted()
            .collect(Collectors.toList());
        
        long maxStreak = 1;
        long currentStreak = 1;
        
        for (int i = 1; i < sortedDates.size(); i++) {
            if (isConsecutiveDay(sortedDates.get(i-1), sortedDates.get(i))) {
                currentStreak++;
                maxStreak = Math.max(maxStreak, currentStreak);
            } else {
                currentStreak = 1;
            }
        }
        
        return maxStreak;
    }
    
    private long calculateCurrentSolvingStreak(Map<String, List<Map<String, Object>>> dailySolvedProblems) {
        if (dailySolvedProblems.isEmpty()) return 0;
        
        String today = LocalDateTime.now().toLocalDate().toString();
        String yesterday = LocalDateTime.now().minusDays(1).toLocalDate().toString();
        
        if (!dailySolvedProblems.containsKey(today) && !dailySolvedProblems.containsKey(yesterday)) {
            return 0;
        }
        
        // Calculate streak from today backwards
        long streak = 0;
        LocalDateTime currentDate = LocalDateTime.now().toLocalDate().atStartOfDay();
        
        while (dailySolvedProblems.containsKey(currentDate.toLocalDate().toString())) {
            streak++;
            currentDate = currentDate.minusDays(1);
        }
        
        return streak;
    }

    private Map<String, Object> getSubmissionCalendar(List<Submission> submissions) {
        Map<String, Object> calendar = new HashMap<>();
        
        // Get submissions from last 365 days
        LocalDateTime oneYearAgo = LocalDateTime.now().minusDays(365);
        LocalDateTime now = LocalDateTime.now();
        
        // Filter submissions from last year
        List<Submission> lastYearSubmissions = submissions.stream()
            .filter(s -> s.getSubmittedAt().isAfter(oneYearAgo))
            .collect(Collectors.toList());
        
        // Group submissions by date
        Map<String, Long> dailySubmissions = lastYearSubmissions.stream()
            .collect(Collectors.groupingBy(
                s -> s.getSubmittedAt().toLocalDate().toString(),
                Collectors.counting()
            ));
        
        // Create timeline structure for GitHub-style calendar
        Map<String, Object> timeline = createTimelineStructure(dailySubmissions, oneYearAgo, now);
        
        // Calculate statistics
        long totalSubmissionsLastYear = dailySubmissions.values().stream()
            .mapToLong(Long::longValue)
            .sum();
        
        long activeDays = dailySubmissions.size();
        long maxStreak = calculateMaxStreak(submissions);
        long currentStreak = calculateCurrentStreak(submissions);
        
        // Calculate contribution levels (0-4 scale like GitHub)
        Map<String, Integer> contributionLevels = calculateContributionLevels(dailySubmissions);
        
        calendar.put("timeline", timeline);
        calendar.put("dailySubmissions", dailySubmissions);
        calendar.put("contributionLevels", contributionLevels);
        calendar.put("totalSubmissions", totalSubmissionsLastYear);
        calendar.put("activeDays", activeDays);
        calendar.put("maxStreak", maxStreak);
        calendar.put("currentStreak", currentStreak);
        
        // Add summary text like "351 submissions in the past one year"
        calendar.put("summaryText", totalSubmissionsLastYear + " submissions in the past one year");
        calendar.put("streakText", "Total active days: " + activeDays + "    Max streak: " + maxStreak);
        
        return calendar;
    }
    
    private Map<String, Object> createTimelineStructure(Map<String, Long> dailySubmissions, 
                                                       LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> timeline = new HashMap<>();
        
        // Create month-wise structure
        Map<String, List<Map<String, Object>>> monthlyData = new LinkedHashMap<>();
        
        LocalDateTime current = startDate;
        while (current.isBefore(endDate) || current.isEqual(endDate.toLocalDate().atStartOfDay())) {
            String monthKey = current.format(DateTimeFormatter.ofPattern("MMM yyyy"));
            String dateKey = current.toLocalDate().toString();
            
            // Get submission count for this date
            long submissionCount = dailySubmissions.getOrDefault(dateKey, 0L);
            
            // Create day data
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", dateKey);
            dayData.put("count", submissionCount);
            dayData.put("level", getContributionLevel(submissionCount));
            dayData.put("dayOfWeek", current.getDayOfWeek().getValue()); // 1=Monday, 7=Sunday
            dayData.put("weekOfYear", current.format(DateTimeFormatter.ofPattern("w")));
            
            // Add to monthly data
            monthlyData.computeIfAbsent(monthKey, k -> new ArrayList<>()).add(dayData);
            
            current = current.plusDays(1);
        }
        
        // Create weeks structure for calendar grid (like GitHub)
        List<List<Map<String, Object>>> weeks = createWeeksStructure(dailySubmissions, startDate, endDate);
        
        timeline.put("months", monthlyData);
        timeline.put("weeks", weeks);
        timeline.put("startDate", startDate.toLocalDate().toString());
        timeline.put("endDate", endDate.toLocalDate().toString());
        
        return timeline;
    }
    
    private List<List<Map<String, Object>>> createWeeksStructure(Map<String, Long> dailySubmissions,
                                                               LocalDateTime startDate, LocalDateTime endDate) {
        List<List<Map<String, Object>>> weeks = new ArrayList<>();
        List<Map<String, Object>> currentWeek = new ArrayList<>();
        
        // Start from the beginning of the week containing startDate
        LocalDateTime current = startDate.toLocalDate().atStartOfDay();
        
        // Adjust to start of week (Monday)
        while (current.getDayOfWeek().getValue() != 1) {
            current = current.minusDays(1);
        }
        
        while (current.isBefore(endDate.plusDays(7))) {
            String dateKey = current.toLocalDate().toString();
            long submissionCount = dailySubmissions.getOrDefault(dateKey, 0L);
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", dateKey);
            dayData.put("count", submissionCount);
            dayData.put("level", getContributionLevel(submissionCount));
            dayData.put("dayOfWeek", current.getDayOfWeek().getValue());
            dayData.put("isCurrentYear", current.isAfter(startDate) && current.isBefore(endDate));
            
            currentWeek.add(dayData);
            
            // If it's Sunday (7) or we've reached 7 days, start a new week
            if (current.getDayOfWeek().getValue() == 7 || currentWeek.size() == 7) {
                weeks.add(new ArrayList<>(currentWeek));
                currentWeek.clear();
            }
            
            current = current.plusDays(1);
        }
        
        // Add remaining days if any
        if (!currentWeek.isEmpty()) {
            weeks.add(currentWeek);
        }
        
        return weeks;
    }
    
    private Map<String, Integer> calculateContributionLevels(Map<String, Long> dailySubmissions) {
        Map<String, Integer> levels = new HashMap<>();
        
        if (dailySubmissions.isEmpty()) {
            return levels;
        }
        
        // Find max submissions in a day
        long maxSubmissions = dailySubmissions.values().stream()
            .mapToLong(Long::longValue)
            .max()
            .orElse(0L);
        
        // Calculate thresholds for contribution levels (0-4 scale)
        double level1Threshold = Math.max(1, maxSubmissions * 0.25);
        double level2Threshold = Math.max(2, maxSubmissions * 0.50);
        double level3Threshold = Math.max(3, maxSubmissions * 0.75);
        
        for (Map.Entry<String, Long> entry : dailySubmissions.entrySet()) {
            levels.put(entry.getKey(), getContributionLevel(entry.getValue()));
        }
        
        return levels;
    }
    
    private int getContributionLevel(long submissionCount) {
        if (submissionCount == 0) return 0;
        if (submissionCount <= 2) return 1;
        if (submissionCount <= 5) return 2;
        if (submissionCount <= 10) return 3;
        return 4; // 10+ submissions
    }
    
    private Map<String, Object> getLanguageStatistics(List<Submission> submissions) {
        Map<String, Long> languageCounts = submissions.stream()
            .collect(Collectors.groupingBy(
                Submission::getLanguage,
                Collectors.counting()
            ));
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("languageBreakdown", languageCounts);
        
        // Find most used language
        String mostUsedLanguage = languageCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("None");
        stats.put("mostUsedLanguage", mostUsedLanguage);
        
        return stats;
    }
    
    private Map<String, Object> getUserRanking(User user) {
        Map<String, Object> ranking = new HashMap<>();
        
        // Get all approved students for ranking
        List<User> allStudents = userService.getApprovedStudents();
        
        // Sort by total solved (descending), then by total submissions (ascending)
        allStudents.sort((a, b) -> {
            int solvedComparison = Integer.compare(b.getTotalSolved(), a.getTotalSolved());
            if (solvedComparison != 0) return solvedComparison;
            return Integer.compare(a.getTotalSubmissions(), b.getTotalSubmissions());
        });
        
        // Find user's rank
        int userRank = -1;
        for (int i = 0; i < allStudents.size(); i++) {
            if (allStudents.get(i).getId().equals(user.getId())) {
                userRank = i + 1;
                break;
            }
        }
        
        ranking.put("globalRank", userRank);
        ranking.put("totalUsers", allStudents.size());
        ranking.put("percentile", userRank > 0 ? 
            Math.round((1.0 - (double) userRank / allStudents.size()) * 10000.0) / 100.0 : 0.0);
        
        return ranking;
    }
    
    private List<Map<String, Object>> getSolvedProblemsDetails(User user, List<Problem> allProblems) {
        List<String> solvedProblemIds = user.getSolvedProblems() != null 
            ? user.getSolvedProblems() 
            : new ArrayList<>();
        
        return allProblems.stream()
            .filter(p -> solvedProblemIds.contains(p.getId()))
            .map(problem -> {
                Map<String, Object> problemData = new HashMap<>();
                problemData.put("id", problem.getId());
                problemData.put("title", problem.getTitle());
                problemData.put("difficulty", problem.getDifficulty());
                problemData.put("topics", problem.getTopics());
                problemData.put("tags", problem.getTags());
                return problemData;
            })
            .collect(Collectors.toList());
    }
    
    private List<Map<String, Object>> getUserBadges(User user, List<Submission> submissions) {
        List<Map<String, Object>> badges = new ArrayList<>();
        
        // 100 Days Badge (if user has submissions in 100+ different days)
        LocalDateTime oneYearAgo = LocalDateTime.now().minusDays(365);
        long uniqueDays = submissions.stream()
            .filter(s -> s.getSubmittedAt().isAfter(oneYearAgo))
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
        
        // Problem Solver Badges
        if (user.getTotalSolved() >= 50) {
            Map<String, Object> badge = new HashMap<>();
            badge.put("name", "Problem Solver");
            badge.put("description", "Solved 50+ problems");
            badge.put("icon", "🎯");
            badge.put("earnedDate", LocalDateTime.now());
            badges.add(badge);
        }
        
        // Language Master Badge (if user has solved problems in 3+ languages)
        long languageCount = submissions.stream()
            .filter(s -> s.getStatus() == Submission.SubmissionStatus.ACCEPTED)
            .map(Submission::getLanguage)
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
        
        return badges;
    }
    
    // Helper methods
    private double calculatePercentage(int solved, int total) {
        if (total == 0) return 0.0;
        return Math.round((double) solved / total * 10000.0) / 100.0;
    }
    
    private long calculateMaxStreak(List<Submission> submissions) {
        if (submissions.isEmpty()) return 0;
        
        Set<String> submissionDates = submissions.stream()
            .map(s -> s.getSubmittedAt().toLocalDate().toString())
            .collect(Collectors.toSet());
        
        List<String> sortedDates = submissionDates.stream()
            .sorted()
            .collect(Collectors.toList());
        
        long maxStreak = 1;
        long currentStreak = 1;
        
        for (int i = 1; i < sortedDates.size(); i++) {
            // Check if dates are consecutive
            if (isConsecutiveDay(sortedDates.get(i-1), sortedDates.get(i))) {
                currentStreak++;
                maxStreak = Math.max(maxStreak, currentStreak);
            } else {
                currentStreak = 1;
            }
        }
        
        return maxStreak;
    }
    
    private long calculateCurrentStreak(List<Submission> submissions) {
        if (submissions.isEmpty()) return 0;
        
        Set<String> submissionDates = submissions.stream()
            .map(s -> s.getSubmittedAt().toLocalDate().toString())
            .collect(Collectors.toSet());
        
        String today = LocalDateTime.now().toLocalDate().toString();
        String yesterday = LocalDateTime.now().minusDays(1).toLocalDate().toString();
        
        if (!submissionDates.contains(today) && !submissionDates.contains(yesterday)) {
            return 0;
        }
        
        // Calculate streak from today backwards
        long streak = 0;
        LocalDateTime currentDate = LocalDateTime.now().toLocalDate().atStartOfDay();
        
        while (submissionDates.contains(currentDate.toLocalDate().toString())) {
            streak++;
            currentDate = currentDate.minusDays(1);
        }
        
        return streak;
    }
    
    private boolean isConsecutiveDay(String date1, String date2) {
        try {
            LocalDateTime d1 = LocalDateTime.parse(date1 + "T00:00:00");
            LocalDateTime d2 = LocalDateTime.parse(date2 + "T00:00:00");
            return d2.toLocalDate().equals(d1.toLocalDate().plusDays(1));
        } catch (Exception e) {
            return false;
        }
    }

    // Helper methods for URL validation
    private boolean isValidLinkedInUrl(String url) {
        return url.matches("^https?://(www\\.)?linkedin\\.com/in/[a-zA-Z0-9-]+/?$");
    }

    private boolean isValidGitHubUrl(String url) {
        return url.matches("^https?://(www\\.)?github\\.com/[a-zA-Z0-9-]+/?$");
    }
}