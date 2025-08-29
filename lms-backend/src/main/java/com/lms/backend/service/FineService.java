package com.lms.backend.service;

import com.lms.backend.dto.fine.FineResponse;
import com.lms.backend.model.entity.Fine;
import com.lms.backend.model.entity.Loan;
import com.lms.backend.model.entity.Member;
import com.lms.backend.model.entity.User;

import java.util.List;

/**
 * Service interface for fine management operations
 */
public interface FineService {
    
    /**
     * Creates a new fine for an overdue loan
     * This method will be called internally by LendingService
     * 
     * @param loan The overdue loan
     * @return The created fine
     */
    Fine createFine(Loan loan);
    
    /**
     * Get all fines for a specific member
     * 
     * @param member The member to get fines for
     * @return List of fine responses
     */
    List<FineResponse> getFinesForMember(Member member);
    
    /**
     * Get all fines for a specific member by member ID
     * 
     * @param memberId The member ID to get fines for
     * @return List of fine responses
     */
    List<FineResponse> getFinesForMember(Long memberId);
    
    /**
     * Get all pending fines in the system
     * 
     * @return List of all pending fine responses
     */
    List<FineResponse> getAllPendingFines();
    
    /**
     * Pay a fine (mark as settled)
     * Only librarians can perform this action
     * 
     * @param fineId The ID of the fine to pay
     * @param librarianUser The librarian processing the payment
     * @return The updated fine response
     */
    FineResponse payFine(Long fineId, User librarianUser);
    
    /**
     * Pay a fine with payment details
     * 
     * @param fineId The ID of the fine to pay
     * @param librarianUser The librarian processing the payment
     * @param paymentMethod The payment method used
     * @param paymentRef The payment reference number
     * @return The updated fine response
     */
    FineResponse payFine(Long fineId, User librarianUser, String paymentMethod, String paymentRef);
    
    /**
     * Get fine by ID
     * 
     * @param fineId The fine ID
     * @return Fine response
     */
    FineResponse getFineById(Long fineId);
    
    /**
     * Check if a member has any pending fines
     * 
     * @param memberId The member ID to check
     * @return true if member has pending fines, false otherwise
     */
    boolean hasPendingFines(Long memberId);
    
    /**
     * Get total pending fine amount for a member
     * 
     * @param memberId The member ID
     * @return Total pending fine amount
     */
    java.math.BigDecimal getTotalPendingFines(Long memberId);
}
