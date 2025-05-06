package com.ylli.base.api.test;

import com.google.gson.Gson;
import com.ylli.base.api.example.model.ExampleModel;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@EnableAsync
public class EventListener {

    @Async
//    @org.springframework.context.event.EventListener(condition = "#event.name.equals('ylli')")
    @org.springframework.context.event.EventListener
    @Order(1)
    public void onEvent(ExampleModel event) {
//        throw new RuntimeException("test");
        System.out.println(Thread.currentThread().getName() + ", event1:" + new Gson().toJson(event));
    }

//    @Async
//    @org.springframework.context.event.EventListener
//    @Order(2)
//    public void onEvent1(ConfigModel event) {
//        System.out.println(Thread.currentThread().getName() + ", event2:" + new Gson().toJson(event));
//    }
}
