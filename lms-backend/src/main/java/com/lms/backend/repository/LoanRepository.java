package com.lms.backend.repository;

import com.lms.backend.model.entity.Book;
import com.lms.backend.model.entity.Loan;
import com.lms.backend.model.entity.Member;
import com.lms.backend.model.enums.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    
    /**
     * Count active loans for a member by status
     * Required for checking borrow limits
     */
    long countByMemberAndStatus(Member member, LoanStatus status);
    
    /**
     * Count active loans for a member by member ID and status
     * Alternative method using member ID
     */
    long countByMemberIdAndStatus(Long memberId, LoanStatus status);
    
    /**
     * Find all loans for a specific member
     */
    List<Loan> findByMember(Member member);
    
    /**
     * Find all loans for a specific member with pagination
     */
    Page<Loan> findByMember(Member member, Pageable pageable);
    
    /**
     * Find all loans for a specific member by status
     */
    List<Loan> findByMemberAndStatus(Member member, LoanStatus status);

    /**
     * Find loans by status (non-paged)
     */
    List<Loan> findByStatus(LoanStatus status);
    
    /**
     * Find all loans for a specific book
     */
    List<Loan> findByBook(Book book);
    
    /**
     * Find all loans for a specific book by status
     */
    List<Loan> findByBookAndStatus(Book book, LoanStatus status);
    
    /**
     * Find overdue loans (due date passed and still active)
     */
    @Query("SELECT l FROM Loan l WHERE l.status = :status AND l.dueAt < :currentDate")
    List<Loan> findOverdueLoans(@Param("status") LoanStatus status, @Param("currentDate") LocalDateTime currentDate);
    
    /**
     * Find loans due within a specific period (for reminders)
     */
    @Query("SELECT l FROM Loan l WHERE l.status = :status AND l.dueAt BETWEEN :startDate AND :endDate")
    List<Loan> findLoansDueBetween(@Param("status") LoanStatus status, 
                                   @Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find active loans for a specific member ID
     */
    List<Loan> findByMemberIdAndStatus(Long memberId, LoanStatus status);
    
    /**
     * Check if a book is currently on loan
     */
    boolean existsByBookAndStatus(Book book, LoanStatus status);
    
    /**
     * Find the most recent loan for a book
     */
    Optional<Loan> findFirstByBookOrderByIssuedAtDesc(Book book);
    
    // ===== REPORTING QUERIES =====
    
    /**
     * Find the most borrowed books with borrow counts
     * Returns Book objects with their borrow counts for reporting
     */
    @Query("SELECT l.book, COUNT(l.book) as borrowCount FROM Loan l GROUP BY l.book ORDER BY COUNT(l.book) DESC")
    List<Object[]> findMostBorrowedBooks();
    
    /**
     * Find the most borrowed books with borrow counts (with limit)
     * Returns top N most borrowed books
     */
    @Query("SELECT l.book, COUNT(l.book) as borrowCount FROM Loan l GROUP BY l.book ORDER BY COUNT(l.book) DESC")
    List<Object[]> findMostBorrowedBooks(Pageable pageable);
    
    /**
     * Find all currently overdue loans (active loans past due date)
     * This is specifically for reporting overdue loans
     */
    @Query("SELECT l FROM Loan l WHERE l.status = 'ACTIVE' AND l.dueAt < CURRENT_TIMESTAMP")
    List<Loan> findCurrentlyOverdueLoans();
    
    /**
     * Find overdue loans with detailed information for reporting
     */
    @Query("SELECT l FROM Loan l " +
           "WHERE l.status = 'ACTIVE' AND l.dueAt < CURRENT_TIMESTAMP " +
           "ORDER BY l.dueAt ASC")
    List<Loan> findOverdueLoansForReport();
    
    /**
     * Get book borrow statistics for a specific book
     */
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.book = :book")
    Long getBorrowCountForBook(@Param("book") Book book);
    
    /**
     * Get book borrow statistics for a specific book by book ID
     */
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.bookId = :bookId")
    Long getBorrowCountForBookId(@Param("bookId") Long bookId);
    
    /**
     * Find most borrowed books by category with counts
     */
    @Query("SELECT l.book, COUNT(l.book) as borrowCount FROM Loan l " +
           "WHERE l.book.category = :category " +
           "GROUP BY l.book ORDER BY COUNT(l.book) DESC")
    List<Object[]> findMostBorrowedBooksByCategory(@Param("category") String category);
    
    /**
     * Get total loans count for reporting
     */
    @Query("SELECT COUNT(l) FROM Loan l")
    Long getTotalLoansCount();
    
    /**
     * Get total loans count by status
     */
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.status = :status")
    Long getTotalLoansByStatus(@Param("status") LoanStatus status);
    
    /**
     * Find loans issued within a date range for reporting
     */
    @Query("SELECT l FROM Loan l WHERE l.issuedAt BETWEEN :startDate AND :endDate ORDER BY l.issuedAt DESC")
    List<Loan> findLoansIssuedBetween(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find loans returned within a date range for reporting
     */
    @Query("SELECT l FROM Loan l WHERE l.returnedAt BETWEEN :startDate AND :endDate ORDER BY l.returnedAt DESC")
    List<Loan> findLoansReturnedBetween(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get member borrowing statistics
     */
    @Query("SELECT l.member, COUNT(l.member) as borrowCount FROM Loan l GROUP BY l.member ORDER BY COUNT(l.member) DESC")
    List<Object[]> getMemberBorrowingStatistics();
    
    /**
     * Find most active members (with pagination)
     */
    @Query("SELECT l.member, COUNT(l.member) as borrowCount FROM Loan l GROUP BY l.member ORDER BY COUNT(l.member) DESC")
    List<Object[]> findMostActiveMembers(Pageable pageable);
}
