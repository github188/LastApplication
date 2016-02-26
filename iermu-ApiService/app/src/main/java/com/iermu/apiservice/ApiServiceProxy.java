package com.iermu.apiservice;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 服务端接口数据缓存及缓存加载策略
 *
 * Created by wcy on 15/7/1.
 */
public class ApiServiceProxy implements InvocationHandler {

    private Object target;

    public ApiServiceProxy(Object target) {
        this.target = target;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy() {
        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                this
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //Logger.i("invoke 1");

        Object result = method.invoke(target, args);

        //Logger.i("invoke 2");
        return result;
    }


}
