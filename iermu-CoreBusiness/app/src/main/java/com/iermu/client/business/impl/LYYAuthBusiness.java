package com.iermu.client.business.impl;

import android.text.TextUtils;

import com.iermu.client.business.dao.AccountWrapper;
import com.iermu.client.business.impl.event.OnLoginSuccessEvent;
import com.iermu.client.business.impl.event.OnLogoutEvent;
import com.iermu.client.model.Account;
import com.iermu.client.util.Logger;
import com.lingyang.sdk.LoginListener;
import com.lingyang.sdk.impl.LYPlatformApi;

/**
 * 羚羊云账号验证业务
 *
 * Created by wcy on 15/8/18.
 */
public class LYYAuthBusiness extends BaseBusiness implements LoginListener, OnLogoutEvent, OnLoginSuccessEvent {

    public LYYAuthBusiness() {
        subscribeEvent(OnLogoutEvent.class, this);
        subscribeEvent(OnLoginSuccessEvent.class, this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                loginCloudService();
            }
        }).start();
    }

    @Override
    public void Online() {
        Logger.i("LYYAuthBus online");
    }

    @Override
    public void OffOnline() {
        Logger.i("LYYAuthBus offonline");
        //logoutCloudService();
    }

    @Override
    public void OnLogoutEvent() {
        logoutCloudService();
    }

    @Override
    public void onLoginSuccess() {
        loginCloudService();
    }


    private void logoutCloudService() {
        LYPlatformApi.getInstance().StopCloudService();
    }

    /** 登录羚羊云SDK */
    private void loginCloudService() {
        //String userToken = "5003061_3222536192_1452400000_00e39ea37914004d90b147dcac33dbc0";
        //String userConfig = "[Config]\r\nIsDebug=1\r\nIsCaptureDev=1\r\nIsPlayDev=1\r\nIsSendBroadcast=0\r\nUdpSendInterval=2\r\n[Tracker]\r\nCount=3\r\nIP1=121.42.156.148\r\nPort1=80\r\nIP2=182.254.149.39\r\nPort2=80\r\nIP3=203.195.157.248\r\nPort3=80\r\n";
        Account account = AccountWrapper.queryAccount();
        if(account == null) return;

        String lyyUToken = account.getLyyUToken();
        String lyyUConfig = account.getLyyUConfig();
        if(!TextUtils.isEmpty(lyyUToken) && !TextUtils.isEmpty(lyyUToken)) {
            LYPlatformApi.getInstance().StartCloudService(lyyUToken, lyyUConfig, this);
        }
    }

}
