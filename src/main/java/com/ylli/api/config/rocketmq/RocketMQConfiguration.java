package com.ylli.api.config.rocketmq;

import jakarta.annotation.PostConstruct;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Supplier;

import static com.ylli.api.config.rocketmq.RocketMQProperties.ProducerProperties.TRANSACTION;

@Configuration
@EnableConfigurationProperties(RocketMQProperties.class)
@ConditionalOnProperty(prefix = "rocketmq", value = "enable", havingValue = "true")
public class RocketMQConfiguration implements DisposableBean, ApplicationContextAware {

    public static final Logger log = LoggerFactory.getLogger(RocketMQConfiguration.class);

    RocketMQProperties rocketMQProperties;
    private ApplicationContext applicationContext;

    public RocketMQConfiguration(RocketMQProperties rocketMQProperties) {
        this.rocketMQProperties = rocketMQProperties;
    }

    public TransactionSendResult sendTransactionMessage(String msg, Supplier<Boolean> supplier) throws MQClientException {
        return sendTransactionMessage("defaultTransaction", msg, supplier);
    }

    /**
     * 普通消息，顺序消息，延时消息 类似，使用DefaultMQProducer
     */
    public TransactionSendResult sendTransactionMessage(String producer, String msg, Supplier<Boolean> supplier) throws MQClientException {
        Message message = new Message("DEFAULT_TOPIC", "DEFAULT_TAGS", msg.getBytes(StandardCharsets.UTF_8));

        TransactionMQProducer transactionMQProducer = (TransactionMQProducer) applicationContext.getBean(producer);
        if (transactionMQProducer == null) {
            throw new NoSuchBeanDefinitionException(producer);
        }
        transactionMQProducer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {

                //执行本地事物
                if (supplier.get()) {
                    return LocalTransactionState.COMMIT_MESSAGE;
                }
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                // 根据事务执行状态，返回对应的事务状态
                // 返回值可以是COMMIT_MESSAGE、ROLLBACK_MESSAGE或UNKNOW
//                if (localCheck()) {
//                    // 本地事物查询
//                }
                return LocalTransactionState.UNKNOW;
            }
        });
        return transactionMQProducer.sendMessageInTransaction(message, null);
    }

    @PostConstruct
    public void init() {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();

        if (!rocketMQProperties.getProducer().isEmpty()) {
            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
            rocketMQProperties.getProducer().entrySet().forEach(entry -> {
                String beanName = entry.getKey();

                if (Arrays.asList(beanDefinitionNames).contains(beanName)) {
                    throw new BeanCreationException(beanName, "There is already defined in the context.");
                }
                BeanDefinitionBuilder beanDefinitionBuilder = null;
                String type = entry.getValue().getType();
                //动态注册bean.
                if (TRANSACTION.equals(type)) {
                    beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(TransactionMQProducer.class);
                } else {
                    beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DefaultMQProducer.class);
                }

                defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
                //TransactionMQProducer extends DefaultMQProducer
                DefaultMQProducer producer = (DefaultMQProducer) applicationContext.getBean(beanName);
                producer.setNamesrvAddr(rocketMQProperties.getNameServer());
                producer.setProducerGroup(entry.getValue().getGroup());

                try {
                    producer.start();
                } catch (MQClientException e) {
                    //throw new RuntimeException(e);
                    log.error("DefaultMQProducer: {}start failed, {}", beanName, e.getErrorMessage());
                }
            });
        }
    }

    @Override
    public void destroy() throws Exception {
        rocketMQProperties.getProducer().entrySet().stream().map(entry -> entry.getKey()).forEach(beanName -> {
            ((DefaultMQProducer) applicationContext.getBean(beanName)).shutdown();
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
