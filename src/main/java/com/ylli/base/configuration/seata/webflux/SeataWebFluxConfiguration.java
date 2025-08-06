package com.ylli.base.configuration.seata.webflux;

import org.springframework.web.server.WebFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeataWebFluxConfiguration {

    @Bean
    public WebFilter seataWebFilter(){
        return new SeataWebFilter();
    }
}
