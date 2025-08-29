package com.lms.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller for Library Management System
 * 
 * Provides endpoints to check the health and status of the application
 */
@RestController
@Tag(name = "Health Check", description = "Application health and status endpoints")
public class HealthController {

    @Autowired(required = false)
    private BuildProperties buildProperties;
    
    @Autowired
    private DataSource dataSource;

    /**
     * Basic health check endpoint
     * GET / - Returns service status
     */
    @GetMapping("/")
    @Operation(summary = "Basic Health Check", description = "Returns basic service status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service is running successfully")
    })
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "Service is running");
        response.put("service", "Library Management System Backend");
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Detailed API health check endpoint
     * GET /api/health - Returns detailed service status with database connectivity
     */
    @GetMapping("/api/health")
    @Operation(summary = "Detailed Health Check", description = "Returns detailed service status including database connectivity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service and dependencies are healthy"),
        @ApiResponse(responseCode = "503", description = "Service or dependencies are unhealthy")
    })
    public ResponseEntity<Map<String, Object>> detailedHealthCheck() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Basic service information
            response.put("status", "UP");
            response.put("service", "lms-backend");
            response.put("timestamp", LocalDateTime.now().toString());
            
            // Version information
            if (buildProperties != null) {
                response.put("version", buildProperties.getVersion());
                response.put("buildTime", buildProperties.getTime().toString());
            } else {
                response.put("version", "1.0.0-SNAPSHOT");
            }
            
            // Database connectivity check
            Map<String, String> database = new HashMap<>();
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(5)) { // 5 second timeout
                    database.put("status", "UP");
                    database.put("database", connection.getMetaData().getDatabaseProductName());
                    database.put("version", connection.getMetaData().getDatabaseProductVersion());
                } else {
                    database.put("status", "DOWN");
                    database.put("error", "Database connection invalid");
                }
            } catch (Exception e) {
                database.put("status", "DOWN");
                database.put("error", e.getMessage());
            }
            response.put("database", database);
            
            // System information
            Map<String, Object> system = new HashMap<>();
            Runtime runtime = Runtime.getRuntime();
            system.put("javaVersion", System.getProperty("java.version"));
            system.put("totalMemory", runtime.totalMemory() / (1024 * 1024) + " MB");
            system.put("freeMemory", runtime.freeMemory() / (1024 * 1024) + " MB");
            system.put("maxMemory", runtime.maxMemory() / (1024 * 1024) + " MB");
            system.put("availableProcessors", runtime.availableProcessors());
            response.put("system", system);
            
            // Check if any component is down
            boolean isHealthy = database.get("status").equals("UP");
            
            if (isHealthy) {
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "DOWN");
                return ResponseEntity.status(503).body(response);
            }
            
        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("error", e.getMessage());
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(503).body(response);
        }
    }
    
    /**
     * Simple ping endpoint
     * GET /api/ping - Returns pong response
     */
    @GetMapping("/api/ping")
    @Operation(summary = "Ping Check", description = "Simple ping-pong health check")
    @ApiResponse(responseCode = "200", description = "Pong response")
    public ResponseEntity<Map<String, String>> ping() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "pong");
        response.put("timestamp", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(response);
    }
}
