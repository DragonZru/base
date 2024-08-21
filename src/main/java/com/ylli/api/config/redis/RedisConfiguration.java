package com.ylli.api.config.redis;

import io.lettuce.core.ReadFrom;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfiguration {

    @Bean
    @ConditionalOnBean(LettuceConnectionFactory.class)
    public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        return new StringRedisTemplate(lettuceConnectionFactory);
    }

    // TODO fix ReactiveStringRedisTemplate not working
//    @Bean
//    @ConditionalOnBean(LettuceConnectionFactory.class)
//    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
//        return new ReactiveStringRedisTemplate(lettuceConnectionFactory);
//    }

    @Bean
    @ConditionalOnBean(RedisClusterConfiguration.class)
    public LettuceConnectionFactory lettuceConnectionFactory(RedisClusterConfiguration redisClusterConfiguration) {
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
