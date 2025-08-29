package com.lms.backend.exception;

/**
 * Custom exception for 404 Not Found errors
 * Thrown when a requested resource is not found
 */
public class NotFoundException extends RuntimeException {
    
    /**
     * Default constructor
     */
    public NotFoundException() {
        super("Resource not found");
    }
    
    /**
     * Constructor with custom message
     * @param message the detail message
     */
    public NotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructor with message and cause
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructor with resource type and identifier
     * @param resourceType the type of resource (e.g., "User", "Book", "Member")
     * @param identifier the identifier that was not found
     */
    public NotFoundException(String resourceType, Object identifier) {
        super(String.format("%s not found with identifier: %s", resourceType, identifier));
    }
}
