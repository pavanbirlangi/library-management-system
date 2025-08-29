package com.lms.backend.model.entity;

import com.lms.backend.model.enums.LoanStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "book_id", nullable = false)
    private Long bookId;
    
    @Column(name = "member_id", nullable = false)
    private Long memberId;
    
    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;
    
    @Column(name = "due_at", nullable = false)
    private LocalDateTime dueAt;
    
    @Column(name = "returned_at")
    private LocalDateTime returnedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LoanStatus status = LoanStatus.ACTIVE;
    
    @Column(name = "issued_by_user_id")
    private Long issuedByUserId;
    
    @Column(name = "returned_by_user_id")
    private Long returnedByUserId;
    
    // Join with other entities
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Book book;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Member member;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by_user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User issuedByUser;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "returned_by_user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User returnedByUser;
    
    // Default constructor
    public Loan() {}
    
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
    
    public Long getMemberId() {
        return memberId;
    }
    
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
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
    
    public Long getReturnedByUserId() {
        return returnedByUserId;
    }
    
    public void setReturnedByUserId(Long returnedByUserId) {
        this.returnedByUserId = returnedByUserId;
    }
    
    public Book getBook() {
        return book;
    }
    
    public void setBook(Book book) {
        this.book = book;
    }
    
    public Member getMember() {
        return member;
    }
    
    public void setMember(Member member) {
        this.member = member;
    }
    
    public User getIssuedByUser() {
        return issuedByUser;
    }
    
    public void setIssuedByUser(User issuedByUser) {
        this.issuedByUser = issuedByUser;
    }
    
    public User getReturnedByUser() {
        return returnedByUser;
    }
    
    public void setReturnedByUser(User returnedByUser) {
        this.returnedByUser = returnedByUser;
    }
}
