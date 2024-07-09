package com.ylli.api.config.shardingsphere;

import com.ylli.api.common.uid.RandomLongGenerator;
import org.apache.shardingsphere.sharding.spi.KeyGenerateAlgorithm;

import java.util.Properties;

public class CustomIdGenerator implements KeyGenerateAlgorithm {

    private Properties props;

    @Override
    public Comparable<?> generateKey() {
        return RandomLongGenerator.nextId(0, Long.MAX_VALUE);
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
