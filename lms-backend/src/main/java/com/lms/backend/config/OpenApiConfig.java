package com.lms.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

/**
 * OpenAPI/Swagger configuration for Library Management System
 * 
 * Configures Swagger UI with JWT authentication support
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Configure OpenAPI with JWT Security
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addServersItem(new Server().url("http://localhost:" + serverPort).description("Local Development Server"))
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .components(new Components()
                        .addSecuritySchemes("JWT", createJwtAuthScheme()));
    }

    /**
     * Configure JWT Security in Swagger
     * - Security scheme named "JWT" 
     * - Type HTTP with bearerFormat JWT and scheme bearer
     */
    private SecurityScheme createJwtAuthScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer")
                .description("Enter JWT token obtained from /api/auth/login endpoint. " +
                           "Click 'Authorize' button and enter: Bearer <your-jwt-token>");
    }

    /**
     * Set API Information
     */
    private Info apiInfo() {
        return new Info()
                .title("Library Management System API")
                .version("1.0")
                .description("REST API for Library Management System with JWT Authentication. " +
                           "Features include book management, lending operations, reservations, " +
                           "fine management, and reporting with role-based access control.");
    }


}
