package com.codepvg.code.service;

import com.codepvg.code.dto.AdminRegistrationDto;
import com.codepvg.code.dto.UserProfile;
import com.codepvg.code.dto.UserRegistrationDto;
import com.codepvg.code.model.User;
import com.codepvg.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${admin.access.code}")
    private String adminAccessCode;

    public User registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        // Generate username from email (part before @)
        String username = registrationDto.getEmail().split("@")[0];
        
        // If username already exists, append a number
        String originalUsername = username;
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = originalUsername + counter;
            counter++;
        }
        
        user.setUsername(username);
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFullName(registrationDto.getFullName());
        user.setYear(registrationDto.getYear());
        user.setBranch(registrationDto.getBranch());
        user.setPrnNumber(registrationDto.getPrnNumber());
        user.setMobileNumber(registrationDto.getMobileNumber());

        if(registrationDto.getRole().equalsIgnoreCase("student")) {
            user.setRole(User.Role.STUDENT);
        } else if(registrationDto.getRole().equalsIgnoreCase("admin")) {
            user.setRole(User.Role.ADMIN);
        }

        user.setStatus(User.UserStatus.PENDING);


        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<UserProfile> getPendingUsers() {

        List<UserProfile> userProfiles = new ArrayList<>();

        List<User> users = userRepository.findByStatus(User.UserStatus.PENDING);
        for (User user : users) {
            UserProfile userProfile = new UserProfile();

            userProfile.setId(user.getId());
            userProfile.setUsername(user.getUsername());
            userProfile.setFullName(user.getFullName());
            userProfile.setEmail(user.getEmail());
            userProfile.setRole(user.getRole().name());
            userProfile.setPrnNumber(user.getPrnNumber());
            userProfile.setMobileNumber(user.getMobileNumber());
            userProfile.setYear(user.getYear());
            userProfile.setBranch(user.getBranch());
            userProfile.setCreatedAt(user.getCreatedAt());

            userProfiles.add(userProfile);
        }

        return userProfiles;
    }

    public UserProfile approveUser(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setStatus(User.UserStatus.APPROVED);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            UserProfile userProfiles = new UserProfile();
            userProfiles.setId(userId);
            userProfiles.setUsername(user.getUsername());
            userProfiles.setFullName(user.getFullName());
            userProfiles.setEmail(user.getEmail());
            userProfiles.setRole(user.getRole().name());
            userProfiles.setPrnNumber(user.getPrnNumber());
            userProfiles.setMobileNumber(user.getMobileNumber());
            userProfiles.setYear(user.getYear());
            userProfiles.setBranch(user.getBranch());
            userProfiles.setCreatedAt(user.getCreatedAt());
            userProfiles.setUpdatedAt(user.getUpdatedAt());

            return userProfiles;

        }
        throw new RuntimeException("User not found");
    }

    public User rejectUser(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setStatus(User.UserStatus.REJECTED);
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }

    public List<User> getLeaderboard() {
        return userRepository.findAllOrderByLeaderboard();
    }

    public User updateUserStats(String userId, boolean solved) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setTotalSubmissions(user.getTotalSubmissions() + 1);
            if (solved) {
                user.setTotalSolved(user.getTotalSolved() + 1);
            }
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found");
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getApprovedStudents() {
        return userRepository.findApprovedStudents();
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User registerAdmin(AdminRegistrationDto registrationDto) {
        // Validate admin access code
        if (!adminAccessCode.equals(registrationDto.getAdminAccessCode())) {
            throw new RuntimeException("Invalid admin access code");
        }

        // Validate password confirmation
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Check if username already exists
        String username = registrationDto.generateUsername();
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new admin user
        User admin = new User();
        admin.setUsername(username);
        admin.setEmail(registrationDto.getEmail());
        admin.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        admin.setFullName(registrationDto.getFullName());
        admin.setBranch(registrationDto.getDepartment()); // Using branch field for department
        admin.setRole(User.Role.ADMIN);
        admin.setStatus(User.UserStatus.APPROVED); // Auto-approve admin users
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(admin);
    }
}