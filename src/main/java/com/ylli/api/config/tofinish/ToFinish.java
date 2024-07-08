package com.ylli.api.config.tofinish;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class ToFinish {

    public void tofinish()
    {
        // 创建代理
        Enhancer eh = new Enhancer();
        //
        //eh.setSuperclass(bean.getClass());
        eh.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                if (true) {
                    // do something
                }
                return proxy.invokeSuper(obj, args);
            }
        });
        eh.create();
    }
}
