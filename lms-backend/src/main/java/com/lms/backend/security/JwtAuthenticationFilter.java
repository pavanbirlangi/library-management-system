package com.lms.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	private static final String BEARER_PREFIX = "Bearer ";
	private static final String AUTHORIZATION_HEADER = "Authorization";

	@Override
	protected void doFilterInternal(HttpServletRequest request, 
								  HttpServletResponse response, 
								  FilterChain filterChain) throws ServletException, IOException {
		try {
			// Extract JWT token from Authorization header
			String token = extractTokenFromRequest(request);
			
			// If token is present and valid, set authentication in security context
			if (token != null && jwtTokenProvider.validate(token)) {
				setAuthenticationInContext(token);
			}
		} catch (Exception e) {
			// Log the exception but don't throw it to client
			// Let the security configuration handle 401/403 responses
			logger.debug("JWT authentication failed: " + e.getMessage());
		}
		
		// Continue with the filter chain regardless of authentication result
		filterChain.doFilter(request, response);
	}

	/**
	 * Extract JWT token from Authorization header
	 */
	private String extractTokenFromRequest(HttpServletRequest request) {
		String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
		
		if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
			return authorizationHeader.substring(BEARER_PREFIX.length());
		}
		
		return null;
	}

	/**
	 * Set authentication in SecurityContext based on JWT token
	 */
	private void setAuthenticationInContext(String token) {
		try {
			// Extract user information from token
			String username = jwtTokenProvider.getUsername(token);
			String roleString = jwtTokenProvider.getRole(token);
			
			if (username != null && roleString != null) {
				// Load full user details so principal is CustomUserPrincipal
				var userDetails = customUserDetailsService.loadUserByUsername(username);
				
				// Create authority with ROLE_ prefix
				String authorityName = "ROLE_" + roleString;
				SimpleGrantedAuthority authority = new SimpleGrantedAuthority(authorityName);
				
				// Create authentication token with full principal
				UsernamePasswordAuthenticationToken authenticationToken = 
					new UsernamePasswordAuthenticationToken(
						userDetails,
						null,
						Collections.singletonList(authority)
					);
				
				// Set authentication in security context
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}
		} catch (Exception e) {
			// If token parsing fails, clear any existing authentication
			SecurityContextHolder.clearContext();
		}
	}

	/**
	 * Check if the request should be filtered
	 * Override this method to skip filtering for certain endpoints if needed
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		// You can add logic here to skip filtering for certain paths
		// For example: return request.getRequestURI().startsWith("/public");
		return false; // Filter all requests by default
	}
}
