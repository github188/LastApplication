package com.lingyang.sdk.impl;

import com.lingyang.sdk.ILYPlatformApi;
import com.lingyang.sdk.LoginListener;
import com.lingyang.sdk.MediaScannerListener;

/**
 * 羚羊云平台接口策略
 *
 * Created by wcy on 15/10/20.
 */
public class LYPlatformApi implements ILYPlatformApi {

    private boolean isLyySDK = false;

    private static LYPlatformApi platform;

    public static LYPlatformApi getInstance() {
        if(platform == null) {
            synchronized (LYPlatformApi.class) {
                if (platform == null) {
                    platform = new LYPlatformApi();
                }
            }
        }
        return platform;
    }

    @Override
    public void StartCloudService(String userToken, String userConfig, LoginListener listener) {
        if(isLyySDK) {
            //LyySDK.getInstance().StartCloudService(userToken, userConfig, listener);
        } else {
            IErmuSDK.getInstance().StartCloudService(userToken, userConfig, listener);
        }
    }

    @Override
    public void StopCloudService() {
        if(isLyySDK) {
            LyySDK.getInstance().StopCloudService();
        } else {
            IErmuSDK.getInstance().StopCloudService();
        }
    }

    @Override
    public void StartConnectPrivateMedia(String devToken, String trackIp, int trackPort) {
        IErmuSDK.getInstance().StartConnectPrivateMedia(devToken, trackIp, trackPort);
    }

    @Override
    public void StartConnectPublicMedia(String rtmpURL) {
        IErmuSDK.getInstance().StartConnectPublicMedia(rtmpURL);
    }

    @Override
    public void StopConnectMedia() {
        IErmuSDK.getInstance().StopConnectMedia();
    }

    @Override
    public void StartRecordMedia(String diskInfo, String devToken, int fromTime, int toTime, int playTime) {
        IErmuSDK.getInstance().StartRecordMedia(diskInfo, devToken, fromTime, toTime, playTime);
    }

    @Override
    public void StopRecordMedia() {
        IErmuSDK.getInstance().StopRecordMedia();
    }

    @Override
    public String GetPlayPath(String devToken) {
        return IErmuSDK.getInstance().GetPlayPath(devToken);
    }

    @Override
    public String GetRecordPlayPath(String devToken) {
        return IErmuSDK.getInstance().GetRecordPlayPath(devToken);
    }

    public void addMediaScannerListener(MediaScannerListener listener) {
        IErmuSDK.getInstance().addMediaScannerListener(listener);
    }

}
