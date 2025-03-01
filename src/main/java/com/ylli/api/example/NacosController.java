package com.ylli.api.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description
 **/
@RestController
@RequestMapping("nacos")
//@RefreshScope
public class NacosController {

    @Value("${ylli.test.key}")
    private String key;

    @Autowired
    private Environment environment;

//    public NacosController(Environment environment) {
//        NacosController.environment = environment;
//    }

    @GetMapping
    public Object test() {

//        Class<?> dynamicType = new ByteBuddy()
//                .subclass(ServiceProvider.class)
//                .method(ElementMatchers.named("getQwqyPlatformHost"))
//                .intercept(FixedValue.value("http://newpath:8080/test"))
//                .make()
//                .load(ClassLoader.getSystemClassLoader())
//                .getLoaded();

        return environment.getProperty("ylli.test.key");
    }
}

