package com.iermu.client.business.impl;

import com.iermu.client.ICamAlarmBusiness;

/**
 * Created by wcy on 16/1/6.
 */
public class CamAlarmBusImpl extends BaseBusiness implements ICamAlarmBusiness {


    @Override
    public void setBaiduPushMessage(String message) {

    }

    @Override
    public void setGetuiPushMessage(String payload, String taskId, String messageId) {

    }

    @Override
    public boolean isOpenAlarmPush(String deviceId) {
        return false;
    }
}
