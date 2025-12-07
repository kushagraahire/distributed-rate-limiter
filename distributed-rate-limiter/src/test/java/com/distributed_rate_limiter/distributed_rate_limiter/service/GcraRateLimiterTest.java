package com.distributed_rate_limiter.distributed_rate_limiter.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    void firstRequest(){
        assertTrue(rateLimiter.allowRequest(TEST_KEY, 10, 1000, 1));
    }

    @Test
    void rapidRequest() throws InterruptedException {
        assertTrue(rateLimiter.allowRequest(TEST_KEY, 5, 1000, 0));

        // 2. Immediate request must fail because 200ms has not passed.
        Thread.sleep(1000);
        assertFalse(rateLimiter.allowRequest(TEST_KEY, 5, 1000, 0));
        assertFalse(rateLimiter.allowRequest(TEST_KEY, 5, 1000, 0));
        assertFalse(rateLimiter.allowRequest(TEST_KEY, 5, 1000, 0));
    }

    @Test
    void correctIntervals() throws InterruptedException {
        assertTrue(rateLimiter.allowRequest(TEST_KEY, 5, 1000, 0));

        Thread.sleep(210);
        assertTrue(rateLimiter.allowRequest(TEST_KEY, 5, 1000, 0));

        Thread.sleep(210);
        assertTrue(rateLimiter.allowRequest(TEST_KEY, 5, 1000, 0));

        Thread.sleep(210);
        assertTrue(rateLimiter.allowRequest(TEST_KEY, 5, 1000, 0));
    }

}
