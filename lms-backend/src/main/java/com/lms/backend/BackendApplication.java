package com.lms.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Library Management System - Spring Boot Application
 * 
 * This is the main entry point for the LMS Backend API.
 * 
 * Features included:
 * - RESTful APIs for library operations
 * - JWT-based authentication and authorization
 * - Role-based access control (ADMIN, LIBRARIAN, MEMBER)
 * - Book management and catalog
 * - Lending and reservation system
 * - Fine management
 * - Reporting and analytics
 * - Swagger/OpenAPI documentation
 * 
 * Access points:
 * - API Base URL: http://localhost:8080/api
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - API Docs: http://localhost:8080/v3/api-docs
 * 
 * @author Library Management System Team
 * @version 1.0.0
 * @since 2025-08-29
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
@EnableAsync
public class BackendApplication {

	/**
	 * Main method to start the Spring Boot application
	 * 
	 * @param args Command line arguments
	 */
	public static void main(String[] args) {
		// Print startup banner
		System.out.println("🚀 Starting Library Management System Backend...");
		System.out.println("📚 LMS API will be available at: http://localhost:8080/api");
		System.out.println("📖 Swagger UI will be available at: http://localhost:8080/swagger-ui");
		
		// Start the Spring Boot application
		SpringApplication.run(BackendApplication.class, args);
		
		// Print success message
		System.out.println("✅ Library Management System Backend started successfully!");
	}
}
