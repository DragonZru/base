package com.ylli.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class ShardingsphereTransactionConfiguration {

    //https://shardingsphere.apache.org/document/5.2.1/cn/dev-manual/transaction/
//    @Bean
//    public ShardingSphereTransactionManager shardingSphereTransactionManager() {
//        return new SeataATShardingSphereTransactionManager();
//    }

    @Bean
    public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
