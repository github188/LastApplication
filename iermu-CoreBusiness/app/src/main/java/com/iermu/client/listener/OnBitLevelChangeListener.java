package com.iermu.client.listener;

import android.os.IInterface;

import com.iermu.client.model.Business;

/**
 * 设置码流
 * Created by zhangxq on 15/9/29.
 */
public interface OnBitLevelChangeListener {

    /**
     * 码流变化时调用
     */
    void onBitLevelChange(Business business, int bitLevel);
}
