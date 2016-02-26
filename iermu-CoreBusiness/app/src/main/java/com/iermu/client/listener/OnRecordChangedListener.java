package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 录像数据更新监听器
 *
 * Created by zhangxq on 15/8/14.
 */
public interface OnRecordChangedListener {
    /**
     * 录像数据更新，包含缩略图
     */
    public void onRecordChanged(Business business);
}
