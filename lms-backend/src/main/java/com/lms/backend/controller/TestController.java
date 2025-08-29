package com.lms.backend.controller;

import com.lms.backend.repository.UserRepository;
import com.lms.backend.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Test controller is working!");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/db-connection")
    public ResponseEntity<Map<String, Object>> testDatabaseConnection() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Test if we can count users
            long userCount = userRepository.count();
            response.put("status", "SUCCESS");
            response.put("message", "Database connection successful");
            response.put("userCount", userCount);
            
            // Test if we can find a specific user
            List<User> users = userRepository.findAll();
            if (!users.isEmpty()) {
                User firstUser = users.get(0);
                response.put("firstUser", Map.of(
                    "id", firstUser.getId(),
                    "username", firstUser.getUsername(),
                    "role", firstUser.getRole().name()
                ));
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Database connection failed");
            response.put("error", e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            e.printStackTrace();
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/test-login")
    public ResponseEntity<Map<String, Object>> testLogin() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Test if we can find the admin user
            var adminUser = userRepository.findByUsername("admin");
            if (adminUser.isPresent()) {
                User user = adminUser.get();
                response.put("status", "SUCCESS");
                response.put("message", "Admin user found");
                response.put("user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "role", user.getRole().name(),
                    "status", user.getStatus().name()
                ));
            } else {
                response.put("status", "ERROR");
                response.put("message", "Admin user not found");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Test login failed");
            response.put("error", e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            e.printStackTrace();
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/test-password")
    public ResponseEntity<Map<String, Object>> testPassword() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Test if we can find the admin user
            var adminUser = userRepository.findByUsername("admin");
            if (adminUser.isPresent()) {
                User user = adminUser.get();
                
                // Test password hashing
                org.springframework.security.crypto.password.PasswordEncoder encoder = 
                    new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
                
                String testPassword = "password123";
                String storedHash = user.getPassword();
                
                boolean matches = encoder.matches(testPassword, storedHash);
                
                response.put("status", "SUCCESS");
                response.put("message", "Password test completed");
                response.put("testPassword", testPassword);
                response.put("storedHash", storedHash);
                response.put("passwordMatches", matches);
                response.put("hashLength", storedHash.length());
                
                // Generate a new hash for comparison
                String newHash = encoder.encode(testPassword);
                response.put("newHash", newHash);
                response.put("newHashMatches", encoder.matches(testPassword, newHash));
                
                // Test common passwords to see what the stored hash actually matches
                String[] commonPasswords = {"password", "admin", "123456", "password123", "admin123", "test", "user"};
                Map<String, Boolean> passwordTests = new HashMap<>();
                
                for (String commonPassword : commonPasswords) {
                    boolean passwordMatch = encoder.matches(commonPassword, storedHash);
                    passwordTests.put(commonPassword, passwordMatch);
                }
                response.put("passwordTests", passwordTests);
                
                // Generate a correct hash for "password123"
                String correctHash = encoder.encode("password123");
                response.put("correctHashForPassword123", correctHash);
                response.put("correctHashVerification", encoder.matches("password123", correctHash));
                
            } else {
                response.put("status", "ERROR");
                response.put("message", "Admin user not found");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Password test failed");
            response.put("error", e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            e.printStackTrace();
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/auth-info")
    public ResponseEntity<Map<String, Object>> getAuthInfo() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get current authentication details
            var authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.isAuthenticated()) {
                response.put("status", "SUCCESS");
                response.put("message", "Authentication info retrieved");
                response.put("authenticated", authentication.isAuthenticated());
                response.put("username", authentication.getName());
                response.put("authorities", authentication.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .collect(java.util.stream.Collectors.toList()));
                response.put("principal", authentication.getPrincipal().getClass().getSimpleName());
            } else {
                response.put("status", "NOT_AUTHENTICATED");
                response.put("message", "No authentication found");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Failed to get auth info");
            response.put("error", e.getMessage());
            response.put("errorType", e.getClass().getSimpleName());
            e.printStackTrace();
            return ResponseEntity.status(500).body(response);
        }
    }
}
