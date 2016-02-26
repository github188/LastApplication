package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 检测摄像机固件版本升级
 * Created by zhoushaopei on 15/11/20.
 */
public interface OnCheckCamFirmwareListener {

    public void onCheckCamFirmware(Business bus);
}
