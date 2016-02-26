package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 注册推送通道监听
 *
 * Created by zhangxq on 15/9/30.
 */
public interface OnRegisteListener {

    /**
     * 注册推送通道完成后调用
     */
    public void onRegisteCompleteListener(Business business, boolean isNotice, String message);
}
