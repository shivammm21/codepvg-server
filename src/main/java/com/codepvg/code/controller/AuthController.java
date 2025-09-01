package com.codepvg.code.controller;

import com.codepvg.code.dto.AdminRegistrationDto;
import com.codepvg.code.dto.LoginDto;
import com.codepvg.code.dto.UserRegistrationDto;
import com.codepvg.code.model.User;
import com.codepvg.code.security.JwtUtil;
import com.codepvg.code.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        try {
            User user = userService.registerUser(registrationDto);
            
            Map<String, Object> response = new HashMap<>();
            
            // Check if user is automatically approved
            if (user.getStatus() == User.UserStatus.APPROVED) {
                // Generate JWT token for approved user
                String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
                response.put("token", token);
                response.put("message", "User registered successfully and approved automatically.");
                
                // Add user object similar to login response
                response.put("user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "fullName", user.getFullName(),
                    "role", user.getRole(),
                    "year", user.getYear() != null ? user.getYear() : "",
                    "branch", user.getBranch() != null ? user.getBranch() : "",
                    "prnNumber", user.getPrnNumber() != null ? user.getPrnNumber() : "",
                    "totalSolved", user.getTotalSolved(),
                    "totalSubmissions", user.getTotalSubmissions()
                ));
            } else {
                response.put("message", "User registered successfully. Waiting for admin approval.");
            }
            
            response.put("userId", user.getId());
            response.put("status", user.getStatus());
            response.put("role", user.getRole());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("fullName", user.getFullName());
            response.put("department", user.getBranch());
            response.put("createdAt", user.getCreatedAt());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginDto loginDto) {
        try {
            Optional<User> userOpt = userService.findByEmail(loginDto.getEmail());
            
            if (!userOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid email or password");
                return ResponseEntity.badRequest().body(error);
            }
            
            User user = userOpt.get();
            
            if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid email or password");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (user.getStatus() != User.UserStatus.APPROVED) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Account not approved yet. Please wait for admin approval.");
                return ResponseEntity.badRequest().body(error);
            }
            
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "fullName", user.getFullName(),
                "role", user.getRole(),
                "totalSolved", user.getTotalSolved(),
                "totalSubmissions", user.getTotalSubmissions()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Login failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody AdminRegistrationDto registrationDto) {
        try {
            User admin = userService.registerAdmin(registrationDto);
            
            // Generate JWT token for the newly registered admin
            String token = jwtUtil.generateToken(admin.getUsername(), admin.getRole().name());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Admin registered successfully and approved automatically.");
            response.put("token", token);
            response.put("adminId", admin.getId());
            response.put("username", admin.getUsername());
            response.put("email", admin.getEmail());
            response.put("fullName", admin.getFullName());
            response.put("department", admin.getBranch());
            response.put("role", admin.getRole());
            response.put("status", admin.getStatus());
            response.put("createdAt", admin.getCreatedAt());
            
            // Add user object similar to login response
            response.put("user", Map.of(
                "id", admin.getId(),
                "username", admin.getUsername(),
                "email", admin.getEmail(),
                "fullName", admin.getFullName(),
                "role", admin.getRole(),
                "department", admin.getBranch(),
                "totalSolved", admin.getTotalSolved(),
                "totalSubmissions", admin.getTotalSubmissions()
            ));
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/status/{username}")
    public ResponseEntity<?> getUserStatus(@PathVariable String username) {
        try {
            Optional<User> userOpt = userService.findByUsername(username);
            
            if (!userOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.badRequest().body(error);
            }
            
            User user = userOpt.get();
            
            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("fullName", user.getFullName());
            response.put("status", user.getStatus());
            response.put("role", user.getRole());
            response.put("registeredAt", user.getCreatedAt());
            
            // Add status-specific messages
            switch (user.getStatus()) {
                case PENDING:
                    response.put("message", "Your account is pending approval. Please wait for an admin to review your registration.");
                    response.put("canLogin", false);
                    break;
                case APPROVED:
                    response.put("message", "Your account has been approved. You can now login.");
                    response.put("canLogin", true);
                    break;
                case REJECTED:
                    response.put("message", "Your account has been rejected. Please contact the administrator for more information.");
                    response.put("canLogin", false);
                    break;
            }
            
            // Add additional info for students
            if (user.getRole() == User.Role.STUDENT) {
                response.put("year", user.getYear());
                response.put("branch", user.getBranch());
                response.put("prnNumber", user.getPrnNumber());
            } else if (user.getRole() == User.Role.ADMIN) {
                response.put("department", user.getBranch());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get user status: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/status/email/{email}")
    public ResponseEntity<?> getUserStatusByEmail(@PathVariable String email) {
        try {
            Optional<User> userOpt = userService.findByEmail(email);
            
            if (!userOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found with this email address");
                return ResponseEntity.badRequest().body(error);
            }
            
            User user = userOpt.get();
            
            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("fullName", user.getFullName());
            response.put("status", user.getStatus());
            response.put("role", user.getRole());
            response.put("registeredAt", user.getCreatedAt());
            
            // Add status-specific messages
            switch (user.getStatus()) {
                case PENDING:
                    response.put("message", "Your account is pending approval. Please wait for an admin to review your registration.");
                    response.put("canLogin", false);
                    break;
                case APPROVED:
                    response.put("message", "Your account has been approved. You can now login.");
                    response.put("canLogin", true);
                    break;
                case REJECTED:
                    response.put("message", "Your account has been rejected. Please contact the administrator for more information.");
                    response.put("canLogin", false);
                    break;
            }
            
            // Add additional info for students
            if (user.getRole() == User.Role.STUDENT) {
                response.put("year", user.getYear());
                response.put("branch", user.getBranch());
                response.put("prnNumber", user.getPrnNumber());
            } else if (user.getRole() == User.Role.ADMIN) {
                response.put("department", user.getBranch());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get user status: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}