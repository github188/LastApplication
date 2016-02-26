package com.iermu.client.listener;

import com.iermu.lan.model.ErrorCode;

/**
 * 播放卡录事件
 * <p/>
 * Created by wcy on 15/9/16.
 */
public interface OnOpenPlayCardRecordListener {

    public void onOpenPlayCardRecord(String playUrl, ErrorCode errorCode);

}
