package com.lms.backend.controller;

import com.lms.backend.dto.report.MostBorrowedResponse;
import com.lms.backend.dto.report.OverdueResponse;
import com.lms.backend.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    
    private final ReportService reportService;
    
    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }
    
    /**
     * Get most borrowed books report
     * GET /api/reports/most-borrowed
     * Accessible to ADMIN and LIBRARIAN roles
     */
    @GetMapping("/most-borrowed")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<List<MostBorrowedResponse>> getMostBorrowedBooks(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<MostBorrowedResponse> mostBorrowed = reportService.getMostBorrowedBooks(limit);
            return ResponseEntity.ok(mostBorrowed);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get overdue books report
     * GET /api/reports/overdue
     * Accessible to ADMIN and LIBRARIAN roles
     */
    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<List<OverdueResponse>> getOverdueBooks() {
        try {
            List<OverdueResponse> overdueBooks = reportService.getOverdueBooks();
            return ResponseEntity.ok(overdueBooks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get most borrowed books by category
     * GET /api/reports/most-borrowed/{category}
     * Accessible to ADMIN and LIBRARIAN roles
     */
    @GetMapping("/most-borrowed/{category}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<List<MostBorrowedResponse>> getMostBorrowedBooksByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<MostBorrowedResponse> mostBorrowed = reportService.getMostBorrowedBooksByCategory(category, limit);
            return ResponseEntity.ok(mostBorrowed);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get system statistics
     * GET /api/reports/statistics
     * Accessible to ADMIN and LIBRARIAN roles
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<Map<String, Object>> getSystemStatistics() {
        try {
            Map<String, Object> statistics = reportService.getSystemStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get most active members
     * GET /api/reports/most-active-members
     * Accessible to ADMIN and LIBRARIAN roles
     */
    @GetMapping("/most-active-members")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<List<Object[]>> getMostActiveMembers(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Object[]> mostActive = reportService.getMostActiveMembers(limit);
            return ResponseEntity.ok(mostActive);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get loans issued within date range
     * GET /api/reports/loans-issued
     * Accessible to ADMIN and LIBRARIAN roles
     */
    @GetMapping("/loans-issued")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<List<Object>> getLoansIssuedBetween(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<Object> loans = reportService.getLoansIssuedBetween(start, end);
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * Get loans returned within date range
     * GET /api/reports/loans-returned
     * Accessible to ADMIN and LIBRARIAN roles
     */
    @GetMapping("/loans-returned")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<List<Object>> getLoansReturnedBetween(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<Object> loans = reportService.getLoansReturnedBetween(start, end);
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
