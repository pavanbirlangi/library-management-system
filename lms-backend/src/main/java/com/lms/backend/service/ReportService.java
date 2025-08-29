package com.lms.backend.service;

import com.lms.backend.dto.report.MostBorrowedResponse;
import com.lms.backend.dto.report.OverdueResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for generating reports and analytics
 */
public interface ReportService {
    
    /**
     * Get the most borrowed books with specified limit
     * 
     * @param limit Maximum number of books to return
     * @return List of most borrowed books with their borrow counts
     */
    List<MostBorrowedResponse> getMostBorrowedBooks(int limit);
    
    /**
     * Get all currently overdue books
     * 
     * @return List of overdue loans with calculated overdue days
     */
    List<OverdueResponse> getOverdueBooks();
    
    /**
     * Get the most borrowed books (default limit of 10)
     * 
     * @return List of top 10 most borrowed books
     */
    List<MostBorrowedResponse> getMostBorrowedBooks();
    
    /**
     * Get most borrowed books by category
     * 
     * @param category Book category to filter by
     * @param limit Maximum number of books to return
     * @return List of most borrowed books in the specified category
     */
    List<MostBorrowedResponse> getMostBorrowedBooksByCategory(String category, int limit);
    
    /**
     * Get system statistics
     * 
     * @return Map containing various system statistics
     */
    java.util.Map<String, Object> getSystemStatistics();
    
    /**
     * Get member borrowing statistics
     * 
     * @param limit Maximum number of members to return
     * @return List of most active members with their borrow counts
     */
    List<Object[]> getMostActiveMembers(int limit);
    
    /**
     * Get loans issued within a date range
     * 
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @return List of loans issued within the specified date range
     */
    List<Object> getLoansIssuedBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get loans returned within a date range
     * 
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @return List of loans returned within the specified date range
     */
    List<Object> getLoansReturnedBetween(LocalDateTime startDate, LocalDateTime endDate);
}
