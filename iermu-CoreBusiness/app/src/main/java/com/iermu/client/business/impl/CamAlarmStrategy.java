package com.iermu.client.business.impl;

import com.iermu.client.ICamAlarmBusiness;

/**
 * Created by wcy on 15/8/3.
 */
public class CamAlarmStrategy extends BaseBusinessStrategy implements ICamAlarmBusiness {

    private ICamAlarmBusiness mBusiness;

    public CamAlarmStrategy(ICamAlarmBusiness business) {
        super(business);
        this.mBusiness = business;
    }

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
