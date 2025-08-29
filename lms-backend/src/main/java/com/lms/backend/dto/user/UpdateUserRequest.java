package com.lms.backend.dto.user;

import com.lms.backend.model.enums.Role;
import com.lms.backend.model.enums.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating user information
 * Used by administrators to modify user accounts
 */
public class UpdateUserRequest {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;
    
    @NotNull(message = "Role is required")
    private Role role;
    
    @NotNull(message = "Status is required")
    private UserStatus status;
    
    // Optional: If updating a member's details
    private String fullName;
    private String email;
    private String phone;
    
    // Default constructor
    public UpdateUserRequest() {}
    
    // Constructor with required fields
    public UpdateUserRequest(String username, Role role, UserStatus status) {
        this.username = username;
        this.role = role;
        this.status = status;
    }
    
    // Constructor with all fields
    public UpdateUserRequest(String username, Role role, UserStatus status, 
                           String fullName, String email, String phone) {
        this.username = username;
        this.role = role;
        this.status = status;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public UserStatus getStatus() {
        return status;
    }
    
    public void setStatus(UserStatus status) {
        this.status = status;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
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
    
    @Override
    public String toString() {
        return "UpdateUserRequest{" +
                "username='" + username + '\'' +
                ", role=" + role +
                ", status=" + status +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
