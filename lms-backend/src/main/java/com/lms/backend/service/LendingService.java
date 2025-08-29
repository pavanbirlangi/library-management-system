package com.lms.backend.service;

import com.lms.backend.dto.lending.IssueRequest;
import com.lms.backend.dto.lending.ReturnRequest;
import com.lms.backend.dto.loan.LoanResponse;
import com.lms.backend.model.entity.Book;
import com.lms.backend.model.entity.User;

public interface LendingService {
    
    /**
     * Borrow a book for a member
     * @param request issue request containing bookId and optional memberId
     * @param currentUser the user performing the operation (librarian or member)
     * @return loan response with details
     * @throws com.lms.backend.exception.NotFoundException if book or member not found
     * @throws com.lms.backend.exception.BadRequestException if book not available
     * @throws com.lms.backend.exception.ConflictException if member has pending fines or reached borrow limit
     */
    LoanResponse borrowBook(IssueRequest request, User currentUser);
    
    /**
     * Return a borrowed book
     * @param request return request containing loanId
     * @param currentUser the user performing the operation
     * @return loan response with updated details including any fine information
     * @throws com.lms.backend.exception.NotFoundException if loan not found
     * @throws com.lms.backend.exception.BadRequestException if loan already returned
     */
    LoanResponse returnBook(ReturnRequest request, User currentUser);
    
    /**
     * Fulfill the next reservation for a book after it's returned
     * @param book the book that was returned
     * @return loan response if reservation was fulfilled, null if no reservations
     */
    LoanResponse fulfillNextReservation(Book book);
}
