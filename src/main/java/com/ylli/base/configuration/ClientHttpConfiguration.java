package com.ylli.base.configuration;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * <a href="https://docs.spring.io/spring-cloud-commons/docs/current/reference/html/#webflux-with-reactive-loadbalancer">webflux-with-reactive-loadbalancer</a>
 * <a href="https://docs.spring.io/spring-cloud-commons/reference/spring-cloud-commons/loadbalancer.html">loadbalancer</a>
 *
 * @author ylli
 * 注解LoadBalanced启动 log warning: <a href="https://github.com/spring-cloud/spring-cloud-commons/issues/1315">issues</a>
 */
@Configuration
public class ClientHttpConfiguration {

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

