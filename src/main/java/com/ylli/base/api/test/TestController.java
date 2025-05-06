package com.ylli.base.api.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author ylli
 */
@RestController
@RequestMapping("/test")
@RefreshScope
public class TestController {

    @Value("${ylli.value}")
    private String value;

    private final WebClient.Builder webClientBuilder;

    public TestController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @GetMapping
    public Mono<Long> redis() {
/*        applicationEventPublisher.publishEvent(new ExampleModel("ylli", "sbl"));
        stringRedisTemplate.opsForValue().set("ylli", value);*/
        return webClientBuilder.build()
                .get()
                .uri("http://leaf/uid")
                .retrieve()
                .bodyToMono(Long.class);
    }
}
