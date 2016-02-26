package com.iermu.client.business.impl.event;

import com.iermu.client.model.Business;

/**
 * 删除授权摄像机事件
 *
 * Created by xjy on 15/8/11.
 */
public interface OnDropGrantCamEvent {

    /**
     * 删除授权摄像机成功
     * @param deviceId
     * @param bus
     */
    public void onDropGrantCamEvent(String deviceId, Business bus);

}
