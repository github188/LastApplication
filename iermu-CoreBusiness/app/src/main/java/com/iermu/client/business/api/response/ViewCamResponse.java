package com.iermu.client.business.api.response;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 观看摄像机(统计计数)
 *
 * Created by wcy on 15/8/14.
 */
public class ViewCamResponse extends Response {

    private String deviceId;
    private int viewnum;

    /**
     * 解析服务端响应
     *
     * @param str
     * @return
     * @throws org.json.JSONException
     */
    public static ViewCamResponse parseResponse(String str) throws JSONException {
        ViewCamResponse response = new ViewCamResponse();
        if (!TextUtils.isEmpty(str)) {
            JSONObject json = new JSONObject(str);
            response.parseJson(json);
        }
        return response;
    }

    /**
     * 解析服务端响应错误信息
     *
     * @param e
     * @return
     */
    public static ViewCamResponse parseResponseError(Exception e) {
        ViewCamResponse response = new ViewCamResponse();
        response.parseError(e);
        return response;
    }

    /**
     * 解析JSON
     * @param json
     */
    public void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        this.deviceId = json.optString(Field.DEVICEID);
        this.viewnum  = json.optInt(Field.VIEWNUM);
    }

    class Field {
        public static final String DEVICEID = "deviceid";
        public static final String VIEWNUM  = "viewnum";
    }

    public String getDeviceId() {
        return deviceId;
    }

    public int getViewnum() {
        return viewnum;
    }
}

