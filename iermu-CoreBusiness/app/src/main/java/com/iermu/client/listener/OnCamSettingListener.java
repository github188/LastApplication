package com.iermu.client.listener;

import com.iermu.client.model.Business;
import com.iermu.client.model.constant.CamSettingType;

/**
 *  摄像机设置回调事件
 *
 * Created by wcy on 15/7/7.
 */
public interface OnCamSettingListener {

    public void onCamSetting(CamSettingType type, String deviceId,  Business business);

}
