package com.ylli.api.config;

import com.ylli.api.common.uid.SnowFlakeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UidConfiguration {

    @Value("${uid.workerId:0}")
    private long workerId;

    @Value("${uid.datacenterId:0}")
    private long datacenterId;

    @Bean
    @ConditionalOnMissingBean(SnowFlakeGenerator.class)
    public SnowFlakeGenerator snowFlakeGenerator() {
        return new SnowFlakeGenerator(workerId, datacenterId);
    }
}
