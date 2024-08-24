package com.ylli.api.config;

import org.apache.shardingsphere.transaction.base.seata.at.SeataATShardingSphereTransactionManager;
import org.apache.shardingsphere.transaction.spi.ShardingSphereTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class ShardingsphereTransactionConfiguration {

    //https://shardingsphere.apache.org/document/5.2.1/cn/dev-manual/transaction/
    @Bean
    public ShardingSphereTransactionManager shardingSphereTransactionManager() {
        return new SeataATShardingSphereTransactionManager();
    }
}
