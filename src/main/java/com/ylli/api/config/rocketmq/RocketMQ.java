package com.ylli.api.config.rocketmq;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
public class RocketMQ implements ApplicationContextAware {

    //save transaction producer
    //注意：当rocketmq producer配置更新后，需要同步更新这里的缓存-外部监听器，否则会导致消息无法正常发送
    static Cache<String, TransactionMQProducer> cache = Caffeine.newBuilder().build();
    private static ApplicationContext applicationContext;
    RocketMQProperties rocketMQProperties;

    public RocketMQ(RocketMQProperties rocketMQProperties) {
        this.rocketMQProperties = rocketMQProperties;
    }

    public RocketMQ() {
    }

    public static TransactionSendResult sendTransactionMessage(Message message, Function<String, Boolean> transaction, Function<String, Boolean> check) throws MQClientException {
        return new RocketMQ().sendTransactionMessage(getTransactionMQProducer("defaultTransactionProducer"), message, transaction, check);
    }

    public static TransactionSendResult sendTransactionMessage(String transactionGroup, Message message, Function<String, Boolean> transaction, Function<String, Boolean> check) throws MQClientException {
        return new RocketMQ().sendTransactionMessage(getTransactionMQProducer(transactionGroup), message, transaction, check);
    }

    public static TransactionMQProducer getTransactionMQProducer(String beanName) {
        if (cache.getIfPresent(beanName) != null) {
            return cache.getIfPresent(beanName);
        }

        TransactionMQProducer transactionMQProducer = Optional.ofNullable(applicationContext.getBean(beanName, TransactionMQProducer.class))
                .orElseThrow(() -> new NoSuchBeanDefinitionException(beanName));
        cache.put(beanName, transactionMQProducer);
        return transactionMQProducer;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public TransactionSendResult sendTransactionMessage(TransactionMQProducer transactionMQProducer,
                                                        Message message,
                                                        Function<String, Boolean> saveTransaction,
                                                        Function<String, Boolean> localCheck) throws MQClientException {

        transactionMQProducer.setTransactionListener(new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
                //执行本地事物
                if (saveTransaction != null && saveTransaction.apply(new String(msg.getBody()))) {
                    return LocalTransactionState.COMMIT_MESSAGE;
                }
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                // 根据事务执行状态，返回对应的事务状态
                // 返回值可以是COMMIT_MESSAGE、ROLLBACK_MESSAGE或UNKNOW
                if (localCheck != null && localCheck.apply(new String(msg.getBody()))) {
                    // 本地事物查询
                    return LocalTransactionState.COMMIT_MESSAGE;
                }
                return LocalTransactionState.UNKNOW;
            }
        });
        return transactionMQProducer.sendMessageInTransaction(message, null);
    }
}
