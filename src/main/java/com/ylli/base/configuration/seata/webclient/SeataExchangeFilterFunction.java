package com.ylli.base.configuration.seata.webclient;

//import org.apache.seata.core.context.RootContext;
//import org.springframework.web.reactive.function.client.ClientRequest;
//import org.springframework.web.reactive.function.client.ClientResponse;
//import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
//import org.springframework.web.reactive.function.client.ExchangeFunction;
//import reactor.core.publisher.Mono;
//
///**
// * @author ylli
// */
//public class SeataExchangeFilterFunction implements ExchangeFilterFunction {
//    @Override
//    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
//        String xid = RootContext.getXID();
//        ClientRequest modifiedRequest = ClientRequest.from(request)
//                .header("TX_XID", xid)
//                .build();
//
//        return next.exchange(modifiedRequest);
//    }
//}
