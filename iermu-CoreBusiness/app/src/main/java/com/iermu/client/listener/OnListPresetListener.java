package com.iermu.client.listener;

import com.iermu.client.model.Business;
import com.iermu.client.model.CloudPreset;

import java.util.List;

/**
 * 获取云台预置点列表
 *
 * Created by zhoushaopei on 15/10/20.
 */
public interface OnListPresetListener {

    /**
     * 接口回调成功｜失败
     * @param business
     */
    public void onListPreset(Business business, List<CloudPreset> list, int count);
}
