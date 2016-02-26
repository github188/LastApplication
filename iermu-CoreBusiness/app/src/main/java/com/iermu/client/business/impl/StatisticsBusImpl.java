package com.iermu.client.business.impl;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IStatisticsBusiness;
import com.iermu.client.business.api.StatisticsApi;
import com.iermu.client.business.impl.event.OnAccountTokenEvent;
import com.iermu.client.business.impl.stat.StatCode;
import com.iermu.client.model.CamDev;
import com.iermu.client.model.CamDevConf;
import com.iermu.client.model.constant.CamDevType;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.util.Logger;
import com.iermu.eventobj.BusObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 统计相关业务
 * <p/>
 * Created by zhangxq on 15/11/24.
 */
public class StatisticsBusImpl extends BaseBusiness implements OnAccountTokenEvent, IStatisticsBusiness {

    private String accessToken;
    private String uid;
    private Map<StatCode, Map<String, String>> statMap = new HashMap<StatCode, Map<String, String>>();

    public StatisticsBusImpl() {
        accessToken = ErmuBusiness.getAccountAuthBusiness().getAccessToken();
        BusObject.subscribeEvent(OnAccountTokenEvent.class, this);
    }

    @Override
    public void statCollectLog(StatCode code, String key, String value) {
        if(statMap.containsKey(code)) {
            Map<String, String> map = statMap.get(code);
            map.put(key, value);
        } else {
            HashMap<String, String> map = new HashMap<String, String>();
            statMap.put(code, map);
            map.put(key, value);
        }
    }

//    public void collectSetupDev(SetupStatus status, ) {
//
//    }

    @Override
    public void pushSetupDevLog() {
        Map<String, String> map = statMap.get(StatCode.SETUPDEV);
        StatisticsApi.apiCustomize(accessToken, map);
    }

    @Override
    public void statStartPlay(String deviceId, int connectType, int connectRet, int status) {
        statStartPlay(deviceId, connectType, connectRet, status, 0);
    }

    @Override
    public void statStartPlay(String deviceId, int connectType, int connectRet, int status, int playerErrorCode) {
        String app = getHandSetInfo();
        String cloud = "";
        String sdk = "";
        int serverStatus = 0;

        if (connectType == ConnectType.BAIDU) {
            cloud = "baidu";
        } else if (connectType == ConnectType.LINYANG) {
            cloud = "lingyang";
        } else if (connectType == ConnectType.OTHER) {
            cloud = "other";
        }
        StatisticsApi.apiStartPlay(deviceId, accessToken, app, cloud, sdk, connectRet, status, serverStatus, playerErrorCode);
    }

    @Override
    public void statSetupDev(CamDev dev, CamDevConf conf, boolean manualMode, int errorCode) {
        String deviceId = dev.getDevID();
        int devType = dev.getDevType();
        int deviceType = dev.getConnectType();  //检测设备平台类型
        String ssid = conf.getSSID();
        String pwd = conf.getWifiPwd();

        String cloud = "";
        String type = "";
        String app = getHandSetInfo();
        if (devType == CamDevType.TYPE_AP) {
            type = manualMode ? "manualap" : "autoap";
        } else if (devType == CamDevType.TYPE_SMART) {
            type = "smartconfig";
        } else if (devType == CamDevType.TYPE_ETH) {
            type = "upnp";
        } else if (devType == CamDevType.TYPE_HIWIFI) {
            type = "hiwifi";
        }
        if (deviceType == ConnectType.BAIDU) {
            cloud = "baidu";
        } else if (deviceType == ConnectType.LINYANG) {
            cloud = "lingyang";
        } else if (deviceType == ConnectType.OTHER) {
            cloud = "other";
        }
        StatisticsApi.apiSetupDev(deviceId, accessToken, uid, type, app, cloud, ssid, pwd, errorCode);
    }

    @Override
    public void statPushFail(String deviceId) {
        String phoneModel = Build.MODEL;
        String phoneBrand = Build.BRAND;
        String phoneSdkVersion = String.valueOf(Build.VERSION.SDK_INT);
        String phoneSysVersion = String.valueOf(Build.VERSION.RELEASE);
        String version = "no version";
        int versionCode = 1;
        PackageManager packageManager = ErmuApplication.getContext().getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(ErmuApplication.getContext().getPackageName(), 0);
            version = packInfo.versionName;
            versionCode = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Logger.d("get packageName faild");
        }
        
        Logger.d("手机型号：" + phoneModel + " | " + "手机品牌：" + phoneBrand + " | " + "手机sdk版本：" + phoneSdkVersion + " | " + "手机系统版本：" + phoneSysVersion);
        StatisticsApi.apiBindBaiduPush(deviceId, accessToken, phoneModel, phoneBrand, phoneSdkVersion, phoneSysVersion, version, versionCode + "");
    }

    @Override
    public void onTokenChanged(String uid, String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.uid = uid;
    }

    private String getHandSetInfo() {
        String handSetInfo =
                "手机型号:" + android.os.Build.MODEL +
                        ",SDK版本:" + android.os.Build.VERSION.SDK +
                        ",系统版本:" + android.os.Build.VERSION.RELEASE +
                        ",软件版本:" + getAppVersionName(ErmuApplication.getContext());
        return handSetInfo;
    }

    private String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            String packageName = context.getPackageName();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }
}
