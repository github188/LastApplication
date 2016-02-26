package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 获取授权用户列表
 *
 * Created by zhoushaopei on 15/8/14.
 */
public interface OnGrantUserListener {

    public void onGrantUser(Business business, String deviceId, int count);
}
