package com.lms.backend.controller;

import com.lms.backend.dto.fine.FineResponse;
import com.lms.backend.model.entity.User;
import com.lms.backend.service.FineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fines")
public class FineController {
    
    private final FineService fineService;
    
    @Autowired
    public FineController(FineService fineService) {
        this.fineService = fineService;
    }
    
    /**
     * Get all pending fines
     * GET /api/fines/pending
     * Accessible to LIBRARIAN role only
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<List<FineResponse>> getAllPendingFines() {
        try {
            List<FineResponse> pendingFines = fineService.getAllPendingFines();
            return ResponseEntity.ok(pendingFines);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Pay a fine (mark as settled)
     * PUT /api/fines/{fineId}/pay
     * Accessible to LIBRARIAN role only
     */
    @PutMapping("/{fineId}/pay")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<FineResponse> payFine(
            @PathVariable Long fineId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User currentUser = getCurrentUserFromAuth(userDetails);
            FineResponse paidFine = fineService.payFine(fineId, currentUser);
            return ResponseEntity.ok(paidFine);
        } catch (IllegalArgumentException e) {
            // Fine not found or invalid arguments
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            // Fine already settled or business rule violation
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Pay a fine with payment details (mark as settled)
     * PUT /api/fines/{fineId}/pay
     * Accessible to LIBRARIAN role only
     * Optional request body with payment details
     */
    @PutMapping("/{fineId}/pay-with-details")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<FineResponse> payFineWithDetails(
            @PathVariable Long fineId,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String paymentRef,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User currentUser = getCurrentUserFromAuth(userDetails);
            FineResponse paidFine = fineService.payFine(fineId, currentUser, paymentMethod, paymentRef);
            return ResponseEntity.ok(paidFine);
        } catch (IllegalArgumentException e) {
            // Fine not found or invalid arguments
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            // Fine already settled or business rule violation
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get fine by ID
     * GET /api/fines/{fineId}
     * Accessible to LIBRARIAN role only
     */
    @GetMapping("/{fineId}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<FineResponse> getFineById(@PathVariable Long fineId) {
        try {
            FineResponse fine = fineService.getFineById(fineId);
            return ResponseEntity.ok(fine);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get fines for a specific member
     * GET /api/fines/member/{memberId}
     * Accessible to LIBRARIAN role only
     */
    @GetMapping("/member/{memberId}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<List<FineResponse>> getFinesForMember(@PathVariable Long memberId) {
        try {
            List<FineResponse> memberFines = fineService.getFinesForMember(memberId);
            return ResponseEntity.ok(memberFines);
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
}
