package com.ylli.base.configuration.seata.feign;

import feign.Client;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(
        proxyBeanMethods = false
)
@ConditionalOnClass({Client.class})
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
