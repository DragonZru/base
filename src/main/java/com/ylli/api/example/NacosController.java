package com.ylli.api.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description
 **/
@RestController
@RequestMapping("nacos")
public class NacosController {
    @GetMapping
    public Object test() {

//        Class<?> dynamicType = new ByteBuddy()
//                .subclass(ServiceProvider.class)
//                .method(ElementMatchers.named("getQwqyPlatformHost"))
//                .intercept(FixedValue.value("http://newpath:8080/test"))
//                .make()
//                .load(ClassLoader.getSystemClassLoader())
//                .getLoaded();

        return "";


    }
}

