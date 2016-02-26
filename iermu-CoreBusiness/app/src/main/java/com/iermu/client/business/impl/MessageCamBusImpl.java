package com.iermu.client.business.impl;

import com.iermu.client.ErmuBusiness;
import com.iermu.client.IMessageCamBusiness;
import com.iermu.client.business.dao.AlarmImageDataWrapper;
import com.iermu.client.business.dao.CamSettingDataWrapper;
import com.iermu.client.business.impl.event.OnAccountTokenEvent;
import com.iermu.client.business.impl.event.OnDropCamEvent;
import com.iermu.client.business.impl.event.OnMimeCamChangedEvent;
import com.iermu.client.business.impl.event.OnSetupDevEvent;
import com.iermu.client.config.ApiConfig;
import com.iermu.client.listener.OnGetMessageMineCamListListener;
import com.iermu.client.listener.OnMessageChangeListener;
import com.iermu.client.model.AlarmDeviceItem;
import com.iermu.client.model.AlarmImageData;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.CamSettingData;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.model.constant.LiveDataType;
import com.iermu.eventobj.BusObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhoushaopei on 15/6/29.
 */
public class MessageCamBusImpl extends BaseBusiness implements IMessageCamBusiness, OnAccountTokenEvent, OnMimeCamChangedEvent, OnDropCamEvent, OnSetupDevEvent {
    private List<AlarmDeviceItem> deviceItems;
    private Map<String, CamLive> camLiveMap;
    private List<AlarmImageData> imageDatas;
    private String accessToken;
    private String uid;
    private CamSettingDataWrapper camSettingDataWrapper;

    private static final int PAGE_NUM = 30;
    private int queryPosition = 0;

    public MessageCamBusImpl() {
        accessToken = ErmuBusiness.getAccountAuthBusiness().getAccessToken();
        uid = ErmuBusiness.getAccountAuthBusiness().getUid();
        camSettingDataWrapper = new CamSettingDataWrapper();
        camLiveMap = new HashMap<String, CamLive>();
        deviceItems = new ArrayList<AlarmDeviceItem>();
        imageDatas = new ArrayList<AlarmImageData>();
        BusObject.subscribeEvent(OnAccountTokenEvent.class, this);
        subscribeEvent(OnDropCamEvent.class, this);
        subscribeEvent(OnSetupDevEvent.class, this);
        subscribeEvent(OnMimeCamChangedEvent.class, this);
    }

    @Override
    public int getMineCamLiveCount() {
        synchronized (MessageCamBusImpl.class) {
            return getCamLiveMap().size();
        }
    }

    @Override
    public void getMineCamList() {
        deviceItems = new ArrayList<AlarmDeviceItem>();
        synchronized (MessageCamBusImpl.class) {
            Map<String, CamLive> map = getCamLiveMap();
            Collection<CamLive> values = new HashSet<CamLive>(map.values());
            Iterator<CamLive> iterator = values.iterator();
            List<CamSettingData> camSettingDatas = CamSettingDataWrapper.getCamSettingDataList(uid);
            while (iterator.hasNext()) {
                CamLive camLive = iterator.next();
                String deviceId = camLive.getDeviceId();

                long count = AlarmImageDataWrapper.getCountByDeviceId(deviceId);
                CamSettingData settingData = null;
                for (int i = 0; i < camSettingDatas.size(); i++) {
                    CamSettingData camSettingData = camSettingDatas.get(i);
                    if (camSettingData.getDeviceId().equals(deviceId)) {
                        settingData = camSettingData;
                    }
                }

                AlarmDeviceItem deviceItem = new AlarmDeviceItem();
                deviceItem.setDeviceId(deviceId);
                deviceItem.setDeviceName(camLive.getDescription());
                deviceItem.setThumbImageUrl(camLive.getThumbnail());
                if (settingData != null) {
                    deviceItem.setAlarmIsOpen(settingData.getIsAlarmOpen() == 1);
                }
                deviceItem.setCount(count);
                if (count > 0) {
                    AlarmImageData imageData = AlarmImageDataWrapper.getLastImageDatasByDeviceId(camLive.getDeviceId());
                    deviceItem.setImageData(imageData);
                    String alarmtime = imageData.getAlarmtime();
                    String picUrl = ApiConfig.getAlarmPicUrl(accessToken, deviceId, alarmtime);
                    imageData.setImageUrl(picUrl);
                }
                deviceItems.add(deviceItem);
            }
        }
        sendListener(OnGetMessageMineCamListListener.class, deviceItems);
    }

