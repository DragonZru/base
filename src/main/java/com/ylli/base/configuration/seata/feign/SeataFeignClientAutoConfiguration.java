package com.ylli.base.configuration.seata.feign;

//import com.alibaba.cloud.seata.feign.SeataFeignBuilderBeanPostProcessor;
//import com.alibaba.cloud.seata.feign.SeataFeignRequestInterceptor;
//import feign.Client;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration(
//        proxyBeanMethods = false
//)
//@ConditionalOnClass({Client.class})
//public class SeataFeignClientAutoConfiguration {
//    public SeataFeignClientAutoConfiguration() {
//    }
//
//    @Bean
//    public static com.alibaba.cloud.seata.feign.SeataFeignBuilderBeanPostProcessor seataFeignBuilderBeanPostProcessor() {
//        return new SeataFeignBuilderBeanPostProcessor();
//    }
//
//    @Bean
//    public SeataFeignRequestInterceptor seataFeignRequestInterceptor() {
//        return new SeataFeignRequestInterceptor();
//    }
//}
