package com.iermu.client.business.api.request;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 注册设备
 *
 * Created by wcy on 15/6/23.
 */
public class RegisterDevRequest extends Request {

    private String  method;      //函数名
    private String  deviceId;    //设备ID,唯一标识摄像头的ID
    private int     deviceType;  //设备类型,默认为1
    private String  desc;        //设备名
    private String accessToken; //

    /**
     *
     * @param deviceId
     * @param deviceType
     * @param desc
     */
    public RegisterDevRequest(String deviceId, int deviceType, String desc) {
        this.method = "register";
        this.accessToken = "3a10b2cb631d1087f67ec8f68922feca";//accessToken;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.desc = desc;
    }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(Field.METHOD, method);
        json.put(Field.DEVICE_ID, deviceId);
        json.put(Field.DEVICE_TYPE, deviceType);
        json.put(Field.DESC, desc);
        json.put(Field.ACCESS_TOKEN, accessToken);
        return json;
    }

    class Field {
        public static final String METHOD       = "method";
        public static final String DEVICE_ID    = "deviceid";
        public static final String DEVICE_TYPE  = "device_type";
        public static final String DESC         = "desc";
        public static final String ACCESS_TOKEN = "access_token";
    }

}
