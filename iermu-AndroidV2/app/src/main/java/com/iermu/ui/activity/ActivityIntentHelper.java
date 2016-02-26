package com.iermu.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.iermu.client.ErmuBusiness;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.fragment.publicchannel.PublicLiveFragment;
import com.iermu.ui.view.AcceptAuthDialog;

/**
 * Activity Intent处理帮助类(解决Intent传递逻辑代码)
 *
 *  第三方调起应用
 *  浏览器调起播放
 *  微信调起授权回调
 *
 * Created by wcy on 16/1/25.
 */
public class ActivityIntentHelper {

    private static final String INTENT_SHAREAUTH_URL = "grant";

    /**
     * 处理Activity Intent
     * @param intent
     */
    public void handleActivityIntent(FragmentActivity activity, Intent intent) {

    }

    private void showAcceptAuth(FragmentActivity activity, String uuName, String deviceName, final String code) {
        final AcceptAuthDialog authDialog = new AcceptAuthDialog(activity, uuName, deviceName);
        authDialog.setClicklistener(new AcceptAuthDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                authDialog.dismiss();
                ErmuBusiness.getShareBusiness().grantShare(code);
            }
            @Override
            public void doCancel() {
                authDialog.dismiss();
            }
        });
        authDialog.show();
    }

    // 第三方分享播放
    private void playShareDev(FragmentActivity activity, Intent intent) {
        Uri uriPath = intent.getData();
        if (uriPath == null) return;

        String scheme = uriPath.getScheme();
        if (scheme == null) return;
        // 分享打开，获取播放地址，直接打开播放画面
        String shareId = uriPath.getQueryParameter("shareid");
        String uk = uriPath.getQueryParameter("uk");
        Fragment fragment = PublicLiveFragment.actionInstance(null, shareId, uk, true);
        BaseFragment.addToBackStack(activity, fragment, true);
    }





}
