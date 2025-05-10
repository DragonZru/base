package com.ylli.base.configuration.seata.rest;

import org.apache.seata.core.context.RootContext;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author ylli
 */
public class SeataRestTemplateInterceptor implements ClientHttpRequestInterceptor {
    public SeataRestTemplateInterceptor() {
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        HttpRequestWrapper requestWrapper = new HttpRequestWrapper(httpRequest);
        String xid = RootContext.getXID();
        if (StringUtils.hasLength(xid)) {
            requestWrapper.getHeaders().add("TX_XID", xid);
        }

        return clientHttpRequestExecution.execute(requestWrapper, bytes);
    }
}
