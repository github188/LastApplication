package com.iermu.client.listener;

import com.iermu.lan.model.ErrorCode;

/**
 * 卡录数据更新监听器
 * <p/>
 * Created by zhangxq on 15/8/14.
 */
public interface OnCardRecordDayChangedListener {

    /**
     * 录像数据更新
     */
    public void onRecordDayChanged(ErrorCode errorCode, int dayNum);
}
