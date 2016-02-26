package com.iermu.client.listener;

/**
 * 直播流信息回调事件
 *
 * Created by wcy on 15/7/29.
 */
public interface OnLiveMediaListener {

    /**
     * 直播流信息回调事件
     * @param deviceId
     */
    public void onLiveMediaChanged(String deviceId);

}
