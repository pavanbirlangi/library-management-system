package com.lms.backend.service.impl;

import com.lms.backend.dto.member.MemberResponse;
import com.lms.backend.exception.NotFoundException;
import com.lms.backend.model.entity.Member;
import com.lms.backend.model.enums.LoanStatus;
import com.lms.backend.model.enums.FineStatus;
import com.lms.backend.repository.MemberRepository;
import com.lms.backend.repository.LoanRepository;
import com.lms.backend.repository.FineRepository;
import com.lms.backend.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of MemberService for administrative member management
 * Provides read operations for member data
 */
@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private LoanRepository loanRepository;
    
    @Autowired
    private FineRepository fineRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse> findAll() {
        List<Member> members = memberRepository.findAll();
        return members.stream()
                     .map(this::createMemberResponseWithStats)
                     .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse findById(Long id) {
        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Member", id));
        return createMemberResponseWithStats(member);
    }

    /**
     * Helper method to create MemberResponse with calculated statistics
     */
    private MemberResponse createMemberResponseWithStats(Member member) {
        Long memberId = member.getId();
        
        // Calculate loan statistics - use the methods that exist in repositories
        Long totalLoans = loanRepository.countByMemberIdAndStatus(memberId, LoanStatus.ACTIVE) + 
                         loanRepository.countByMemberIdAndStatus(memberId, LoanStatus.RETURNED);
        Long activeLoans = loanRepository.countByMemberIdAndStatus(memberId, LoanStatus.ACTIVE);
        
        // Calculate fine statistics - use the methods that exist in repositories
        Long totalFines = fineRepository.countByMemberIdAndStatus(memberId, FineStatus.PENDING) + 
                         fineRepository.countByMemberIdAndStatus(memberId, FineStatus.SETTLED);
        Long pendingFines = fineRepository.countByMemberIdAndStatus(memberId, FineStatus.PENDING);
        
        // Monetary aggregates
        BigDecimal totalPaid = safeBigDecimal(fineRepository.sumAmountByMemberIdAndStatus(memberId, FineStatus.SETTLED));
        BigDecimal pendingDue = safeBigDecimal(fineRepository.sumAmountByMemberIdAndStatus(memberId, FineStatus.PENDING));
        
        return MemberResponse.fromEntityWithStats(member, totalLoans, activeLoans, totalFines, pendingFines, totalPaid, pendingDue);
    }

    private BigDecimal safeBigDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
