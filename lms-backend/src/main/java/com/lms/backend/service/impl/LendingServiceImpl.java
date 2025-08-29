package com.lms.backend.service.impl;

import com.lms.backend.dto.lending.IssueRequest;
import com.lms.backend.dto.lending.ReturnRequest;
import com.lms.backend.dto.loan.LoanResponse;
import com.lms.backend.exception.BadRequestException;
import com.lms.backend.exception.ConflictException;
import com.lms.backend.exception.NotFoundException;
import com.lms.backend.model.entity.*;
import com.lms.backend.model.enums.LoanStatus;
import com.lms.backend.model.enums.ReservationStatus;
import com.lms.backend.model.enums.Role;
import com.lms.backend.repository.*;
import com.lms.backend.service.LendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@Transactional
public class LendingServiceImpl implements LendingService {
    
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    
    // Business constants
    private static final int DEFAULT_LOAN_PERIOD_DAYS = 14;
    private static final double FINE_PER_DAY = 5.0;
    private static final int DEFAULT_BORROW_LIMIT = 5; // Default member borrow limit
    
    @Autowired
    public LendingServiceImpl(BookRepository bookRepository,
                             LoanRepository loanRepository,
                             MemberRepository memberRepository,
                             UserRepository userRepository,
                             ReservationRepository reservationRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
    }
    
