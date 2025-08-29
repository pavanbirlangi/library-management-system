package com.lms.backend.controller;

import com.lms.backend.dto.loan.LoanResponse;
import com.lms.backend.model.enums.LoanStatus;
import com.lms.backend.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@Tag(name = "Loans", description = "Loan listing and filters for staff")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @Operation(summary = "List all loans", description = "Returns all loans. Optional status filter: ACTIVE or RETURNED")
    public ResponseEntity<List<LoanResponse>> listLoans(@RequestParam(required = false) String status) {
        if (status == null || status.isBlank()) {
            return ResponseEntity.ok(loanService.findAll());
        }
        LoanStatus st = LoanStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(loanService.findByStatus(st));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @Operation(summary = "List overdue loans", description = "Returns all ACTIVE loans past due date")
    public ResponseEntity<List<LoanResponse>> listOverdueLoans() {
        return ResponseEntity.ok(loanService.findOverdue());
    }
}
