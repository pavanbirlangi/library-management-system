package com.lms.backend.repository;

import com.lms.backend.model.entity.User;
import com.lms.backend.model.enums.Role;
import com.lms.backend.model.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Find users by role with pagination
     */
    Page<User> findByRole(Role role, Pageable pageable);
    
    /**
     * Find users by status with pagination
     */
    Page<User> findByStatus(UserStatus status, Pageable pageable);

    /**
     * Find users by role (no pagination)
     */
    List<User> findByRole(Role role);

    /**
     * Find users by status (no pagination)
     */
    List<User> findByStatus(UserStatus status);
}
