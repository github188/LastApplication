package com.iermu.client.business.impl.event;

import com.iermu.client.model.CamLive;

/**
 * 注册设备事件
 *
 * Created by wcy on 15/8/11.
 */
public interface OnSetupDevEvent {

    /**
     * 注册摄像机成功
     * @param live  摄像头信息
     */
    public void onSetupDevEvent(CamLive live);

}
