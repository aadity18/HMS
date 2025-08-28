package com.hms.auth.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
	    String message = ex.getRootCause() != null && ex.getRootCause().getMessage().contains("Duplicate entry")
	        ? "Email or username already exists."
	        : "Data integrity violation occurred.";

	    Map<String, Object> errorBody = new HashMap<>();
	    errorBody.put("timestamp", LocalDateTime.now());
	    errorBody.put("status", HttpStatus.BAD_REQUEST.value());
	    errorBody.put("error", "Bad Request");
	    errorBody.put("message", message);
	    errorBody.put("path", request.getDescription(false));

	    return new ResponseEntity<>(errorBody, HttpStatus.BAD_REQUEST);
	}

	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
	    Map<String, Object> errorBody = new HashMap<>();
	    errorBody.put("timestamp", LocalDateTime.now());
	    errorBody.put("status", HttpStatus.FORBIDDEN.value());
	    errorBody.put("error", "Forbidden");
	    errorBody.put("message", "You don't have permission to access this resource.");
	    errorBody.put("path", request.getDescription(false));

	    return new ResponseEntity<>(errorBody, HttpStatus.FORBIDDEN);
	}

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorBody.put("error", "Internal Server Error");
        errorBody.put("message", ex.getMessage());
        errorBody.put("path", request.getDescription(false));

        return new ResponseEntity<>(errorBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
