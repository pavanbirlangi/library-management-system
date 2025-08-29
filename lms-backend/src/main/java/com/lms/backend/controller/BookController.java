package com.lms.backend.controller;

import com.lms.backend.dto.book.BookRequest;
import com.lms.backend.dto.book.BookResponse;
import com.lms.backend.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Book management endpoints")
public class BookController {
    
    private final BookService bookService;
    
    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    
    /**
     * Search books with optional filters
     * GET /api/books/search?title=&author=&category=
     */
    @GetMapping("/search")
    @Operation(summary = "Search Books", description = "Search books by title, author, or category")
    public ResponseEntity<List<BookResponse>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category) {
        
        List<BookResponse> books = bookService.searchBooks(title, author, category);
        return ResponseEntity.ok(books);
    }
    
    /**
     * Get all books
     * GET /api/books
     */
    @GetMapping
    @Operation(summary = "Get All Books", description = "Retrieve all books in the system")
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        
        List<BookResponse> books = bookService.findAll();
        return ResponseEntity.ok(books);
    }
    
    /**
     * Get book by ID
     * GET /api/books/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        BookResponse book = bookService.findById(id);
        return ResponseEntity.ok(book);
    }
    
    /**
     * Create a new book (ADMIN only)
     * POST /api/books
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Book", description = "Create a new book (ADMIN only)")
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest request) {
        BookResponse createdBook = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }
    
    /**
     * Update an existing book (ADMIN only)
     * PUT /api/books/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Book", description = "Update an existing book by ID (ADMIN only)")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id, 
            @Valid @RequestBody BookRequest request) {
        
        BookResponse updatedBook = bookService.updateBook(id, request);
        return ResponseEntity.ok(updatedBook);
    }
    
    /**
     * Delete a book (ADMIN only)
     * DELETE /api/books/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete Book", description = "Delete a book by ID (ADMIN only)")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
