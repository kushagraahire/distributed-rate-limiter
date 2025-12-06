package com.distributed_rate_limiter.distributed_rate_limiter.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

public class GcraRateLimiterTest {

    private GcraRateLimiter rateLimiter;

    @BeforeEach
    void setup() {
        rateLimiter = new GcraRateLimiter();
    }

    @Test
    void firstRequest(){
        assertTrue(rateLimiter.allowRequest("user1", 10, 1000, 1));
    }

    @Test
    void rapidRequest(){
        rateLimiter.allowRequest("user2", 1, 5000, 0);
        assertFalse(rateLimiter.allowRequest("user2", 5, 1000, 0));
        assertFalse(rateLimiter.allowRequest("user2", 5, 1000, 0));
        assertFalse(rateLimiter.allowRequest("user2", 5, 1000, 0));
    }

    @Test
    void correctIntervals() throws InterruptedException {
        assertTrue(rateLimiter.allowRequest("user3", 5, 1000, 0));

        Thread.sleep(210);
        assertTrue(rateLimiter.allowRequest("user3", 5, 1000, 0));

        Thread.sleep(210);
        assertTrue(rateLimiter.allowRequest("user3", 5, 1000, 0));

        Thread.sleep(210);
        assertTrue(rateLimiter.allowRequest("user3", 5, 1000, 0));
    }

    @Test
    void testTatUpdatesAfterAcceptedRequest() throws InterruptedException {

        GcraRateLimiter limiter = new GcraRateLimiter();

        String key = "user4";
        long limit = 5;
        long window = 1000;
        int burst = 2;
        long interval = window / limit;

        assertTrue(limiter.allowRequest(key, limit, window, burst));
        long tat1 = limiter.tatStore.get(key);

        Thread.sleep(interval + 10);

        assertTrue(limiter.allowRequest(key, limit, window, burst));
        long tat2 = limiter.tatStore.get(key);

        // Assertions
        assertTrue(tat2 > tat1, "TAT must increase after second request");
        assertEquals(tat1 + interval, tat2, 5, "TAT should increase by interval");
    }

}
