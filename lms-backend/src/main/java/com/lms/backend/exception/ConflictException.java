package com.lms.backend.exception;

/**
 * Custom exception for 409 Conflict errors
 * Thrown when a request conflicts with the current state of the resource
 * (e.g., duplicate username, attempting to borrow when limit reached)
 */
public class ConflictException extends RuntimeException {
    
    /**
     * Default constructor
     */
    public ConflictException() {
        super("Conflict occurred");
    }
    
    /**
     * Constructor with custom message
     * @param message the detail message
     */
    public ConflictException(String message) {
        super(message);
    }
    
    /**
     * Constructor with message and cause
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
