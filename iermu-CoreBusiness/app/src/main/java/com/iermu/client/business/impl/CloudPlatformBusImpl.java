package com.iermu.client.business.impl;

import android.content.Context;

import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICloudPaltformBusiness;
import com.iermu.client.IPreferenceBusiness;
import com.iermu.client.business.api.CloudPlatformApi;
import com.iermu.client.business.api.StreamMediaApi;
import com.iermu.client.business.api.response.CloudListPresetResponse;
import com.iermu.client.business.api.response.CloudMoveResponse;
import com.iermu.client.business.api.response.CloudPlatResponse;
import com.iermu.client.business.api.response.LiveMediaResponse;
import com.iermu.client.business.api.response.Response;
import com.iermu.client.business.impl.event.OnAccountTokenEvent;
import com.iermu.client.business.impl.event.OnMimeCamChangedEvent;
import com.iermu.client.business.impl.event.OnSetupDevEvent;
import com.iermu.client.listener.OnAddPresetListener;
import com.iermu.client.listener.OnCheckCloudPlatFormListener;
import com.iermu.client.listener.OnCheckIsRotateListener;
import com.iermu.client.listener.OnCloudMoveListener;
import com.iermu.client.listener.OnCloudMovePresetListener;
import com.iermu.client.listener.OnCloudRotateListener;
import com.iermu.client.listener.OnDropPresetListener;
import com.iermu.client.listener.OnListPresetListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.CloudPlat;
import com.iermu.client.model.CloudPreset;
import com.iermu.client.model.LiveMedia;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.util.Logger;
import com.iermu.lan.cloud.impl.LanDevPlatformImpl;
import com.iermu.lan.model.ErrorCode;
import com.iermu.lan.model.Result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhoushaopei on 15/10/19.
 */
