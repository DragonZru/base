package com.ylli.api.config;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Configuration
@EnableCaching
public class CacheConfiguration {

    AsyncLoadingCache<String, Object> caffeine = Caffeine.newBuilder()
            .buildAsync(new AsyncCacheLoader<String, Object>() {
                @Override
                public CompletableFuture<? extends Object> asyncLoad(String key, Executor executor) throws Exception {
                    return CompletableFuture.supplyAsync(() -> {
                        //load data from db
//                        LeafAlloc leafAlloc = getLeafAllocByTag(key);
//                        if (leafAlloc != null) {
//                            if (isFirstLoad.get()) {
//                                leafAlloc = updateAndGet(key);
//                                isFirstLoad.set(false);
//                            }
//                            return new Segment(leafAlloc);
//                        }
                        return null;
                    }, executor);
                }
            });

//    @Bean
//    public CaffeineCacheManager caffeineCacheManager() {
//        //loadbalancer caffeine manager
//        // spring.cloud.loadbalancer.cache.enabled
//        // spring.cloud.loadbalancer.cache.ttl default is 35s
//        // spring.cloud.loadbalancer.cache.capacity default is 256
//        CaffeineCacheManager caffeineCacheManager = new CaffeineBasedLoadBalancerCacheManager(new LoadBalancerCacheProperties());
//        return caffeineCacheManager;
//    }
}
