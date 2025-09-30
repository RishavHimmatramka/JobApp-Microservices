package com.reviewms.controller;

import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.reviewms.exception.ReviewNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<String> handleReviewNotFound(ReviewNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(feign.FeignException.class)
    public ResponseEntity<String> handleFeignStatus(feign.FeignException ex) {
        String message = ex.responseBody() // 1. Try to get raw response body (Optional<ByteBuffer>)
            .map(bb -> StandardCharsets.UTF_8.decode(bb).toString()) // 2. Decode bytes → String
            .filter(body -> !body.isBlank()) // 3. Make sure it's not empty
            .orElse(ex.getMessage());        // 4. Fallback to full Feign message if no body present
        return ResponseEntity.status(ex.status()).body(message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
    }
}
