package com.ylli.api.config.rocketmq;

import lombok.Data;
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

        //事物消息和其他producer 不一样
        public static final String NORMAL = "normal";
        public static final String TRANSACTION = "transaction";

        // normal or transaction
        public String type = NORMAL;

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
