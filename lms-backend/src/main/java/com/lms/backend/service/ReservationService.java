package com.lms.backend.service;

import com.lms.backend.dto.reservation.ReservationResponse;
import com.lms.backend.model.entity.User;

public interface ReservationService {
    
    /**
     * Create a reservation for a book by a member
     * @param bookId the ID of the book to reserve
     * @param currentUser the member creating the reservation
     * @return reservation response with details
     * @throws com.lms.backend.exception.NotFoundException if book not found
     * @throws com.lms.backend.exception.BadRequestException if book is available or member not found
     * @throws com.lms.backend.exception.ConflictException if member already has reservation for this book
     */
    ReservationResponse createReservation(Long bookId, User currentUser);
}
