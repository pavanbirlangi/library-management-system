package com.lms.backend.dto.report;

/**
 * DTO for most borrowed books report
 */
public class MostBorrowedResponse {
    
    private Long bookId;
    private String title;
    private String author;
    private String isbn;
    private Long borrowCount;
    
    // Default constructor
    public MostBorrowedResponse() {}
    
    // Constructor with required fields
    public MostBorrowedResponse(Long bookId, String title, Long borrowCount) {
        this.bookId = bookId;
        this.title = title;
        this.borrowCount = borrowCount;
    }
    
    // Constructor with all fields
    public MostBorrowedResponse(Long bookId, String title, String author, String isbn, Long borrowCount) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.borrowCount = borrowCount;
    }
    
    // Getters and Setters
    public Long getBookId() {
        return bookId;
    }
    
    public void setBookId(Long bookId) {
        this.bookId = bookId;
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
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public Long getBorrowCount() {
        return borrowCount;
    }
    
    public void setBorrowCount(Long borrowCount) {
        this.borrowCount = borrowCount;
    }
    
    @Override
    public String toString() {
        return "MostBorrowedResponse{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", borrowCount=" + borrowCount +
                '}';
    }
}
