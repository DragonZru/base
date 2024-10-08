package com.ylli.api.config.shardingsphere;

import com.alibaba.csp.sentinel.SphO;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import jakarta.annotation.PostConstruct;
import org.apache.shardingsphere.sharding.algorithm.keygen.SnowflakeKeyGenerateAlgorithm;
import org.apache.shardingsphere.sharding.spi.KeyGenerateAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class LeafGenerator implements KeyGenerateAlgorithm {

    private static final Logger logger = LoggerFactory.getLogger(LeafGenerator.class);

    RestTemplate restTemplate = new RestTemplate();

    private Properties props;

    @Override
    public Comparable<Long> generateKey() {
        if (SphO.entry("LeafGenerator")) {
            try {
                return restTemplate.getForObject(props.getProperty("uri"), Long.class);
            } catch (Throwable t) {
                if (!BlockException.isBlockException(t)) {
                    logger.error("LeafServer An unexpected exception occurred:{}", t.getMessage());
                    return new SnowflakeKeyGenerateAlgorithm().generateKey();
                }
            } finally {
                SphO.exit();
            }
        }
        logger.error("LeafGenerator is blocked");
        return new SnowflakeKeyGenerateAlgorithm().generateKey();
    }

    @PostConstruct
    public void initDegradeRule() {
        List<DegradeRule> degradeRules = new ArrayList<>();
        DegradeRule rule = new DegradeRule("LeafGenerator")
                .setGrade(CircuitBreakerStrategy.SLOW_REQUEST_RATIO.getType())
                //ms
                .setCount(200)
                .setSlowRatioThreshold(0.2)
                .setTimeWindow(10);
//        rule.setMinRequestAmount(5);
//        rule.setStatIntervalMs(1000);
        degradeRules.add(rule);
        DegradeRuleManager.loadRules(degradeRules);
    }

    @Override
    public String getType() {
        return "LeafGenerator";
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
