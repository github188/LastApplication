package com.iermu.client.business.impl;

import com.iermu.client.ErmuApplication;
import com.iermu.client.IBaseBusiness;
import com.iermu.eventobj.BusObject;

/**
 *  基础业务
 *
 * Created by wcy on 15/6/22.
 */
public class BaseBusiness implements IBaseBusiness {

    /**
     * 订阅事件
     * @param event
     */
    public <T> void subscribeEvent(final Class<?> clz, T event) {
        BusObject.subscribeEvent(clz, event);
    }

    /**
     * 取消订阅事件
     * @param event
     */
    public <T> void unSubscribeEvent(final Class<?> clz, T event) {
        BusObject.subscribeEvent(clz, event);
    }

    @Override
    public void publishEvent(String methodName, Class<?> listenerClz, Object... args) {
        BusObject.publishEvent(methodName, listenerClz, args);
    }

    @Override
    public void publishEvent(Class<?> listenerClz, Object... args) {
        BusObject.publishEvent(listenerClz, args);
    }

    @Override
    public void sendListener(String methodName, Class<?> listenerClz, Object... args) {
        BusObject.sendListener(methodName, listenerClz, args);
    }

    @Override
    public void sendListener(Class<?> listenerClz, Object... args) {
        BusObject.sendListener(listenerClz, args);
    }

    @Override
    public <T> void registerListener(Class<T> listenerClz, T listener) {
        BusObject.registerListener(listenerClz, listener);
    }

    @Override
    public <T> void unRegisterListener(Class<T> listenerClz, T listener) {
        BusObject.unRegisterListener(listenerClz, listener);
    }

    public void execMainThread(Runnable runnable) {
        if(runnable == null) return;
        ErmuApplication.execMainThread(runnable);
    }

    public void execBackground(Runnable runnable) {
        if(runnable == null) return;
        ErmuApplication.execBackground(runnable);
    }

}
