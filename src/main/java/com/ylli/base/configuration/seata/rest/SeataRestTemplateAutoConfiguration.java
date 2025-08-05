package com.ylli.base.configuration.seata.rest;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

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

    @Bean
    @LoadBalanced
    RestTemplate seataRestTemplate(SeataRestTemplateInterceptor seataRestTemplateInterceptor) {
        RestTemplate restTemplate = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(restTemplate.getInterceptors());
        interceptors.add(seataRestTemplateInterceptor);
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }
}
