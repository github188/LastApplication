package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 删除云台预置点接口
 *
 * Created by zhoushaopei on 15/10/20.
 */
public interface OnDropPresetListener {

    /**
     * 接口回调成功｜失败
     * @param business
     */
    public void onDropPreset(Business business);
}
