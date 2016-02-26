package com.iermu.apiservice.body;

/**
 * 获取摄像机设置Body
 *
 * Created by wcy on 15/7/1.
 */
public class CamSettingsBody {

    private String method;          //请求函数类型
    private String type;            //查询请求类型
    private String deviceid;        //摄像机设备ID
    private String access_token;    //
    private String refresh_token;   //


    public CamSettingsBody(String deviceId, String accessToken, String refreshToken) {
        this.method = "get_dev_setting";
        this.type   = "all";
        this.deviceid = deviceId;
        this.access_token = accessToken;
        this.refresh_token = refreshToken;
    }

}
