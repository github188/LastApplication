package com.iermu.client.business.api.response;

import android.text.TextUtils;

import com.iermu.client.business.api.converter.LiveMediaConverter;
import com.iermu.client.model.LiveMedia;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 直播信息
 *
 * Created by wcy on 15/7/29.
 */
public class LiveMediaResponse extends Response {

    private LiveMedia mLiveMedia;

    /**
     * 解析JSON
     * @param deviceId
     * @param json
     */
    public void parseJson(String deviceId, JSONObject json) throws JSONException {
        super.parseJson(json);
        this.mLiveMedia = LiveMediaConverter.fromJson(deviceId, json);
    }

    /**
     * 解析服务端响应
     *
     * @param deviceId
     * @param str
     * @return
     * @throws org.json.JSONException
     */
    public static LiveMediaResponse parseResponse(String deviceId, String str) throws JSONException {
        LiveMediaResponse response = new LiveMediaResponse();
        if(!TextUtils.isEmpty(str)) {
            JSONObject json = new JSONObject(str);
            response.parseJson(deviceId, json);
        }
        return response;
    }

    /**
     * 解析服务端响应错误信息
     * @param e
     * @return
     */
    public static LiveMediaResponse parseResponseError(Exception e) {
        LiveMediaResponse response = new LiveMediaResponse();
        response.parseError(e);
        return response;
    }

    public LiveMedia getLiveMedia() {
        return mLiveMedia;
    }

}
