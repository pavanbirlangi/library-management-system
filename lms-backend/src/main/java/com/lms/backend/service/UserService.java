package com.lms.backend.service;

import com.lms.backend.dto.user.CreateUserRequest;
import com.lms.backend.dto.user.UpdateUserRequest;
import com.lms.backend.dto.user.UserResponse;

import java.util.List;

/**
 * Service interface for user operations
 * Administrative operations for managing users (ADMIN only)
 */
public interface UserService {
    
    /**
     * Get all users
     * @return list of user responses
     */
    List<UserResponse> findAll();
    
    /**
     * Find user by ID
     * @param id user ID
     * @return user response
     * @throws IllegalArgumentException if user not found
     */
    UserResponse findById(Long id);
    
    /**
     * Create a new user (ADMIN only)
     * Can create LIBRARIAN or MEMBER users
     * If MEMBER role, also creates associated Member entity
     * @param request user creation request
     * @return created user response
     * @throws IllegalArgumentException if username already exists or invalid data
     */
    UserResponse createUser(CreateUserRequest request);
    
    /**
     * Update an existing user
     * @param id user ID to update
     * @param request update user request
     * @return updated user response
     * @throws IllegalArgumentException if user not found or invalid data
     */
    UserResponse updateUser(Long id, UpdateUserRequest request);
    
    /**
     * Suspend a user account
     * @param id user ID to suspend
     * @return updated user response
     * @throws IllegalArgumentException if user not found
     */
    UserResponse suspendUser(Long id);
    
    /**
     * Activate a user account
     * @param id user ID to activate
     * @return updated user response
     * @throws IllegalArgumentException if user not found
     */
    UserResponse activateUser(Long id);
    
    /**
     * Delete a user (and linked member if any) after safety checks
     */
    void deleteUser(Long id);
    
    /**
     * Check if username is available
     * @param username username to check
     * @return true if available, false if already exists
     */
    boolean isUsernameAvailable(String username);
    
    /**
     * Get users by role (no pagination)
     */
    List<UserResponse> findByRole(String role);
    
    /**
     * Get users by status (no pagination)
     */
    List<UserResponse> findByStatus(String status);
}
