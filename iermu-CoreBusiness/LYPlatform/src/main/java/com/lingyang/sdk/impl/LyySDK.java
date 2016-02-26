package com.lingyang.sdk.impl;

import com.lingyang.sdk.LoginListener;
import com.lingyang.sdk.api.CloudOpenAPI;
import com.lingyang.sdk.api.ICloudOpenAPI;


/**
 * Created by wcy on 15/10/20.
 */
public class LyySDK {

    public static LyySDK getInstance() {
        return new LyySDK();
    }

    /**
     * 启动云服务器
     * @param appId
     * @param username
     * @param password
     * @param listener
     */
    public void StartCloudService(String appId, String username, String password, final LoginListener listener) {
        CloudOpenAPI.getInstance().startCloudService(appId, username, password, "", new ICloudOpenAPI.onlineLoginStatesChangeListener() {
            @Override
            public void onUserOnline(String... strings) {
                listener.Online();
            }
            @Override
            public void onUserOffline() {
                listener.OffOnline();
            }
        });
    }

    /**
     * 停止云服务
     */
    public void StopCloudService() {
        CloudOpenAPI.getInstance().stopCloudService();
    }

    /**
     * 获取设备状态
     * @param hashId
     * @return
     */
    public int GetStatus(String hashId) {
        return CloudOpenAPI.getInstance().getDeviceStatus(hashId);
    }

    //获取UserToken
    public String GetUserToken() {
        return CloudOpenAPI.getInstance().getUserToken();
    }

    //获取设备Token
    public String GetCamToken(String hashId) {
        return CloudOpenAPI.getInstance().getDeviceAccessToken(hashId);
    }

}
