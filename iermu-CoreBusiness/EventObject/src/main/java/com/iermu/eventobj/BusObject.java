package com.iermu.eventobj;

import android.os.Handler;
import android.text.TextUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 业务事件对象
 *
 * Created by wcy on 15/7/1.
 */
public class BusObject {

    private static Map<Class<?>, Collection<?>> mEventListener = new HashMap<Class<?>, Collection<?>>();
    private static Map<Class<?>, Collection<?>> mBusEvent = new HashMap<Class<?>, Collection<?>>();
    private static Handler mainHandler = new Handler();

    /**
     * 订阅事件
     * @param event
     */
    public static synchronized <T> void subscribeEvent(final Class<?> clz, T event) {
        getBusEvent(clz).add(event);
    }

    /**
     * 取消订阅事件
     * @param event
     */
    public static synchronized <T> void unSubscribeEvent(final Class<?> clz, T event) {
        getBusEvent(clz).remove(event);
    }

//    /**
//     * 触发事件 (该事件工作在当前线程)
//     * @param listenerClz	事件Class
//     * @param methodName	触发的事件函数
//     * @param args			回调函数的参数
//     */
//    public static void publishEvent(final Class<?> listenerClz, String methodName, final Object... args) {
//        publishEvent(methodName, listenerClz, args);
//    }

    /**
     * 触发事件监听 (该事件工作在主线程)
     * @param listenerClz	事件监听Class
     * @param args			回调函数的参数
     */
    public static void publishEvent(final Class<?> listenerClz, final Object... args) {
        publishEvent("", listenerClz, args);
    }

    /**
     * 发布事件
     * @param methodName    触发的事件函数
     * @param clz           事件Class
     * @param args          回调函数的参数
     */
    public static <T> void publishEvent(String methodName, Class<T> clz, final Object... args) {
        synchronized (BusObject.class) {
            try {
                Collection<?> events = getBusEvent(clz);
                Iterator<?> iterator = events.iterator();
                Method[] listenerMethods = clz.getMethods();
                while(iterator.hasNext()) {
                    Object next = iterator.next();
                    for(Method method : listenerMethods) {
                        String name = method.getName();
                        if (!TextUtils.isEmpty(methodName) && !name.equals(methodName)) {
                            continue;
                        }
                        Class<?>[] clzs = method.getParameterTypes();
                        int compare = ParameterTypesComparator.compare(args, clzs);
                        if(compare >= 0) {
                            invokeEvent(next, method, args);
                            //return;
                        }
                    }
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 注册事件监听
     * @param listener	事件对象
     */
    public static <T> void registerListener(final Class<?> listenerClz,T listener) {
        synchronized (BusObject.class) {
            getEventListener(listenerClz).add(listener);
        }
    }

    /**
     * 取消注册事件监听
     * @param listener	事件对象
     */
    public static <T> void unRegisterListener(final Class<?> listenerClz,T listener) {
        synchronized (BusObject.class) {
            getEventListener(listenerClz).remove(listener);
        }
    }

//    /**
//     * 触发事件 (该事件工作在当前线程)
//     * @param listenerClz	事件Class
//     * @param methodName	触发的事件函数
//     * @param args			回调函数的参数
//     */
//    public static void sendListener(final Class<?> listenerClz, String methodName, final Object... args) {
//        sendListener(methodName, listenerClz, args);
//    }

    /**
     * 触发事件监听 (该事件工作在主线程)
     * @param listenerClz	事件监听Class
     * @param args			回调函数的参数
     */
    public static void sendListener(final Class<?> listenerClz, final Object... args) {
        sendListener("", listenerClz, args);
    }

    /**
     * 触发事件监听
     * @param methodName    触发的事件函数
     * @param listenerClz   事件监听Class
     * @param args          回调函数的参数
     */
    public static void sendListener(final String methodName, final Class<?> listenerClz, final Object... args) {
//        execBackground(new Runnable() {
//            @Override
//            public void run() {
                synchronized (BusObject.class) {
                    try {
                        Collection<?> events = getEventListener(listenerClz);
                        Iterator<?> iterator = events.iterator();
                        Method[] listenerMethods = listenerClz.getMethods();
                        while (iterator.hasNext()) {
                            Object next = iterator.next();
                            for (Method method : listenerMethods) {
                                String name = method.getName();
                                if (!TextUtils.isEmpty(methodName) && !name.equals(methodName)) {
                                    continue;
                                }
                                Class<?>[] clzs = method.getParameterTypes();
                                int compare = ParameterTypesComparator.compare(args, clzs);
                                if (compare >= 0) {
                                    invokeListener(next, method, args);
//                                    return;
                                }
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
//            }
//        });
    }

    @SuppressWarnings("unchecked")
    protected static <T> Collection<T> getEventListener(Class<?> clz) {
        Collection<?> collection = mEventListener.get(clz);
        if(collection == null) {
            collection = new ArrayList<Class>();
            mEventListener.put(clz, collection);
        }
        return (Collection<T>) collection;
    }

    @SuppressWarnings("unchecked")
    protected static <T> Collection<T> getBusEvent(Class<?> clz) {
        Collection<?> collection = mBusEvent.get(clz);
        if(collection == null) {
            collection = new ArrayList<Object>();
            mBusEvent.put(clz, collection);
        }
        return (Collection<T>) collection;
    }

    /**
     * 执行工作线程
     * @param runnable
     */
    public static void execBackground(Runnable runnable) {
        new Thread(runnable).start();
    }

    /**
     * 执行主线程
     * @param runnable
     */
    public static void execMainThread(Runnable runnable) {
        if(mainHandler == null) {
            mainHandler = new Handler();
        }
        mainHandler.post(runnable);
    }

    private static void invokeEvent(final Object obj, final Method targetMethod, final Object... args) {
        execBackground(new Runnable() {
            @Override
            public void run() {
                try {
                    String name		= targetMethod.getName();
                    Class<?>[] clzs = targetMethod.getParameterTypes();
                    Method method	= obj.getClass().getMethod(name, clzs);
                    method.invoke(obj, args);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void invokeListener(final Object obj, final Method targetMethod, final Object... args) {
        execMainThread(new Runnable() {
            @Override
            public void run() {
                try {
                    String name = targetMethod.getName();
                    Class<?>[] clzs = targetMethod.getParameterTypes();
                    Method method = obj.getClass().getMethod(name, clzs);
                    method.invoke(obj, args);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
