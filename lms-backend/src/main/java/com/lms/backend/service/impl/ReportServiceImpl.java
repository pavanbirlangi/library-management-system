package com.lms.backend.service.impl;

import com.lms.backend.dto.report.MostBorrowedResponse;
import com.lms.backend.dto.report.OverdueResponse;
import com.lms.backend.model.entity.Book;
import com.lms.backend.model.entity.Loan;
import com.lms.backend.model.enums.LoanStatus;
import com.lms.backend.repository.LoanRepository;
import com.lms.backend.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final LoanRepository loanRepository;
    
    // Fine rate per day (₹5 as per business rules)
    private static final double FINE_PER_DAY = 5.0;

    @Autowired
    public ReportServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public List<MostBorrowedResponse> getMostBorrowedBooks(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> results = loanRepository.findMostBorrowedBooks(pageable);
        
        return results.stream()
                .map(this::mapToMostBorrowedResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OverdueResponse> getOverdueBooks() {
        List<Loan> overdueLoans = loanRepository.findCurrentlyOverdueLoans();
        
        return overdueLoans.stream()
                .map(this::mapToOverdueResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MostBorrowedResponse> getMostBorrowedBooks() {
        return getMostBorrowedBooks(10); // Default limit of 10
    }

    @Override
    public List<MostBorrowedResponse> getMostBorrowedBooksByCategory(String category, int limit) {
        List<Object[]> results = loanRepository.findMostBorrowedBooksByCategory(category);
        
        return results.stream()
                .limit(limit)
                .map(this::mapToMostBorrowedResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Total loans
        Long totalLoans = loanRepository.getTotalLoansCount();
        stats.put("totalLoans", totalLoans);
        
        // Active loans
        Long activeLoans = loanRepository.getTotalLoansByStatus(LoanStatus.ACTIVE);
        stats.put("activeLoans", activeLoans);
        
        // Returned loans
        Long returnedLoans = loanRepository.getTotalLoansByStatus(LoanStatus.RETURNED);
        stats.put("returnedLoans", returnedLoans);
        
        // Overdue loans count
        List<Loan> overdueLoans = loanRepository.findCurrentlyOverdueLoans();
        stats.put("overdueLoans", overdueLoans.size());
        
        // Calculate total estimated fines for overdue loans
        double totalEstimatedFines = overdueLoans.stream()
                .mapToDouble(this::calculateFine)
                .sum();
        stats.put("totalEstimatedFines", totalEstimatedFines);
        
        return stats;
    }

    @Override
    public List<Object[]> getMostActiveMembers(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return loanRepository.findMostActiveMembers(pageable);
    }

    @Override
    public List<Object> getLoansIssuedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        List<Loan> loans = loanRepository.findLoansIssuedBetween(startDate, endDate);
        return loans.stream()
                .map(loan -> (Object) loan)
                .collect(Collectors.toList());
    }

    @Override
    public List<Object> getLoansReturnedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        List<Loan> loans = loanRepository.findLoansReturnedBetween(startDate, endDate);
        return loans.stream()
                .map(loan -> (Object) loan)
                .collect(Collectors.toList());
    }

    /**
     * Map Object[] from repository query to MostBorrowedResponse
     */
    private MostBorrowedResponse mapToMostBorrowedResponse(Object[] result) {
        Book book = (Book) result[0];
        Long borrowCount = (Long) result[1];
        
        return new MostBorrowedResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                borrowCount
        );
    }

    /**
     * Map Loan entity to OverdueResponse
     */
    private OverdueResponse mapToOverdueResponse(Loan loan) {
        LocalDateTime now = LocalDateTime.now();
        long daysOverdue = ChronoUnit.DAYS.between(loan.getDueAt(), now);
        double estimatedFine = calculateFine(loan);
        
        OverdueResponse response = new OverdueResponse();
        response.setLoanId(loan.getId());
        response.setBookId(loan.getBookId());
        response.setMemberId(loan.getMemberId());
        response.setIssuedAt(loan.getIssuedAt());
        response.setDueAt(loan.getDueAt());
        response.setDaysOverdue(daysOverdue);
        response.setEstimatedFine(estimatedFine);
        
        // Set book information if available
        if (loan.getBook() != null) {
            response.setBookTitle(loan.getBook().getTitle());
            response.setBookAuthor(loan.getBook().getAuthor());
            response.setBookIsbn(loan.getBook().getIsbn());
        }
        
        // Set member information if available
        if (loan.getMember() != null) {
            response.setMemberFullName(loan.getMember().getFullName());
            response.setMemberEmail(loan.getMember().getEmail());
            response.setMemberPhone(loan.getMember().getPhone());
        }
        
        return response;
    }

    /**
     * Calculate fine for an overdue loan
     * Business rule: ₹5 per day fine
     */
    private double calculateFine(Loan loan) {
        if (loan.getDueAt() == null) {
            return 0.0;
        }
        
        LocalDateTime now = LocalDateTime.now();
        long overdueDays = ChronoUnit.DAYS.between(loan.getDueAt(), now);
        
        if (overdueDays <= 0) {
            return 0.0; // No fine if not overdue
        }
        
        return FINE_PER_DAY * overdueDays;
    }
}
