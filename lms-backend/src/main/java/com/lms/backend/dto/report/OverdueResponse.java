package com.lms.backend.dto.report;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * DTO for overdue loans report
 */
public class OverdueResponse {
    
    private Long loanId;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private Long memberId;
    private String memberFullName;
    private String memberEmail;
    private String memberPhone;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime issuedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueAt;
    
    private Long daysOverdue;
    private Double estimatedFine;
    
    // Default constructor
    public OverdueResponse() {}
    
    // Constructor with required fields
    public OverdueResponse(Long loanId, String bookTitle, String memberFullName, 
                          LocalDateTime dueAt, Long daysOverdue) {
        this.loanId = loanId;
        this.bookTitle = bookTitle;
        this.memberFullName = memberFullName;
        this.dueAt = dueAt;
        this.daysOverdue = daysOverdue;
    }
    
    // Constructor with essential fields
    public OverdueResponse(Long loanId, Long bookId, String bookTitle, Long memberId, 
                          String memberFullName, LocalDateTime issuedAt, LocalDateTime dueAt, 
                          Long daysOverdue) {
        this.loanId = loanId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.memberId = memberId;
        this.memberFullName = memberFullName;
        this.issuedAt = issuedAt;
        this.dueAt = dueAt;
        this.daysOverdue = daysOverdue;
    }
    
    // Getters and Setters
    public Long getLoanId() {
        return loanId;
    }
    
    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }
    
    public Long getBookId() {
        return bookId;
    }
    
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
    
    public String getBookTitle() {
        return bookTitle;
    }
    
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
    
    public String getBookAuthor() {
        return bookAuthor;
    }
    
    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }
    
    public String getBookIsbn() {
        return bookIsbn;
    }
    
    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }
    
    public Long getMemberId() {
        return memberId;
    }
    
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
    
    public String getMemberFullName() {
        return memberFullName;
    }
    
    public void setMemberFullName(String memberFullName) {
        this.memberFullName = memberFullName;
    }
    
    public String getMemberEmail() {
        return memberEmail;
    }
    
    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }
    
    public String getMemberPhone() {
        return memberPhone;
    }
    
    public void setMemberPhone(String memberPhone) {
        this.memberPhone = memberPhone;
    }
    
    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }
    
    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }
    
    public LocalDateTime getDueAt() {
        return dueAt;
    }
    
    public void setDueAt(LocalDateTime dueAt) {
        this.dueAt = dueAt;
    }
    
    public Long getDaysOverdue() {
        return daysOverdue;
    }
    
    public void setDaysOverdue(Long daysOverdue) {
        this.daysOverdue = daysOverdue;
    }
    
    public Double getEstimatedFine() {
        return estimatedFine;
    }
    
    public void setEstimatedFine(Double estimatedFine) {
        this.estimatedFine = estimatedFine;
    }
    
    @Override
    public String toString() {
        return "OverdueResponse{" +
                "loanId=" + loanId +
                ", bookTitle='" + bookTitle + '\'' +
                ", memberFullName='" + memberFullName + '\'' +
                ", dueAt=" + dueAt +
                ", daysOverdue=" + daysOverdue +
                ", estimatedFine=" + estimatedFine +
                '}';
    }
}
