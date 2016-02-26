package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 创建分享链接
 *
 * Created by zhangxq on 15/8/11.
 */
public interface OnCamShareChangedListener {

    /**
     * 分享链接创建成功后调用
     *
     * @param deviceId
     * @param bus
     */
    public void onShareCreated(String deviceId, Business bus);
}
