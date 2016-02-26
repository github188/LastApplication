package com.iermu.client.business.impl;

import android.content.Context;
import android.text.TextUtils;

import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IStreamMediaBusiness;
import com.iermu.client.business.api.StreamMediaApi;
import com.iermu.client.business.api.response.LiveMediaResponse;
import com.iermu.client.business.api.response.VodSeekResponse;
import com.iermu.client.business.dao.CamSettingDataWrapper;
import com.iermu.client.business.impl.event.OnAccountTokenEvent;
import com.iermu.client.business.impl.event.OnLogoutEvent;
import com.iermu.client.business.impl.event.OnMimeCamChangedEvent;
import com.iermu.client.business.impl.event.OnPubCamChangedEvent;
import com.iermu.client.business.impl.event.OnSetupDevEvent;
import com.iermu.client.business.impl.runnable.BaiduStreamMediaScanner;
import com.iermu.client.business.impl.runnable.LyyStreamMediaScanner;
import com.iermu.client.config.ApiConfig;
import com.iermu.client.listener.OnLiveMediaListener;
import com.iermu.client.listener.OnOpenPlayCardRecordListener;
import com.iermu.client.listener.OnOpenPlayRecordListener;
import com.iermu.client.listener.OnVodSeekListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamInfo;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.LiveMedia;
import com.iermu.client.model.RecordMedia;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.model.constant.LiveType;
import com.iermu.client.model.constant.ShareType;
import com.iermu.client.util.DateUtil;
import com.iermu.client.util.Logger;
import com.iermu.lan.NasDevApi;
import com.iermu.lan.cloud.impl.LanDevPlatformImpl;
import com.iermu.lan.model.CamRecord;
import com.iermu.lan.model.ErrorCode;
import com.iermu.lan.model.Result;
import com.iermu.lan.nas.impl.NasDevImpl;
import com.lingyang.sdk.MediaScannerListener;
import com.lingyang.sdk.impl.LYPlatformApi;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 流媒体业务
 * 1. 百度设备
 * 获取个人摄像头直播地址
 * 获取公共摄像机直播shareId、uk
 * <p>
 * 播放状态不正常:
 * 重新获取播放地址
 * <p>
 * 2. 羚羊云设备
 * 连接摄像机
 * 获取摄像机状态
 * 状态正常: 获取直播地址(封装的播放器Jar 里的lyy)
 * <p>
 * 播放过程中状态不正常:
 * 重连摄像机
 * 获取摄像机状态
 * 状态正常: 获取直播地址(封装的播放器Jar 里的lyy)
 * <p>
 * 接口:
 * 同步个人摄像头直播地址
 * 成功, 返回直播地址
 * <p>
 * Created by wcy on 15/7/29.
 */
