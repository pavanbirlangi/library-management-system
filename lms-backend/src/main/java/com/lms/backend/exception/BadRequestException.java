package com.lms.backend.exception;

/**
 * Custom exception for 400 Bad Request errors
 * Thrown when a request contains invalid data or validation errors
 */
public class BadRequestException extends RuntimeException {
    
    /**
     * Default constructor
     */
    public BadRequestException() {
        super("Bad request");
    }
    
    /**
     * Constructor with custom message
     * @param message the detail message
     */
    public BadRequestException(String message) {
        super(message);
    }
    
    /**
     * Constructor with message and cause
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