public class CloudPlatformBusImpl extends BaseBusiness implements ICloudPaltformBusiness, OnAccountTokenEvent
        , OnMimeCamChangedEvent, OnSetupDevEvent {

    private Context mContext;
    private String accessToken;
    Map<String, CamLive> camLiveMap = new HashMap<String, CamLive>();
    private IPreferenceBusiness mPreferenceBus;
    private String leftBorder = "1"; //左边界
    private String rightBorder = "2"; //右边界

    public CloudPlatformBusImpl() {
        super();
        this.mContext = ErmuApplication.getContext();
        mPreferenceBus = ErmuBusiness.getPreferenceBusiness();
        this.accessToken = ErmuBusiness.getAccountAuthBusiness().getAccessToken();
        subscribeEvent(OnAccountTokenEvent.class, this);
        subscribeEvent(OnMimeCamChangedEvent.class, this);
        subscribeEvent(OnSetupDevEvent.class, this);
    }


    @Override
    public void cloudMove(String deviceId, int xEnd, int yEnd, int xCenter, int yCenter) {
        String uid = getCamUid(deviceId);
        LanDevPlatformImpl lanDevPlatform = new LanDevPlatformImpl();
        Logger.d("xEnd:" + xEnd + " yEnd:" + yEnd + " xCenter:" + xCenter + " yCenter:" + yCenter);
        Result result = lanDevPlatform.setDevMovePoint(mContext, deviceId, uid, xEnd, yEnd, xCenter, yCenter);
        ErrorCode errorCode = result.getErrorCode();
        Business business = null;
        int num = -1;
        if (errorCode == ErrorCode.SUCCESS) {
            business = new Business();
            business.setCode(BusinessCode.SUCCESS);
            num = result.getResultInt();
        } else if (errorCode == ErrorCode.NETEXCEPT) {
            CloudMoveResponse response = CloudPlatformApi.apiCloudMove(deviceId, xEnd, yEnd, xCenter, yCenter, accessToken);
            String resultStr = response.getResult();
            if (resultStr != null) {
                num = borderMsg(response.getResult());
            }
            business = response.getBusiness();
        } else if (errorCode == ErrorCode.EXECUTEFAILED) {
            business = new Business();
            business.setCode(BusinessCode.LAN_ERROR);
        }
        sendListener(OnCloudMoveListener.class, business, num);
    }

    @Override
    public void cloudMovePreset(String deviceId, int preset) {
        String uid = getCamUid(deviceId);
        LanDevPlatformImpl lanDevPlatform = new LanDevPlatformImpl();
        Result result = lanDevPlatform.toDevPresetPoint(mContext, deviceId, uid, preset);
        ErrorCode errorCode = result.getErrorCode();
        Business business = null;
        if (errorCode == ErrorCode.SUCCESS) {
            business = new Business();
            business.setCode(BusinessCode.SUCCESS);
        } else if (errorCode == ErrorCode.NETEXCEPT) {
            Response response = CloudPlatformApi.apiCloudMovePreset(deviceId, preset, accessToken);
            business = response.getBusiness();
        } else if (errorCode == ErrorCode.EXECUTEFAILED) {
            business = new Business();
            business.setCode(BusinessCode.LAN_ERROR);
        }
        sendListener(OnCloudMovePresetListener.class, business);
    }

    @Override
    public void startCloudRotate(String deviceId) {
        String uid = getCamUid(deviceId);
        String rotate = "auto";
        LanDevPlatformImpl lanDevPlatform = new LanDevPlatformImpl();
        Result result = lanDevPlatform.openDevRotate(mContext, deviceId, uid);
        ErrorCode errorCode = result.getErrorCode();
        Business business = null;
        if (errorCode == ErrorCode.SUCCESS) {
            business = new Business();
            business.setCode(BusinessCode.SUCCESS);
        } else if (errorCode == ErrorCode.NETEXCEPT) {
            Response response = CloudPlatformApi.apiCloudRotate(deviceId, rotate, accessToken);
            business = response.getBusiness();
        } else if (errorCode == ErrorCode.EXECUTEFAILED) {//执行命令失败，不错处理
            business = new Business();
            business.setCode(BusinessCode.LAN_ERROR);
        }
        sendListener(OnCloudRotateListener.class, business);

    }

    @Override
    public void stopCloudRotate(String deviceId) {
        String uid = getCamUid(deviceId);
        String rotate = "stop";
        LanDevPlatformImpl lanDevPlatform = new LanDevPlatformImpl();
        Result result = lanDevPlatform.closeDevRotate(mContext, deviceId, uid);
        ErrorCode errorCode = result.getErrorCode();
        Business business = null;
        if (errorCode == ErrorCode.SUCCESS) {
            business = new Business();
            business.setCode(BusinessCode.SUCCESS);
        } else if (errorCode == ErrorCode.NETEXCEPT) {
            Response response = CloudPlatformApi.apiCloudRotate(deviceId, rotate, accessToken);
            business = response.getBusiness();
        } else if (errorCode == ErrorCode.EXECUTEFAILED) {
            business = new Business();
            business.setCode(BusinessCode.LAN_ERROR);
        }
        sendListener(OnCloudRotateListener.class, business);
    }

    @Override
    public void addPreset(String deviceId, int preset, String title) {
        Response response = CloudPlatformApi.apiAddPreset(deviceId, preset, title, accessToken);
        Business business = response.getBusiness();
        sendListener(OnAddPresetListener.class, business, preset);
    }

    @Override
    public void dropPreset(String deviceId, int preset) {
        Response response = CloudPlatformApi.apiDropPreset(deviceId, preset, accessToken);
        Business business = response.getBusiness();
        sendListener(OnDropPresetListener.class, business);
    }

    @Override
    public void getListPreset(String deviceId) {
        CloudListPresetResponse response = CloudPlatformApi.apiListPreset(deviceId, accessToken);
        int count = response.getCount();
        List<CloudPreset> list = response.getList();
        Business business = response.getBusiness();
        sendListener(OnListPresetListener.class, business, list, count);
    }

    @Override
    public void checkCloudPlatForm(String deviceId) {
        LiveMediaResponse res = StreamMediaApi.apiLivePlay(deviceId, accessToken, "", "");
        LiveMedia liveMedia = res.getLiveMedia();
        boolean b;
        if (liveMedia != null) {
            int status = liveMedia.getStatus();
            b = ((status >> 8) & 1) == 1;
        } else {
            b = false;
        }
        sendListener(OnCheckCloudPlatFormListener.class, res.getBusiness(), b, deviceId);
    }

    @Override
    public void checkIsRotate(String deviceId) {
        String uid = getCamUid(deviceId);
        LanDevPlatformImpl lanDevPlatform = new LanDevPlatformImpl();
        Result result = lanDevPlatform.isRotate(mContext, deviceId, uid);
        ErrorCode errorCode = result.getErrorCode();
        boolean b = false;
        Business business = null;
        if (errorCode == ErrorCode.SUCCESS) {
            b = result.isResultBooean();
            business = new Business();
            business.setCode(BusinessCode.SUCCESS);
        } else if (errorCode == ErrorCode.NETEXCEPT) {
            CloudPlatResponse response = CloudPlatformApi.apiGetPlatInfo(deviceId, accessToken);
            CloudPlat cloudPlat = response.getCloudPlat();
            b = (cloudPlat != null) ? cloudPlat.isPlatRotateStatus() : false;
            business = response.getBusiness();
        } else if (errorCode == ErrorCode.EXECUTEFAILED) {
            business = new Business();
            business.setCode(BusinessCode.LAN_ERROR);
        }
        sendListener(OnCheckIsRotateListener.class, business, b);
    }

    @Override
    public void onTokenChanged(String uid, String accessToken, String refreshToken) {
        this.accessToken = accessToken;
    }

    @Override
    public void onMimeCamChanged(List<CamLive> list) {
        if (list == null) return;
        for (int i = 0; i < list.size(); i++) {
            CamLive camLive = list.get(i);
            String deviceId = camLive.getDeviceId();
            getCamLiveMap().put(deviceId, camLive);
        }
    }

    @Override
    public void onSetupDevEvent(CamLive live) {
        if (live == null) return;
        String deviceId = live.getDeviceId();
        getCamLiveMap().put(deviceId, live);
    }

    private String getCamUid(String deviceId) {
        String uid;
        CamLive camLive = getCamLiveMap().get(deviceId);
        int connectType = camLive.getConnectType();
        if (connectType == ConnectType.BAIDU) {
            uid = ErmuBusiness.getAccountAuthBusiness().getBaiduUid();
        } else {
            uid = ErmuBusiness.getAccountAuthBusiness().getUid();
        }
        return uid;
    }

    private Map<String, CamLive> getCamLiveMap() {
        if (camLiveMap == null) {
            camLiveMap = new HashMap<String, CamLive>();
        }
        return camLiveMap;
    }

    private int borderMsg(String result) {
        char num = result.charAt(result.length() - 1);
        if (num == 'a' || num == 'b' || num == 'c' || num == 'd' || num == 'e' || num == 'f') {
            return -1;
        } else {
            Character c = new Character(num);
            return Integer.parseInt(c.toString());
        }
    }

}
