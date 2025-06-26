package com.ylli.base;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.TimeZone;

/**
 * @author ylli
 */
@SpringBootApplication
@EnableDiscoveryClient
public class BaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaseApplication.class, args);
    }

    @PostConstruct
    void defaultTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }
}
