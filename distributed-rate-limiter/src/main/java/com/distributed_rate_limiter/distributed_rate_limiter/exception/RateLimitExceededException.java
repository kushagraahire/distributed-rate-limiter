package com.distributed_rate_limiter.distributed_rate_limiter.exception;

public class RateLimitExceededException extends RuntimeException {
    
    private final long retryAfterMs;
    
    public RateLimitExceededException(String message, long retryAfterMs) {
        super(message);
        this.retryAfterMs = retryAfterMs;
    }
    
    public long getRetryAfterMs() {
        return retryAfterMs;
    }
}