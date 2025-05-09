package com.ylli.base.configuration.seata.feign;

//import feign.Feign;
//import feign.Retryer;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.BeanPostProcessor;
//
//public class SeataFeignBuilderBeanPostProcessor implements BeanPostProcessor {
//    private static final Logger LOGGER = LoggerFactory.getLogger(com.alibaba.cloud.seata.feign.SeataFeignBuilderBeanPostProcessor.class);
//
//    public SeataFeignBuilderBeanPostProcessor() {
//    }
//
//    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//        if (bean instanceof Feign.Builder) {
//            ((Feign.Builder)bean).retryer(Retryer.NEVER_RETRY);
//            LOGGER.info("change the retryer of the bean '{}' to 'Retryer.NEVER_RETRY'", beanName);
//        }
//
//        return bean;
//    }
//}
