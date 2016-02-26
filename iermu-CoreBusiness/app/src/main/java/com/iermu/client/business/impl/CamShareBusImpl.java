package com.iermu.client.business.impl;

import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamShareBusiness;
import com.iermu.client.IMimeCamBusiness;
import com.iermu.client.business.api.CamShareApi;
import com.iermu.client.business.api.response.CreateShareResponse;
import com.iermu.client.business.api.response.GetGrantCodeResponse;
import com.iermu.client.business.api.response.GrantShareResponse;
import com.iermu.client.business.api.response.GrantUsersResponse;
import com.iermu.client.business.api.response.Response;
import com.iermu.client.business.impl.event.OnAccountTokenEvent;
import com.iermu.client.business.impl.event.OnDropGrantCamEvent;
import com.iermu.client.business.impl.event.OnGrantShareEvent;
import com.iermu.client.business.impl.event.OnMineCamShareEvent;
import com.iermu.client.config.ApiConfig;
import com.iermu.client.listener.OnCamShareChangedListener;
import com.iermu.client.listener.OnCancleShareListener;
import com.iermu.client.listener.OnDropGrantDeviceListener;
import com.iermu.client.listener.OnDropGrantUserListener;
import com.iermu.client.listener.OnGetGrantCodeListener;
import com.iermu.client.listener.OnGrantUserListener;
import com.iermu.client.listener.onGrantShareListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.GrantUser;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.model.constant.ShareType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhoushaopei on 15/7/13.
 */
