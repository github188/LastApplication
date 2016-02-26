package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 设置摄像机邮件报警开关回调事件
 * Created by zhoushaopei on 15/8/5.
 */
public interface OnSetCamEmailCronListener {

    public void onSetAlarmMial(Business business,boolean isCron);
}
