package com.lms.backend.service;

import com.lms.backend.dto.loan.LoanResponse;
import com.lms.backend.model.enums.LoanStatus;

import java.util.List;

public interface LoanService {
    List<LoanResponse> findAll();
    List<LoanResponse> findByStatus(LoanStatus status);
    List<LoanResponse> findOverdue();
}
