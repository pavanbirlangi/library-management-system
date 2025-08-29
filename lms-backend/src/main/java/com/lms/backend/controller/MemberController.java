package com.lms.backend.controller;

import com.lms.backend.dto.fine.FineResponse;
import com.lms.backend.dto.loan.LoanResponse;
import com.lms.backend.dto.member.MemberResponse;
import com.lms.backend.dto.reservation.ReservationResponse;
import com.lms.backend.model.entity.Loan;
import com.lms.backend.model.entity.Member;
import com.lms.backend.model.entity.Reservation;
import com.lms.backend.model.entity.User;
import com.lms.backend.model.enums.ReservationStatus;
import com.lms.backend.repository.LoanRepository;
import com.lms.backend.repository.MemberRepository;
import com.lms.backend.repository.ReservationRepository;
import com.lms.backend.service.FineService;
import com.lms.backend.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/members")
@Tag(name = "Member Management", description = "Member self-service and administrative operations")
@SecurityRequirement(name = "JWT")
public class MemberController {
    
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;
    private final FineService fineService;
    private final MemberService memberService;
    
    @Autowired
    public MemberController(MemberRepository memberRepository,
                           LoanRepository loanRepository,
                           ReservationRepository reservationRepository,
                           FineService fineService,
                           MemberService memberService) {
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
        this.reservationRepository = reservationRepository;
        this.fineService = fineService;
        this.memberService = memberService;
    }
    
    // ========================================
    // ADMINISTRATIVE ENDPOINTS (ADMIN ONLY)
    // ========================================
    
    @Operation(summary = "Get all members", description = "Retrieve all members (ADMIN only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Members retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MemberResponse>> getAllMembers() {
        List<MemberResponse> members = memberService.findAll();
        return ResponseEntity.ok(members);
    }
    
    @Operation(summary = "Get member by ID", description = "Retrieve a specific member by their ID (ADMIN only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Member retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Member not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberResponse> getMemberById(
            @Parameter(description = "Member ID") @PathVariable Long id) {
        MemberResponse member = memberService.findById(id);
        return ResponseEntity.ok(member);
    }
    
    // ========================================
    // MEMBER SELF-SERVICE ENDPOINTS
    // ========================================
    
    /**
     * Get loan history for currently logged-in member
     * GET /api/members/me/loans
     * Accessible to MEMBER role only
     */
    @GetMapping("/me/loans")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<LoanResponse>> getMyLoans(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User currentUser = getCurrentUserFromAuth(userDetails);
            Member currentMember = getCurrentMember(currentUser.getId());
            
            List<Loan> loans = loanRepository.findByMember(currentMember);
            List<LoanResponse> loanResponses = loans.stream()
                    .map(LoanResponse::fromEntity)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(loanResponses);
        } catch (IllegalArgumentException e) {
            // Member not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get fines for currently logged-in member
     * GET /api/members/me/fines
     * Accessible to MEMBER role only
     */
    @GetMapping("/me/fines")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<FineResponse>> getMyFines(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User currentUser = getCurrentUserFromAuth(userDetails);
            Member currentMember = getCurrentMember(currentUser.getId());
            
            List<FineResponse> fines = fineService.getFinesForMember(currentMember);
            return ResponseEntity.ok(fines);
        } catch (IllegalArgumentException e) {
            // Member not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get active reservations for currently logged-in member
     * GET /api/members/me/reservations
     * Accessible to MEMBER role only
     */
    @GetMapping("/me/reservations")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User currentUser = getCurrentUserFromAuth(userDetails);
            Member currentMember = getCurrentMember(currentUser.getId());
            
            // Get active reservations only
            List<Reservation> reservations = reservationRepository.findByMemberAndStatus(
                    currentMember, ReservationStatus.ACTIVE);
            List<ReservationResponse> reservationResponses = reservations.stream()
                    .map(ReservationResponse::fromEntity)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(reservationResponses);
        } catch (IllegalArgumentException e) {
            // Member not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get all reservations (including history) for currently logged-in member
     * GET /api/members/me/reservations/all
     * Accessible to MEMBER role only
     */
    @GetMapping("/me/reservations/all")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<ReservationResponse>> getAllMyReservations(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User currentUser = getCurrentUserFromAuth(userDetails);
            Member currentMember = getCurrentMember(currentUser.getId());
            
            // Get all reservations (including history)
            List<Reservation> reservations = reservationRepository.findByMember(currentMember);
            List<ReservationResponse> reservationResponses = reservations.stream()
                    .map(ReservationResponse::fromEntity)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(reservationResponses);
        } catch (IllegalArgumentException e) {
            // Member not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get member profile information
     * GET /api/members/me
     * Accessible to MEMBER role only
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<Member> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User currentUser = getCurrentUserFromAuth(userDetails);
            Member currentMember = getCurrentMember(currentUser.getId());
            
            return ResponseEntity.ok(currentMember);
        } catch (IllegalArgumentException e) {
            // Member not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Helper method to extract User from UserDetails
     */
    private User getCurrentUserFromAuth(UserDetails userDetails) {
        // Extract User from CustomUserPrincipal
        if (userDetails instanceof com.lms.backend.security.CustomUserDetailsService.CustomUserPrincipal) {
            com.lms.backend.security.CustomUserDetailsService.CustomUserPrincipal customUserPrincipal = 
                (com.lms.backend.security.CustomUserDetailsService.CustomUserPrincipal) userDetails;
            return customUserPrincipal.getUser();
        }
        
        throw new RuntimeException("Invalid UserDetails type. Expected CustomUserPrincipal but got: " + 
                                 userDetails.getClass().getSimpleName());
    }
    
    /**
     * Helper method to get Member from User ID
     */
    private Member getCurrentMember(Long userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found for user ID: " + userId));
    }
}
