package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 注册用户回调
 * Created by zhoushaopei on 15/12/1.
 */
public interface OnRegisterListener {

    /**
     * 回调成功｜失败
     * @param business
     */
    public void onRegister(Business business);
}
