package com.ylli.base.configuration.seata.web;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author ylli
 */
@AutoConfiguration
@ConditionalOnWebApplication
public class SeataHandlerInterceptorAutoConfiguration implements WebMvcConfigurer {
    public SeataHandlerInterceptorAutoConfiguration() {
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SeataHandlerInterceptor()).addPathPatterns(new String[]{"/**/seata/**"});
    }
}