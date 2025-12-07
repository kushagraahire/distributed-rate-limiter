package com.distributed_rate_limiter.distributed_rate_limiter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.distributed_rate_limiter.distributed_rate_limiter.DTO.RateLimitErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<RateLimitErrorResponse> handleRateLimitExceeded(RateLimitExceededException ex) {
        
        long retryAfterSeconds = (ex.getRetryAfterMs() + 999) / 1000;
        
        RateLimitErrorResponse response = new RateLimitErrorResponse(
            "Too Many Requests",
            ex.getMessage(),
            retryAfterSeconds
        );
        
        return ResponseEntity
            .status(HttpStatus.TOO_MANY_REQUESTS)  // 429
            .header("Retry-After", String.valueOf(retryAfterSeconds))
            .body(response);
    }
}