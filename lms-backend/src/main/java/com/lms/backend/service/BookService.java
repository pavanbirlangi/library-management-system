package com.lms.backend.service;

import com.lms.backend.dto.book.BookRequest;
import com.lms.backend.dto.book.BookResponse;
import java.util.List;

public interface BookService {
    
    /**
     * Find all books
     * @return list of all book responses
     */
    List<BookResponse> findAll();
    
    /**
     * Find book by ID
     * @param id book ID
     * @return book response
     * @throws com.lms.backend.exception.NotFoundException if book not found
     */
    BookResponse findById(Long id);
    
    /**
     * Search books with dynamic filters
     * @param title title filter (optional)
     * @param author author filter (optional)
     * @param category category filter (optional)
     * @return list of book responses matching the filters
     */
    List<BookResponse> searchBooks(String title, String author, String category);
    
    /**
     * Create a new book
     * @param request book creation request
     * @return created book response
     */
    BookResponse createBook(BookRequest request);
    
    /**
     * Update an existing book
     * @param id book ID to update
     * @param request book update request
     * @return updated book response
     * @throws com.lms.backend.exception.NotFoundException if book not found
     */
    BookResponse updateBook(Long id, BookRequest request);
    
    /**
     * Delete a book
     * @param id book ID to delete
     * @throws com.lms.backend.exception.NotFoundException if book not found
     * @throws com.lms.backend.exception.ConflictException if book has active loans
     */
    void deleteBook(Long id);
}
