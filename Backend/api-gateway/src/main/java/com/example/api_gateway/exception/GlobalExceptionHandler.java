package com.example.api_gateway.exception;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(-1) // High priority to catch exceptions before default handlers
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        logger.error("Error during gateway request processing: {}", ex.getMessage(), ex);

        HttpStatus status;
        String message;

        // Map different exceptions to appropriate HTTP status codes
        if (ex.getMessage().contains("Missing or invalid Authorization header")) {
            status = HttpStatus.UNAUTHORIZED;
            message = "Authorization header is missing or invalid";
        } else if (ex.getMessage().contains("Unauthorized access: Invalid token")) {
            status = HttpStatus.UNAUTHORIZED;
            message = "Invalid or expired authentication token";
        } else if (ex.getMessage().contains("Unauthorized access: Insufficient permissions")) {
            status = HttpStatus.FORBIDDEN;
            message = "You don't have permission to access this resource";
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "An unexpected error occurred";
        }

        // Create error response object
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("path", exchange.getRequest().getURI().getPath());

        // Convert error response to JSON
        byte[] responseBytes;
        try {
            responseBytes = objectMapper.writeValueAsString(errorResponse).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            logger.error("Error creating error response JSON", e);
            responseBytes = "Internal server error".getBytes(StandardCharsets.UTF_8);
        }

        // Set response properties
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        // Write response
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(responseBytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}