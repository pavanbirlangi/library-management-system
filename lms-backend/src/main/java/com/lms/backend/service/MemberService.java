package com.lms.backend.service;

import com.lms.backend.dto.member.MemberResponse;

import java.util.List;

/**
 * Service interface for member operations
 * Administrative operations for managing members (ADMIN only)
 */
public interface MemberService {
    
    /**
     * Get all members
     * @return list of all member responses
     */
    List<MemberResponse> findAll();
    
    /**
     * Find member by ID
     * @param id member ID
     * @return member response
     * @throws IllegalArgumentException if member not found
     */
    MemberResponse findById(Long id);
}
