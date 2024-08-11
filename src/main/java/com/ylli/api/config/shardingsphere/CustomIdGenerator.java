package com.ylli.api.config.shardingsphere;

import org.apache.shardingsphere.sharding.spi.KeyGenerateAlgorithm;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

@Component
public class CustomIdGenerator implements KeyGenerateAlgorithm {

    RestTemplate restTemplate = new RestTemplate();

    private Properties props;

    @Override
    public Comparable<?> generateKey() {
        return restTemplate.getForObject("http://127.0.0.1:18080/leaf", Long.class);
        //return RandomLongGenerator.nextId(0, Long.MAX_VALUE);
    }

    @Override
    public String getType() {
        return "CustomIdGenerator";
    }

    @Override
    public Properties getProps() {
        return props;
    }

    @Override
    public void init(Properties props) {
        this.props = props;
    }
}
