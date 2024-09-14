package com.ylli.api.mq;

import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 消息消费：失败重试；顺序消息会自动不断进行消息重试（间隔时间1s）一直到消费成功。。（有点坑）
 * 这时，应用会出现消息消费被阻塞的情况。因此，建议您使用顺序消息时，务必保证应用能够及时监控并处理消费失败的情况，避免阻塞现象的发生
 * <p>
 * 对于其他无序消息（普通，定时，延时，事物）会默认尝试16次（全部失败进入死信队列）10s - 30s - 1min - 2min - 3min -4 -5-6-7-8-9-10min
 * - 20min - 30min - 1h - 2h
 * 广播模式下不会进行重试！！
 */
public class MQConsumer {

    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumerGroup-1");
        consumer.setNamesrvAddr("localhost:9876;localhost:9877");
        //consumer.setMessageModel(MessageModel.BROADCASTING);
        //注册监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                msgs.forEach(msg -> {
                    System.out.println("consumerGroup-1" + "received msg:" + new String(msg.getBody(), StandardCharsets.UTF_8));

                    /*
                    因为消息队列优先保证消息至少消费一次 at last once 所以可能会出现重复投送的问题；又或者，
                    因为网络问题，消息在路上，没有收到消费者反馈，一直投送出现重复投送，
                    所以需要消费者自行控制消息幂等

                    场景：上游订单下单成功，通过消息队列来扣减库存，因为不在一个事物里，所以乐观锁version 并不解决问题，送达几次就会扣减几次
                    solution：
                        1。建立防重表，可以用订单id or 请求全局唯一id token 关联此消息，扣减成功则插入，后续判断防重表有无记录，有则表示
                        已经处理直接pass
                        2。使用redis set nx 原理一样
                     */
                });
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.subscribe("DEFAULT_TOPIC", "DEFAULT_TAGS");
        // 默认队尾开始读
        //consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        //批量读取，默认=1
        //consumer.setConsumeMessageBatchMaxSize(10);
        consumer.start();

//        DefaultMQPushConsumer consumer2 = new DefaultMQPushConsumer("consumerGroup-2");
//        consumer2.setNamesrvAddr("localhost:9876;localhost:9877");
//        //指定为广播模式，默认集群模式
//        consumer2.setMessageModel(MessageModel.BROADCASTING);
//
//        // 作用于顺序消息监听，采用分段锁，只有当上一条消息成功消费了，才会进行下一条消息消费
//        /*consumer2.registerMessageListener(new MessageListenerOrderly() {
//            @Override
//            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
//                return null;
//            }
//        });*/
//
//        //注册监听器
//        consumer2.registerMessageListener(new MessageListenerConcurrently() {
//            @Override
//            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
//                msgs.forEach(msg -> {
//                    System.out.println("consumerGroup-2" + "received msg:" + new String(msg.getBody(), StandardCharsets.UTF_8));
//                });
//                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//            }
//        });
//        consumer2.subscribe("DEFAULT_TOPIC", "DEFAULT_TAGS");
//        consumer2.start();


//        PullConsumer consumer = new PullConsumer("default-pull-consumer");
//        consumer.start();
//        consumer.consumer("DEFAULT_TOPIC", "*");


    }


    /**
     * tags tag1 || tag2
     */
    interface Consumer {

        default boolean syncConsumer() {
            return true;
        }

        default void asyncConsumer() {
        }

        //集群模式，默认
        //每条消息只被一个消费者处理，失败从投不一定会路由到同一个消费者
        default void clusterConsumer(String topic, String tags) throws MQClientException {

        }

        //广播模式不支持顺序消息，不支持重置消费位点
        //消息会发送给每一个订阅topic的consumer；但是对失败的消息并不会失败从投
        //启动时只会从当前开始消息消费，对客户端关闭期间消息会丢弃
        //替代方案：在集群模式下，使用不同consumerGroup 订阅相同topic 即可
        default void broadcastConsumer(String topic, String tags) throws MQClientException {

        }

        void start() throws MQClientException;

        void shutDown();

    }

    /**
     * DefaultMQPullConsumer V 4.7 之后被弃用 （需要手动维护偏移量）
     * 使用 DefaultLitePullConsumer 替代
     */
    static class PullConsumer {

        DefaultLitePullConsumer consumer;

        public PullConsumer(DefaultLitePullConsumer consumer) {
            this.consumer = consumer;
        }

        public PullConsumer(String groupName) {
            this.consumer = new DefaultLitePullConsumer(groupName);
            consumer.setNamesrvAddr("localhost:9876;localhost:9877");
        }

        public void consumer(String topic, String tags) throws MQClientException {
            consumer.subscribe(topic, tags);
            //设置每批次消费10条消息
            //consumer.setPullBatchSize(10);
            while (true) {
                spinPull(consumer);
            }
        }

        void spinPull(DefaultLitePullConsumer consumer) {
            List<MessageExt> list;
            if ((list = consumer.poll()).size() > 0) {
                for (MessageExt ext : list) {
                    try {
                        //逻辑处理
                        System.out.println("received msg: " + new String(ext.getBody(), StandardCharsets.UTF_8));
                    } catch (Exception e) {
                        //这里需要对消费失败的消息手动进行处理，（失败重投）

                    }
                }
                //提交偏移量
                consumer.commit();
            } else {
                spinPull(consumer);
                Thread.onSpinWait();
            }
        }

        public void start() throws MQClientException {
            this.consumer.start();
        }

        public void shutDown() {
            this.consumer.shutdown();
        }


    }

    static class FIFOConsumer {

        void consumer() throws MQClientException {
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumerGroup-1");
            consumer.setNamesrvAddr("localhost:9876;localhost:9877");
            //consumer.setMessageModel(MessageModel.BROADCASTING);
            //注册监听器,区别与普通消息，监听器为 MessageListenerOrderly ，保证消费顺序
            consumer.registerMessageListener(new MessageListenerOrderly() {
                @Override
                public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                    msgs.forEach(messageExt -> {
                        //逻辑处理
                        System.out.println("message:" + new String(messageExt.getBody(), StandardCharsets.UTF_8));
                    });
                    return ConsumeOrderlyStatus.SUCCESS;
                }
            });
            consumer.subscribe("DEFAULT_TOPIC", "DEFAULT_TAGS");

            consumer.start();
        }
    }
}
