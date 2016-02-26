package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 修改用户名密码
 * Created by zhoushaopei on 15/12/9.
 */
public interface OnUpdatePasswordListener {

    /**
     * 修改密码成功｜失败
     * @param business
     */
    public void onUpdatePassword(Business business);
}
