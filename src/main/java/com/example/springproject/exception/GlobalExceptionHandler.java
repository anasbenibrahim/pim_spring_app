package com.example.springproject.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        Map<String, String> errorResponse = new HashMap<>();
        String message = e.getMessage();
        
        // Check if it's a duplicate email error
        if (message != null && message.contains("email") && message.contains("already exists")) {
            errorResponse.put("message", "Email already exists. Please use a different email or try logging in.");
        } else if (message != null && message.contains("uk_6dotkott2kjsp8vw4d0m25fb7")) {
            // This is the unique constraint name for email
            errorResponse.put("message", "Email already exists. Please use a different email or try logging in.");
        } else if (message != null && message.contains("duplicate key")) {
            errorResponse.put("message", "This information already exists in the system. Please use different values.");
        } else {
            errorResponse.put("message", "Data integrity violation: " + (message != null ? message : "Unknown error"));
        }
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", e.getMessage() != null ? e.getMessage() : "An error occurred");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "An unexpected error occurred: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
