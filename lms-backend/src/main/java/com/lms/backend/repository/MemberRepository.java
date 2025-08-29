package com.lms.backend.repository;

import com.lms.backend.model.entity.Member;
import com.lms.backend.model.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    /**
     * Find member by user ID
     */
    Optional<Member> findByUserId(Long userId);
    
    /**
     * Find member by email
     */
    Optional<Member> findByEmail(String email);
    
    /**
     * Find members by status
     */
    List<Member> findByStatus(UserStatus status);
    
    /**
     * Find members by status with pagination
     */
    Page<Member> findByStatus(UserStatus status, Pageable pageable);
    
    /**
     * Check if member exists by user ID
     */
    boolean existsByUserId(Long userId);
    
    /**
     * Check if member exists by email
     */
    boolean existsByEmail(String email);
}
