package com.lms.backend.dto.common;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Generic DTO to wrap paginated results from Spring Data's Page object
 * Provides a consistent structure for paginated API responses
 */
public class PageResponse<T> {
    
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean empty;
    
    /**
     * Default constructor
     */
    public PageResponse() {}
    
    /**
     * Constructor with all fields
     */
    public PageResponse(List<T> content, int pageNumber, int pageSize, long totalElements, 
                       int totalPages, boolean first, boolean last, boolean empty) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
        this.empty = empty;
    }
    
    /**
     * Static factory method to create PageResponse from Spring Data Page
     * @param page Spring Data Page object
     * @param <T> type of content
     * @return PageResponse wrapping the page data
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast(),
            page.isEmpty()
        );
    }
    
    /**
     * Static factory method to create PageResponse with transformed content
     * @param page Spring Data Page object
     * @param transformedContent transformed content list
     * @param <T> type of transformed content
     * @param <U> type of original content
     * @return PageResponse with transformed content
     */
    public static <T, U> PageResponse<T> from(Page<U> page, List<T> transformedContent) {
        return new PageResponse<>(
            transformedContent,
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast(),
            page.isEmpty()
        );
    }
    
    // Getters and Setters
    public List<T> getContent() {
        return content;
    }
    
    public void setContent(List<T> content) {
        this.content = content;
    }
    
    public int getPageNumber() {
        return pageNumber;
    }
    
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
    
    public int getPageSize() {
        return pageSize;
    }
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    public long getTotalElements() {
        return totalElements;
    }
    
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    
    public boolean isFirst() {
        return first;
    }
    
    public void setFirst(boolean first) {
        this.first = first;
    }
    
    public boolean isLast() {
        return last;
    }
    
    public void setLast(boolean last) {
        this.last = last;
    }
    
    public boolean isEmpty() {
        return empty;
    }
    
    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
    
    @Override
    public String toString() {
        return "PageResponse{" +
                "content=" + content +
                ", pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                ", first=" + first +
                ", last=" + last +
                ", empty=" + empty +
                '}';
    }
}
