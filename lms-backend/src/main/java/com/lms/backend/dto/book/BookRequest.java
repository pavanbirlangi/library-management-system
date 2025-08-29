package com.lms.backend.dto.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class BookRequest {
    
    @NotBlank(message = "ISBN is required")
    @Size(max = 20, message = "ISBN must not exceed 20 characters")
    private String isbn;
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;
    
    @NotBlank(message = "Author is required")
    @Size(max = 150, message = "Author must not exceed 150 characters")
    private String author;
    
    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;
    
    @NotNull(message = "Total copies is required")
    @Min(value = 0, message = "Total copies must be non-negative")
    private Integer totalCopies;
    
    // Default constructor
    public BookRequest() {}
    
    // Constructor with all fields
    public BookRequest(String isbn, String title, String author, String category, Integer totalCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.totalCopies = totalCopies;
    }
    
    // Getters and Setters
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
    
    @Override
    public String toString() {
        return "BookRequest{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", totalCopies=" + totalCopies +
                '}';
    }
}
