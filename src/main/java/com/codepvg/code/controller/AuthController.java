package com.codepvg.code.controller;

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
@CrossOrigin(origins = "*")
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
            response.put("message", "User registered successfully. Waiting for admin approval.");
            response.put("userId", user.getId());
            response.put("status", user.getStatus());
            
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
            Optional<User> userOpt = userService.findByUsername(loginDto.getUsername());
            
            if (!userOpt.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid username or password");
                return ResponseEntity.badRequest().body(error);
            }
            
            User user = userOpt.get();
            
            if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid username or password");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (user.getStatus() != User.UserStatus.APPROVED) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Account not approved yet. Please wait for admin approval.");
                return ResponseEntity.badRequest().body(error);
            }
            
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
            
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
}