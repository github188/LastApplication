package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 意见反馈接口回调
 * Created by zhoushaopei on 15/12/11.
 */
public interface OnFeedBackListener {

    /**
     * 回调成功｜失败
     * @param business
     */
    public void onFeedBack(Business business);
}
