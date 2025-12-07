package com.distributed_rate_limiter.distributed_rate_limiter.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GcraRateLimiterTest {

    @Autowired
    private GcraRateLimiter rateLimiter;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private static final String TEST_KEY = "gcra:test:user:integration";

    @BeforeEach
    @AfterEach
    void cleanUp() {
        redisTemplate.delete(TEST_KEY);
    }

    @Test
    void shouldAllowFirstRequest() {
        long result = rateLimiter.allowRequest(TEST_KEY, 5, 60000, 0);
        assertEquals(1, result);
    }

    @Test
    void shouldRejectImmediateSecondRequest() {
        assertEquals(1, rateLimiter.allowRequest(TEST_KEY, 5, 60000, 0));
        long result = rateLimiter.allowRequest(TEST_KEY, 5, 60000, 0);
        assertTrue(result > 1, "Should return wait time in ms when rejected");
    }

    @Test
    void shouldAllowRequestAfterInterval() throws InterruptedException {
        assertEquals(1, rateLimiter.allowRequest(TEST_KEY, 10, 1000, 0));
        Thread.sleep(110);
        assertEquals(1, rateLimiter.allowRequest(TEST_KEY, 10, 1000, 0));
    }

    @Test
    void shouldAllowBurstRequests() {
        assertEquals(1, rateLimiter.allowRequest(TEST_KEY, 5, 5000, 2));
        assertEquals(1, rateLimiter.allowRequest(TEST_KEY, 5, 5000, 2));
        assertEquals(1, rateLimiter.allowRequest(TEST_KEY, 5, 5000, 2));
        long result = rateLimiter.allowRequest(TEST_KEY, 5, 5000, 2);
        assertTrue(result > 1, "Should reject after burst allowance exhausted");
    }

    @Test
    void shouldReturnCorrectWaitTime() {
        assertEquals(1, rateLimiter.allowRequest(TEST_KEY, 1, 10000, 0));
        long waitTime = rateLimiter.allowRequest(TEST_KEY, 1, 10000, 0);
        assertTrue(waitTime > 9000 && waitTime <= 10000, 
            "Wait time should be approximately 10 seconds, got: " + waitTime);
    }
}
