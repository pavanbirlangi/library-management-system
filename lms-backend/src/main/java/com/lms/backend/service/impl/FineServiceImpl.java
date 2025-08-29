package com.lms.backend.service.impl;

import com.lms.backend.dto.fine.FineResponse;
import com.lms.backend.model.entity.Fine;
import com.lms.backend.model.entity.Loan;
import com.lms.backend.model.entity.Member;
import com.lms.backend.model.entity.User;
import com.lms.backend.model.enums.FineStatus;
import com.lms.backend.model.enums.Role;
import com.lms.backend.repository.FineRepository;
import com.lms.backend.service.FineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FineServiceImpl implements FineService {

    private final FineRepository fineRepository;
    
    // Fine rate per day (₹5 as per business rules)
    private static final BigDecimal FINE_PER_DAY = new BigDecimal("5.00");

    @Autowired
    public FineServiceImpl(FineRepository fineRepository) {
        this.fineRepository = fineRepository;
    }

    @Override
    public Fine createFine(Loan loan) {
        // Check if fine already exists for this loan
        if (fineRepository.findByLoanIdAndStatus(loan.getId(), FineStatus.PENDING).isPresent()) {
            throw new IllegalStateException("Fine already exists for loan ID: " + loan.getId());
        }
        
        // Calculate fine amount
        BigDecimal fineAmount = calculateFineAmount(loan);
        
        if (fineAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Fine amount must be greater than zero");
        }
        
        // Create new fine
        Fine fine = new Fine();
        fine.setLoanId(loan.getId());
        fine.setMemberId(loan.getMemberId());
        fine.setAmount(fineAmount);
        fine.setStatus(FineStatus.PENDING);
        fine.setCalculatedAt(LocalDateTime.now());
        
        return fineRepository.save(fine);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FineResponse> getFinesForMember(Member member) {
        List<Fine> fines = fineRepository.findByMember(member);
        return fines.stream()
                .map(this::convertToFineResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FineResponse> getFinesForMember(Long memberId) {
        List<Fine> fines = fineRepository.findByMemberId(memberId);
        return fines.stream()
                .map(this::convertToFineResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FineResponse> getAllPendingFines() {
        List<Fine> pendingFines = fineRepository.findByStatus(FineStatus.PENDING);
        return pendingFines.stream()
                .map(this::convertToFineResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FineResponse payFine(Long fineId, User librarianUser) {
        return payFine(fineId, librarianUser, null, null);
    }

    @Override
    public FineResponse payFine(Long fineId, User librarianUser, String paymentMethod, String paymentRef) {
        // Validate librarian role
        if (!librarianUser.getRole().equals(Role.LIBRARIAN)) {
            throw new IllegalArgumentException("Only librarians can process fine payments");
        }
        
        // Find the fine
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new IllegalArgumentException("Fine not found with ID: " + fineId));
        
        // Check if fine is already settled
        if (fine.getStatus() != FineStatus.PENDING) {
            throw new IllegalStateException("Fine is already " + fine.getStatus().toString().toLowerCase());
        }
        
        // Update fine status
        fine.setStatus(FineStatus.SETTLED);
        fine.setSettledAt(LocalDateTime.now());
        fine.setSettledByUserId(librarianUser.getId());
        fine.setPaymentMethod(paymentMethod);
        fine.setPaymentRef(paymentRef);
        
        Fine savedFine = fineRepository.save(fine);
        return convertToFineResponse(savedFine);
    }

    @Override
    @Transactional(readOnly = true)
    public FineResponse getFineById(Long fineId) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new IllegalArgumentException("Fine not found with ID: " + fineId));
        return convertToFineResponse(fine);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPendingFines(Long memberId) {
        return fineRepository.existsByMemberIdAndStatus(memberId, FineStatus.PENDING);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalPendingFines(Long memberId) {
        return fineRepository.sumAmountByMemberIdAndStatus(memberId, FineStatus.PENDING);
    }

    /**
     * Calculate fine amount based on overdue days
     * Business rule: ₹5 per day fine
     */
    private BigDecimal calculateFineAmount(Loan loan) {
        if (loan.getDueAt() == null) {
            throw new IllegalArgumentException("Loan due date cannot be null");
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueDate = loan.getDueAt();
        
        // Calculate overdue days
        long overdueDays = ChronoUnit.DAYS.between(dueDate, now);
        
        if (overdueDays <= 0) {
            return BigDecimal.ZERO; // No fine if not overdue
        }
        
        return FINE_PER_DAY.multiply(BigDecimal.valueOf(overdueDays));
    }

    /**
     * Convert Fine entity to FineResponse DTO
     */
    private FineResponse convertToFineResponse(Fine fine) {
        FineResponse response = new FineResponse();
        response.setId(fine.getId());
        response.setLoanId(fine.getLoanId());
        response.setMemberId(fine.getMemberId());
        response.setAmount(fine.getAmount());
        response.setStatus(fine.getStatus());
        response.setPaymentMethod(fine.getPaymentMethod());
        response.setPaymentRef(fine.getPaymentRef());
        response.setCalculatedAt(fine.getCalculatedAt());
        response.setSettledAt(fine.getSettledAt());
        response.setSettledByUserId(fine.getSettledByUserId());
        
        // Set member information if available
        if (fine.getMember() != null) {
            response.setMemberName(fine.getMember().getFullName());
            response.setMemberEmail(fine.getMember().getEmail());
        }
        
        // Set book information if available
        if (fine.getLoan() != null && fine.getLoan().getBook() != null) {
            response.setBookTitle(fine.getLoan().getBook().getTitle());
            response.setBookIsbn(fine.getLoan().getBook().getIsbn());
        }
        
        // Set settled by user name if available
        if (fine.getSettledByUser() != null) {
            response.setSettledByUserName(fine.getSettledByUser().getUsername());
        }
        
        return response;
    }
}
