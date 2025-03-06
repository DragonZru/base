package com.ylli.api.base.configuration.rocketmq;

import lombok.Data;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "rocketmq")
@Data
public class RocketMQProperties {

    public String nameServer;

    //<beanName,properties>
    public Map<String, ProducerProperties> producer;

    public Map<String, ConsumerProperties> consumer;

    @Data
    static class ProducerProperties {

        // normal or transaction
        public Class<? extends MQProducer> cls = DefaultMQProducer.class;

        public String group;
    }

    @Data
    static class ConsumerProperties {

        public String group;

        public String topic;

        public List<String> tags;

        public Integer batchSize;

    }
}

