package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 *  重启摄像机回调事件
 *
 * Created by wcy on 15/7/7.
 */
public interface OnRestartCamListener {


    /**
     * 重启摄像机
     * @param bus
     */
    public void onRestartCam(Business bus);


}
