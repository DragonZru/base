package com.ylli.api.test;

import com.ylli.api.config.model.ConfigModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/test")
public class TestController {



    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @GetMapping("/redis")
    public Mono<String> redis() {
        applicationEventPublisher.publishEvent(new ConfigModel("ylli", "sbl", "2025"));
        return Mono.empty();
    }
}
