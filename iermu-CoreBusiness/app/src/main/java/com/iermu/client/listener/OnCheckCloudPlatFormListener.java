package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 检测当前设备是否有云平台
 *
 * Created by zhoushaopei on 15/10/21.
 */
public interface OnCheckCloudPlatFormListener {

    /**
     * 接口回调 成功｜失败
     * @param b
     */
    public void onCheckCloudPlatForm(Business business, boolean b, String deviceId);
}
