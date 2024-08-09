package com.ylli.api.config;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.cloud.loadbalancer.cache.CaffeineBasedLoadBalancerCacheManager;
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Configuration
@EnableCaching
public class CacheConfiguration {

//    public void asyncLoadingCache() {
//        AsyncLoadingCache<String, String> asyncLoadingCache = Caffeine.newBuilder()
//                // default is ForkJoinPool.commonPool()
//                //.executor(Executors.newFixedThreadPool(2))
//                .buildAsync(new AsyncCacheLoader<String, String>() {
//                    @Override
//                    public CompletableFuture<? extends String> asyncLoad(String key, Executor executor) throws Exception {
//                        return CompletableFuture.supplyAsync(() -> {
//                            // DO SOMETHING eg. load data from db
//                            // autoload
//                            if (key.equals("ylli")) {
//                                return key.toUpperCase();
//                            }
//                            return null;
//                        }, executor);
//                    }
//                });
//    }

    @Bean
    public CaffeineCacheManager caffeineCacheManager() {
        //loadbalancer caffeine manager
        // spring.cloud.loadbalancer.cache.enabled
        // spring.cloud.loadbalancer.cache.ttl default is 35s
        // spring.cloud.loadbalancer.cache.capacity default is 256
        CaffeineCacheManager caffeineCacheManager = new CaffeineBasedLoadBalancerCacheManager(new LoadBalancerCacheProperties());
        return caffeineCacheManager;
    }

    @Bean
    public SimpleCacheManager simpleCacheManager() {
        // 不支持asyncLoadingCache
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();

//        List<CaffeineCache> caches = new ArrayList<>();
//        caches.add(new CaffeineCache("default", Caffeine.newBuilder().build()));
//        simpleCacheManager.setCaches(caches);

        return simpleCacheManager;
    }
}
