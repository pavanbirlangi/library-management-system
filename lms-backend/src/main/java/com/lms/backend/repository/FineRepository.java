package com.lms.backend.repository;

import com.lms.backend.model.entity.Fine;
import com.lms.backend.model.entity.Member;
import com.lms.backend.model.enums.FineStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {
    
    /**
     * Find all fines for a specific member
     * Required method
     */
    List<Fine> findByMember(Member member);
    
    /**
     * Find all fines by status
     * Required method
     */
    List<Fine> findByStatus(FineStatus status);
    
    /**
     * Find all fines for a specific member by member ID
     */
    List<Fine> findByMemberId(Long memberId);
    
    /**
     * Find all fines for a specific member with pagination
     */
    Page<Fine> findByMember(Member member, Pageable pageable);
    
    /**
     * Find all fines for a specific member by status
     */
    List<Fine> findByMemberAndStatus(Member member, FineStatus status);
    
    /**
     * Find all fines for a specific member by member ID and status
     */
    List<Fine> findByMemberIdAndStatus(Long memberId, FineStatus status);
    
    /**
     * Find all fines for a specific loan
     */
    List<Fine> findByLoanId(Long loanId);
    
    /**
     * Find fine by loan ID and status
     */
    Optional<Fine> findByLoanIdAndStatus(Long loanId, FineStatus status);
    
    /**
     * Check if a member has any pending fines
     */
    boolean existsByMemberAndStatus(Member member, FineStatus status);
    
    /**
     * Check if a member has any pending fines by member ID
     */
    boolean existsByMemberIdAndStatus(Long memberId, FineStatus status);
    
    /**
     * Count fines by member and status
     */
    long countByMemberAndStatus(Member member, FineStatus status);
    
    /**
     * Count fines by member ID and status
     */
    long countByMemberIdAndStatus(Long memberId, FineStatus status);
    
    /**
     * Calculate total fine amount for a member by status
     */
    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM Fine f WHERE f.memberId = :memberId AND f.status = :status")
    BigDecimal sumAmountByMemberIdAndStatus(@Param("memberId") Long memberId, @Param("status") FineStatus status);
    
    /**
     * Calculate total fine amount for a member
     */
    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM Fine f WHERE f.memberId = :memberId")
    BigDecimal sumAmountByMemberId(@Param("memberId") Long memberId);
    
    /**
     * Find fines settled by a specific user (librarian)
     */
    List<Fine> findBySettledByUserId(Long settledByUserId);
    
    /**
     * Find fines settled within a date range
     */
    @Query("SELECT f FROM Fine f WHERE f.status = :status AND f.settledAt BETWEEN :startDate AND :endDate")
    List<Fine> findSettledFinesBetween(@Param("status") FineStatus status, 
                                       @Param("startDate") LocalDateTime startDate, 
                                       @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find overdue fines (calculated before a certain date but still pending)
     */
    @Query("SELECT f FROM Fine f WHERE f.status = :status AND f.calculatedAt < :beforeDate")
    List<Fine> findOverdueFines(@Param("status") FineStatus status, @Param("beforeDate") LocalDateTime beforeDate);
    
    /**
     * Find fines by payment method
     */
    List<Fine> findByPaymentMethod(String paymentMethod);
    
    /**
     * Find fine by payment reference
     */
    Optional<Fine> findByPaymentRef(String paymentRef);
}