public class CamShareBusImpl extends BaseBusiness implements ICamShareBusiness, OnAccountTokenEvent
                                                //, OnMimeCamChangedEvent
                                                {
    private String accessToken;
    private Map<String, List<GrantUser>> mGrantUserMap = new HashMap<String, List<GrantUser>>();    //{uk:GrantUser}
    private List<String> mPreloadCache = new ArrayList<String>();

    public CamShareBusImpl() {
        accessToken = ErmuBusiness.getAccountAuthBusiness().getAccessToken();
        subscribeEvent(OnAccountTokenEvent.class, this);
        //subscribeEvent(OnMimeCamChangedEvent.class, this);
    }

    @Override
    public void syncGrantUsers(String deviceId) {
        GrantUsersResponse response = CamShareApi.apiGrantUsers(deviceId, accessToken);
        int count = response.getCount();
        Business bus = response.getBusiness();
        switch(bus.getCode()){
        case BusinessCode.SUCCESS:
            addCamCloudMap(deviceId, response.getList());
        default:
            getPreloadCache().remove(deviceId);//清除预加载缓存Key
            break;
        }
        sendListener(OnGrantUserListener.class, bus, deviceId, count);
    }

    @Override
    public void createShare(String deviceId, String introduce, int shareType) {
        IMimeCamBusiness business = ErmuBusiness.getMimeCamBusiness();
        CamLive live = business.getCamLive(deviceId);
        String description = (live!=null)?live.getDescription() : "";

        CreateShareResponse response = CamShareApi.createShare(deviceId, description, introduce, shareType, accessToken);
        Business bus    = response.getBusiness();
        String shareId  = response.getShareId();
        String uk       = response.getUk();

        if (bus.isSuccess() && live != null) {
            live.setShareType(shareType);
            live.setShareId(shareId);
            live.setUk(uk);
        }

        publishEvent(OnMineCamShareEvent.class, live);
        sendListener(OnCamShareChangedListener.class, deviceId, bus);
    }

    @Override
    public void cancleShare(String deviceId) {
        Response response = CamShareApi.cancleShare(accessToken, deviceId);
        IMimeCamBusiness business = ErmuBusiness.getMimeCamBusiness();
        CamLive live = business.getCamLive(deviceId);
        if (live != null) {
            live.setShareType(ShareType.PRIVATE);
            live.setShareId("");
            live.setUk("");
        }
        publishEvent(OnMineCamShareEvent.class, live);
        Business bus = response.getBusiness();
        sendListener(OnCancleShareListener.class, bus);
    }

    @Override
    public void getGrantCode(String deviceId) {
        GetGrantCodeResponse response = CamShareApi.apiGetGrantCode(deviceId, accessToken);
        Business bus = response.getBusiness();
        String code  = response.getCode();
        sendListener(OnGetGrantCodeListener.class, bus, deviceId, code);
    }

    @Override
    public void grantShare(String code) {
        GrantShareResponse response = CamShareApi.apiGrantShare(code, accessToken);
        Business business = response.getBusiness();
        String uid       = response.getUid();
        String user_name = response.getUser_name();
        super.publishEvent(OnGrantShareEvent.class, (business.getCode() == BusinessCode.SUCCESS));
        sendListener(onGrantShareListener.class, business, uid, user_name);
    }

    @Override
    public String getShareLink(String deviceId) {
        IMimeCamBusiness business = ErmuBusiness.getMimeCamBusiness();
        CamLive live = business.getCamLive(deviceId);
        String shareId = "";
        int shareType = 0;
        String uk = "";
        int connectType = ConnectType.BAIDU;
        if(live != null) {
            shareId  = live.getShareId();
            shareType= live.getShareType();
            uk       = live.getUk();
            connectType = live.getConnectType();
        }
//        return (shareType != ShareType.PRIVATE) ?
//                ((connectType==ConnectType.BAIDU)
//                        ? ApiConfig.getShareLink(uk, shareId, shareType)
//                    : ApiConfig.getLYYShareLink(uk, shareId, shareType))
//                : "";
        return ApiConfig.getLYYShareLink(uk, shareId, shareType);
    }

    @Override
    public List<GrantUser> getGrantUser(String deviceId) {
        synchronized (CamShareBusImpl.class) {
            if (getGrantUserMap().size() > 0) {
                List<GrantUser> grantUsers = getGrantUserMap().get(deviceId);
                if (grantUsers != null) {
                    ArrayList<GrantUser> users = new ArrayList<GrantUser>(grantUsers);
                    ArrayList<GrantUser> list = new ArrayList<GrantUser>(Arrays.asList(new GrantUser[users.size()]));
                    Collections.copy(list, users);
                    return list;
                } else {
                    return new ArrayList<GrantUser>();
                }
            } else {
                return new ArrayList<GrantUser>();
            }
        }
    }

    @Override
    public void dropGrantUser(String deviceId, String uk) {
        Response response = CamShareApi.apiDropUser(deviceId, uk, accessToken);
        Business bus = response.getBusiness();
        if (bus.isSuccess()) {
            synchronized (CamShareBusImpl.class) {
                List<GrantUser> list = getGrantUserMap().get(deviceId);
                int size = list.size();
                for (int i=0;i<size;i++) {
                    GrantUser user = list.get(i);
                    String uk1 = user.getUk();
                    if (uk1.equals(uk)){
                        list.remove(i);
                        break;
                    }
                }
            }
        }
        sendListener(OnDropGrantUserListener.class, bus);
    }

    @Override
    public void dropGrantDevice(String deviceId) {
        Response response = CamShareApi.apiDropGrantDevice(deviceId, accessToken);
        Business bus = response.getBusiness();
        publishEvent(OnDropGrantCamEvent.class, deviceId, bus);
        sendListener(OnDropGrantDeviceListener.class, bus);
    }

    @Override
    public void onTokenChanged(String uid, String accessToken, String refreshToken) {
        this.accessToken = accessToken;
    }

//    @Override
//    public synchronized void onMimeCamChanged(List<CamLive> list) {
//        for(int i=0; i<list.size(); i++) {
//            CamLive live    = list.get(i);
//            String deviceId = live.getDeviceId();
//            int dataType    = live.getDataType();
//            Map<String, List<GrantUser>> map = getGrantUserMap();
//            boolean cache   = map.containsKey(deviceId);
//            boolean preloadCache = getPreloadCache().contains(deviceId);
//            if(!preloadCache && !cache && dataType == LiveDataType.MIME) {  //没有预加载Key、没有缓存数据、个人摄像机
//                getPreloadCache().add(deviceId);
//                syncGrantUsers(deviceId);
//            }
//        }
//    }

    private void addCamCloudMap(String deviceId, List<GrantUser> list) {
        if(list == null ) return;
        synchronized (CamShareBusImpl.class) {
            getGrantUserMap().put(deviceId, list);
        }
    }

    private List<String> getPreloadCache() {
        if(mPreloadCache == null) {
            mPreloadCache = new ArrayList<String>();
        }
        return mPreloadCache;
    }
    private Map<String, List<GrantUser>> getGrantUserMap() {
        if(mGrantUserMap == null) {
            mGrantUserMap = new HashMap<String, List<GrantUser>>( );
        }
        return mGrantUserMap;
    }

}
