package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * Created by zhoushaopei on 15/8/13.
 */
public interface OnGetGrantCodeListener {

    public void onGetGrantCode(Business bus, String deviceId, String code);
}
