package com.iermu.client.business.api;

import android.text.TextUtils;

import com.iermu.apiservice.service.StreamMediaService;
import com.iermu.client.business.api.response.LiveMediaResponse;
import com.iermu.client.business.api.response.VodSeekResponse;
import com.iermu.client.config.ApiConfig;
import com.iermu.client.util.Logger;
import com.iermu.client.util.LoggerUtil;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by guixiaomei on 15/7/29.
 */
public class StreamMediaApi extends BaseHttpApi {

    @Inject
    static StreamMediaService mApiService;

    /**
     * 获取直播信息
     *
     * @param deviceId    设备ID
     * @param accessToken AccessToken
     * @param shareId     公开分享ID
     * @param uk          用户ID
     * @return
     */
    public static LiveMediaResponse apiLivePlay(String deviceId, String accessToken, String shareId, String uk) {
        LiveMediaResponse response;
        String method = "liveplay";
        long startTime = System.currentTimeMillis();
        try {
            Logger.i("apiLivePlay start:" + deviceId + " "+startTime);
            String res = mApiService.getLivePlay(method, deviceId, accessToken, shareId, uk);
            response = LiveMediaResponse.parseResponse(deviceId, res);
        } catch (Exception e) {
            LoggerUtil.e("apiLivePlay", e);
            response = LiveMediaResponse.parseResponseError(e);
        }
        long endTime = System.currentTimeMillis();
        Logger.i("apiLivePlay start:"+deviceId+ " "+endTime+" 耗时:"+(endTime-startTime)+" \r\n");
        return response;
    }

    /**
     * 请求百度直播地址
     * @param deviceId
     * @param accessToken
     * @param shareId
     * @param uk
     * @return
     */
    public static LiveMediaResponse apiBaiduLivePlay(String deviceId, String accessToken, String shareId, String uk) {
        LiveMediaResponse response;
        Map<String, String> params = new HashMap<String, String>();
        params.put("method", "liveplay");
        String baseUrl = ApiConfig.BAIDU_URLDEV;
        if(!TextUtils.isEmpty(shareId) && !TextUtils.isEmpty(uk)){ // share
            params.put("shareid", shareId);
            params.put("uk", uk);//509623807
        } else { // my cam
            params.put("access_token", accessToken);
            params.put("deviceid", deviceId);
            params.put("device_type", "1");
        }

        long startTime = System.currentTimeMillis();
        Logger.i("apiBaiduLivePlay start:" + deviceId + " "+startTime);
        try {
            URL url = BaseHttpApi.buildURL(baseUrl, params);
            Logger.i("apiBaiduLivePlay url:" + url);
            OkHttpClient client = new OkClientHelper().buildOkHttpClient();
            Request request = new Request.Builder()
                                        .url(url)
                                        .build();
            Call call = client.newCall(request);
            Response execute = call.execute();
            String s = execute.body().string();
            Logger.i("apiBaiduLivePlay response:" + s);
            response = LiveMediaResponse.parseResponse(deviceId, s);
        } catch (Exception e) {
            LoggerUtil.e("apiBaiduLivePlay", e);
            response = LiveMediaResponse.parseResponseError(e);
        }
        long endTime = System.currentTimeMillis();
        Logger.i("apiBaiduLivePlay start:"+deviceId+ " "+endTime+" 耗时:"+(endTime-startTime)+" \r\n");
        return response;
    }

    /**
     * 获取对讲rtmp控制通道server
     * @param bdAccessToken 百度AccessToken
     * @param deviceId      设备ID
     * @param streamId
     * @return
     */
    public static String getAudioCHRtmpServer(String bdAccessToken, String deviceId, String streamId) {
        String rtmpServer = "rtmp://qd.cam.baidu.com:1935/live";
        if(TextUtils.isEmpty(bdAccessToken)) return rtmpServer;

        String baseUrl = ApiConfig.BAIDU_URLDEV;
        long startTime = System.currentTimeMillis();
        Map<String, String> params = new HashMap<String, String>();
        params.put("method", "info");
        params.put("access_token", bdAccessToken);
        params.put("deviceid", deviceId);
        params.put("device_type", "1");

        try {
            URL url = BaseHttpApi.buildURL(baseUrl, params);
            Logger.i("getAudioCHRtmpServer url:" + url);
            OkHttpClient client = new OkClientHelper().buildOkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Call call = client.newCall(request);
            Response execute = call.execute();
            String s = execute.body().string();
            Logger.i("getAudioCHRtmpServer "+s);
            if(!TextUtils.isEmpty(s)) {
                JSONObject json = new JSONObject(s);
                String server   = json.optString("server", "qd.cam.baidu.com:1935");
                rtmpServer      = "rtmp://"+server+"/live";
            }
        } catch (Exception e) {
            LoggerUtil.e("getAudioCHRtmpServer", e);
        }
        long endTime = System.currentTimeMillis();
        Logger.i("getAudioCHRtmpServer start:"+deviceId+ " "+endTime+" 耗时:"+(endTime-startTime)+" \r\n");
        return rtmpServer;
    }

    /**
     * 校准开始时间
     *
     * @param deviceId
     * @param accessToken
     * @param oldStartTime
     * @return
     */
    public static VodSeekResponse vodSeek(String deviceId, String accessToken, int oldStartTime) {
        VodSeekResponse response;
        String method = "vodseek";
        try {
            String res = mApiService.recordVodSeek(method, deviceId, accessToken, oldStartTime);
            response = VodSeekResponse.parseResponse(res);
        } catch (Exception e) {
            LoggerUtil.e("apiLivePlay", e);
            response = VodSeekResponse.parseResponseError(e);
        }
        return response;
    }
}
