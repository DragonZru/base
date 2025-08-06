package com.ylli.base.configuration.seata.feign;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(
        proxyBeanMethods = false
)
public class SeataFeignClientAutoConfiguration {
    public SeataFeignClientAutoConfiguration() {
    }

    @Bean
    public static SeataFeignBuilderBeanPostProcessor seataFeignBuilderBeanPostProcessor() {
        return new SeataFeignBuilderBeanPostProcessor();
    }

    @Bean
    public SeataFeignRequestInterceptor seataFeignRequestInterceptor() {
        return new SeataFeignRequestInterceptor();
    }
}
