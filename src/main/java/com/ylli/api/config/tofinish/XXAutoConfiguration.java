package com.ylli.api.config.tofinish;

//@AutoConfiguration(before = {FlywayAutoConfiguration.class})
//@EnableConfigurationProperties(ShardingFlywayProperties.class)
//@ConditionalOnProperty(prefix = "sharding.flyway", value = "enabled", havingValue = "true")
public class XXAutoConfiguration {

    // TODO remove.
    // 考虑到mysql多数据源情况下会自动同步，所以这里完全可以使用spring.flyway 直接使用
//    @Bean
//    @ConditionalOnMissingBean
//    public List<MigrateResult> flywayMigrationInitializers(ShardingFlywayProperties properties) {
//        List<MigrateResult> results = new ArrayList<>();
//        if (!properties.multimap.isEmpty()) {
//
//            properties.multimap.forEach((key, value) -> {
//
//                Arrays.stream(value.getSchemas()).forEach(schema -> {
//                    Flyway flyway = Flyway.configure().dataSource(value.getUrl(), value.getUser(), value.getPassword())
//                            .schemas(schema)
//                            .locations(Optional.ofNullable(value.locations).orElse("classpath:db/migration"))
//                            .cleanDisabled(Optional.ofNullable(value.cleanDisabled).orElse(true))
//                            .outOfOrder(Optional.ofNullable(value.outOfOrder).orElse(false))
//                            .validateOnMigrate(Optional.ofNullable(value.validateOnMigrate).orElse(true))
//                            .baselineOnMigrate(Optional.ofNullable(value.baselineOnMigrate).orElse(true))
//                            .load();
//                    results.add(flyway.migrate());
//                });
//            });
//        }
//        return results;
//    }
}