    @Override
    public long getOpendAlarmCamCount() {
        Map<String, CamLive> map = getCamLiveMap();
        Collection<CamLive> values = new HashSet<CamLive>(map.values());
        Iterator<CamLive> iterator = values.iterator();
        List<CamSettingData> camSettingDatas = CamSettingDataWrapper.getCamSettingDataList(uid);
        int count = 0;
        while (iterator.hasNext()) {
            CamLive camLive = iterator.next();
            String deviceId = camLive.getDeviceId();
            for (CamSettingData settingData : camSettingDatas) {
                if (settingData.getDeviceId().equals(deviceId) && settingData.getIsAlarmOpen() == 1) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public void syncNewImageDatas(String deviceId) {
        queryPosition = 0;
        List<AlarmImageData> list = AlarmImageDataWrapper.getImageDatasByDeviceId(deviceId, queryPosition, PAGE_NUM);
        imageDatas = list;
        for (AlarmImageData item : imageDatas) {
            String alarmtime = item.getAlarmtime();
            String picUrl = ApiConfig.getAlarmPicUrl(accessToken, deviceId, alarmtime);
            item.setImageUrl(picUrl);
        }
        queryPosition = list.size();
        sendListener(OnMessageChangeListener.class);
    }

    @Override
    public void syncOldImageDatas(String deviceId) {
        List<AlarmImageData> list = AlarmImageDataWrapper.getImageDatasByDeviceId(deviceId, queryPosition, PAGE_NUM);
        List<AlarmImageData> list1 = new ArrayList<AlarmImageData>(list);
        for (AlarmImageData imageData : list1) {
            boolean isContain = false;
            for (int i = imageDatas.size() - 1; i > 0; i--) {
                AlarmImageData imageData1 = imageDatas.get(i);
                if (imageData.getId() == imageData1.getId()) {
                    isContain = true;
                    break;
                }
            }
            if (isContain) {
                list.remove(imageData);
            } else {
                break;
            }
        }
        for (int i = 0; i < list.size(); i++) {
            AlarmImageData item = list.get(i);
            String alarmtime = item.getAlarmtime();
            String picUrl = ApiConfig.getAlarmPicUrl(accessToken, deviceId, alarmtime);
            item.setImageUrl(picUrl);
            imageDatas.add(item);
        }
        queryPosition = imageDatas.size();
        sendListener(OnMessageChangeListener.class);
    }

    @Override
    public List<AlarmImageData> getImageDatas() {
        return imageDatas;
    }

    @Override
    public void deleteImageDatas(List<Long> ids) {
        List<AlarmImageData> imageDatasCopy = new ArrayList<AlarmImageData>(imageDatas);
        for (int i = imageDatasCopy.size() - 1; i >= 0; i--) {
            AlarmImageData imageData = imageDatasCopy.get(i);
            if (ids.contains(imageData.getId())) {
                imageDatas.remove(i);
                AlarmImageDataWrapper.deleteById(imageData.getId());
            }
        }
        sendListener(OnMessageChangeListener.class);
    }

    @Override
    public void onTokenChanged(String uid, String accessToken, String refreshToken) {
        this.uid = uid;
        this.accessToken = accessToken;
    }

    @Override
    public void onMimeCamChanged(List<CamLive> list) {
        synchronized (MessageCamBusImpl.class) {
            getCamLiveMap().clear();
            for (CamLive live : list) {
                if (live == null
                        || live.getDataType() != LiveDataType.MIME) {
                    continue;//过滤掉羚羊云设备  //|| live.getConnectType() == ConnectType.LINYANG
                }
                String deviceId = live.getDeviceId();
                Map<String, CamLive> map = getCamLiveMap();
                map.put(deviceId, live);
            }
        }
    }

    @Override
    public void onDropCamEvent(String deviceId, Business bus) {
        synchronized (MessageCamBusImpl.class) {
            Map<String, CamLive> map = getCamLiveMap();
            map.remove(deviceId);
        }
    }

    @Override
    public void onSetupDevEvent(CamLive live) {
        synchronized (MessageCamBusImpl.class) {
            if (live == null) return;//过滤掉羚羊云设备 // || live.getConnectType() == ConnectType.LINYANG
            String deviceId = live.getDeviceId();
            Map<String, CamLive> map = getCamLiveMap();
            map.put(deviceId, live);
        }
    }

    private Map<String, CamLive> getCamLiveMap() {
        if (camLiveMap == null) {
            camLiveMap = new HashMap<String, CamLive>();
        }
        return camLiveMap;
    }
}
