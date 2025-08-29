package com.lms.backend.dto.reservation;

import com.lms.backend.model.entity.Reservation;
import com.lms.backend.model.enums.ReservationStatus;
import java.time.LocalDateTime;

public class ReservationResponse {
    
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookIsbn;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private ReservationStatus status;
    private Integer queuePosition;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isNext; // Indicates if this is the next reservation to be fulfilled
    private long totalQueueAhead; // Number of reservations ahead in the queue
    
    // Default constructor
    public ReservationResponse() {}
    
    // Static factory method to create ReservationResponse from Reservation entity
    public static ReservationResponse fromEntity(Reservation reservation) {
        ReservationResponse response = new ReservationResponse();
        response.setId(reservation.getId());
        response.setBookId(reservation.getBookId());
        response.setMemberId(reservation.getMemberId());
        response.setStatus(reservation.getStatus());
        response.setQueuePosition(reservation.getQueuePosition());
        response.setCreatedAt(reservation.getCreatedAt());
        response.setUpdatedAt(reservation.getUpdatedAt());
        
        // Set book details if available
        if (reservation.getBook() != null) {
            response.setBookTitle(reservation.getBook().getTitle());
            response.setBookAuthor(reservation.getBook().getAuthor());
            response.setBookIsbn(reservation.getBook().getIsbn());
        }
        
        // Set member details if available
        if (reservation.getMember() != null) {
            response.setMemberName(reservation.getMember().getFullName());
            response.setMemberEmail(reservation.getMember().getEmail());
        }
        
        // Set queue status
        if (reservation.getQueuePosition() != null) {
            response.setNext(reservation.getQueuePosition() == 1);
            response.setTotalQueueAhead(Math.max(0, reservation.getQueuePosition() - 1));
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
    
    public ReservationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
    
    public Integer getQueuePosition() {
        return queuePosition;
    }
    
    public void setQueuePosition(Integer queuePosition) {
        this.queuePosition = queuePosition;
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
    
    public boolean isNext() {
        return isNext;
    }
    
    public void setNext(boolean next) {
        isNext = next;
    }
    
    public long getTotalQueueAhead() {
        return totalQueueAhead;
    }
    
    public void setTotalQueueAhead(long totalQueueAhead) {
        this.totalQueueAhead = totalQueueAhead;
    }
    
    @Override
    public String toString() {
        return "ReservationResponse{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", bookTitle='" + bookTitle + '\'' +
                ", memberId=" + memberId +
                ", memberName='" + memberName + '\'' +
                ", status=" + status +
                ", queuePosition=" + queuePosition +
                ", createdAt=" + createdAt +
                ", isNext=" + isNext +
                ", totalQueueAhead=" + totalQueueAhead +
                '}';
    }
}
