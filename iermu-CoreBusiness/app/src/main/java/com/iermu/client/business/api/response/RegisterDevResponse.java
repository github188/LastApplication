package com.iermu.client.business.api.response;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 注册摄像机设备响应
 *
 * Created by wcy on 15/6/23.
 */
public class RegisterDevResponse extends Response {

    private String  streamId;   //百度设备使用
    private String  connectId;  //羚羊云设备使用
    private String  deviceId;   //
    private int     connectType;//平台类型

    /**
     * 解析JSON
     * @param json
     */
    public void parseJson(JSONObject json) throws JSONException {
        super.parseJson(json);
        this.streamId = json.optString(Field.STREAM_ID);
        this.connectId   = json.optString(Field.CONNECT_ID);
        this.deviceId = json.optString(Field.DEVICE_ID);
        this.connectType=json.optInt(Field.CONNECT_TYPE);
    }

    /**
     * 解析服务端响应
     * @param str
     * @return
     * @throws JSONException
     */
    public static RegisterDevResponse parseResponse(String str) throws JSONException {
        RegisterDevResponse response = new RegisterDevResponse();
        if(!TextUtils.isEmpty(str)) {
            JSONObject json = new JSONObject(str);
            response.parseJson(json);
        }
        return response;
    }

    /**
     * 解析服务端响应错误信息
     * @param exception
     * @return
     */
    public static RegisterDevResponse parseResponseError(Exception exception) {
        RegisterDevResponse response = new RegisterDevResponse();
        response.parseError(exception);
        return response;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getConnectId() {
        return connectId;
    }

    public void setConnectId(String connectId) {
        this.connectId = connectId;
    }

    public int getConnectType() {
        return connectType;
    }

    public void setConnectType(int connectType) {
        this.connectType = connectType;
    }

    class Field {
        public static final String STREAM_ID    = "stream_id";
        public static final String DEVICE_ID    = "deviceid";
        public static final String CONNECT_ID   = "connect_cid";
        public static final String CONNECT_TYPE = "connect_type";
    }

}
