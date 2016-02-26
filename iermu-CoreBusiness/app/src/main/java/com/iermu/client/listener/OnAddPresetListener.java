package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 添加云台预置点接口
 *
 * Created by zhoushaopei on 15/10/20.
 */
public interface OnAddPresetListener {

    /**
     * 接口回调成功｜失败
     * @param business
     */
    public void onAddPreset(Business business, int preset);
}
