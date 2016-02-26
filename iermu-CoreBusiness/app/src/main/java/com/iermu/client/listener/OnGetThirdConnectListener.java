package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 获取第三方账号信息回调
 * Created by zhoushaopei on 15/12/9.
 */
public interface OnGetThirdConnectListener {

    /**
     * 回调成功｜失败
     * @param business
     */
    public void onGetThirdConnect(Business business, String uid, String bdAccount);
}
