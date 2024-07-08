package com.ylli.api.config.shardingsphere;

import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingValue;

import java.util.Collection;
import java.util.Properties;

public class ExampleComplexKeysShardingAlgorithm implements ComplexKeysShardingAlgorithm<Long> {


    @Override
    public Properties getProps() {
        return null;
    }

    @Override
    public void init(Properties props) {

    }

    @Override
    public String getType() {
        return "EXAMPLE_COMPLEX";
    }

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, ComplexKeysShardingValue<Long> shardingValue) {

        return null;
    }
}
