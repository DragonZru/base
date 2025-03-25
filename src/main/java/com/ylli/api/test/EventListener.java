package com.ylli.api.test;

import com.google.gson.Gson;
import com.ylli.api.config.model.ConfigModel;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@EnableAsync
public class EventListener {

    @Async
    @org.springframework.context.event.EventListener
    public void onEvent(ConfigModel event) {
        System.out.println(Thread.currentThread().getName() + ", event:" + new Gson().toJson(event));
    }
}
