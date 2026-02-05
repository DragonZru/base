package com.ylli.base.configuration.rocketmq;

import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.StaticSessionCredentialsProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;

public class RocketMQTest {

    public static void main(String[] args) throws Exception {
        // 1. 创建凭证提供者

        StaticSessionCredentialsProvider credentialsProvider =
                new StaticSessionCredentialsProvider("rocketmq","12345678");

        // 2. 配置客户端
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints("127.0.0.1:8081")  // Proxy地址或Broker地址
                .setCredentialProvider(credentialsProvider)
                .build();

        // 3. 创建生产者
        ClientServiceProvider provider = ClientServiceProvider.loadService();
        Producer producer = provider.newProducerBuilder()
                .setClientConfiguration(clientConfiguration)
                .setTopics("TestTopic")
                .build();

        // 4. 发送消息
        Message message = provider.newMessageBuilder()
                .setTopic("TestTopic")
                .setBody("Hello RocketMQ with ACL".getBytes())
                .build();

        producer.send(message);
        System.out.println("消息发送成功");

        // 5. 关闭生产者
        producer.close();
    }
}
