package com.lms.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    
    @NotBlank(message = "Full name is required")
    @Size(max = 150, message = "Full name must not exceed 150 characters")
    private String fullName;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    private String username;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
    
    @Size(max = 150, message = "Email must not exceed 150 characters")
    @jakarta.validation.constraints.Email(message = "Email format is invalid")
    private String email;
    
    @Size(max = 25, message = "Phone number must not exceed 25 characters")
    @jakarta.validation.constraints.Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]+$", message = "Phone number format is invalid")
    private String phone;
    
    // Default constructor
    public RegisterRequest() {}
    
    // Constructor with all fields
    public RegisterRequest(String fullName, String username, String password, String email, String phone) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
    }
    
    // Getters and Setters
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
