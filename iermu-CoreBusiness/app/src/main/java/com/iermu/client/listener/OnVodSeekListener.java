package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 时间校准回调
 *
 * Created by zhangxq on 15/11/17.
 */
public interface OnVodSeekListener {

    /**
     * 校准时间后调用
     *
     * @param business
     */
    public void onVodSeek(int oldStartTime, int trueStartTime, Business business, int num);
}
