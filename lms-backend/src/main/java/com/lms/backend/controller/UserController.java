package com.lms.backend.controller;

import com.lms.backend.dto.user.CreateUserRequest;
import com.lms.backend.dto.user.UpdateUserRequest;
import com.lms.backend.dto.user.UserResponse;
import com.lms.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for administrative user management
 * All endpoints require ADMIN role
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Administrative operations for managing users")
@SecurityRequirement(name = "JWT")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Get all users", description = "Retrieve all users as a list (ADMIN only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsersNoPagination() {
        List<UserResponse> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID (ADMIN only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id) {
        UserResponse user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Create new user", description = "Create a new LIBRARIAN or MEMBER user (ADMIN only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data or username already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Operation(summary = "Update user", description = "Update an existing user's information (ADMIN only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Suspend user", description = "Suspend a user account (ADMIN only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User suspended successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot suspend ADMIN users"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @PatchMapping("/{id}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> suspendUser(
            @Parameter(description = "User ID") @PathVariable Long id) {
        UserResponse user = userService.suspendUser(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Activate user", description = "Activate a suspended user account (ADMIN only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User activated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> activateUser(
            @Parameter(description = "User ID") @PathVariable Long id) {
        UserResponse user = userService.activateUser(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Check username availability", description = "Check if a username is available (ADMIN only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Username availability checked"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @GetMapping("/username/{username}/available")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Boolean>> checkUsernameAvailability(
            @Parameter(description = "Username to check") @PathVariable String username) {
        boolean available = userService.isUsernameAvailable(username);
        return ResponseEntity.ok(Map.of("available", available));
    }

    @Operation(summary = "Get users by role", description = "Retrieve users filtered by role (ADMIN only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid role"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getUsersByRole(
            @Parameter(description = "Role (ADMIN, LIBRARIAN, MEMBER)") @PathVariable String role) {
        List<UserResponse> users = userService.findByRole(role);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get users by status", description = "Retrieve users filtered by status (ADMIN only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getUsersByStatus(
            @Parameter(description = "Status (ACTIVE, SUSPENDED)") @PathVariable String status) {
        List<UserResponse> users = userService.findByStatus(status);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Delete user", description = "Delete a user (and member if applicable) with safety checks (ADMIN only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot delete ADMIN users or user has pending obligations"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@Parameter(description = "User ID") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
