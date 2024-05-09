package com.ylli.api.config.flyway;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@AutoConfiguration
@EnableConfigurationProperties(ShardingFlywayProperties.class)
@ConditionalOnProperty(prefix = "sharding.flyway", value = "enabled", havingValue = "true")
public class ShardingFlywayAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public List<MigrateResult> flywayMigrationInitializers(ShardingFlywayProperties properties,
                                                           ObjectProvider<FlywayMigrationStrategy> migrationStrategy) {
        List<MigrateResult> results = new ArrayList<>();
        if (!properties.multimap.isEmpty()) {

            properties.multimap.forEach((key, value) -> {

                Arrays.stream(value.getSchemas()).forEach(schema -> {
                    Flyway flyway = Flyway.configure().dataSource(value.getUrl(), value.getUser(), value.getPassword())
                            .schemas(schema)
                            .locations(Objects.isNull(value.locations) ? "classpath:db/migration" : value.locations)
                            .cleanDisabled(Objects.isNull(value.cleanDisabled) ? true : value.cleanDisabled)
                            .outOfOrder(Objects.isNull(value.outOfOrder) ? false : value.outOfOrder)
                            .validateOnMigrate(Objects.isNull(value.validateOnMigrate) ? true : value.validateOnMigrate)
                            .baselineOnMigrate(Objects.isNull(value.baselineOnMigrate) ? true : value.baselineOnMigrate)
                            .load();
                    results.add(flyway.migrate());
                });
            });
        }
        return results;
    }
}

