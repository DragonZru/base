package com.ylli.base.api.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ylli
 */
@RestController
@RequestMapping("/test")
@RefreshScope
public class TestController {

//    private final WebClient.Builder webClientBuilder;

//    public TestController(WebClient.Builder webClientBuilder) {
//        this.webClientBuilder = webClientBuilder;
//    }

//    @Autowired
//    DefaultMQProducer defaultMQProducer;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Value("${spring.data.redis.password}")
    String password;

    @GetMapping
    public Object redis() {
/*        applicationEventPublisher.publishEvent(new ExampleModel("ylli", "sbl"));
        stringRedisTemplate.opsForValue().set("ylli", value);*/

//        SendResult sendResult = defaultMQProducer.send(new Message("TestTopic", "TagA", ("Hello RocketMQ").getBytes()));
//        return sendResult;
//        return webClientBuilder.build()
//                .get()
//                .uri("http://leaf/uid")
//                .retrieve()
//                .bodyToMono(Long.class);

        return password;
    }
}
