package com.dev.petmarket_backend.common.config;

import com.dev.petmarket_backend.common.util.EmailBasedRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterConfig {

    @Bean
    public EmailBasedRateLimiter emailBasedRateLimiter() {
        return new EmailBasedRateLimiter();
    }
}
