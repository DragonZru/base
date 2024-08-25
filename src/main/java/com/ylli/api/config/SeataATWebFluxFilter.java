package com.ylli.api.config;

import io.seata.common.util.StringUtils;
import io.seata.core.context.RootContext;
import io.seata.tm.api.GlobalTransaction;
import io.seata.tm.api.GlobalTransactionContext;
import org.apache.shardingsphere.transaction.base.seata.at.SeataTransactionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class SeataATWebFluxFilter implements WebFilter {

    private static final Logger logger = LoggerFactory.getLogger(SeataATWebFluxFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String rpcXid = exchange.getRequest().getHeaders().getFirst(RootContext.KEY_XID);
        String xid = RootContext.getXID();
        //logger.debug("xid in RootContext[{}] xid in HttpContext[{}]", xid, rpcXid);
        System.out.println("-----------------------------xid in RootContext[" + xid + "] xid in HttpContext[" + rpcXid + "]");

        if (StringUtils.isBlank(xid) && StringUtils.isNotBlank(rpcXid)) {
            RootContext.bind(rpcXid);
            SeataTransactionHolder.set(GlobalTransactionContext.getCurrentOrCreate());
            //logger.debug("bind[{}] to RootContext", rpcXid);
            System.out.println("--------------------------bind[" + rpcXid + "] to RootContext");
        }

        return chain.filter(exchange).doFinally(signalType -> {
            if (RootContext.inGlobalTransaction()) {
                String unbindXid = RootContext.unbind();
                //logger.debug("xid[{}] unbind from RootContext", unbindXid);
                System.out.println("-----------------------xid[" + unbindXid + "] unbind from RootContext");
                if (StringUtils.isNotBlank(RootContext.getXID()) && !StringUtils.equalsIgnoreCase(exchange.getRequest().getHeaders().getFirst(RootContext.KEY_XID), unbindXid)) {
                    if (StringUtils.isNotBlank(unbindXid)) {
                        RootContext.bind(unbindXid);
                        SeataTransactionHolder.set(GlobalTransactionContext.getCurrentOrCreate());
                        //logger.debug("xid[{}] bind to RootContext", unbindXid);
                        System.out.println("---------------------xid[" + unbindXid + "] bind to RootContext");
                    }
                }
            }
        });
    }
}
