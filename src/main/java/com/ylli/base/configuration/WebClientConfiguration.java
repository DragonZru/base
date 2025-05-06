package com.ylli.base.configuration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.DeferringLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * https://docs.spring.io/spring-cloud-commons/docs/current/reference/html/#webflux-with-reactive-loadbalancer
 * https://docs.spring.io/spring-cloud-commons/reference/spring-cloud-commons/loadbalancer.html
 *
 * @author ylli
 * @LoadBalanced log warning: https://github.com/spring-cloud/spring-cloud-commons/issues/1315
 */
@Configuration
public class WebClientConfiguration {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    // 或者手动添加 lb filter
//    @Bean
//    public WebClient.Builder webClientBuilder(ObjectProvider<ReactorLoadBalancerExchangeFilterFunction> reactorLoadBalancerExchangeFilterFunctionProvider) {
//        DeferringLoadBalancerExchangeFilterFunction<ReactorLoadBalancerExchangeFilterFunction> filterFunction =
//                new DeferringLoadBalancerExchangeFilterFunction<>(reactorLoadBalancerExchangeFilterFunctionProvider);
//        return WebClient.builder()
//                .filter(filterFunction);
//    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

