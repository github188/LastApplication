package com.iermu.client.listener;

import com.iermu.client.model.Business;
import com.iermu.client.model.NewPoster;

/**
 * 获取活动页面回调
 * Created by zhoushaopei on 15/11/26.
 */
public interface OnClientListener {

    /**
     * 接口回调 成功｜失败
     * @param business
     * @param clientLogin
     */
    public void onClient(Business business, NewPoster clientLogin);
}
