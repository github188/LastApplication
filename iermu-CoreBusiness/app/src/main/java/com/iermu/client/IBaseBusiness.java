package com.iermu.client;

/**
 * 基础业务相关接口
 * Created by wcy on 15/6/27.
 */
public interface IBaseBusiness {

    /**
     * 订阅事件
     * @param event
     */
    public <T> void subscribeEvent(final Class<?> clz, T event);

    /**
     * 取消订阅事件
     * @param event
     */
    public <T> void unSubscribeEvent(final Class<?> clz, T event);

    /**
     * 触发事件 (该事件工作在当前线程)
     * @param methodName	触发的事件函数
     * @param clz	事件Class
     * @param args			回调函数的参数
     */
    public void publishEvent(String methodName, Class<?> clz, Object... args);

    /**
     * 触发事件监听 (该事件工作在主线程)
     * @param clz	事件监听Class
     * @param args			回调函数的参数
     */
    public void publishEvent(final Class<?> clz, final Object... args);

    /**
     * 触发事件 (该事件工作在当前线程)
     * @param methodName	触发的事件函数
     * @param listenerClz	事件Class
     * @param args			回调函数的参数
     */
    public void sendListener(String methodName, Class<?> listenerClz, Object... args);

    /**
     * 触发事件监听 (该事件工作在主线程)
     * @param listenerClz	事件监听Class
     * @param args			回调函数的参数
     */
    public void sendListener(final Class<?> listenerClz, final Object... args);

    /**
     * 注册业务事件监听
     * @param listener 事件对象(相应事件Class)
     * @param <T>
     */
    public <T> void registerListener(Class<T> listenerClz, T listener);

    /**
     * 取消注册业务事件监听
     * @param listener 事件对象(相应事件Class)
     * @param <T>
     */
    public <T> void unRegisterListener(Class<T> listenerClz, T listener);

}
