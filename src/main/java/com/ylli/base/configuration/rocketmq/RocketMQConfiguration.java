package com.ylli.base.configuration.rocketmq;

import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * @author ylli
 */
@Configuration
@EnableConfigurationProperties(RocketMQProperties.class)
@ConditionalOnProperty(name = "rocketmq.enabled", havingValue = "true")
public class RocketMQConfiguration implements DisposableBean, ApplicationContextAware {

    public static final Logger log = LoggerFactory.getLogger(RocketMQConfiguration.class);

    RocketMQProperties properties;
    private ApplicationContext applicationContext;

    public RocketMQConfiguration(RocketMQProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void init() {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();

        if (!properties.getProducer().isEmpty()) {
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
            properties.getProducer().entrySet().forEach(entry -> {
                String beanName = entry.getKey();

                if (Arrays.asList(beanDefinitionNames).contains(beanName)) {
                    throw new BeanCreationException(beanName, "There is already defined in the context.");
                }
                BeanDefinitionBuilder beanDefinitionBuilder = null;
                //动态注册bean.
                if (entry.getValue().getCls() == TransactionMQProducer.class) {
                    beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(TransactionMQProducer.class);
                } else {
                    beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DefaultMQProducer.class);
                }

                defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
                //TransactionMQProducer extends DefaultMQProducer
                DefaultMQProducer producer = (DefaultMQProducer) applicationContext.getBean(beanName);
                producer.setNamesrvAddr(properties.getNameServer());
                producer.setProducerGroup(entry.getValue().getGroup());

                try {
                    producer.start();
                } catch (MQClientException e) {
                    //throw new RuntimeException(e);
                    log.error("MQProducer: {}start failed, {}", beanName, e.getErrorMessage());
                }
            });
        }
    }

    @Override
    public void destroy() throws Exception {
        properties.getProducer().keySet().stream()
                .map(producerProperties -> applicationContext.getBean(producerProperties))
                .filter(entry -> entry instanceof DefaultMQProducer)
                .map(DefaultMQProducer.class::cast)
                .forEach(DefaultMQProducer::shutdown);
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
