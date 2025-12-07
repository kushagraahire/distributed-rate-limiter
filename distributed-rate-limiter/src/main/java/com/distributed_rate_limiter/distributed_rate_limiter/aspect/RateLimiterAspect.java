package com.distributed_rate_limiter.distributed_rate_limiter.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.distributed_rate_limiter.distributed_rate_limiter.annotation.RateLimiter;
import com.distributed_rate_limiter.distributed_rate_limiter.configuration.RateLimiterConfig;
import com.distributed_rate_limiter.distributed_rate_limiter.exception.RateLimitExceededException;
import com.distributed_rate_limiter.distributed_rate_limiter.service.GcraRateLimiter;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class RateLimiterAspect {

    private final GcraRateLimiter gcraRateLimiter;
    private final RateLimiterConfig rateLimiterConfig;
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public RateLimiterAspect(GcraRateLimiter gcraRateLimiter, RateLimiterConfig rateLimiterConfig) {
        this.gcraRateLimiter = gcraRateLimiter;
        this.rateLimiterConfig = rateLimiterConfig;
    }

    @Around("@annotation(com.distributed_rate_limiter.distributed_rate_limiter.annotation.RateLimiter)")
    public Object handleAnnotation(ProceedingJoinPoint joinPoint) throws Throwable{
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        RateLimiter rateLimiter = method.getAnnotation(RateLimiter.class);

        String configKey = rateLimiter.configKey();
        RateLimiterConfig.EndpointConfig config = rateLimiterConfig.getEndpoints().get(configKey);
        
        if (config == null) {
            log.warn("No config found for key: {}, skipping rate limiting", configKey);
            return joinPoint.proceed();
        }

        String userId = resolveUserId(rateLimiter.userIdExpression(), method, joinPoint.getArgs());
        boolean enabled = rateLimiter.enabled();

        if (enabled) {
            log.info("Config Key: {}, User ID: {}, Limit: {}, Window: {}, Burst: {}", 
                    configKey, userId, config.limit(), config.window(), config.burst());

            String redisKey = configKey + ":" + userId + ":" + method.getName();
            long response = gcraRateLimiter.allowRequest(
                    redisKey,
                    config.limit(),
                    config.window(),
                    config.burst()
            );

            if(response != 1){
                long retryAfterMs = response;
                log.warn("Rate limit exceeded for key: {}", redisKey);
                throw new RateLimitExceededException("Rate limit exceeded", retryAfterMs);
            }
        }
        return joinPoint.proceed();
    }

    private String resolveUserId(String expression, Method method, Object[] args) {
        if (expression == null || expression.isEmpty()) {
            return "anonymous";
        }

        try {
            String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
            StandardEvaluationContext context = new StandardEvaluationContext();
            
            if (parameterNames != null) {
                for (int i = 0; i < parameterNames.length; i++) {
                    context.setVariable(parameterNames[i], args[i]);
                }
            }
            
            Object result = expressionParser.parseExpression(expression).getValue(context);
            return result != null ? result.toString() : "anonymous";
        } catch (Exception e) {
            log.warn("Failed to resolve userId expression '{}': {}", expression, e.getMessage());
            return "anonymous";
        }
    }
}
