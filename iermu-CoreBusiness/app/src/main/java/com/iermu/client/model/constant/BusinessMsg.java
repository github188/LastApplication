package com.iermu.client.model.constant;

/**
 * 业务状态码对应消息
 *
 * Created by wcy on 15/6/25.
 */
public class BusinessMsg {

    //Api状态码对应
    private static final String RESPONSE_ERROR  = "网络响应错误";
    private static final String HTTP_ERROR      = "HttpExecption错误";
    private static final String IO_ERROR        = "IOExecption错误";
    private static final String JSON_ERROR      = "JsonExecption错误";

    //业务状态码对应
    public static final String WIFI_CLOSE      = "Wifi连接已关闭";
    public static final String DEV_NOTACTIVE   = "设备未激活";
    public static final String DEV_REGISTED    = "设备已注册";
    public static final String NOTFIND_SCANDEV = "没有找到摄像机设备";
    public static final String HIWIFI_CONNECT_FAIL = "摄像机连接失败";


    /**
     * 匹配业务状态码对应消息
     * @param businessCode
     * @return
     */
    public static String matchMessage(int businessCode) {
        String message = "";
        switch(businessCode) {
        case BusinessCode.RESPONSE_ERROR:
            message = RESPONSE_ERROR;
            break;
        case BusinessCode.HTTP_ERROR:
            message = HTTP_ERROR;
            break;
        case BusinessCode.IO_ERROR:
            message = IO_ERROR;
            break;
        case BusinessCode.JSON_ERROR:
            message = JSON_ERROR;
            break;
        case BusinessCode.NOTFIND_SCANDEV:
            message = NOTFIND_SCANDEV;
            break;
        case BusinessCode.HIWIFI_CONNECT_FAIL:
            message = NOTFIND_SCANDEV;
            break;
        }
        return message;
    }

}
