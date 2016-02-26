package com.iermu.client.business.impl.event;

import com.iermu.client.model.Business;

/**
 * 注销摄像机事件
 *
 * Created by xjy on 15/8/11.
 */
public interface OnDropCamEvent {

    /**
     * 注销摄像机成功
     * @param deviceId
     * @param bus
     */
    public void onDropCamEvent(String deviceId, Business bus);

}
