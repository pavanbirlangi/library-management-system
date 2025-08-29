package com.lms.backend.exception;

/**
 * Custom exception for 403 Forbidden errors
 * Thrown when a user attempts to access a resource they don't have permission for
 */
public class AccessDeniedAppException extends RuntimeException {
    
    /**
     * Default constructor
     */
    public AccessDeniedAppException() {
        super("Access denied");
    }
    
    /**
     * Constructor with custom message
     * @param message the detail message
     */
    public AccessDeniedAppException(String message) {
        super(message);
    }
    
    /**
     * Constructor with message and cause
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public AccessDeniedAppException(String message, Throwable cause) {
        super(message, cause);
    }
}
