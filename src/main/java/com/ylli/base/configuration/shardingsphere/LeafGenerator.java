package com.ylli.base.configuration.shardingsphere;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import jakarta.annotation.PostConstruct;
import org.apache.shardingsphere.infra.algorithm.core.context.AlgorithmSQLContext;
import org.apache.shardingsphere.infra.algorithm.keygen.core.KeyGenerateAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @author ylli
 */
@Component
public class LeafGenerator implements KeyGenerateAlgorithm {

    static final String SENTINEL_RESOURCE = "LeafGenerator";

    private static final Logger logger = LoggerFactory.getLogger(LeafGenerator.class);

    RestTemplate restTemplate = new RestTemplate();

    private Properties props;

//    @Override
//    public Comparable<Long> generateKey() {
//        if (SphO.entry(SENTINEL_RESOURCE)) {
//            try {
//                return restTemplate.getForObject(props.getProperty("uri"), Long.class);
//            } catch (Throwable t) {
//                if (!BlockException.isBlockException(t)) {
//                    logger.error("LeafServer An unexpected exception occurred:{}", t.getMessage());
//                    return new SnowflakeKeyGenerateAlgorithm().generateKey();
//                }
//            } finally {
//                SphO.exit();
//            }
//        }
//        logger.warn("LeafGenerator is blocked");
//        return new SnowflakeKeyGenerateAlgorithm().generateKey();
//    }

    @PostConstruct
    public void initDegradeRule() {
        List<DegradeRule> degradeRules = new ArrayList<>();
        DegradeRule rule = new DegradeRule(SENTINEL_RESOURCE)
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
        return SENTINEL_RESOURCE;
    }

//    @Override
//    public Properties getProps() {
//        return props;
//    }

    @Override
    public void init(Properties props) {
        this.props = props;
    }

    @Override
    public Collection<Long> generateKeys(AlgorithmSQLContext context, int i) {
        Collection<Long> result = new LinkedList<>();
        for(int index = 0; index < i; ++index) {
            result.add(generateKey());
        }
        return result;
    }

    public Long generateKey() {
        return 0L;
    }
}
