package com.iermu.client.business.impl.setupdev.setup;

/**
 * 添加(安装)设备状态监听事件
 *
 * Created by wcy on 15/8/10.
 */
public interface SetupStatusListener {

    /**
     * 添加(安装)设备状态变化事件
     * @param status
     */
    void onSetupStatusChange(SetupStatus status);


}
