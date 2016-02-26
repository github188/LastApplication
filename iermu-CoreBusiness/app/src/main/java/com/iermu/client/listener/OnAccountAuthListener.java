package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 获取服务器AccessToken事件
 *
 * Created by wcy on 15/7/23.
 */
public interface OnAccountAuthListener {

    /**
     * 登录成功
     * @param business
     */
    public void onLoginSuccess(Business business);

}
