package com.lms.backend.controller;

import com.lms.backend.dto.lending.IssueRequest;
import com.lms.backend.dto.lending.ReturnRequest;
import com.lms.backend.dto.loan.LoanResponse;
import com.lms.backend.model.entity.User;
import com.lms.backend.service.LendingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lending")
public class LendingController {
    
    private final LendingService lendingService;
    
    @Autowired
    public LendingController(LendingService lendingService) {
        this.lendingService = lendingService;
    }
    
    /**
     * Borrow a book
     * POST /api/lending/borrow
     * Accessible to MEMBER and LIBRARIAN roles
     */
    @PostMapping("/borrow")
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN')")
    public ResponseEntity<LoanResponse> borrowBook(
            @Valid @RequestBody IssueRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Get current user from authentication context
        User currentUser = getCurrentUserFromAuth(userDetails);
        
        LoanResponse loanResponse = lendingService.borrowBook(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(loanResponse);
    }
    
    /**
     * Return a borrowed book
     * POST /api/lending/return
     * Accessible to MEMBER and LIBRARIAN roles
     */
    @PostMapping("/return")
    @PreAuthorize("hasAnyRole('MEMBER', 'LIBRARIAN')")
    public ResponseEntity<LoanResponse> returnBook(
            @Valid @RequestBody ReturnRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // Get current user from authentication context
        User currentUser = getCurrentUserFromAuth(userDetails);
        
        LoanResponse loanResponse = lendingService.returnBook(request, currentUser);
        return ResponseEntity.ok(loanResponse);
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
