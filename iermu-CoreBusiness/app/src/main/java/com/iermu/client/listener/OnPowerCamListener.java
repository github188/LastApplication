package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 *  摄像机开关回调事件
 *
 * Created by wcy on 15/7/7.
 */
public interface OnPowerCamListener {


    /**
     * 摄像机开关状态切换回调
     * @param bus
     * @param powerSwitched 开关切换状态
     */
    public void onPowerCam(Business bus, boolean powerSwitched);


}
