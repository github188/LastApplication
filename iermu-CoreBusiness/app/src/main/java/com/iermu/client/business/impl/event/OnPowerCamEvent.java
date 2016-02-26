package com.iermu.client.business.impl.event;

/**
 * 更新摄像机开关机状态会回调 成功｜失败
 * Created by zhoushaopei on 15/11/20.
 */
public interface OnPowerCamEvent {

    public void onPowerCam(String deviceId);

}
