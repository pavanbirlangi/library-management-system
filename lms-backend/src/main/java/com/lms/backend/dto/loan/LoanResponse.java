package com.lms.backend.dto.loan;

import com.lms.backend.model.entity.Loan;
import com.lms.backend.model.enums.LoanStatus;
import java.time.LocalDateTime;

public class LoanResponse {
    
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private LocalDateTime issuedAt;
    private LocalDateTime dueAt;
    private LocalDateTime returnedAt;
    private LoanStatus status;
    private Long issuedByUserId;
    private String issuedByUsername;
    private Long returnedByUserId;
    private String returnedByUsername;
    private boolean isOverdue;
    private long overdueDays;
    private double fineAmount;
    
    // Default constructor
    public LoanResponse() {}
    
    // Static factory method to create LoanResponse from Loan entity
    public static LoanResponse fromEntity(Loan loan) {
        LoanResponse response = new LoanResponse();
        response.setId(loan.getId());
        response.setBookId(loan.getBookId());
        response.setMemberId(loan.getMemberId());
        response.setIssuedAt(loan.getIssuedAt());
        response.setDueAt(loan.getDueAt());
        response.setReturnedAt(loan.getReturnedAt());
        response.setStatus(loan.getStatus());
        response.setIssuedByUserId(loan.getIssuedByUserId());
        response.setReturnedByUserId(loan.getReturnedByUserId());
        
        // Set book details if available
        if (loan.getBook() != null) {
            response.setBookTitle(loan.getBook().getTitle());
            response.setBookAuthor(loan.getBook().getAuthor());
            response.setBookIsbn(loan.getBook().getIsbn());
        }
        
        // Set member details if available
        if (loan.getMember() != null) {
            response.setMemberName(loan.getMember().getFullName());
            response.setMemberEmail(loan.getMember().getEmail());
        }
        
        // Set issued by user details if available
        if (loan.getIssuedByUser() != null) {
            response.setIssuedByUsername(loan.getIssuedByUser().getUsername());
        }
        
        // Set returned by user details if available
        if (loan.getReturnedByUser() != null) {
            response.setReturnedByUsername(loan.getReturnedByUser().getUsername());
        }
        
        // Calculate overdue status and fine (business rule: ₹5 per day)
        if (loan.getStatus() == LoanStatus.ACTIVE && loan.getDueAt() != null) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(loan.getDueAt())) {
                response.setOverdue(true);
                long days = java.time.Duration.between(loan.getDueAt(), now).toDays();
                response.setOverdueDays(days);
                response.setFineAmount(days * 5.0); // ₹5 per day
            }
        }
        
        return response;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public String getMemberName() {
        return memberName;
    }
    
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
    
    public String getMemberEmail() {
        return memberEmail;
    }
    
    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
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
    
    public LocalDateTime getReturnedAt() {
        return returnedAt;
    }
    
    public void setReturnedAt(LocalDateTime returnedAt) {
        this.returnedAt = returnedAt;
    }
    
    public LoanStatus getStatus() {
        return status;
    }
    
    public void setStatus(LoanStatus status) {
        this.status = status;
    }
    
    public Long getIssuedByUserId() {
        return issuedByUserId;
    }
    
    public void setIssuedByUserId(Long issuedByUserId) {
        this.issuedByUserId = issuedByUserId;
    }
    
    public String getIssuedByUsername() {
        return issuedByUsername;
    }
    
    public void setIssuedByUsername(String issuedByUsername) {
        this.issuedByUsername = issuedByUsername;
    }
    
    public Long getReturnedByUserId() {
        return returnedByUserId;
    }
    
    public void setReturnedByUserId(Long returnedByUserId) {
        this.returnedByUserId = returnedByUserId;
    }
    
    public String getReturnedByUsername() {
        return returnedByUsername;
    }
    
    public void setReturnedByUsername(String returnedByUsername) {
        this.returnedByUsername = returnedByUsername;
    }
    
    public boolean isOverdue() {
        return isOverdue;
    }
    
    public void setOverdue(boolean overdue) {
        isOverdue = overdue;
    }
    
    public long getOverdueDays() {
        return overdueDays;
    }
    
    public void setOverdueDays(long overdueDays) {
        this.overdueDays = overdueDays;
    }
    
    public double getFineAmount() {
        return fineAmount;
    }
    
    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }
    
    @Override
    public String toString() {
        return "LoanResponse{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", bookTitle='" + bookTitle + '\'' +
                ", memberId=" + memberId +
                ", memberName='" + memberName + '\'' +
                ", issuedAt=" + issuedAt +
                ", dueAt=" + dueAt +
                ", returnedAt=" + returnedAt +
                ", status=" + status +
                ", isOverdue=" + isOverdue +
                ", fineAmount=" + fineAmount +
                '}';
    }
}
