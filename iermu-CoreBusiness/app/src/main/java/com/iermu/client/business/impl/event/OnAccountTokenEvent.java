package com.iermu.client.business.impl.event;

/**
 * 获取Token业务事件(爱耳目服务器AccessToken)
 *
 * Created by wcy on 15/7/16.
 */
public interface OnAccountTokenEvent {

    /**
     * Token更新
     * @param uid           用户ID
     * @param accessToken   服务器AccessToken
     * @param refreshToken  服务器AccessToken
     */
    public void onTokenChanged(String uid, String accessToken, String refreshToken);

}
