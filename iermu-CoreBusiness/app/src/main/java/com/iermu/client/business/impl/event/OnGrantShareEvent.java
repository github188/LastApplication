package com.iermu.client.business.impl.event;

/**
 * 接受授权分享事件
 *
 * Created by zhoushaopei on 15/8/15.
 */
public interface OnGrantShareEvent {


    /**
     * 接受授权邀请分享成功
     * @param success
     */
    public void onGrantShare(boolean success);

}
