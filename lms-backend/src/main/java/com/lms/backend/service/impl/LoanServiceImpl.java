package com.lms.backend.service.impl;

import com.lms.backend.dto.loan.LoanResponse;
import com.lms.backend.model.entity.Loan;
import com.lms.backend.model.enums.LoanStatus;
import com.lms.backend.repository.LoanRepository;
import com.lms.backend.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Override
    public List<LoanResponse> findAll() {
        List<Loan> loans = loanRepository.findAll();
        return loans.stream().map(LoanResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<LoanResponse> findByStatus(LoanStatus status) {
        List<Loan> loans = loanRepository.findByStatus(status);
        return loans.stream().map(LoanResponse::fromEntity).collect(Collectors.toList());
    }

    @Override
    public List<LoanResponse> findOverdue() {
        List<Loan> loans = loanRepository.findOverdueLoans(LoanStatus.ACTIVE, LocalDateTime.now());
        return loans.stream().map(LoanResponse::fromEntity).collect(Collectors.toList());
    }
}
