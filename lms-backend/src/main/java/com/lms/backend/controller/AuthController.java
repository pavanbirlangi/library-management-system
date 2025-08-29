package com.lms.backend.controller;

import com.lms.backend.dto.auth.LoginRequest;
import com.lms.backend.dto.auth.LoginResponse;
import com.lms.backend.dto.auth.RegisterRequest;
import com.lms.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication endpoints")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Register a new member
     * @param request RegisterRequest containing fullName, username, password, email, and phone
     * @return LoginResponse with token, username, and role
     */
    @PostMapping("/register")
    @Operation(summary = "Register New Member", description = "Register a new member with full details including email and phone")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = authService.registerMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticate user and generate JWT token
     * @param request LoginRequest containing username and password
     * @return LoginResponse with token, username, and role
     */
    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticate user and get JWT token. Use this token in the Authorize button above to test other APIs.")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Logout user by clearing the security context
     * Since we're using stateless JWT tokens, logout is mainly handled on the client side
     * This endpoint clears the server-side security context and provides a confirmation response
     * @return Success message confirming logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        // Clear the security context on the server side
        SecurityContextHolder.clearContext();
        
        // Create response message
        Map<String, String> response = new HashMap<>();
        response.put("message", "Successfully logged out");
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }
}
