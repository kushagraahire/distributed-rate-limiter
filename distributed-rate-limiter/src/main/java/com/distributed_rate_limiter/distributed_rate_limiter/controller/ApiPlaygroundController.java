package com.distributed_rate_limiter.distributed_rate_limiter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.distributed_rate_limiter.distributed_rate_limiter.DTO.LoginRequest;
import com.distributed_rate_limiter.distributed_rate_limiter.DTO.LoginResponse;
import com.distributed_rate_limiter.distributed_rate_limiter.annotation.RateLimiter;

@RestController
@RequestMapping("/api")
class ApiPlaygroundController {
    @RateLimiter(configKey = "login", userIdExpression = "#loginRequest.username")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        boolean success = Math.random() < 0.3;
        
        if (success) {
            return ResponseEntity.ok(new LoginResponse(true, "Login successful", loginRequest.getUsername()));
        }
        
        return ResponseEntity
            .status(401)
            .body(new LoginResponse(false, "Invalid credentials", null));
    }
}