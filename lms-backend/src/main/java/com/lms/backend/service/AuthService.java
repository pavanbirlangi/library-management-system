package com.lms.backend.service;

import com.lms.backend.dto.auth.LoginRequest;
import com.lms.backend.dto.auth.LoginResponse;
import com.lms.backend.dto.auth.RegisterRequest;

public interface AuthService {
    
    /**
     * Register a new member
     * @param request RegisterRequest containing fullName, username, and password
     * @return LoginResponse with token, username, and role
     * @throws IllegalArgumentException if username already exists
     */
    LoginResponse registerMember(RegisterRequest request);
    
    /**
     * Authenticate user and generate JWT token
     * @param request LoginRequest containing username and password
     * @return LoginResponse with token, username, and role
     * @throws org.springframework.security.core.AuthenticationException if authentication fails
     */
    LoginResponse login(LoginRequest request);
}
