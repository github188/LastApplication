package com.iermu.client.model;

/**
 * 对象数据拷贝接口
 *
 * Created by wcy on 15/8/9.
 */
public interface Copyable<T> {

    /**
     * 对象数据拷贝
     * @param source 数据源
     */
    public void fromCopy(T source);


}
