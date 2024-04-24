package com.ylli.api.config.flyway;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;

@ConfigurationProperties(prefix = "sharding.flyway")
@Data
public class ShardingFlywayProperties {

    Boolean enabled = false;

    HashMap<String, FlywayProperties> multimap;

    @Data
    static class FlywayProperties {
        public String driver;
        public String url;
        public String user;
        public String password;
        public String[] schemas;
        public Boolean validateOnMigrate;
        public Boolean baselineOnMigrate;
        public Boolean cleanDisabled;
        public Boolean outOfOrder;
        public String locations;
    }

}
