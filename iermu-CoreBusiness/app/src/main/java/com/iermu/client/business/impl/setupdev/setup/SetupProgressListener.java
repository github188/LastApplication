package com.iermu.client.business.impl.setupdev.setup;

/**
 * 添加(安装)设备进度监听事件
 *
 * Created by wcy on 15/8/10.
 */
public interface SetupProgressListener {

    /**
     * 添加(安装)摄像头进度
     * @param progress
     */
    public void onProgress(int progress);

}
