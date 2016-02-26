package com.iermu.client.business.impl.runnable;

import com.iermu.client.business.api.StreamMediaApi;
import com.iermu.client.business.api.response.LiveMediaResponse;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.LiveMedia;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.model.constant.LiveType;
import com.iermu.client.util.Logger;
import com.lingyang.sdk.MediaScannerListener;
import com.lingyang.sdk.impl.LYPlatformApi;

/**
 * Created by wcy on 16/1/9.
 */
public class LyyStreamMediaScanner {

    private static boolean stopScanner = false;


    /**
     * 开始扫描羚羊设备流媒体数据
     * @param live
     * @param accessToken
     */
    public static void start(CamLive live, String accessToken) {
        stopScanner = false;
        final String deviceId = live.getDeviceId();
        String shareId  = live.getShareId();
        String uk       = live.getUk();
        LiveMedia media = null;
        Business bus = new Business();

        long startTime  = System.currentTimeMillis();
        while(!bus.isSuccess()
                && !stopScanner
                && System.currentTimeMillis()-startTime<=3000) {
            LiveMediaResponse res = StreamMediaApi.apiLivePlay(deviceId, accessToken, shareId, uk);
            bus = res.getBusiness();
            media = res.getLiveMedia();
        }

        final int status = (media==null)? 0:media.getStatus();
        if(bus.isSuccess() && media!=null) {
            LYPlatformApi.getInstance().addMediaScannerListener(new MediaScannerListener() {
                @Override
                public void mediaScanner(int connect, String devToken) {
                    String path = LYPlatformApi.getInstance().GetPlayPath(devToken);
                    Logger.i("openLiveMediaByLyy ret:" + connect + " playPath: " + path);

                    LiveMedia liveMedia = new LiveMedia();
                    liveMedia.setDeviceId(deviceId);
                    liveMedia.setConnectType(ConnectType.LINYANG);
                    liveMedia.setConnectRet(connect);
                    liveMedia.setStatus(status);
                    liveMedia.setDevToken(devToken);
                    liveMedia.setPlayUrl(path);
//                    appendLiveMediaMap(deviceId, liveMedia);
//                    sendListener(OnLiveMediaListener.class, deviceId);
                }
            });
            if(media.getType().equals(LiveType.P2P)){
                String devToken = media.getDevToken();
                String trackIp  = media.getTrackIp();
                int trackPort   = media.getTrackPort();
                LYPlatformApi.getInstance().StartConnectPrivateMedia(devToken, trackIp, trackPort);
            } else {
                String rtmpURL = media.getPlayUrl();
                LYPlatformApi.getInstance().StartConnectPublicMedia(rtmpURL);
            }
        } else {
            //TODO 加载播放地址出错
        }
    }

    public static void stop(String deviceId) {
        stopScanner = true;
    }
}
