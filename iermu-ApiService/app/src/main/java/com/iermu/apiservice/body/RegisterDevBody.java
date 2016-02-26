package com.iermu.apiservice.body;

/**
 * 注册设备Body
 *
 * Created by wcy on 15/7/23.
 */
public class RegisterDevBody {

    private String method;          //请求方法名(register)
    private String deviceid;        //设备ID
    private int    device_type;     //设备类型，默认为1
    private int    connect_type;    //平台类型，1:百度 2: 羚羊云
    private String connect_did;     //平台设备ID，如羚羊云hash_cid
    private String desc;            //设备名
    private String access_token;    //access token

    /**
     * 注册设备Body
     * @param method
     * @param deviceId
     * @param deviceType
     * @param connectType
     * @param connectDid
     * @param desc
     * @param accessToken
     */
    public RegisterDevBody(String method, String deviceId, int deviceType, int connectType
            , String connectDid, String desc, String accessToken) {
        this.method         = method;
        this.deviceid       = deviceId;
        this.device_type    = deviceType;
        this.connect_type   = connectType;
        this.connect_did    = connectDid;
        this.desc           = desc;
        this.access_token   = accessToken;
    }

}
