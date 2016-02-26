package com.iermu.client.business.impl;

import com.iermu.client.IBaseBusiness;

/**
 *
 *
 * Created by wcy on 15/6/29.
 */
public class BaseBusinessStrategy extends BaseBusiness implements IBaseBusiness {

    private IBaseBusiness mBusiness;

    public BaseBusinessStrategy(IBaseBusiness business) {
        this.mBusiness = business;
    }

    @Override
    public void publishEvent(String methodName, Class<?> clz, Object... args) {
        mBusiness.publishEvent(methodName, clz, args);
    }

    @Override
    public void publishEvent(Class<?> clz, Object... args) {
        mBusiness.publishEvent(clz, args);
    }

    @Override
    public void sendListener(String methodName, Class<?> listenerClz, Object... args) {
        mBusiness.sendListener(methodName, listenerClz, args);
    }

    @Override
    public void sendListener(Class<?> listenerClz, Object... args) {
        mBusiness.sendListener(listenerClz, args);
    }

    @Override
    public <T> void registerListener(Class<T> listenerClz, T listener) {
        mBusiness.registerListener(listenerClz, listener);
    }

    @Override
    public <T> void unRegisterListener(Class<T> listenerClz, T listener) {
        mBusiness.unRegisterListener(listenerClz, listener);
    }

}