    @Override
    public LoanResponse borrowBook(IssueRequest request, User currentUser) {
        // 1. Identify the member
        Member member = identifyMember(request, currentUser);
        
        // 2. Find and validate book
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + request.getBookId()));
        
        // 3. Perform validations
        validateBookAvailability(book);
        validateMemberEligibility(member);
        
        // 4. Create loan and update book availability
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueAt;
        if (currentUser.getRole() == Role.LIBRARIAN || currentUser.getRole() == Role.ADMIN) {
            // Allow manual due date; fallback to default if not provided
            dueAt = (request.getDueAt() != null) ? request.getDueAt() : now.plusDays(DEFAULT_LOAN_PERIOD_DAYS);
        } else {
            // Members always get default 14 days
            dueAt = now.plusDays(DEFAULT_LOAN_PERIOD_DAYS);
        }
        Loan loan = createLoanWithDue(book, member, currentUser, now, dueAt);
        updateBookAvailability(book, -1); // Decrement available copies
        
        // 5. Save entities
        bookRepository.save(book);
        Loan savedLoan = loanRepository.save(loan);
        
        return LoanResponse.fromEntity(savedLoan);
    }
    
    @Override
    public LoanResponse returnBook(ReturnRequest request, User currentUser) {
        // 1. Find the loan
        Loan loan = loanRepository.findById(request.getLoanId())
                .orElseThrow(() -> new NotFoundException("Loan not found with ID: " + request.getLoanId()));
        
        // 1a. Ownership check: members may only return their own loans
        if (currentUser.getRole() == Role.MEMBER) {
            Member currentMember = memberRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new BadRequestException("Member profile not found for current user"));
            if (!loan.getMemberId().equals(currentMember.getId())) {
                throw new BadRequestException("You can only return your own loans");
            }
        }
        // Librarian/Admin can return any member's loan
        
        // 2. Validate loan can be returned
        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new BadRequestException("Loan has already been returned");
        }
        
        // 3. Update loan status
        LocalDateTime returnedAt = LocalDateTime.now();
        loan.setReturnedAt(returnedAt);
        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnedByUserId(currentUser.getId());
        
        // 4. Update book availability
        Book book = bookRepository.findById(loan.getBookId())
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + loan.getBookId()));
        updateBookAvailability(book, 1); // Increment available copies
        
        // 5. Calculate fine if overdue (for now, just log the fine calculation)
        calculateFineAmount(loan, returnedAt);
        
        // 6. Save entities
        bookRepository.save(book);
        Loan savedLoan = loanRepository.save(loan);
        
        // 7. Check for reservations and fulfill if any
        fulfillNextReservation(book);
        
        return LoanResponse.fromEntity(savedLoan);
    }
    
    @Override
    public LoanResponse fulfillNextReservation(Book book) {
        // Find the next reservation in queue
        Optional<Reservation> nextReservation = reservationRepository
                .findFirstByBookAndStatusOrderByCreatedAtAsc(book, ReservationStatus.ACTIVE);
        
        if (nextReservation.isPresent()) {
            Reservation reservation = nextReservation.get();
            Member member = memberRepository.findById(reservation.getMemberId())
                    .orElseThrow(() -> new NotFoundException("Member not found for reservation"));
            
            // Create loan for the reserved member
            User systemUser = getCurrentSystemUser(); // Get system user for automatic fulfillment
            Loan loan = createLoan(book, member, systemUser);
            updateBookAvailability(book, -1); // Decrement available copies again
            
            // Update reservation status
            reservation.setStatus(ReservationStatus.FULFILLED);
            reservation.setUpdatedAt(LocalDateTime.now());
            
            // Save entities
            bookRepository.save(book);
            reservationRepository.save(reservation);
            Loan savedLoan = loanRepository.save(loan);
            
            return LoanResponse.fromEntity(savedLoan);
        }
        
        return null; // No reservations to fulfill
    }
    
    /**
     * Identify the member based on request and current user
     */
    private Member identifyMember(IssueRequest request, User currentUser) {
        if (request.getMemberId() != null) {
            // Librarian or Admin specifying member ID
            if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.LIBRARIAN) {
                throw new BadRequestException("Only librarians and admins can specify member ID");
            }
            return memberRepository.findById(request.getMemberId())
                    .orElseThrow(() -> new NotFoundException("Member not found with ID: " + request.getMemberId()));
        } else {
            // Member borrowing for themselves - find member by user ID
            if (currentUser.getRole() != Role.MEMBER) {
                throw new BadRequestException("Member ID is required for non-member users");
            }
            return memberRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new NotFoundException("Member profile not found for current user"));
        }
    }
    
    /**
     * Validate book availability
     */
    private void validateBookAvailability(Book book) {
        if (book.getAvailableCopies() <= 0) {
            throw new BadRequestException("Book is not available for borrowing. Available copies: " + book.getAvailableCopies());
        }
    }
    
    /**
     * Validate member eligibility for borrowing
     */
    private void validateMemberEligibility(Member member) {
        // Check borrow limit
        long activeLoans = loanRepository.countByMemberAndStatus(member, LoanStatus.ACTIVE);
        if (activeLoans >= DEFAULT_BORROW_LIMIT) {
            throw new ConflictException("Member has reached the borrow limit of " + DEFAULT_BORROW_LIMIT + " books. Current active loans: " + activeLoans);
        }
    }
    
    /**
     * Create a new loan
     */
    private Loan createLoan(Book book, Member member, User issuedByUser) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueAt = now.plusDays(DEFAULT_LOAN_PERIOD_DAYS);
        
        Loan loan = new Loan();
        loan.setBookId(book.getId());
        loan.setMemberId(member.getId());
        loan.setIssuedAt(now);
        loan.setDueAt(dueAt);
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setIssuedByUserId(issuedByUser.getId());
        
        return loan;
    }

    private Loan createLoanWithDue(Book book, Member member, User issuedByUser, LocalDateTime issuedAt, LocalDateTime dueAt) {
        Loan loan = new Loan();
        loan.setBookId(book.getId());
        loan.setMemberId(member.getId());
        loan.setIssuedAt(issuedAt);
        loan.setDueAt(dueAt);
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setIssuedByUserId(issuedByUser.getId());
        return loan;
    }
    
    /**
     * Update book availability
     */
    private void updateBookAvailability(Book book, int change) {
        int newAvailableCopies = book.getAvailableCopies() + change;
        
        // Validate the change doesn't make available copies negative or exceed total copies
        if (newAvailableCopies < 0) {
            throw new BadRequestException("Cannot reduce available copies below zero");
        }
        if (newAvailableCopies > book.getTotalCopies()) {
            throw new BadRequestException("Available copies cannot exceed total copies");
        }
        
        book.setAvailableCopies(newAvailableCopies);
    }
    
    /**
     * Calculate fine amount if the book is returned late
     * For now, this just calculates and returns the fine amount
     * Later, this can be integrated with FineService to create actual fine records
     */
    private double calculateFineAmount(Loan loan, LocalDateTime returnedAt) {
        if (returnedAt.isAfter(loan.getDueAt())) {
            long overdueDays = ChronoUnit.DAYS.between(loan.getDueAt(), returnedAt);
            if (overdueDays > 0) {
                double fineAmount = overdueDays * FINE_PER_DAY;
                System.out.println("Fine calculated for loan " + loan.getId() + ": ₹" + fineAmount + 
                    " (" + overdueDays + " days overdue at ₹" + FINE_PER_DAY + " per day)");
                return fineAmount;
            }
        }
        return 0.0;
    }
    
    /**
     * Get system user for automatic operations
     */
    private User getCurrentSystemUser() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.ADMIN)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No admin user found for system operations"));
    }
}
