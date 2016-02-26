package com.iermu.client.service;

import android.content.Context;
import android.text.TextUtils;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.util.Logger;

import java.util.List;

/**
 * Created by zhangxq on 15/8/24.
 */
public class BaiduPushReceiver extends PushMessageReceiver {

    public static int errorcode = 1;

    @Override
    public void onBind(Context context, int i, String appId, String userId, String channelId, String requestId) {
        if (i != 0) {
            errorcode = i;
        }
        Logger.d("errorCodePush:"+i+" appId:"+appId+" userId:"+userId+" channelId:"+channelId+" requestId:"+requestId);
        if(TextUtils.isEmpty(userId) || TextUtils.isEmpty(channelId)) {
            return;
        }
        ErmuBusiness.getCamSettingBusiness().startRegisterBaiduPush(userId, channelId);
    }

    @Override
    public void onUnbind(Context context, int i, String s) {
        Logger.d("onUnbind:" + "s=" + s + " i=" + i);
    }

    @Override
    public void onSetTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onDelTags(Context context, int i, List<String> list, List<String> list1, String s) {

    }

    @Override
    public void onListTags(Context context, int i, List<String> list, String s) {

    }

    @Override
    public void onMessage(Context context, String s, String s1) {
        Logger.d("onMessage s=" + s + " :::: s1=" + s1);
        ParsePushMessageHelper.handleBaiduPushMessage(context, s, s1);
    }

    @Override
    public void onNotificationClicked(Context context, String s, String s1, String s2) {
        Logger.i("onNotificationClicked: s=" + s + " s1=" + s1 + " s2=" + s2);
    }

    @Override
    public void onNotificationArrived(Context context, String s, String s1, String s2) {
        Logger.i("onNotificationArrived: s=" + s + " s1=" + s1 + " s2=" + s2);
    }
}
