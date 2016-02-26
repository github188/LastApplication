package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 登录接口回调
 * Created by zhoushaopei on 15/12/1.
 */
public interface OnLoginListener {

    /**
     * 登录成功｜失败
     * @param business
     */
    public void onLogin(Business business);
}
