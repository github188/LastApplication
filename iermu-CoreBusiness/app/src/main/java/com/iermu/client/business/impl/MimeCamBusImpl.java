package com.iermu.client.business.impl;

import android.text.TextUtils;

import com.iermu.client.ErmuBusiness;
import com.iermu.client.IMimeCamBusiness;
import com.iermu.client.IPreferenceBusiness;
import com.iermu.client.business.api.MimeCamApi;
import com.iermu.client.business.api.PubCamApi;
import com.iermu.client.business.api.response.CamMetaResponse;
import com.iermu.client.business.api.response.MimeCamListResponse;
import com.iermu.client.business.dao.AccountWrapper;
import com.iermu.client.business.dao.CamLiveWrapper;
import com.iermu.client.business.impl.event.OnAccountTokenEvent;
import com.iermu.client.business.impl.event.OnDropCamEvent;
import com.iermu.client.business.impl.event.OnDropGrantCamEvent;
import com.iermu.client.business.impl.event.OnGrantShareEvent;
import com.iermu.client.business.impl.event.OnLogoutEvent;
import com.iermu.client.business.impl.event.OnMimeCamChangedEvent;
import com.iermu.client.business.impl.event.OnPowerCamEvent;
import com.iermu.client.business.impl.event.OnSetupDevEvent;
import com.iermu.client.business.impl.event.OnUpdateCamNameEvent;
import com.iermu.client.business.impl.event.OnViewCamEvent;
import com.iermu.client.listener.OnMimeCamChangedErrorListener;
import com.iermu.client.listener.OnMimeCamChangedListener;
import com.iermu.client.listener.OnMyCamCountListener;
import com.iermu.client.model.Account;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.model.constant.LiveDataType;
import com.iermu.client.model.viewmodel.CollectCamItem;
import com.iermu.client.model.viewmodel.MimeCamItem;
import com.iermu.client.util.CamLiveComparator;
import com.iermu.client.util.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * 我的摄像头
 * <p/>
 * Created by zhoushaopei on 15/6/24.
 */
