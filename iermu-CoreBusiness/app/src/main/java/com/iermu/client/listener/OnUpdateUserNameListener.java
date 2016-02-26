package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 修改用户名回调
 * Created by zhoushaopei on 15/12/9.
 */
public interface OnUpdateUserNameListener {

    /**
     * 修改用户名成功｜失败
     * @param business
     */
    public void onUpdateUserName(Business business);
}
