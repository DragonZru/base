package com.ylli.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * https://docs.spring.io/spring-cloud-commons/docs/current/reference/html/#webflux-with-reactive-loadbalancer
 */
@Configuration
public class WebClientConfiguration {

    @Autowired
    private ReactorLoadBalancerExchangeFilterFunction reactorLoadBalancerExchangeFilterFunction;

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().resolver(spec -> {
                    //查询超时时间，默认5s.
                    spec.queryTimeout(Duration.ofMillis(2000));
                })))
                .filter(reactorLoadBalancerExchangeFilterFunction);
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
