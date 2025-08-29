package com.lms.backend.dto.lending;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class IssueRequest {
    
    @NotNull(message = "Book ID is required")
    private Long bookId;
    
    // Optional memberId - can be inferred from JWT token for member users
    private Long memberId;

    // Optional custom due date (allowed for LIBRARIAN/ADMIN only)
    private LocalDateTime dueAt;
    
    // Default constructor
    public IssueRequest() {}
    
    // Constructor with bookId only
    public IssueRequest(Long bookId) {
        this.bookId = bookId;
    }
    
    // Constructor with fields
    public IssueRequest(Long bookId, Long memberId, LocalDateTime dueAt) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.dueAt = dueAt;
    }
    
    // Getters and Setters
    public Long getBookId() {
        return bookId;
    }
    
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
    
    public Long getMemberId() {
        return memberId;
    }
    
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public LocalDateTime getDueAt() {
        return dueAt;
    }

    public void setDueAt(LocalDateTime dueAt) {
        this.dueAt = dueAt;
    }
    
    @Override
    public String toString() {
        return "IssueRequest{" +
                "bookId=" + bookId +
                ", memberId=" + memberId +
                ", dueAt=" + dueAt +
                '}';
    }
}
