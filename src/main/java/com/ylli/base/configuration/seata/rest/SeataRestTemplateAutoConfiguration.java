package com.ylli.base.configuration.seata.rest;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author ylli
 */
@AutoConfiguration
public class SeataRestTemplateAutoConfiguration {
    public SeataRestTemplateAutoConfiguration() {
    }

    @Bean
    public SeataRestTemplateInterceptor seataRestTemplateInterceptor() {
        return new SeataRestTemplateInterceptor();
    }
}
