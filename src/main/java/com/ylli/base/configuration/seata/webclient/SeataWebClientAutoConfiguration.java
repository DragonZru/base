package com.ylli.base.configuration.seata.webclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author ylli
 */
@Configuration
public class SeataWebClientAutoConfiguration {

    @Bean
    public SeataExchangeFilterFunction seataExchangeFilterFunction() {
        return new SeataExchangeFilterFunction();
    }

    @Bean
    public WebClient.Builder seataWebClientBuilder(SeataExchangeFilterFunction seataExchangeFilterFunction) {
        return WebClient.builder()
                .filter(seataExchangeFilterFunction);
    }
}
