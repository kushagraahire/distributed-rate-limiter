package com.distributed_rate_limiter.distributed_rate_limiter.DTO;

public class RateLimitErrorResponse {
    
    private String error;
    private String message;
    private long retryAfterSeconds;
    
    public RateLimitErrorResponse(String error, String message, long retryAfterSeconds) {
        this.error = error;
        this.message = message;
        this.retryAfterSeconds = retryAfterSeconds;
    }
    
    public String getError() { return error; }
    public String getMessage() { return message; }
    public long getRetryAfterSeconds() { return retryAfterSeconds; }
}