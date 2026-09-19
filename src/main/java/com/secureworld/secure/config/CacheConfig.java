package com.secureworld.secure.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(
            @Value("${app.cache.ttl:1m}") Duration cacheTtl) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "employees",
                "employeeById",
                "departments",
                "departmentById"
        );
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(cacheTtl)
                .recordStats());
        return cacheManager;
    }
}
