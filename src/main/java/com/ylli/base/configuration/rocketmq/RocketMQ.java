package com.ylli.base.configuration.rocketmq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
public class RocketMQ {

    @Autowired
    ObjectProvider<TransactionMQProducer> transactionProducerProvider;

    public RocketMQ() {
    }

    public TransactionSendResult sendTransactionMessage(Message message, Function<String, Boolean> transaction, Function<String, Boolean> check) throws MQClientException {
        return sendTransactionMessage(getTransactionMQProducer(null), message, transaction, check);
    }

    public TransactionSendResult sendTransactionMessage(String group, Message message, Function<String, Boolean> transaction, Function<String, Boolean> check) throws MQClientException {
        return sendTransactionMessage(getTransactionMQProducer(group), message, transaction, check);
    }

    public TransactionMQProducer getTransactionMQProducer(String group) {
        return transactionProducerProvider.stream().filter(producer ->
        {
            return (Optional.ofNullable(group).orElse("defaultTransactionProducerGroup")).equals(producer.getProducerGroup());
        }).findFirst().orElse(null);
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
