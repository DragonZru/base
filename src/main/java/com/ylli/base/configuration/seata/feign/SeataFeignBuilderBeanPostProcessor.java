package com.ylli.base.configuration.seata.feign;

import feign.Feign;
import feign.Retryer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;


/**
 * @author ylli
 *
 * 禁用Feign重试，seata AT模式下，若因为网络抖动导致请求失败，feign默认会重试，会存在:
 *  数据一致性：A -> B => A -> BB..
 *  数据回滚失败
 */
public class SeataFeignBuilderBeanPostProcessor implements BeanPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(SeataFeignBuilderBeanPostProcessor.class);

    public SeataFeignBuilderBeanPostProcessor() {
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Feign.Builder) {
            ((Feign.Builder)bean).retryer(Retryer.NEVER_RETRY);
            LOGGER.info("change the retryer of the bean '{}' to 'Retryer.NEVER_RETRY'", beanName);
        }

        return bean;
    }
}
