package com.ylli.base.api.test;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final WebClient.Builder webClientBuilder;

    public TestController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Autowired
    DefaultMQProducer defaultMQProducer;

    @GetMapping
    public Object redis() throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
/*        applicationEventPublisher.publishEvent(new ExampleModel("ylli", "sbl"));
        stringRedisTemplate.opsForValue().set("ylli", value);*/

        SendResult sendResult = defaultMQProducer.send(new Message("TestTopic", "TagA", ("Hello RocketMQ").getBytes()));
        return sendResult;
//        return webClientBuilder.build()
//                .get()
//                .uri("http://leaf/uid")
//                .retrieve()
//                .bodyToMono(Long.class);
    }
}