public class StreamMediaBusiness extends BaseBusiness implements IStreamMediaBusiness, OnAccountTokenEvent
        , OnLogoutEvent, OnMimeCamChangedEvent, OnPubCamChangedEvent
        , OnSetupDevEvent {

    private String accessToken;
    private Context context;
    private Map<String, CamLive> mLiveMap;    //{deviceId, CamLive}
    private Map<String, LiveMedia> liveMap;   //{deviceId, LiveMedia}
    private Map<String, String> lyyRtmp;   //{deviceId, rtmpURL}

    public StreamMediaBusiness() {
        this.mLiveMap = new HashMap<String, CamLive>();
        this.liveMap = new HashMap<String, LiveMedia>();
        this.lyyRtmp = new HashMap<String, String>();
        accessToken = ErmuBusiness.getAccountAuthBusiness().getAccessToken();
        context = ErmuApplication.getContext();
        subscribeEvent(OnAccountTokenEvent.class, this);
        subscribeEvent(OnLogoutEvent.class, this);
        subscribeEvent(OnMimeCamChangedEvent.class, this);
        subscribeEvent(OnPubCamChangedEvent.class, this);
        subscribeEvent(OnSetupDevEvent.class, this);
    }

    @Override
    public void reloadLiveMedia(String deviceId) {
        Map<String, CamLive> map = getCamLiveMap();
        if (!map.containsKey(deviceId)) {
            throw new RuntimeException("Not find the device.");
        }

        CamLive live = map.get(deviceId);
        int connectType = live.getConnectType();
        int shareType = live.getShareType();
        if (connectType == ConnectType.BAIDU) {
            BaiduStreamMediaScanner.start(live, accessToken);
        } else if (connectType == ConnectType.LINYANG
                && (shareType == ShareType.PUB_HAVCLOUD
                || shareType == ShareType.PUB_NOTCLOUD
                || shareType == ShareType.PRI_NOTCLOUD
                || shareType == ShareType.PRI_HAVCLOUD)) {
            loaderPubLiveMediaByLyy(live);
            connectCamByLyy(live, false);
        } else if (connectType == ConnectType.LINYANG) {
            connectCamByLyy(live, false);
        }
    }

    @Override
    public void openLiveMedia(String deviceId) {
        Logger.i("StreamMediaBusiness", "openLiveMedia " + deviceId + " "+System.currentTimeMillis());
        Map<String, CamLive> map = getCamLiveMap();
        if (!map.containsKey(deviceId)) {
            throw new RuntimeException("Not find the device.");
        }

        CamLive live = map.get(deviceId);
        int connectType = live.getConnectType();
        int shareType = live.getShareType();
        if (connectType == ConnectType.BAIDU) {
            Map<String, LiveMedia> mediaMap = getLiveMediaMap();
            LiveMedia media = mediaMap.get(deviceId);
            if (media != null) {
                sendListener(OnLiveMediaListener.class, deviceId);
                return;
            }
            LanDevPlatformImpl platform = new LanDevPlatformImpl();
            String uid = ErmuBusiness.getAccountAuthBusiness().getUid();
            String baiduUid = ErmuBusiness.getAccountAuthBusiness().getBaiduUid();
            CamInfo info = CamSettingDataWrapper.getCamSettingInfo(uid, deviceId);
            String version = (info!=null) ? info.getFirmware() : "";
            Result lanPlayUrl = platform.getLanPlayUrl(ErmuApplication.getContext(), deviceId, baiduUid,version);
            if (lanPlayUrl.getErrorCode() == ErrorCode.SUCCESS) {
                String resultString = lanPlayUrl.getResultString();
                LiveMedia liveMedia = new LiveMedia();
                liveMedia.setLanRtmp(true);
                liveMedia.setDeviceId(deviceId);
                liveMedia.setConnectType(ConnectType.BAIDU);
                liveMedia.setStatus(live.getStatus());
                liveMedia.setPlayUrl(resultString);
                appendLiveMediaMap(deviceId, liveMedia);
                sendListener(OnLiveMediaListener.class, deviceId);
            } else {
                BaiduStreamMediaScanner.start(live, accessToken);
            }
        } else if (connectType == ConnectType.LINYANG
                && (shareType == ShareType.PUB_HAVCLOUD
                || shareType == ShareType.PUB_NOTCLOUD
                || shareType == ShareType.PRI_NOTCLOUD
                || shareType == ShareType.PRI_HAVCLOUD)) {
            loaderPubLiveMediaByLyy(live);
            connectCamByLyy(live, false);
        } else if (connectType == ConnectType.LINYANG) {
            connectCamByLyy(live, false);
//            LyyStreamMediaScanner.start();
        }
    }

    @Override
    public void openPubLiveMedia(String deviceId, String shareId, String uk) {
        Logger.i("StreamMediaBusiness", "openPubLiveMedia " + deviceId + " "+System.currentTimeMillis());
        Map<String, CamLive> map = getCamLiveMap();
        if (!map.containsKey(deviceId)) {
            throw new RuntimeException("Not find the device.");
        }
        CamLive live = map.get(deviceId);
        int connectType = live.getConnectType();
        if (connectType == ConnectType.BAIDU) {
            Map<String, LiveMedia> mediaMap = getLiveMediaMap();
            LiveMedia media = mediaMap.get(deviceId);
            if (media != null) {
                sendListener(OnLiveMediaListener.class, deviceId);
                return;
            }
            LanDevPlatformImpl platform = new LanDevPlatformImpl();
            String uid = ErmuBusiness.getAccountAuthBusiness().getUid();
            String baiduUid = ErmuBusiness.getAccountAuthBusiness().getBaiduUid();
            CamInfo info = CamSettingDataWrapper.getCamSettingInfo(uid, deviceId);
            String version = (info!=null) ? info.getFirmware() : "";
            Result lanPlayUrl = platform.getLanPlayUrl(ErmuApplication.getContext(), deviceId, baiduUid,version);
            if (lanPlayUrl.getErrorCode() == ErrorCode.SUCCESS) {
                String resultString = lanPlayUrl.getResultString();
                LiveMedia liveMedia = new LiveMedia();
                liveMedia.setLanRtmp(true);
                liveMedia.setDeviceId(deviceId);
                liveMedia.setConnectType(ConnectType.BAIDU);
                liveMedia.setStatus(live.getStatus());
                liveMedia.setPlayUrl(resultString);
                appendLiveMediaMap(deviceId, liveMedia);
                sendListener(OnLiveMediaListener.class, deviceId);
            } else {
                BaiduStreamMediaScanner.start(live, accessToken);
            }
        } else if (connectType == ConnectType.LINYANG) {
            loaderPubLiveMediaByLyy(live);
            connectCamByLyy(live, true);
        }
    }

    @Override
    public void closeLiveMedia(String deviceId) {
        Map<String, CamLive> map = getCamLiveMap();
        if (!map.containsKey(deviceId)) {
            throw new RuntimeException("Not find the device.");
        }

        CamLive live = map.get(deviceId);
        int connectType = live.getConnectType();
        //目前仅有羚羊云设备需要关闭
        if (connectType == ConnectType.BAIDU) {
            //xx
        } else if (connectType == ConnectType.LINYANG) {
            LyyStreamMediaScanner.stop(deviceId);
            LYPlatformApi.getInstance().StopConnectMedia();
        }
    }

    @Override
    public void openPlayRecord(String deviceId, final int startTime, final int endTime, CamRecord record) {
        Map<String, CamLive> map = getCamLiveMap();
        if (!map.containsKey(deviceId)) {
            throw new RuntimeException("Not find the device.");
        }
        CamLive live = map.get(deviceId);
        int connectType = live.getConnectType();
        String diskInfo = record.getDiskInfo();
        final RecordMedia media = new RecordMedia();
        media.setDeviceId(deviceId);
        media.setConnectType(connectType);
        media.setCamRecord(record);

        if (connectType == ConnectType.BAIDU) {
            String recordUrl = ApiConfig.getBaiduRecordUrl(accessToken, deviceId, startTime + DateUtil.TIME_ZONE_OFFECT, endTime + DateUtil.TIME_ZONE_OFFECT);
            Logger.d("recordUrl:" + recordUrl);
            media.setPlayUrl(recordUrl);
            sendListener(OnOpenPlayRecordListener.class, media);
        } else if (connectType == ConnectType.LINYANG) {
            if(getLiveDevTokenMap().containsKey(deviceId)) {
                LiveMedia liveMedia = getLiveDevTokenMap().get(deviceId);
                if(liveMedia.isInvalidToken()) {
                    LiveMediaResponse res = StreamMediaApi.apiLivePlay(deviceId, accessToken, "", "");
                    liveMedia = res.getLiveMedia();
                    getLiveDevTokenMap().put(deviceId, liveMedia);
                }
            } else {
                LiveMediaResponse res = StreamMediaApi.apiLivePlay(deviceId, accessToken, "", "");
                LiveMedia liveMedia = res.getLiveMedia();
                getLiveDevTokenMap().put(deviceId, liveMedia);
            }

            LYPlatformApi.getInstance().addMediaScannerListener(new MediaScannerListener() {
                @Override
                public void mediaScanner(int connect, String devToken) {
                    Logger.d("IErmuSDK.java", ">>>>>>> startTime:" + startTime + " " + "endTime:" + endTime + " connect:" + connect);
                    String path = LYPlatformApi.getInstance().GetRecordPlayPath(devToken);
                    media.setPlayUrl(path);
                    media.setStatus(connect);
                    media.setConnectRet(connect);
                    sendListener(OnOpenPlayRecordListener.class, media);
                }
            });

            if(getLiveDevTokenMap().containsKey(deviceId)) {
                LiveMedia liveMedia = getLiveDevTokenMap().get(deviceId);
                String devToken = liveMedia.getDevToken();
                LYPlatformApi.getInstance().StartRecordMedia(diskInfo, devToken, startTime, endTime, startTime);
            } else {
                sendListener(OnOpenPlayRecordListener.class, media);
            }
        }
    }
    private Map<String, LiveMedia> liveDevToken = new HashMap<String, LiveMedia>();
    private Map<String, LiveMedia> getLiveDevTokenMap() {
        if(liveDevToken == null) {
            liveDevToken = new HashMap<String, LiveMedia>();
        }
        return liveDevToken;
    }

    @Override
    public void openCardPlayRecord(String deviceId, int startTime, int endTime) {
        NasDevApi nasDevApi = new NasDevImpl();
        String startTimeStr = DateUtil.formatDate(new Date(startTime * 1000l), DateUtil.FORMAT_ONE);
        String endTimeStr = DateUtil.formatDate(new Date(endTime * 1000l), DateUtil.FORMAT_ONE);
        Result result = nasDevApi.startNasPlay(context, deviceId, ErmuBusiness.getAccountAuthBusiness().getBaiduUid(), startTimeStr, endTimeStr);
        ErrorCode errorCode = result.getErrorCode();
        String playUrl = result.getResultString();
        sendListener(OnOpenPlayCardRecordListener.class, playUrl, errorCode);
    }

    @Override
    public void closeRecord(String deviceId) {
        Map<String, CamLive> map = getCamLiveMap();
        if (!map.containsKey(deviceId)) {
            Logger.e("Not find the device.");
//            throw new RuntimeException("Not find the device.");
            return;
        }
        CamLive live = map.get(deviceId);
        if (live.getConnectType() == ConnectType.LINYANG) {
            LYPlatformApi.getInstance().StopRecordMedia();
        }
    }

    @Override
    public LiveMedia getLiveMedia(String deviceId) {
        LiveMedia media = BaiduStreamMediaScanner.search(deviceId);
        if (media == null) {
            return getLiveMediaMap().get(deviceId);
        }
        return media;
    }

    @Override
    public void vodSeek(String deviceId, int oldStartTime, int num) {
        VodSeekResponse response = StreamMediaApi.vodSeek(deviceId, accessToken, oldStartTime);
        Business bus = response.getBusiness();
        sendListener(OnVodSeekListener.class, response.getOldStartTime(), response.getTrueStartTime(), bus, num);
    }

    @Override
    public void onTokenChanged(String uid, String accessToken, String refreshToken) {
        this.accessToken = accessToken;
    }

    @Override
    public void onMimeCamChanged(List<CamLive> list) {
        synchronized (StreamMediaBusiness.class) {
            Iterator<CamLive> iterator = list.iterator();
            while (iterator.hasNext()) {
                CamLive next = iterator.next();
                String deviceId = next.getDeviceId();
                getCamLiveMap().put(deviceId, next);
            }
        }
    }

    @Override
    public void onPubCamChanged(List<CamLive> list) {
        synchronized (StreamMediaBusiness.class) {
            Iterator<CamLive> iterator = list.iterator();
            while (iterator.hasNext()) {
                CamLive next = iterator.next();
                String deviceId = next.getDeviceId();
                getCamLiveMap().put(deviceId, next);
                //优化羚羊云直播地址加载策略(羚羊云公开摄像机直播地址是不变化的、可缓存)
                if (next.getConnectType() == ConnectType.LINYANG) {
                    loaderPubLiveMediaByLyy(next);
                }
            }
        }
    }

    @Override
    public void onSetupDevEvent(CamLive live) {
        if (live == null) return;
        String deviceId = live.getDeviceId();
        getCamLiveMap().put(deviceId, live);
    }

    @Override
    public void OnLogoutEvent() {
        getCamLiveMap().clear();
        getLiveMediaMap().clear();
    }

    private void loaderPubLiveMediaByLyy(CamLive live) {
        Map<String, String> map = getLyyRtmpMap();
        String deviceId = live.getDeviceId();
        String shareId  = live.getShareId();
        String uk       = live.getUk();
        String rtmp = map.get(deviceId);
        if (TextUtils.isEmpty(rtmp) || !isRtmpLive(rtmp)) {
            LiveMediaResponse res = StreamMediaApi.apiLivePlay(deviceId, accessToken, shareId, uk);
            Business bus = res.getBusiness();
            if (bus.isSuccess()) {
                LiveMedia media = res.getLiveMedia();
                rtmp = media.getPlayUrl();
                if (TextUtils.isEmpty(rtmp)) return;
                map.put(deviceId, rtmp);
            } else {
                //TODO 加载直播地址失败
            }
        }
    }

    private void connectCamByLyy(CamLive live, boolean isPub) {
        final String deviceId = live.getDeviceId();
        //final String connectCid = live.getConnectCid();
        String shareId  = isPub ? live.getShareId() : "";
        String uk       = isPub ? live.getUk() : "";
        LiveMediaResponse res = StreamMediaApi.apiLivePlay(deviceId, accessToken, shareId, uk);
        //Business bus = res.getBusiness();
        final LiveMedia media = res.getLiveMedia();

        LYPlatformApi.getInstance().addMediaScannerListener(new MediaScannerListener() {
            @Override
            public void mediaScanner(int connect, String devToken) {
                String path = LYPlatformApi.getInstance().GetPlayPath(devToken);
                Logger.i("openLiveMediaByLyy ret:" + connect + " playPath: " + path);

                LiveMedia liveMedia = new LiveMedia();
                liveMedia.setDeviceId(deviceId);
                liveMedia.setConnectType(ConnectType.LINYANG);
                liveMedia.setConnectRet(connect);
                liveMedia.setStatus((media==null)?0: media.getStatus());
                liveMedia.setDevToken(devToken);
                liveMedia.setPlayUrl(path);
                appendLiveMediaMap(deviceId, liveMedia);
                sendListener(OnLiveMediaListener.class, deviceId);
            }
        });
        if((media!=null) && media.getType().equals(LiveType.RTMP)) {
            String rtmpURL = media.getPlayUrl();
            LYPlatformApi.getInstance().StartConnectPublicMedia(rtmpURL);
        } else if((media!=null) && media.getType().equals(LiveType.P2P)){
            String devToken = media.getDevToken();
            String trackIp  = media.getTrackIp();
            int trackPort   = media.getTrackPort();
            LYPlatformApi.getInstance().StartConnectPrivateMedia(devToken, trackIp, trackPort);
        } else {
            sendListener(OnLiveMediaListener.class, deviceId);
        }
    }

    /**
     * 判断是否是Rtmp地址
     * @return
     */
    public boolean isRtmpLive(String rtmpUrl) {
        return rtmpUrl.startsWith("rtmp://");
    }

    private void appendLiveMediaMap(String deviceId, LiveMedia media) {
        if (TextUtils.isEmpty(deviceId) || media == null) return;
        getLiveMediaMap().put(deviceId, media);
    }

    private Map<String, LiveMedia> getLiveMediaMap() {
        if (liveMap == null) {
            liveMap = new HashMap<String, LiveMedia>();
        }
        return liveMap;
    }

    private Map<String, String> getLyyRtmpMap() {
        if(lyyRtmp == null) {
            lyyRtmp = new HashMap<String, String>();
        }
        return lyyRtmp;
    }
    private Map<String, CamLive> getCamLiveMap() {
        if (mLiveMap == null) {
            mLiveMap = new HashMap<String, CamLive>();
        }
        return mLiveMap;
    }

}
