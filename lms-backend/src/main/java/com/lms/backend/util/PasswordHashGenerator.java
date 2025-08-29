package com.lms.backend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class to generate BCrypt password hashes
 * This is used to verify and generate correct password hashes for testing
 */
public class PasswordHashGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String password = "password123";
        String hash = encoder.encode(password);
        
        System.out.println("Password: " + password);
        System.out.println("Generated Hash: " + hash);
        System.out.println("Verification: " + encoder.matches(password, hash));
        
        // Test the hash from data.sql
        String dataSqlHash = "$2a$10$5K1zwsEr4tFX8O7LlFX8eO/HgQZyJ4W4xZhE3c1KE8FXYzRjL0lmK";
        System.out.println("\nData.sql hash: " + dataSqlHash);
        System.out.println("Data.sql hash verification: " + encoder.matches(password, dataSqlHash));
        
        // Generate a new hash with the same strength
        String newHash = encoder.encode(password);
        System.out.println("New hash: " + newHash);
        System.out.println("New hash verification: " + encoder.matches(password, newHash));
    }
}
