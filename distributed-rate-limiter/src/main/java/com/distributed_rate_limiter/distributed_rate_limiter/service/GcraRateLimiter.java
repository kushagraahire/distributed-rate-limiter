package com.distributed_rate_limiter.distributed_rate_limiter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class GcraRateLimiter {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisScript<Long> rateLimiterScript;

    @Autowired
    public GcraRateLimiter(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("script/gcra_tat_update.lua"));
        script.setResultType(Long.class);
        this.rateLimiterScript = script;
    }

    public long allowRequest(String key, long limit, long window, int burstRequests){
        long interval = (window) / limit;
        long burst = burstRequests * interval;

        List<String> keys = Collections.singletonList(key);

        Object[] args = new Object[]{
                String.valueOf(System.currentTimeMillis()),
                String.valueOf(interval),
                String.valueOf(burst)
        };

        Long result = redisTemplate.execute(rateLimiterScript, keys, args);
        return result;
    }
}
