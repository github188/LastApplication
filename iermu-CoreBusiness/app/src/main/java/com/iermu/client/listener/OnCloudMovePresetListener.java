package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 根据预置点控制云台转动
 * Created by zhoushaopei on 15/10/20.
 */
public interface OnCloudMovePresetListener {

    /**
     * 接口回调 成功｜失败
     * @param business
     */
    public void onCloudMovePreset(Business business);
}
