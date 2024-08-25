package com.ylli.api.config;

import io.seata.common.util.StringUtils;
import io.seata.core.context.RootContext;
import io.seata.integration.http.SeataWebMvcConfigurer;
import io.seata.tm.api.GlobalTransactionContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.shardingsphere.transaction.base.seata.at.SeataTransactionHolder;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@Configuration
public class JakartaSeataWebMvcConfigurer extends SeataWebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(new JakartaTransactionPropagationInterceptor());
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String xid = RootContext.getXID();
                if (xid == null) {
                    xid = request.getHeader(RootContext.KEY_XID);
                }
                if (!StringUtils.isBlank(xid)) {
                    RootContext.bind(xid);
                    if (SeataTransactionHolder.get() == null) {
                        SeataTransactionHolder.set(GlobalTransactionContext.getCurrentOrCreate());
                    }
                }
                return true;
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
                if (RootContext.inGlobalTransaction()) {
                    if (StringUtils.isNotBlank(RootContext.getXID())) {
                        String unbindXid = RootContext.unbind();
                        SeataTransactionHolder.clear();
                        if (!StringUtils.equalsIgnoreCase(request.getHeader(RootContext.KEY_XID), unbindXid)) {
                            if (StringUtils.isNotBlank(unbindXid)) {
                                RootContext.bind(unbindXid);
                                SeataTransactionHolder.set(GlobalTransactionContext.getCurrentOrCreate());
                            }
                        }
                    }
                }
            }
        });
    }
}