public class MimeCamBusImpl extends BaseBusiness implements IMimeCamBusiness, OnAccountTokenEvent
        , OnLogoutEvent, OnDropCamEvent, OnSetupDevEvent, OnUpdateCamNameEvent
        , OnGrantShareEvent, OnViewCamEvent, OnDropGrantCamEvent, OnPowerCamEvent {

    private List<CamLive> mCamList;//我的摄像列表
    private List<MimeCamItem> mCamItems;
    private String accessToken;
    private int page = 1;           //分页加载页数
    private int nextPage; // 下一页页数
    private String uid;

    public MimeCamBusImpl() {
        super();
        this.mCamList = new ArrayList<CamLive>();
        this.mCamItems = new ArrayList<MimeCamItem>();
        this.accessToken = ErmuBusiness.getAccountAuthBusiness().getAccessToken();
        this.uid = ErmuBusiness.getAccountAuthBusiness().getUid();

        subscribeEvent(OnAccountTokenEvent.class, this);
        subscribeEvent(OnLogoutEvent.class, this);
        subscribeEvent(OnDropCamEvent.class, this);
        subscribeEvent(OnSetupDevEvent.class, this);
        subscribeEvent(OnUpdateCamNameEvent.class, this);
        subscribeEvent(OnGrantShareEvent.class, this);
        subscribeEvent(OnViewCamEvent.class, this);
        subscribeEvent(OnDropGrantCamEvent.class, this);
        subscribeEvent(OnPowerCamEvent.class, this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Account account = AccountWrapper.queryAccount();
                if (account != null) {
                    Logger.i("accountUid:" + account.getUid());
                    List<CamLive> list = CamLiveWrapper.queryCamLive(account.getUid());
                    if (list != null && (list.size() > 0)) {
                        mCamList = list;
                        sendMimeCamChanged();
                        publishEvent(OnMimeCamChangedEvent.class, getCamList());
                    } else {
                        syncNewCamList();
                    }
                }
            }
        }).start();
    }

    @Override
    public void syncNewCamList() {
        this.page = 1;
        MimeCamListResponse response = MimeCamApi.apiDeviceList(ConnectType.BAIDU, page, accessToken);
        this.nextPage = response.getNextPageNum();
        Business bus = response.getBusiness();
        switch (bus.getCode()) {
        case BusinessCode.SUCCESS:
            List<CamLive> list = response.getList();
            insertMimeCams(list);
            CamLiveWrapper.clearCamLive(uid);
            if (list.size() > 0) CamLiveWrapper.insert(uid, list);
            break;
        default:
            //TODO 通知界面错误
            sendListener(OnMimeCamChangedErrorListener.class, bus);
            break;
        }
        sendMimeCamChanged();
        publishEvent(OnMimeCamChangedEvent.class, getCamList());
    }

    public void syncOldCamList() {
        this.page++;
        MimeCamListResponse response = MimeCamApi.apiDeviceList(ConnectType.BAIDU, page, accessToken);
        this.nextPage = response.getNextPageNum();
        Business bus = response.getBusiness();
        switch (bus.getCode()) {
        case BusinessCode.SUCCESS:
            List<CamLive> list = response.getList();
            appendMimeCams(list);
            break;
        default:
            //TODO 通知界面错误
            sendListener(OnMimeCamChangedErrorListener.class, bus);
            break;
        }
        sendMimeCamChanged();
        publishEvent(OnMimeCamChangedEvent.class, getCamList());
    }

    @Override
    public void findCamLive(String deviceId) {
        CamMetaResponse res = PubCamApi.apiCamMeta(deviceId, accessToken, "", "");
        Business bus = res.getBusiness();
        switch (bus.getCode()) {
        case BusinessCode.SUCCESS:
            CamLive live = res.getCamLive();
            appendMimeCam(live);
            break;
        default:
            //TODO 通知界面错误
            break;
        }
        sendMimeCamChanged();
        publishEvent(OnMimeCamChangedEvent.class, getCamList());
    }

    @Override
    public void syncLiveStatus(String deviceId) {
        CamMetaResponse res = PubCamApi.apiCamMeta(deviceId, accessToken, "", "");
        Business bus = res.getBusiness();
        switch (bus.getCode()) {
            case BusinessCode.SUCCESS:
                CamLive item = res.getCamLive();
                if (item == null) return;
                synchronized (MimeCamBusImpl.class) {
                    ListIterator<CamLive> iterator = mCamList.listIterator();
                    while (iterator.hasNext()) {
                        CamLive next = iterator.next();
                        if (deviceId.equals(next.getDeviceId())) {
                            int status = item.getStatus();
                            next.setStatus(status);
                            break;
                        }
                    }
                }
                break;
        }
        sendMimeCamChanged();
        publishEvent(OnMimeCamChangedEvent.class, getCamList());
    }

    @Override
    public CamLive getCamLive(String deviceId) {
        CamLive item = null;
        synchronized (MimeCamBusImpl.class) {
            ListIterator<CamLive> iterator = mCamList.listIterator();
            while (iterator.hasNext()) {
                CamLive next = iterator.next();
                if (deviceId.equals(next.getDeviceId())) {
                    item = next;
                    break;
                }
            }
        }
        return item;
    }

    public List<CamLive> getCamList() {
        synchronized (MimeCamBusImpl.class) {
            List<CamLive> list = new ArrayList<CamLive>(Arrays.asList(new CamLive[mCamList.size()]));
            Collections.copy(list, mCamList);
            return list;
        }
    }

    @Override
    public List<MimeCamItem> getCamItemList() {
        synchronized (MimeCamBusImpl.class) {
            List<MimeCamItem> list = new ArrayList<MimeCamItem>(Arrays.asList(new MimeCamItem[mCamItems.size()]));
            Collections.copy(list, mCamItems);
            return list;
        }
    }

    @Override
    public void getMineCamCount() {
            int count = 0;
            MimeCamListResponse response = MimeCamApi.apiDeviceOfTypeMyList(ConnectType.BAIDU,1,accessToken);
            Business bus = response.getBusiness();
            IPreferenceBusiness pre = ErmuBusiness.getPreferenceBusiness();

            switch (bus.getCode()) {
                case BusinessCode.SUCCESS:
                    count = response.getTotalNum();
                    pre.setMyCamCount(count);
                    break;
                default:
                    count = pre.getMyCamCount();
                    break;
            }
            sendListener(OnMyCamCountListener.class,count);

//            for (int i = 0; i < mCamList.size(); i++) {
//                CamLive live = mCamList.get(i);
//                if (live.getDataType() == LiveDataType.MIME) {
//                    count++;
//                }
//            }
    }

    @Override
    public List<CamLive> getMineCamList() {
        synchronized (MimeCamBusImpl.class) {
            List<CamLive> camLives = new ArrayList<CamLive>();
            for (int i = 0; i < mCamList.size(); i++) {
                CamLive live = mCamList.get(i);
                if (live.getDataType() == LiveDataType.MIME) {
                    camLives.add(live);
                }
            }
            return camLives;
        }
    }

    @Override
    public void deleteCamLive(String deviceId) {
        synchronized (MimeCamBusImpl.class) {
            ListIterator<CamLive> iterator = mCamList.listIterator();
            while (iterator.hasNext()) {
                CamLive next = iterator.next();
                // >= 2 说明是收藏的摄像头
                if (deviceId.equals(next.getDeviceId()) && next.getDataType() >= 2) {
                    mCamList.remove(next);
                    sendMimeCamChanged();
                    break;
                }
            }
        }
    }

    @Override
    public String getCamDescription(String deviceId) {
        String description = "";
        if (mCamList.size() > 0) {
            for (int i = 0; i < mCamList.size(); i++) {
                CamLive camLive = mCamList.get(i);
                if (deviceId.equals(camLive.getDeviceId())) {
                    description = camLive.getDescription();
                }
            }
        }
        return description;
    }

    @Override
    public int getNextPageNum() {
        return this.nextPage;
    }

    @Override
    public void onTokenChanged(String uid, String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.uid = uid;
        List<CamLive> list = CamLiveWrapper.queryCamLive(uid);
        if (list != null && (list.size() > 0)) {
            mCamList = list;
            publishEvent(OnMimeCamChangedEvent.class, getCamList());
            //sendMimeCamChanged();
        } else {
            syncNewCamList();
        }
    }

    @Override
    public void onDropCamEvent(String deviceId, Business bus) {
        if (bus.getCode() == BusinessCode.SUCCESS) {
            Logger.i("注销摄像机成功: " + deviceId);
            IPreferenceBusiness pre = ErmuBusiness.getPreferenceBusiness();
            int count = pre.getMyCamCount();
            count = count>0?count-1:0;
            pre.setMyCamCount(count);
//        sendListener(OnMyCamCountListener.class,count);
            deleteMimeCam(deviceId);
            sendMimeCamChanged();
        }
    }

    @Override
    public void onSetupDevEvent(CamLive live) {
        if (live == null) return;
        IPreferenceBusiness pre = ErmuBusiness.getPreferenceBusiness();
        pre.setMyCamCount(pre.getMyCamCount()+1);
//            sendListener(OnMyCamCountListener.class,pre.getMyCamCount()+1);
        appendMimeCam(live);
        sendMimeCamChanged();
    }

    @Override
    public void onUpdateCamName(String deviceId, String name, boolean success) {
        if (!success) return;
        CamLive live = getCamLive(deviceId);
        if (live != null) live.setDescription(name);
    }

    @Override
    public void onGrantShare(boolean success) {
        if (!success) return;
        MimeCamListResponse res = MimeCamApi.apiGrantDeviceList(ConnectType.BAIDU, accessToken);
        Business bus = res.getBusiness();

        switch (bus.getCode()) {
        case BusinessCode.SUCCESS:
            List<CamLive> list = res.getList();
            appendMimeCams(list);
            break;
        default:
            //TODO 通知界面错误
            break;
        }
        sendMimeCamChanged();
        publishEvent(OnMimeCamChangedEvent.class, getCamList());
    }

    @Override
    public void onViewCamEvent(String deviceId, int num) {
        if (TextUtils.isEmpty(deviceId)) return;
        synchronized (MimeCamBusImpl.class) {
            ListIterator<CamLive> iterator = mCamList.listIterator();
            while (iterator.hasNext()) {
                CamLive next = iterator.next();
                if (deviceId.equals(next.getDeviceId())) {
                    next.setPersonNum(num);
                    break;
                }
            }
        }
    }

    @Override
    public void onDropGrantCamEvent(String deviceId, Business bus) {
        if (bus.getCode() == BusinessCode.SUCCESS) {
            Logger.i("删除摄像机成功: " + deviceId);
            deleteMimeCam(deviceId);
            sendMimeCamChanged();
        }
    }


    @Override
    public void onPowerCam(String deviceId) {
        syncLiveStatus(deviceId);
    }

    @Override
    public void OnLogoutEvent() {
        synchronized (MimeCamBusImpl.class) {
            mCamList.clear();
            mCamItems.clear();
        }
    }

    private void sendMimeCamChanged() {
        //拆分收藏的摄像机数据
        List<CamLive> collectAll = new ArrayList<CamLive>();
        List<CamLive> mimeAll = new ArrayList<CamLive>();
        List<CamLive> authAll = new ArrayList<CamLive>();
        mCamItems.clear();
        synchronized (MimeCamBusImpl.class) {
            ListIterator<CamLive> iterator = mCamList.listIterator();
            while (iterator.hasNext()) {
                CamLive next = iterator.next();
                int dataType = next.getDataType();
                switch (dataType) {
                case 0:
                    mimeAll.add(next);
                    break;
                case 1:
                    authAll.add(next);
                    break;
                case 2://收藏
                    if (!next.isOffline() || next.getConnectType() == ConnectType.LINYANG) {
                        collectAll.add(next);
                    }
                    break;
                }
            }
        }

        //个人
        Collections.sort(mimeAll, new CamLiveComparator<CamLive>());
        Iterator<CamLive> iteratorMime = mimeAll.iterator();
        while (iteratorMime.hasNext()) {
            CamLive next = iteratorMime.next();
            MimeCamItem mimeItem = new MimeCamItem();
            mimeItem.setItemType(MimeCamItem.TYPE_MIME);
            mimeItem.setItem(next);
            mCamItems.add(mimeItem);
        }

        //授权
        Collections.sort(authAll, new CamLiveComparator<CamLive>());
        Iterator<CamLive> iteratorAuth = authAll.iterator();
        while (iteratorAuth.hasNext()) {
            CamLive next = iteratorAuth.next();
            MimeCamItem authorizeItem = new MimeCamItem();
            authorizeItem.setItemType(MimeCamItem.TYPE_AUTHORIZE);
            authorizeItem.setItem(next);
            mCamItems.add(authorizeItem);
        }

        //收藏
        Iterator<CamLive> iterator1 = collectAll.iterator();
        while (iterator1.hasNext()) {
            CamLive next1 = iterator1.next();
            if (iterator1.hasNext()) {
                CamLive next2 = iterator1.next();
                CollectCamItem item = new CollectCamItem(next1, next2);
                mCamItems.add(item);
            } else {
                CollectCamItem item = new CollectCamItem(next1);
                mCamItems.add(item);
            }
        }
        sendListener(OnMimeCamChangedListener.class);
    }

    //插入一组最新的摄像机列表
    private void insertMimeCams(List<CamLive> list) {
        if (list == null || list.size() <= 0) return;
        synchronized (MimeCamBusImpl.class) {
            mCamList.clear();
            Iterator<CamLive> iterator1 = list.listIterator();
            while (iterator1.hasNext()) {
                CamLive next1 = iterator1.next();
                boolean contains = false;

                ListIterator<CamLive> iterator2 = mCamList.listIterator();
                while (iterator2.hasNext()) {
                    CamLive next2 = iterator2.next();
                    contains = next1.getDeviceId().equals(next2.getDeviceId());
                    if (contains) {
                        iterator2.set(next1);
                        break;
                    }
                }
                if (contains) {
                    iterator1.remove();
                }
            }
            mCamList.addAll(0, list);
        }
    }

    //插入一组最老的摄像机列表
    private void appendMimeCams(List<CamLive> list) {
        if (list == null || list.size() <= 0) return;
        synchronized (MimeCamBusImpl.class) {
            Iterator<CamLive> iterator1 = list.iterator();
            while (iterator1.hasNext()) {
                CamLive next1 = iterator1.next();
                String deviceId = next1.getDeviceId();
                int dataType = next1.getDataType();
                boolean contains = false;

                ListIterator<CamLive> iterator2 = mCamList.listIterator();
                while (iterator2.hasNext()) {
                    CamLive next2 = iterator2.next();
                    contains = deviceId.equals(next2.getDeviceId()) && (dataType == next2.getDataType());
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

    private void deleteMimeCam(String deviceId) {
        if (TextUtils.isEmpty(deviceId)) return;
        synchronized (MimeCamBusImpl.class) {
            ListIterator<CamLive> iterator = mCamList.listIterator();
            while (iterator.hasNext()) {
                CamLive next = iterator.next();
                if (deviceId.equals(next.getDeviceId())) {
                    iterator.remove();
                    break;
                }
            }
        }
    }

    private void appendMimeCam(CamLive item) {
        if (item == null) return;
        synchronized (MimeCamBusImpl.class) {
            boolean contains = false;
            String deviceId = item.getDeviceId();
            ListIterator<CamLive> iterator = mCamList.listIterator();
            while (iterator.hasNext()) {
                CamLive next = iterator.next();
                contains = deviceId.equals(next.getDeviceId());
                if (contains) {
                    iterator.set(item);
                    break;
                }
            }
            if (!contains) {
                iterator.add(item);
            }
        }
    }
}
