package com.distributed_rate_limiter.distributed_rate_limiter.configuration;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "rate-limiter")
@Configuration
@Getter
public class RateLimiterConfig {
    private final Map<String, EndpointConfig> endpoints;

    @ConstructorBinding
    public RateLimiterConfig(Map<String, EndpointConfig> endpoints) {
        this.endpoints = endpoints != null ? endpoints : new HashMap<>();
    }

    public record EndpointConfig(int limit, int window, int burst) {
        @ConstructorBinding
        public EndpointConfig {
        }
        }
}
