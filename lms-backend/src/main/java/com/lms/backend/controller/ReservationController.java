package com.lms.backend.controller;

import com.lms.backend.dto.reservation.ReservationResponse;
import com.lms.backend.model.entity.User;
import com.lms.backend.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReservationController {
    
    private final ReservationService reservationService;
    
    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }
    
    /**
     * Create a reservation for a book
     * POST /api/books/{bookId}/reserve
     * Accessible to MEMBER role only
     */
    @PostMapping("/api/books/{bookId}/reserve")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ReservationResponse> reserveBook(
            @PathVariable Long bookId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Get current user from authentication context
        User currentUser = getCurrentUserFromAuth(userDetails);
        
        ReservationResponse reservationResponse = reservationService.createReservation(bookId, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationResponse);
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
