package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 把云摄像头授权给其他用户
 * Created by zhoushaopei on 15/8/14.
 */
public interface onGrantShareListener {

    public void onGrantShare(Business bus, String uid, String userName);
}
