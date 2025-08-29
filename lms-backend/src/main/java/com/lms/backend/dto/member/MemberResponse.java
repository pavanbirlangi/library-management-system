package com.lms.backend.dto.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lms.backend.dto.user.UserResponse;
import com.lms.backend.model.entity.Member;
import com.lms.backend.model.enums.UserStatus;
import java.math.BigDecimal;

import java.time.LocalDateTime;

/**
 * DTO for member response data
 * Contains all member details including nested UserResponse
 */
public class MemberResponse {
    
    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private UserStatus status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime joinedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // Nested user information
    private UserResponse user;
    
    // Statistics (optional)
    private Long totalLoans;
    private Long activeLoans;
    private Long totalFines;
    private Long pendingFines;
    
    // Monetary aggregates (optional)
    private BigDecimal totalFineAmountPaid;
    private BigDecimal pendingFineAmountDue;
    
    // Default constructor
    public MemberResponse() {}
    
    // Constructor with essential fields
    public MemberResponse(Long id, Long userId, String fullName, String email, 
                         String phone, UserStatus status) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.status = status;
    }
    
    // Constructor with all fields
    public MemberResponse(Long id, Long userId, String fullName, String email, String phone, 
                         UserStatus status, LocalDateTime joinedAt, LocalDateTime updatedAt, 
                         UserResponse user) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.joinedAt = joinedAt;
        this.updatedAt = updatedAt;
        this.user = user;
    }
    
    // Static factory method to create MemberResponse from Member entity
    public static MemberResponse fromEntity(Member member) {
        MemberResponse response = new MemberResponse();
        response.setId(member.getId());
        response.setUserId(member.getUserId());
        response.setFullName(member.getFullName());
        response.setEmail(member.getEmail());
        response.setPhone(member.getPhone());
        response.setStatus(member.getStatus());
        response.setJoinedAt(member.getJoinedAt());
        response.setUpdatedAt(member.getUpdatedAt());
        
        // Set nested user information if available
        if (member.getUser() != null) {
            response.setUser(UserResponse.fromEntity(member.getUser()));
        }
        
        return response;
    }
    
    // Static factory method with user information
    public static MemberResponse fromEntityWithUser(Member member) {
        MemberResponse response = fromEntity(member);
        
        // Ensure user information is included
        if (member.getUser() != null) {
            response.setUser(UserResponse.fromEntity(member.getUser()));
        }
        
        return response;
    }

    // Static factory method with statistics
    public static MemberResponse fromEntityWithStats(Member member, Long totalLoans, Long activeLoans, Long totalFines, Long pendingFines, BigDecimal totalFineAmountPaid, BigDecimal pendingFineAmountDue) {
        MemberResponse response = fromEntityWithUser(member);
        response.setTotalLoans(totalLoans);
        response.setActiveLoans(activeLoans);
        response.setTotalFines(totalFines);
        response.setPendingFines(pendingFines);
        response.setTotalFineAmountPaid(totalFineAmountPaid);
        response.setPendingFineAmountDue(pendingFineAmountDue);
        return response;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public UserStatus getStatus() {
        return status;
    }
    
    public void setStatus(UserStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
    
    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public UserResponse getUser() {
        return user;
    }
    
    public void setUser(UserResponse user) {
        this.user = user;
    }
    
    public Long getTotalLoans() {
        return totalLoans;
    }
    
    public void setTotalLoans(Long totalLoans) {
        this.totalLoans = totalLoans;
    }
    
    public Long getActiveLoans() {
        return activeLoans;
    }
    
    public void setActiveLoans(Long activeLoans) {
        this.activeLoans = activeLoans;
    }
    
    public Long getTotalFines() {
        return totalFines;
    }
    
    public void setTotalFines(Long totalFines) {
        this.totalFines = totalFines;
    }
    
    public Long getPendingFines() {
        return pendingFines;
    }
    
    public void setPendingFines(Long pendingFines) {
        this.pendingFines = pendingFines;
    }

    public BigDecimal getTotalFineAmountPaid() {
        return totalFineAmountPaid;
    }

    public void setTotalFineAmountPaid(BigDecimal totalFineAmountPaid) {
        this.totalFineAmountPaid = totalFineAmountPaid;
    }

    public BigDecimal getPendingFineAmountDue() {
        return pendingFineAmountDue;
    }

    public void setPendingFineAmountDue(BigDecimal pendingFineAmountDue) {
        this.pendingFineAmountDue = pendingFineAmountDue;
    }
    
    @Override
    public String toString() {
        return "MemberResponse{" +
                "id=" + id +
                ", userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", status=" + status +
                ", joinedAt=" + joinedAt +
                ", user=" + user +
                '}';
    }
}
