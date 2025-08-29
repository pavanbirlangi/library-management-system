package com.lms.backend.model.entity;

import com.lms.backend.model.enums.FineStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fines")
public class Fine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "loan_id", nullable = false)
    private Long loanId;
    
    @Column(name = "member_id", nullable = false)
    private Long memberId;
    
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FineStatus status = FineStatus.PENDING;
    
    @Column(name = "calculated_at", nullable = false, updatable = false)
    private LocalDateTime calculatedAt;
    
    @Column(name = "settled_at")
    private LocalDateTime settledAt;
    
    @Column(name = "settled_by_user_id")
    private Long settledByUserId;
    
    @Column(name = "payment_method", length = 30)
    private String paymentMethod;
    
    @Column(name = "payment_ref", length = 100)
    private String paymentRef;
    
    // Join with other entities
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Loan loan;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Member member;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settled_by_user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User settledByUser;
    
    @PrePersist
    protected void onCreate() {
        calculatedAt = LocalDateTime.now();
    }
    
    // Default constructor
    public Fine() {}
    
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
    
    public Loan getLoan() {
        return loan;
    }
    
    public void setLoan(Loan loan) {
        this.loan = loan;
    }
    
    public Member getMember() {
        return member;
    }
    
    public void setMember(Member member) {
        this.member = member;
    }
    
    public User getSettledByUser() {
        return settledByUser;
    }
    
    public void setSettledByUser(User settledByUser) {
        this.settledByUser = settledByUser;
    }
}
