package com.ylli.base.configuration.seata.feign;

//import feign.RequestInterceptor;
//import feign.RequestTemplate;
//import io.seata.core.context.RootContext;
//import org.springframework.util.StringUtils;
//
//public class SeataFeignRequestInterceptor implements RequestInterceptor {
//    public SeataFeignRequestInterceptor() {
//    }
//
//    public void apply(RequestTemplate template) {
//        String xid = RootContext.getXID();
//        if (StringUtils.hasLength(xid)) {
//            template.header("TX_XID", new String[]{xid});
//        }
//    }
//}
