package com.ylli.base.configuration.seata.rest;

//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.client.ClientHttpRequestInterceptor;
//import org.springframework.web.client.RestTemplate;
//
//public class SeataRestTemplateInterceptorAfterPropertiesSet implements InitializingBean {
//    @Autowired(
//            required = false
//    )
//    private Collection<RestTemplate> restTemplates;
//    @Autowired
//    private SeataRestTemplateInterceptor seataRestTemplateInterceptor;
//
//    public SeataRestTemplateInterceptorAfterPropertiesSet() {
//    }
//
//    public void afterPropertiesSet() {
//        if (this.restTemplates != null) {
//            for(RestTemplate restTemplate : this.restTemplates) {
//                List<ClientHttpRequestInterceptor> interceptors = new ArrayList(restTemplate.getInterceptors());
//                interceptors.add(this.seataRestTemplateInterceptor);
//                restTemplate.setInterceptors(interceptors);
//            }
//        }
//
//    }
//}
