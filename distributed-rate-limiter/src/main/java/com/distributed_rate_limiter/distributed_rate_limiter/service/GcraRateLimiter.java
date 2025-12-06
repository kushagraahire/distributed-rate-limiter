package com.distributed_rate_limiter.distributed_rate_limiter.service;

import com.distributed_rate_limiter.distributed_rate_limiter.configuration.RateLimiterConfig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
//@AllArgsConstructor
public class GcraRateLimiter {
    public Map<String, Long> tatStore = new ConcurrentHashMap<>();
    public boolean allowRequest(String key, long limit, long window, int burstRequests){
        long interval = (window) / limit;
        long burst = burstRequests * interval;
        long tat = tatStore.getOrDefault(key, 0L);
        long now = System.currentTimeMillis();
        long earliestTime = tat - burst;
        if(now >= earliestTime){
            tat = Math.max(tat, now) + interval;
            tatStore.put(key, tat);
            return true;
        }
        return false;
    }
}
