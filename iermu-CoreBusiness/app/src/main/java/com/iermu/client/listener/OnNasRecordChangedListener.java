package com.iermu.client.listener;

import com.iermu.lan.model.ErrorCode;

/**
 * nas录像数据更新监听器
 * <p>
 * Created by zhangxq on 15/8/14.
 */
public interface OnNasRecordChangedListener {
    /**
     * 录像数据更新
     */
    public void onRecordChanged(ErrorCode errorCode);
}
