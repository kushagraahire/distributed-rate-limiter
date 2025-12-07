package com.distributed_rate_limiter.aspect;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.distributed_rate_limiter.distributed_rate_limiter.annotation.RateLimiter;
import com.distributed_rate_limiter.distributed_rate_limiter.aspect.RateLimiterAspect;
import com.distributed_rate_limiter.distributed_rate_limiter.configuration.RateLimiterConfig;
import com.distributed_rate_limiter.distributed_rate_limiter.exception.RateLimitExceededException;
import com.distributed_rate_limiter.distributed_rate_limiter.service.GcraRateLimiter;

@ExtendWith(MockitoExtension.class)
class RateLimiterAspectTest {

    @Mock
    private GcraRateLimiter gcraRateLimiter;
    @Mock
    private ProceedingJoinPoint joinPoint;
    @Mock
    private MethodSignature methodSignature;

    private RateLimiterAspect rateLimiterAspect;

    @BeforeEach
    void setUp() {
        RateLimiterConfig config = new RateLimiterConfig(Map.of(
            "test", new RateLimiterConfig.EndpointConfig(5, 60000, 0)
        ));
        rateLimiterAspect = new RateLimiterAspect(gcraRateLimiter, config);
    }

    @Test
    void shouldAllowRequest() throws Throwable {
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(getClass().getMethod("annotatedMethod"));
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        when(joinPoint.proceed()).thenReturn("ok");
        when(gcraRateLimiter.allowRequest(anyString(), anyLong(), anyLong(), anyInt())).thenReturn(1L);

        Object result = rateLimiterAspect.handleAnnotation(joinPoint);

        assertEquals("ok", result);
    }

    @Test
    void shouldRejectRequest() throws Throwable {
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(getClass().getMethod("annotatedMethod"));
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        when(gcraRateLimiter.allowRequest(anyString(), anyLong(), anyLong(), anyInt())).thenReturn(5000L);

        assertThrows(RateLimitExceededException.class, () -> rateLimiterAspect.handleAnnotation(joinPoint));
    }

    @RateLimiter(configKey = "test")
    public void annotatedMethod() {}
}
