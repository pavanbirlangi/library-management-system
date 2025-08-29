package com.lms.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for APIs
            .csrf(csrf -> csrf.disable())
            
            // Disable sessions (stateless)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configure authorization rules
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers(HttpMethod.GET, "/").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
                
                // Health check endpoint
                .requestMatchers(HttpMethod.GET, "/api/health").permitAll()
                
                // Test endpoints
                .requestMatchers("/api/test/**").permitAll()
                
                // Swagger/OpenAPI endpoints
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-resources/**", "/webjars/**").permitAll()
                
                // MEMBER self endpoints (must be before broader /api/members/** rule)
                .requestMatchers("/api/members/me", "/api/members/me/**").hasRole("MEMBER")
                
                // MEMBER only endpoints - Book reservations
                .requestMatchers(HttpMethod.POST, "/api/books/*/reserve").hasRole("MEMBER")
                
                // ADMIN only endpoints
                .requestMatchers(HttpMethod.POST, "/api/books").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/members/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/members/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/members/**").hasRole("ADMIN")
                .requestMatchers("/api/loans/admin/**").hasRole("ADMIN")
                
                // Reports - ADMIN and LIBRARIAN access only
                .requestMatchers("/api/reports/**").hasAnyRole("ADMIN", "LIBRARIAN")
                
                // LIBRARIAN only endpoints
                .requestMatchers(HttpMethod.POST, "/api/fines/pay").hasRole("LIBRARIAN")
                
                // Read access for books (authenticated users)
                .requestMatchers(HttpMethod.GET, "/api/books/**").authenticated()
                
                // Read access for members (A/L)
                .requestMatchers(HttpMethod.GET, "/api/members/**").hasAnyRole("ADMIN", "LIBRARIAN")
                
                // Loan operations - read access for A/L/M (self-access checked in controllers)
                .requestMatchers(HttpMethod.GET, "/api/loans/**").hasAnyRole("ADMIN", "LIBRARIAN", "MEMBER")
                .requestMatchers("/api/loans/**").hasAnyRole("ADMIN", "LIBRARIAN")
                
                // Lending operations - A/L can issue/return, M can request
                .requestMatchers("/api/lending/**").hasAnyRole("ADMIN", "LIBRARIAN", "MEMBER")
                
                // Reservation operations - A/L/M (self-access checked in controllers)
                .requestMatchers("/api/reservations/**").hasAnyRole("ADMIN", "LIBRARIAN", "MEMBER")
                
                // Fine operations - read access for A/L/M (self-access checked in controllers)
                .requestMatchers(HttpMethod.GET, "/api/fines/**").hasAnyRole("ADMIN", "LIBRARIAN", "MEMBER")
                .requestMatchers("/api/fines/**").hasAnyRole("ADMIN", "LIBRARIAN")
                
                // Require authentication for all other requests
                .anyRequest().authenticated()
            )
            
            // Configure exception handling
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            
            // Add JWT filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
