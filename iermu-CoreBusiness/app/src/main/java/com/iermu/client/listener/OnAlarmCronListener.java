package com.iermu.client.listener;

import com.iermu.client.model.Business;
import com.iermu.client.model.constant.CamSettingType;
import com.iermu.client.model.constant.CronType;

/**
 * 设置摄像机报警定时
 *
 */
public interface OnAlarmCronListener {

    public void onAlarmCron(CamSettingType type, String deviceId,  Business business);

}
