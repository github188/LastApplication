package com.iermu.client.listener;

import com.iermu.client.model.UserInfo;

/**
 * 获取用户信息回调事件
 *
 * Created by xjy on 15/8/27.
 */
public interface OnUserInfoListener {

    public void onUserInfo(UserInfo info);

}
