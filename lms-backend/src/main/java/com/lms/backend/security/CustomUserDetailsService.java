package com.lms.backend.security;

//import com.lms.backend.exception.NotFoundException;
import com.lms.backend.model.entity.User;
import com.lms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            System.out.println("=== CustomUserDetailsService: Loading user by username: " + username + " ===");
            
            var userOptional = userRepository.findByUsername(username);
            if (userOptional.isEmpty()) {
                System.err.println("User not found in CustomUserDetailsService: " + username);
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
            
            User user = userOptional.get();
            System.out.println("User found in CustomUserDetailsService: " + user.getUsername() + " with role: " + user.getRole());
            
            return new CustomUserPrincipal(user);
        } catch (Exception e) {
            System.err.println("Error in CustomUserDetailsService for username: " + username + " - " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Custom implementation of UserDetails
     */
    public static class CustomUserPrincipal implements UserDetails {
        private final User user;

        public CustomUserPrincipal(User user) {
            this.user = user;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            // Map Role enum to SimpleGrantedAuthority with "ROLE_" prefix
            String roleName = "ROLE_" + user.getRole().name();
            return Collections.singletonList(new SimpleGrantedAuthority(roleName));
        }

        @Override
        public String getPassword() {
            return user.getPassword();
        }

        @Override
        public String getUsername() {
            return user.getUsername();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true; // Can be customized based on business logic
        }

        @Override
        public boolean isAccountNonLocked() {
            // Check if user status is ACTIVE
            return user.getStatus() != null && user.getStatus().name().equals("ACTIVE");
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true; // Can be customized based on business logic
        }

        @Override
        public boolean isEnabled() {
            // User is enabled if status is ACTIVE
            return user.getStatus() != null && user.getStatus().name().equals("ACTIVE");
        }

        // Getter for the underlying User entity
        public User getUser() {
            return user;
        }

        public Long getUserId() {
            return user.getId();
        }
    }
}
