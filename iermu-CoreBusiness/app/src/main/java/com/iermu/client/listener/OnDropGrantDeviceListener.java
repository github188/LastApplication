package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 删除某个用户名下被授权的云摄像头
 *
 * Created by zhoushaopei on 15/9/17.
 */
public interface OnDropGrantDeviceListener {

    public void onDropGrantDevice(Business business);

}
