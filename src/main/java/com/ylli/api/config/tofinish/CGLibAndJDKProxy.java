package com.ylli.api.config.tofinish;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class CGLibAndJDKProxy {

    public static void main(String[] args) {
//cglib
//        SayHello proxy = CGLibProxyFactory.createProxy(Hello.class, new CGLibProxy());
//        proxy.sayHello();

        //jdk
        ProxyHandler handler = new ProxyHandler(new Hello());
        //创建代理对象，传入类加载器、接口、handler
        SayHello helloProxy = (SayHello) Proxy.newProxyInstance(
                SayHello.class.getClassLoader(),
                new Class[]{SayHello.class}, handler);
        helloProxy.sayHello();
    }

    public static interface SayHello {
        void sayHello();
    }

    public static class CGLibProxy implements MethodInterceptor {
        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            // do something
            Object result = proxy.invokeSuper(obj, args);
            // do something
            return result;
        }
    }

    public static class CGLibProxyFactory {
        // 工厂方法，接收要代理的类并返回代理对象
        public static <T> T createProxy(Class<T> clazz, MethodInterceptor interceptor) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(clazz);
            enhancer.setCallback(interceptor);
            return (T) enhancer.create(); // 生成代理对象
        }
    }

    public static class ProxyHandler implements InvocationHandler {
        private Object subject; // 这个就是我们要代理的真实对象，也就是真正执行业务逻辑的类

        public ProxyHandler(Object subject) {// 通过构造方法传入这个被代理对象
            this.subject = subject;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result = method.invoke(subject, args);
            return result;
        }
    }

    public static class Hello implements SayHello {
        @Override
        public void sayHello() {
            System.out.println("hello world");
        }
    }
}
