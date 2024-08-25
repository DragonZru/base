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

    // todo 注释 看下 是否工作
    // seata.conf 是否必须存在 和以下参数重叠
    //seata.application-id=${spring.application.name}
    //seata.tx-service-group=default-tx-group

//    @Bean
//    public SeataATShardingSphereTransactionManager shardingSphereTransactionManager() {
//        return new SeataATShardingSphereTransactionManager();
//    }

    @Bean
    public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
