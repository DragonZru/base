package com.ylli.api.test;

import com.ylli.api.example.model.ExampleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/test")
@RefreshScope
public class TestController {

    @Value("${ylli.value}")
    private String value;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @GetMapping("/redis")
    public Mono<String> redis() {
        applicationEventPublisher.publishEvent(new ExampleModel("ylli", "sbl"));
        stringRedisTemplate.opsForValue().set("ylli", value);
        return Mono.just(value);
    }
}
