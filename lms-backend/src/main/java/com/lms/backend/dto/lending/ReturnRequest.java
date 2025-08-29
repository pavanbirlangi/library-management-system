package com.lms.backend.dto.lending;

import jakarta.validation.constraints.NotNull;

public class ReturnRequest {
    
    @NotNull(message = "Loan ID is required")
    private Long loanId;
    
    // Default constructor
    public ReturnRequest() {}
    
    // Constructor with loanId
    public ReturnRequest(Long loanId) {
        this.loanId = loanId;
    }
    
    // Getters and Setters
    public Long getLoanId() {
        return loanId;
    }
    
    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }
    
    @Override
    public String toString() {
        return "ReturnRequest{" +
                "loanId=" + loanId +
                '}';
    }
}
