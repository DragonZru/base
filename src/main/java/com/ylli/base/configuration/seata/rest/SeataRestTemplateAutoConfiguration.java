package com.ylli.base.configuration.seata.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(
        proxyBeanMethods = false
)
public class SeataRestTemplateAutoConfiguration {
    public SeataRestTemplateAutoConfiguration() {
    }

    @Bean
    public SeataRestTemplateInterceptor seataRestTemplateInterceptor() {
        return new SeataRestTemplateInterceptor();
    }

//    @Bean
//    public SeataRestTemplateInterceptorAfterPropertiesSet seataRestTemplateInterceptorConfiguration() {
//        return new SeataRestTemplateInterceptorAfterPropertiesSet();
//    }
}
