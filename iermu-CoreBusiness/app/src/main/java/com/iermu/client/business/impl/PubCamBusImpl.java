package com.iermu.client.business.impl;

import com.iermu.client.ErmuBusiness;
import com.iermu.client.IPubCamBusiness;
import com.iermu.client.business.api.CamDeviceApi;
import com.iermu.client.business.api.PubCamApi;
import com.iermu.client.business.api.response.PubCamListResponse;
import com.iermu.client.business.api.response.ViewCamResponse;
import com.iermu.client.business.impl.event.OnAccountTokenEvent;
import com.iermu.client.business.impl.event.OnPubCamChangedEvent;
import com.iermu.client.business.impl.event.OnViewCamEvent;
import com.iermu.client.config.AppConfig;
import com.iermu.client.listener.OnPublicCamChangedListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.PubCamCategory;
import com.iermu.client.util.Logger;
import com.iermu.client.util.StringUtil;
import com.iermu.eventobj.BusObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * 公开摄像头业务
 * <p>
 * Created by zhoushaopei on 15/6/26.
 */
public class PubCamBusImpl extends BaseBusiness implements IPubCamBusiness, OnAccountTokenEvent {

    private Map<String, List<CamLive>> dataMap; // 记录列表数据
    private Map<String, Integer> dataPage;      // 记录分页
    private Map<String, Integer> nextPage; // 记录下一页

    private String accessToken;

    public PubCamBusImpl() {
        dataMap = new HashMap<String, List<CamLive>>();
        dataMap.put(PubCamCategory.VIEW, new ArrayList<CamLive>());
        dataMap.put(PubCamCategory.NEW, new ArrayList<CamLive>());
        dataMap.put(PubCamCategory.RECOMMEND, new ArrayList<CamLive>());

        dataPage = new HashMap<String, Integer>();
        dataPage.put(PubCamCategory.VIEW, 1);
        dataPage.put(PubCamCategory.NEW, 1);
        dataPage.put(PubCamCategory.RECOMMEND, 1);

        nextPage = new HashMap<String, Integer>();
        nextPage.put(PubCamCategory.VIEW, 2);
        nextPage.put(PubCamCategory.NEW, 2);
        nextPage.put(PubCamCategory.RECOMMEND, 2);

        accessToken = ErmuBusiness.getAccountAuthBusiness().getAccessToken();
        BusObject.subscribeEvent(OnAccountTokenEvent.class, this);
    }

    @Override
    public void syncNewCamList(String orderby) {
        dataPage.put(orderby, 1);
        int currentTime = (int) (new Date().getTime() / 1000);
        Logger.d("currentTime:" + currentTime);
        PubCamListResponse response = PubCamApi.getPubCamList(accessToken, getSign(currentTime),
                currentTime + AppConfig.EXPIRE, dataPage.get(orderby), orderby);
        nextPage.put(orderby, response.getNextPageNum());
        Business bus = response.getBusiness();
        switch (bus.getCode()) {
            case BusinessCode.SUCCESS:
                List<CamLive> list = response.getList();
//                dataMap.get(orderby).clear();
//                dataMap.get(orderby).addAll(list);
                dataMap.put(orderby, list);
                break;
            default:
                //TODO 通知界面错误
                break;
        }
        sendListener(OnPublicCamChangedListener.class, bus);
        publishEvent(OnPubCamChangedEvent.class, getCamList(orderby));
    }

    @Override
    public void syncOldCamList(String orderby) {
        dataPage.put(orderby, dataPage.get(orderby) + 1);
        int currentTime = (int) (new Date().getTime() / 1000);
        PubCamListResponse response = PubCamApi.getPubCamList(accessToken, getSign(currentTime),
                currentTime + AppConfig.EXPIRE, dataPage.get(orderby), orderby);
        nextPage.put(orderby, response.getNextPageNum());
        Business bus = response.getBusiness();
        switch (bus.getCode()) {
            case BusinessCode.SUCCESS:
                List<CamLive> list = response.getList();
                appendMimeCams(list, orderby);
                break;
            default:
                //TODO 通知界面错误
                break;
        }
        sendListener(OnPublicCamChangedListener.class, bus);
        publishEvent(OnPubCamChangedEvent.class, getCamList(orderby));
    }

    @Override
    public void viewCam(String deviceId) {
        ViewCamResponse response = CamDeviceApi.viewCam(deviceId, accessToken);
        int viewnum = response.getViewnum();
        publishEvent(OnViewCamEvent.class, deviceId, viewnum);

        synchronized (PubCamBusImpl.class) {
            Collection<List<CamLive>> collection = dataMap.values();
            Iterator<List<CamLive>> iterator = collection.iterator();
            while (iterator.hasNext()) {
                List<CamLive> next = iterator.next();
                Iterator<CamLive> iterator1 = next.iterator();
                while (iterator1.hasNext()) {
                    CamLive next1 = iterator1.next();
                    if (next1.getDeviceId().equals(deviceId)) {
                        next1.setPersonNum(viewnum);
                        return;
                    }
                }
            }
        }
    }

    public List<CamLive> getCamList(String orderby) {
        List<CamLive> lives = dataMap.get(orderby);
        List<CamLive> list = new ArrayList<CamLive>(Arrays.asList(new CamLive[lives.size()]));
        Collections.copy(list, lives);
        return list;
    }

    @Override
    public CamLive getCamLive(String shareId, String uk) {
        Collection<List<CamLive>> camLiveCollections = dataMap.values();
        for (List<CamLive> camLives : camLiveCollections) {
            for (CamLive camLive : camLives) {
                if (camLive.getShareId().equals(shareId) && camLive.getUk().equals(uk)) {
                    return camLive;
                }
            }
        }
        return null;
    }

    @Override
    public int getNextPageNum(String orderby) {
        return nextPage.get(orderby);
    }

    //插入一组最老的摄像机列表
    private void appendMimeCams(List<CamLive> list, String orderby) {
        if (list == null || list.size() <= 0) return;
        List<CamLive> mCamList = dataMap.get(orderby);
        synchronized (PubCamBusImpl.class) {
            Iterator<CamLive> iterator1 = list.iterator();
            while (iterator1.hasNext()) {
                CamLive next1 = iterator1.next();
                String deviceId = next1.getDeviceId();
                boolean contains = false;
                ListIterator<CamLive> iterator2 = mCamList.listIterator();
                while (iterator2.hasNext()) {
                    CamLive next2 = iterator2.next();
                    contains = deviceId.equals(next2.getDeviceId());
                    if (contains) {
                        iterator2.set(next1);
                        break;
                    }
                }
                if (contains) {
                    iterator1.remove();
                }
            }
            mCamList.addAll(list);
        }
    }

    /**
     * 拼接参数sign
     *
     * @return
     */
    private String getSign(int currentTime) {
        String appid = AppConfig.APPID;
        String ak = AppConfig.AK;
        String sk = AppConfig.SK;
        int expire = currentTime + AppConfig.EXPIRE;

        String realSignStr = appid + expire + ak + sk;
        String realSignMd5 = StringUtil.string2MD5(realSignStr);
        String sign = appid + "-" + ak + "-" + realSignMd5;

        return sign;
    }

    @Override
    public void onTokenChanged(String uid, String accessToken, String refreshToken) {
        this.accessToken = accessToken;
    }
}
