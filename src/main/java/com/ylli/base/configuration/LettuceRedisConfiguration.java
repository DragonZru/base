package com.ylli.base.configuration;

import io.lettuce.core.ReadFrom;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
public class LettuceRedisConfiguration {

    @Bean
    @ConditionalOnBean(LettuceConnectionFactory.class)
    public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        return new StringRedisTemplate(lettuceConnectionFactory);
    }

    @Bean
    @ConditionalOnBean(LettuceConnectionFactory.class)
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        return new ReactiveStringRedisTemplate(lettuceConnectionFactory);
    }

    @Bean
    @ConditionalOnBean(RedisConfiguration.class)
    public LettuceConnectionFactory lettuceConnectionFactory(RedisConfiguration redisConfiguration) {
        // 优先主读，fail back to replica
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder().readFrom(ReadFrom.MASTER_PREFERRED).build();

        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfiguration, clientConfig);
        lettuceConnectionFactory.start();
        return lettuceConnectionFactory;
    }

    @Bean
    public RedisConfiguration redisConfiguration(ObjectProvider<RedisProperties> redisPropertiesProvider) {
        // getIfAvailable return instance if exists, otherwise return null
        // RedisProperties host(default 127.0.0.1) and port(default 6379) has value, so it must obtain an instance.
        RedisProperties redisProperties = redisPropertiesProvider.getIfAvailable();

        if (redisProperties.getCluster() != null) {
            RedisProperties.Cluster cluster = redisProperties.getCluster();
            RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(cluster.getNodes());
            // 密码和用户名
            Optional.ofNullable(redisProperties.getUsername()).ifPresent(clusterConfiguration::setUsername);
            Optional.ofNullable(redisProperties.getPassword()).ifPresent(clusterConfiguration::setPassword);
            return clusterConfiguration;
        }

        if (redisProperties.getSentinel() != null) {
            // TODO sentinel check.
            RedisProperties.Sentinel sentinel = redisProperties.getSentinel();
            RedisSentinelConfiguration sentinelConfiguration = new RedisSentinelConfiguration().master(sentinel.getMaster());
            sentinelConfiguration.setSentinels(sentinel.getNodes().stream().map(nodeStr -> {
                return RedisNode.fromString(nodeStr);
            }).collect(Collectors.toSet()));

            Optional.ofNullable(redisProperties.getUsername()).ifPresent(sentinelConfiguration::setUsername);
            Optional.ofNullable(redisProperties.getPassword()).ifPresent(sentinelConfiguration::setPassword);
            return sentinelConfiguration;
        }

        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());
        Optional.ofNullable(redisProperties.getUsername()).ifPresent(standaloneConfiguration::setUsername);
        Optional.ofNullable(redisProperties.getPassword()).ifPresent(standaloneConfiguration::setPassword);
        return standaloneConfiguration;
    }
}
