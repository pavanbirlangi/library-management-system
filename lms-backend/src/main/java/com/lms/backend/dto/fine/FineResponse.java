package com.lms.backend.dto.fine;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lms.backend.model.enums.FineStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO to represent a fine in API responses
 */
public class FineResponse {
    
    private Long id;
    
    private Long loanId;
    
    private Long memberId;
    
    private String memberName;
    
    private String memberEmail;
    
    private String bookTitle;
    
    private String bookIsbn;
    
    private BigDecimal amount;
    
    private FineStatus status;
    
    private String paymentMethod;
    
    private String paymentRef;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime calculatedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime settledAt;
    
    private Long settledByUserId;
    
    private String settledByUserName;
    
    private String reason;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // Default constructor
    public FineResponse() {}
    
    // Constructor with essential fields
    public FineResponse(Long id, Long loanId, Long memberId, String memberName, 
                       String bookTitle, BigDecimal amount, FineStatus status) {
        this.id = id;
        this.loanId = loanId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.bookTitle = bookTitle;
        this.amount = amount;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getLoanId() {
        return loanId;
    }
    
    public void setLoanId(Long loanId) {
        this.loanId = loanId;
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
    
    public String getBookTitle() {
        return bookTitle;
    }
    
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
    
    public String getBookIsbn() {
        return bookIsbn;
    }
    
    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public FineStatus getStatus() {
        return status;
    }
    
    public void setStatus(FineStatus status) {
        this.status = status;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getPaymentRef() {
        return paymentRef;
    }
    
    public void setPaymentRef(String paymentRef) {
        this.paymentRef = paymentRef;
    }
    
    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }
    
    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }
    
    public LocalDateTime getSettledAt() {
        return settledAt;
    }
    
    public void setSettledAt(LocalDateTime settledAt) {
        this.settledAt = settledAt;
    }
    
    public Long getSettledByUserId() {
        return settledByUserId;
    }
    
    public void setSettledByUserId(Long settledByUserId) {
        this.settledByUserId = settledByUserId;
    }
    
    public String getSettledByUserName() {
        return settledByUserName;
    }
    
    public void setSettledByUserName(String settledByUserName) {
        this.settledByUserName = settledByUserName;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "FineResponse{" +
                "id=" + id +
                ", loanId=" + loanId +
                ", memberId=" + memberId +
                ", memberName='" + memberName + '\'' +
                ", bookTitle='" + bookTitle + '\'' +
                ", amount=" + amount +
                ", status=" + status +
                ", calculatedAt=" + calculatedAt +
                ", settledAt=" + settledAt +
                '}';
    }
}
