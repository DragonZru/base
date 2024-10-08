package com.ylli.api.config;

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
 * @LoadBalanced log warning: https://github.com/spring-cloud/spring-cloud-commons/issues/1315
 */
@Configuration
public class WebClientConfiguration {

    @Bean
    public WebClient.Builder webClientBuilder(ObjectProvider<ReactorLoadBalancerExchangeFilterFunction> reactorLoadBalancerExchangeFilterFunctionProvider) {
        DeferringLoadBalancerExchangeFilterFunction<ReactorLoadBalancerExchangeFilterFunction> filterFunction =
                new DeferringLoadBalancerExchangeFilterFunction<>(reactorLoadBalancerExchangeFilterFunctionProvider);

        return WebClient.builder()
//                .clientConnector(new ReactorClientHttpConnector(HttpClient.create(
//                                //https://stackoverflow.com/questions/68640474/facing-issue-webclientrequestexception-pending-acquire-queue-has-reached-its-m
//                                ConnectionProvider.builder("connectionPool")
//                                        .maxConnections(1000)
//                                        .pendingAcquireMaxCount(Integer.MAX_VALUE)
//                                        .build()
//                        )))
                .filter(filterFunction);
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

