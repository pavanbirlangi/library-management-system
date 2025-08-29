package com.lms.backend.service.impl;

import com.lms.backend.dto.reservation.ReservationResponse;
import com.lms.backend.exception.BadRequestException;
import com.lms.backend.exception.ConflictException;
import com.lms.backend.exception.NotFoundException;
import com.lms.backend.model.entity.Book;
import com.lms.backend.model.entity.Member;
import com.lms.backend.model.entity.Reservation;
import com.lms.backend.model.entity.User;
import com.lms.backend.model.enums.ReservationStatus;
import com.lms.backend.model.enums.Role;
import com.lms.backend.repository.BookRepository;
import com.lms.backend.repository.MemberRepository;
import com.lms.backend.repository.ReservationRepository;
import com.lms.backend.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {
    
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    
    @Autowired
    public ReservationServiceImpl(BookRepository bookRepository,
                                 MemberRepository memberRepository,
                                 ReservationRepository reservationRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.reservationRepository = reservationRepository;
    }
    
    @Override
    public ReservationResponse createReservation(Long bookId, User currentUser) {
        // 1. Validate user is a member
        if (currentUser.getRole() != Role.MEMBER) {
            throw new BadRequestException("Only members can create reservations");
        }
        
        // 2. Find the member profile for the current user
        Member member = findMemberByUser(currentUser);
        
        // 3. Find and validate the book
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + bookId));
        
        // 4. Check if book is available (if available, no need to reserve)
        if (book.getAvailableCopies() > 0) {
            throw new BadRequestException("Book is currently available for borrowing. No reservation needed.");
        }
        
        // 5. Check if member already has an active reservation for this book
        boolean hasActiveReservation = reservationRepository
                .existsByMemberAndBookAndStatus(member, book, ReservationStatus.ACTIVE);
        
        if (hasActiveReservation) {
            throw new ConflictException("Member already has an active reservation for this book");
        }
        
        // 6. Calculate queue position (next position in line)
        long activeReservations = reservationRepository.countByBookAndStatus(book, ReservationStatus.ACTIVE);
        int queuePosition = (int) (activeReservations + 1);
        
        // 7. Create the reservation
        Reservation reservation = new Reservation();
        reservation.setBookId(book.getId());
        reservation.setMemberId(member.getId());
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setQueuePosition(queuePosition);
        // createdAt and updatedAt will be set by @PrePersist
        
        // 8. Save the reservation
        Reservation savedReservation = reservationRepository.save(reservation);
        
        return ReservationResponse.fromEntity(savedReservation);
    }
    
    /**
     * Find member profile for the current user
     */
    private Member findMemberByUser(User currentUser) {
        // Find member by user relationship
        return memberRepository.findAll().stream()
                .filter(member -> member.getUser() != null && member.getUser().getId().equals(currentUser.getId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Member profile not found for current user"));
    }
}
