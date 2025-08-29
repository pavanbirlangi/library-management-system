package com.lms.backend.service.impl;

import com.lms.backend.dto.auth.LoginRequest;
import com.lms.backend.dto.auth.LoginResponse;
import com.lms.backend.dto.auth.RegisterRequest;
import com.lms.backend.model.entity.Member;
import com.lms.backend.model.entity.User;
import com.lms.backend.model.enums.Role;
import com.lms.backend.model.enums.UserStatus;
import com.lms.backend.repository.MemberRepository;
import com.lms.backend.repository.UserRepository;
import com.lms.backend.security.JwtTokenProvider;
import com.lms.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Override
	public LoginResponse registerMember(RegisterRequest request) {
		// Check if username already exists
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new IllegalArgumentException("Username already exists: " + request.getUsername());
		}
		
		// Create and save new User entity
		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword())); // Hash password with BCrypt
		user.setRole(Role.MEMBER);
		user.setStatus(UserStatus.ACTIVE);
		
		User savedUser = userRepository.save(user);
		
		// Create and save new Member entity linked to the user
		Member member = new Member();
		member.setUserId(savedUser.getId());
		member.setFullName(request.getFullName());
		member.setEmail(request.getEmail());
		member.setPhone(request.getPhone());
		member.setStatus(UserStatus.ACTIVE);
		
		memberRepository.save(member);
		
		// Generate JWT token for the new user
		String token = jwtTokenProvider.generateToken(
			savedUser.getId(), 
			savedUser.getUsername(), 
			savedUser.getRole()
		);
		
		// Return login response with token
		return new LoginResponse(token, savedUser.getUsername(), savedUser.getRole().name());
	}

	@Override
	public LoginResponse login(LoginRequest request) throws AuthenticationException {
		try {
			System.out.println("=== Starting login process for user: " + request.getUsername() + " ===");
			
			// Authenticate user using Spring Security's AuthenticationManager
			System.out.println("Authenticating with Spring Security...");
			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
					request.getUsername(),
					request.getPassword()
				)
			);
			
			System.out.println("Authentication successful for user: " + authentication.getName());
			
			// Prefer the authenticated principal to avoid repository mismatches
			User user;
			Object principal = authentication.getPrincipal();
			if (principal instanceof com.lms.backend.security.CustomUserDetailsService.CustomUserPrincipal principalUser) {
				user = principalUser.getUser();
			} else {
				// Fallback to repository lookup
				user = userRepository.findByUsername(authentication.getName())
					.orElseThrow(() -> new IllegalStateException("User not found after successful authentication"));
			}
			
			// Generate JWT token
			System.out.println("Generating JWT token...");
			String token = jwtTokenProvider.generateToken(
				user.getId(),
				user.getUsername(),
				user.getRole()
			);
			
			System.out.println("JWT token generated successfully");
			
			// Return login response with token, username, and role
			return new LoginResponse(token, user.getUsername(), user.getRole().name());
			
		} catch (AuthenticationException e) {
			// Log the authentication error
			System.err.println("Authentication failed for user: " + request.getUsername() + " - " + e.getMessage());
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			// Log any other unexpected errors
			System.err.println("Unexpected error during login for user: " + request.getUsername() + " - " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("An unexpected error occurred during login", e);
		}
	}
}
