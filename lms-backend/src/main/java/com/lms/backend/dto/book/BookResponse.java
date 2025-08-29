package com.lms.backend.dto.book;

import com.lms.backend.model.entity.Book;
import java.time.LocalDateTime;

public class BookResponse {
    
    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String category;
    private Integer totalCopies;
    private Integer availableCopies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Default constructor
    public BookResponse() {}
    
    // Constructor with all fields
    public BookResponse(Long id, String isbn, String title, String author, String category, 
                       Integer totalCopies, Integer availableCopies, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Static factory method to create BookResponse from Book entity
    public static BookResponse fromEntity(Book book) {
        return new BookResponse(
            book.getId(),
            book.getIsbn(),
            book.getTitle(),
            book.getAuthor(),
            book.getCategory(),
            book.getTotalCopies(),
            book.getAvailableCopies(),
            book.getCreatedAt(),
            book.getUpdatedAt()
        );
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Integer getTotalCopies() {
        return totalCopies;
    }
    
    public void setTotalCopies(Integer totalCopies) {
        this.totalCopies = totalCopies;
    }
    
    public Integer getAvailableCopies() {
        return availableCopies;
    }
    
    public void setAvailableCopies(Integer availableCopies) {
        this.availableCopies = availableCopies;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "BookResponse{" +
                "id=" + id +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", totalCopies=" + totalCopies +
                ", availableCopies=" + availableCopies +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
