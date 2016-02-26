package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 完善用户资料
 * Created by zhoushaopei on 15/12/9.
 */
public interface OnCompleteUserInfoListener {

    /**
     * 修改密码成功｜失败
     * @param business
     */
    public void onPerfectUserInfo(Business business);
}
