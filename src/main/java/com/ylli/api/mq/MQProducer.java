package com.ylli.api.mq;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;

/**
 * https://rocketmq.apache.org/zh/docs/sdk/02java
 */
public class MQProducer {

    final static String DEFAULT_TOPIC = "DEFAULT_TOPIC";
    final static String DEFAULT_TAGS = "DEFAULT_TAGS";
    static DefaultMQProducer defaultMQProducer;

    static {
        defaultMQProducer = new DefaultMQProducer("produce-Group");
        defaultMQProducer.setNamesrvAddr("localhost:9876;localhost:9877");
        //producer.setSendMsgTimeout(60000);
    }


    public static void main(String[] args) throws MQClientException, MQBrokerException, RemotingException, InterruptedException {

//        NormalProducer normalProducer = new NormalProducer();
//        normalProducer.start();
//        boolean success = normalProducer.syncSend(DEFAULT_TOPIC, DEFAULT_TAGS, "wo shi message 1");
//        normalProducer.syncSend(DEFAULT_TOPIC, DEFAULT_TAGS, "wo shi message 2");
//        normalProducer.syncSend(DEFAULT_TOPIC, DEFAULT_TAGS, "wo shi message 3");
//
//        System.out.println("is success: " + success);
//        normalProducer.asyncSend(DEFAULT_TOPIC,DEFAULT_TAGS,"我是async message");
//        normalProducer.oneWaySend(DEFAULT_TOPIC,DEFAULT_TAGS,"wos oneway message");

        //normalProducer.shutDown();

        String now = Instant.now().atZone(ZoneId.of("Asia/Shanghai")).toString();

        DelayProducer producer = new DelayProducer();
        producer.start();
        producer.send(now);


    }

    interface Producer {


        //在收到接收方回应后才会发送下一条消息；通常用于重要消息通知，如通知邮件，短信..
        default boolean syncSend(String topic, String tags, String message) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
            return false;
        }

        //不等接收方反应就发送下一条消息；通常用于耗时较长而时间敏感的业务，如上传下载..
        default void asyncSend(String topic, String tags, String message) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {

        }

        //当向发送且没有回调函数，通常用于对可靠性不强要去的场景，如日志采集..
        default void oneWaySend(String topic, String tags, String message) throws RemotingException, InterruptedException, MQClientException {

        }

        void start() throws MQClientException;

        void shutDown();

    }

    /**
     * 普通消息
     */
    static class NormalProducer implements Producer {

        DefaultMQProducer producer;

        public NormalProducer(DefaultMQProducer producer) {
            this.producer = producer;
        }

        public NormalProducer() {
            this.producer = defaultMQProducer;
        }

        @Override
        public boolean syncSend(String topic, String tags, String message) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
            Message msg = new Message(topic, tags, message.getBytes(StandardCharsets.UTF_8));
            //发送消息并返回结果
            SendResult sendResult = producer.send(msg);
            //对相同订单号的消息发送到相同队列，通过messageQueueSelector 实现
            SendResult result = producer.send(msg,new SelectMessageQueueByHash(),"order.id");
//            List<Message> list = new ArrayList<>();
//            producer.send(list);    // 批量发送
            return sendResult.getSendStatus().equals(SendStatus.SEND_OK);
        }

        @Override
        public void asyncSend(String topic, String tags, String message) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
            //禁止失败重试
            producer.setRetryTimesWhenSendAsyncFailed(0);
            Message msg = new Message(topic, tags, message.getBytes(StandardCharsets.UTF_8));
            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.println("sendStatus: " + sendResult.getSendStatus() + " ,msg id: " + sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable e) {

                }
            });
        }

        @Override
        public void oneWaySend(String topic, String tags, String message) throws RemotingException, InterruptedException, MQClientException {
            Message msg = new Message(topic, tags, message.getBytes(StandardCharsets.UTF_8));
            producer.sendOneway(msg);
        }

        @Override
        public void start() throws MQClientException {
            this.producer.start();
        }

        @Override
        public void shutDown() {
            this.producer.shutdown();
        }
    }

    /**
     * rocketmq，默认支持18个级别延时消息（可以在broker.conf 配置）
     * messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     * 实际上rocketmq的消息重试就是用延时消息来实现的，忽略 1s 5s
     * 5.x版本支持已经支持自定义时间控制
     * <p>
     * 可以使用 DDMQ（对rocketmq 进行一层包装）支持延时消息。
     */

    //                  -------------------------------------
    //                  - Broker
    //   producer ====> - 缓存topic                 目标topic - =========> consumer
    //                  ---- ⬇️ ---------️-------------⬆------
    //                       ⬇️
    //                       ⬇️                       ⬆
    //                  ------------------------------⬆-----
    //                  - DelayService                ⬆    -
    //                  -    ⬇️                       ⬆ ️  -
    //                  - consumer -> rocketsDb -> producer -
    //                 -------------------------------------

    static class DelayProducer implements Producer {

        DefaultMQProducer producer;

        public DelayProducer(DefaultMQProducer producer) {
            this.producer = producer;
        }

        public DelayProducer() {
            this.producer = defaultMQProducer;
        }

        void send(String msg) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
            Message message = new Message(DEFAULT_TOPIC, DEFAULT_TAGS, msg.getBytes(StandardCharsets.UTF_8));
            message.setDelayTimeSec(90);
            //message.setDelayTimeLevel(5);
            producer.send(message);

        }


        @Override
        public void start() throws MQClientException {
            this.producer.start();
        }

        @Override
        public void shutDown() {

        }
    }

    /*
    顺序消息示例： first in first out
     */
    static class FIFOProducer implements Producer {

        DefaultMQProducer producer;

        public FIFOProducer() {
            this.producer = defaultMQProducer;
        }

        public FIFOProducer(DefaultMQProducer producer) {
            this.producer = producer;
        }

        @Override
        public void start() throws MQClientException {

        }

        @Override
        public void shutDown() {

        }

        public void send(String msg) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
            Message message = new Message(DEFAULT_TOPIC, DEFAULT_TAGS, msg.getBytes(StandardCharsets.UTF_8));

            //这里解析出msg ,以订单id 进行hash 分类，
            //rocketmq 无法保证全局顺序，但是可以保证局部顺序，同一个queue中消息总是先进先出，所以按订单id 顺序发送消息 可以保证顺序
            producer.send(message, new SelectMessageQueueByHash(), "order.id");
        }
    }


}
