package com.distributed_rate_limiter.distributed_rate_limiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.distributed_rate_limiter.distributed_rate_limiter.configuration.RateLimiterConfig;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableConfigurationProperties(RateLimiterConfig.class)
public class DistributedRateLimiterApplication {

	public static void main(String[] args) {
		SpringApplication.run(DistributedRateLimiterApplication.class, args);
	}

}
