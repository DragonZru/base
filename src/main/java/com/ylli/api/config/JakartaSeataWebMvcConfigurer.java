package com.ylli.api.config;

import io.seata.integration.http.JakartaTransactionPropagationInterceptor;
import io.seata.integration.http.SeataWebMvcConfigurer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@Configuration
public class JakartaSeataWebMvcConfigurer extends SeataWebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JakartaTransactionPropagationInterceptor());
    }
}
