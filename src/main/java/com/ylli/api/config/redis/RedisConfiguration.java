package com.ylli.api.config.redis;

import io.lettuce.core.ReadFrom;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfiguration {

    @Bean
    @ConditionalOnBean(RedisClusterConfiguration.class)
    public StringRedisTemplate stringRedisTemplate(RedisClusterConfiguration redisClusterConfiguration) {
        // 优先主读，fail back to replica
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .readFrom(ReadFrom.MASTER_PREFERRED)
                .build();

        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisClusterConfiguration, clientConfig);
        lettuceConnectionFactory.start();
        return new StringRedisTemplate(lettuceConnectionFactory);
    }

    @Bean
    @ConditionalOnBean(ReactiveRedisConnectionFactory.class)
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        return new ReactiveStringRedisTemplate(reactiveRedisConnectionFactory);
    }

    @Bean
    @ConditionalOnBean(RedisClusterConfiguration.class)
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(RedisClusterConfiguration redisClusterConfiguration) {
        // 优先主读，fail back to replica
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .readFrom(ReadFrom.MASTER_PREFERRED)
                .build();

        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisClusterConfiguration, clientConfig);
        lettuceConnectionFactory.start();
        return lettuceConnectionFactory;
    }

    @Bean
    public RedisClusterConfiguration redisClusterConfiguration(ObjectProvider<RedisProperties> redisPropertiesProvider) {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(redisPropertiesProvider.getIfAvailable().getCluster().getNodes());
        //redisClusterConfiguration.setUsername("root");
        //redisClusterConfiguration.setPassword("123456");
        return redisClusterConfiguration;
    }
}
