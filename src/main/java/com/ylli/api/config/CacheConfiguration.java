package com.ylli.api.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    public CaffeineCacheManager caffeineCacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        caffeineCacheManager.setCaffeine(com.github.benmanes.caffeine.cache.Caffeine.newBuilder()
                //.expireAfterWrite(24, TimeUnit.HOURS)
        );
        return caffeineCacheManager;
    }
}
