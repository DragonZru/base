package com.ylli.api.example;

import com.ylli.api.example.model.ExampleModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExampleTest {

    @Autowired
    WebClient.Builder webClientBuilder;

    @Test
    void test() {
        ExampleModel exampleModel = new ExampleModel("username", "password");

        Mono<String> mono = webClientBuilder.build()
                .post()
                .uri(URI.create("http://127.0.0.1:8080/example"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(exampleModel)
                .retrieve()
                .bodyToMono(String.class);

        Assert.isTrue(mono.block().equals("1"), "failed");
    }
}
