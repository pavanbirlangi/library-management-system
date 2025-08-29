package com.lms.backend.repository;

import com.lms.backend.model.entity.Book;
import com.lms.backend.model.entity.Member;
import com.lms.backend.model.entity.Reservation;
import com.lms.backend.model.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    /**
     * Find the first reservation in queue for a book (first-come, first-served)
     * Required for reservation fulfillment
     */
    Optional<Reservation> findFirstByBookAndStatusOrderByCreatedAtAsc(Book book, ReservationStatus status);
    
    /**
     * Find the first reservation in queue by book ID and status
     * Alternative method using book ID
     */
    Optional<Reservation> findFirstByBookIdAndStatusOrderByCreatedAtAsc(Long bookId, ReservationStatus status);
    
    /**
     * Find all reservations for a specific book ordered by creation time
     */
    List<Reservation> findByBookAndStatusOrderByCreatedAtAsc(Book book, ReservationStatus status);
    
    /**
     * Find all reservations for a specific member
     */
    List<Reservation> findByMember(Member member);
    
    /**
     * Find all reservations for a specific member with pagination
     */
    Page<Reservation> findByMember(Member member, Pageable pageable);
    
    /**
     * Find all reservations for a specific member by status
     */
    List<Reservation> findByMemberAndStatus(Member member, ReservationStatus status);
    
    /**
     * Count active reservations for a specific book
     */
    long countByBookAndStatus(Book book, ReservationStatus status);
    
    /**
     * Count active reservations for a specific member
     */
    long countByMemberAndStatus(Member member, ReservationStatus status);
    
    /**
     * Check if a member already has an active reservation for a book
     */
    boolean existsByMemberAndBookAndStatus(Member member, Book book, ReservationStatus status);
    
    /**
     * Find all reservations for a specific book (all statuses)
     */
    List<Reservation> findByBook(Book book);
    
    /**
     * Get queue position for a specific reservation
     */
    @Query("SELECT COUNT(r) + 1 FROM Reservation r WHERE r.book = :book AND r.status = :status AND r.createdAt < :createdAt")
    Integer getQueuePosition(@Param("book") Book book, @Param("status") ReservationStatus status, @Param("createdAt") LocalDateTime createdAt);
    
    /**
     * Update queue positions after a reservation is fulfilled or cancelled
     */
    @Modifying
    @Query("UPDATE Reservation r SET r.queuePosition = r.queuePosition - 1 WHERE r.book = :book AND r.status = :status AND r.queuePosition > :position")
    void updateQueuePositions(@Param("book") Book book, @Param("status") ReservationStatus status, @Param("position") Integer position);
    
    /**
     * Find expired reservations (for cleanup)
     */
    @Query("SELECT r FROM Reservation r WHERE r.status = :status AND r.updatedAt < :expiryDate")
    List<Reservation> findExpiredReservations(@Param("status") ReservationStatus status, @Param("expiryDate") LocalDateTime expiryDate);
    
    /**
     * Find reservations by member ID and status
     */
    List<Reservation> findByMemberIdAndStatus(Long memberId, ReservationStatus status);
}
