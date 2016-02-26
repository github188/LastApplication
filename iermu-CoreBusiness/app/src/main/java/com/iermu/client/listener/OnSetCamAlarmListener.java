package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * Created by zhoushaopei on 15/8/5.
 */
public interface OnSetCamAlarmListener {

    public void onSetCamAlarm(String deviceId, Business bus);
}
