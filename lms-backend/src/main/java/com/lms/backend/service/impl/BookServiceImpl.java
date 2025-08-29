package com.lms.backend.service.impl;

import com.lms.backend.dto.book.BookRequest;
import com.lms.backend.dto.book.BookResponse;
import com.lms.backend.exception.ConflictException;
import com.lms.backend.exception.NotFoundException;
import com.lms.backend.model.entity.Book;
import com.lms.backend.repository.BookRepository;
import com.lms.backend.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookServiceImpl implements BookService {
    
    private final BookRepository bookRepository;
    
    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> findAll() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(BookResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public BookResponse findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + id));
        return BookResponse.fromEntity(book);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> searchBooks(String title, String author, String category) {
        Specification<Book> spec = createBookSpecification(title, author, category);
        List<Book> books = bookRepository.findAll(spec);
        return books.stream()
                .map(BookResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public BookResponse createBook(BookRequest request) {
        // Check if ISBN already exists
        if (bookRepository.findByIsbn(request.getIsbn()).isPresent()) {
            throw new ConflictException("Book with ISBN " + request.getIsbn() + " already exists");
        }
        
        Book book = new Book();
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setCategory(request.getCategory());
        book.setTotalCopies(request.getTotalCopies());
        // Set available copies equal to total copies upon creation
        book.setAvailableCopies(request.getTotalCopies());
        
        Book savedBook = bookRepository.save(book);
        return BookResponse.fromEntity(savedBook);
    }
    
    @Override
    public BookResponse updateBook(Long id, BookRequest request) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + id));
        
        // Check if ISBN is being changed and if the new ISBN already exists
        if (!existingBook.getIsbn().equals(request.getIsbn())) {
            if (bookRepository.findByIsbn(request.getIsbn()).isPresent()) {
                throw new ConflictException("Book with ISBN " + request.getIsbn() + " already exists");
            }
        }
        
        // Calculate the difference in total copies to adjust available copies
        int copyDifference = request.getTotalCopies() - existingBook.getTotalCopies();
        int newAvailableCopies = existingBook.getAvailableCopies() + copyDifference;
        
        // Ensure available copies doesn't go below 0
        if (newAvailableCopies < 0) {
            throw new ConflictException("Cannot reduce total copies below the number of currently loaned books");
        }
        
        existingBook.setIsbn(request.getIsbn());
        existingBook.setTitle(request.getTitle());
        existingBook.setAuthor(request.getAuthor());
        existingBook.setCategory(request.getCategory());
        existingBook.setTotalCopies(request.getTotalCopies());
        existingBook.setAvailableCopies(newAvailableCopies);
        
        Book updatedBook = bookRepository.save(existingBook);
        return BookResponse.fromEntity(updatedBook);
    }
    
    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + id));
        
        // Check if book has active loans (books with available copies less than total copies)
        if (book.getAvailableCopies() < book.getTotalCopies()) {
            throw new ConflictException("Cannot delete book with active loans. Available copies: " 
                    + book.getAvailableCopies() + ", Total copies: " + book.getTotalCopies());
        }
        
        bookRepository.delete(book);
    }
    
    /**
     * Create dynamic specification for book search
     */
    private Specification<Book> createBookSpecification(String title, String author, String category) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (title != null && !title.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")), 
                        "%" + title.toLowerCase() + "%"
                ));
            }
            
            if (author != null && !author.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("author")), 
                        "%" + author.toLowerCase() + "%"
                ));
            }
            
            if (category != null && !category.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("category")), 
                        category.toLowerCase()
                ));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
