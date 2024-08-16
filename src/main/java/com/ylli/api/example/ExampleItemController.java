package com.ylli.api.example;

import com.ylli.api.example.service.ExampleItemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.IntStream;

/**
 * Created by ylli on 2024/7/7.
 * 分库分表eg.
 */
@RestController
@RequestMapping("/example/item")
public class ExampleItemController {

    ExampleItemService exampleItemService;

    public ExampleItemController(ExampleItemService exampleItemService) {
        this.exampleItemService = exampleItemService;
    }

    @PostMapping()
    public void create() {
        exampleItemService.create();
    }

    @GetMapping()
    public Object get() {
        return exampleItemService.get();
    }

    @GetMapping("/test")
    public Object test() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 16, 100,
                java.util.concurrent.TimeUnit.SECONDS, new java.util.concurrent.LinkedBlockingQueue<>());

        IntStream.range(0,10000).parallel().forEach(i -> {
            executor.execute(() -> {
                exampleItemService.create();
            });
        });

        return "ok";

    }

}
