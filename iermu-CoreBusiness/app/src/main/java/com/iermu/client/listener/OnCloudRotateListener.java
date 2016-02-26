package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 控制云台平扫接口
 *
 * Created by zhoushaopei on 15/10/20.
 */
public interface OnCloudRotateListener {

    /**
     * 接口回调成功｜失败
     * @param business
     */
    public void onCloudRotate(Business business);
}
