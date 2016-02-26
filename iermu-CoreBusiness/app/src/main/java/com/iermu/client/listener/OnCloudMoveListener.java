package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 控制云台转动接口
 *
 * Created by zhoushaopei on 15/10/20.
 */
public interface OnCloudMoveListener {

    /**
     * 接口回调成功｜失败
     * @param business
     */
    public void onCloudMove(Business business, int num);
}
