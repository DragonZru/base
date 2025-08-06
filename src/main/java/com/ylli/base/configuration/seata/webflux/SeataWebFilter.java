package com.ylli.base.configuration.seata.webflux;

import org.apache.seata.common.util.StringUtils;
import org.apache.seata.core.context.RootContext;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class SeataWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String xid = RootContext.getXID();
        String rpcXid = exchange.getRequest().getHeaders().getFirst("TX_XID");
        if (StringUtils.isBlank(xid) && rpcXid != null) {
            RootContext.bind(rpcXid);
        }

        return chain.filter(exchange).doFinally(signalType -> {
            if (RootContext.inGlobalTransaction()) {
                RootContext.unbind();
            }
        });
    }
}
