package com.ylli.api.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.DeferringLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * https://docs.spring.io/spring-cloud-commons/docs/current/reference/html/#webflux-with-reactive-loadbalancer
 */
@Configuration
public class WebClientConfiguration {

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder(ObjectProvider<ReactorLoadBalancerExchangeFilterFunction> reactorLoadBalancerExchangeFilterFunctionProvider) {
        DeferringLoadBalancerExchangeFilterFunction<ReactorLoadBalancerExchangeFilterFunction> filterFunction =
                new DeferringLoadBalancerExchangeFilterFunction<>(reactorLoadBalancerExchangeFilterFunctionProvider);

        return WebClient.builder()
//                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().resolver(spec -> {
//                    //查询超时时间，默认5s.
//                    spec.queryTimeout(Duration.ofMillis(2000));
//                })))
                .filter(filterFunction);


    }

//    public Mono<String> doOtherStuff() {
//        return WebClient.builder().baseUrl("http://stores")
//                .filter(lbFunction)
//                .build()
//                .get()
//                .uri("/stores")
//                .retrieve()
//                .bodyToMono(String.class);
//    }
}

