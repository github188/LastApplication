package com.iermu.apiservice;

/**
 * Api接口配置
 *
 * Created by wcy on 15/7/1.
 */
public class ApiRoute {

    private final static String HTTP 		= "http://";
    private final static String HTTPS 		= "https://";
    private final static String HOST_v1 		= "api.iermu.com";      //:8002//v1版本
    private final static String HOST_v2 		= "123.57.4.235:8081";  //v2版本
    private final static String HOST_v3 		= "123.57.4.235:8083";  //v3版本

    public final static String HOST         = HOST_v1;
    public final static String IP           = HTTPS + HOST;
    public final static String CHARSET		= "utf-8";
    public final static String ACCEPT       = "application/json";
    public final static String API_VERSION  = "iermuV2";    //Api版本号(注意: 每次升级Api需要修改该字段)

    /*服务器接口路由 v1版本*/
    public final static String v1_BASEURI      = "/service/api.php";


    /*服务器接口路由 v2版本*/
    public final static String v2_REGISTER      = "/v2/passport/user";            //注册用户
    public final static String v2_AUTHORIZE     = "/oauth2/authorize";            //授权登录页面
    public final static String v2_TOKEN         = "/oauth2/token";                //获取AccessToken
    public final static String v2_AUTHQDLT      = "/v2/connect/qdlt/auth";        //青岛联通授权接口

    public final static String v2_REGISTERDEV   = "/v2/pcs/device#register";      //注册设备
    public final static String v2_DEVICELIST    = "/v2/pcs/device#list";          //获取我的设备列表
    public final static String v2_DEVMETA       = "/v2/pcs/device#meta";          //获取设备信息
    public final static String v2_LIVEPLAY      = "/v2/pcs/device#liveplay";      //获取直播信息
    public final static String v2_PUBLIST       = "/v2/pcs/device#listshare";     //获取公共频道列表
    public final static String v2_SETTING       = "/v2/pcs/device#setting";       //获取摄像机设置信息
    public final static String v2_UPDATESETTING = "/v2/pcs/device#updatesetting"; //更新设备状态及设置
    public final static String v2_UPGRADE       = "/v2/pcs/device#upgrade";       //检测固件升级
    public final static String v2_DROP          = "/v2/pcs/device#drop";          //注销设备
    public final static String v2_UPDATECAM     = "/v2/pcs/device#update";        //更新设备名称
    public final static String v2_COMMENTLIST   = "/v2/pcs/device#listcomment";   //获取公共评论列表
    public final static String v2_COMMENT       = "/v2/pcs/device#comment";       //发送公共评论
    public final static String v2_APPROVE       = "/v2/pcs/device#approve";       //点赞
    public final static String v2_SUBSCRIBE     = "/v2/pcs/device#subscribe";     //发送公共评论
    public final static String v2_UNSUBSCRIBE   = "/v2/pcs/device#unsubscribe";   //发送公共评论

    public final static String v2_GRANTUSERS    = "/v2/pcs/device#listgrantuser"; //获取云摄像头所有授权过多的用户列表
    public final static String v2_CREATE_SHARE  = "/v2/pcs/device#createshare";   //创建分享链接
    public final static String v2_CREATE_CODE   = "/v2/pcs/device#grantcode";     //获取设备授权吗
    public final static String v2_GRANT_SHARE   = "/v2/pcs/device#grant";         //把云摄像头授权给其他用户
    public final static String v2_DROP_GRANTUSER   = "/v2/pcs/device#dropgrantuser";         //把云摄像头授权给其他用户
    public final static String v2_CANCLE_SHARE  = "/v2/pcs/device#cancelshare";   //取消分享链接
    public final static String v2_DROP_GRANTDEVICE = "/v2/pcs/device#dropgrantdevice";       //取消分享链接

    public final static String v2_RECORD_LIST   = "/v2/pcs/device#playlist";      //获取录像列表
    public final static String v2_THUMBNAIL_LIST= "/v2/pcs/device#thumbnail";     //获取缩略图列表
    //public final static String v2_USERINFO      = "/v2/user/info";                //获取用户信息
    public final static String v2_LOGOUT        = "/v2/user";                     //注销登陆
    public final static String v2_VODSEEK       = "/v2/pcs/device#vodseek";       //录像开始时间校准
    public final static String v2_VOD           = "/v2/pcs/device?method=vod";    //录像Url播放地址(百度录像)

    public final static String v2_PUSHREGISTER  = "/v2/push/client#register";     //注册百度推送信道
    public final static String v2_CAMVIEW       = "/v2/pcs/device#view";          //观看摄像机(计数)
    public final static String v2_UPDATECONNECT = "/v2/user#updateconnect";       //更新平台信息

    public final static String V2_CLOUD_MOVE        = "/v2/pcs/device#move";      //控制云台转动
    public final static String V2_CLOUD_ROTATE      = "/v2/pcs/device#rotate";    //控制云台平扫
    public final static String V2_CLOUD_ADDPRESET   = "/v2/pcs/device#addpreset"; //添加云台预置点
    public final static String V2_CLOUD_DROPPRESET  = "/v2/pcs/device#droppreset";//删除云台预置点
    public final static String V2_CLOUD_LISTPRESET  = "/v2/pcs/device#listpreset";//获取云台预置点列表
    public final static String V2_CLOUD_PLAT        = "/v2/pcs/device#setting";   //获取云台信息

    //TODO 包括alarmpic，dropalarmpic和downloadalarmpic，请求地址保持用/rest/2.0/pcs/device,其他接口请求/v2/pcs/device
    public final static String V2_DOWNALARMPIC  = "/rest/2.0/pcs/device?method=downloadalarmpic";//获取报警图片URL地址
    public final static String V2_CLIENT        = "/v2/app/client";               //获取活动海报
    public final static String v2_UPDATESTATUS  = "/v2/pcs/device";               //检测固件升级
    public final static String v2_USERINFO      = "/v2/passport/user";            //获取用户信息
    public final static String v2_FEEDBACK      = "/v2/app/client";               //反馈信息

    public final static String V2_CLIP = "/pcs.baidu.com/rest/2.0/pcs/file";            //获取剪辑
    public final static String V2_CLIPS = "https://pcs.baidu.com/rest/2.0/pcs/file?method=list&path=/apps/iermu/clip&format=json&access_token=";    //获取剪辑列表
    public final static String V2_CLIPS_DELETE = "https://pcs.baidu.com/rest/2.0/pcs/file";//?method=delete&access_token=                           //删除剪辑列表


    //** 统计接口路由 **//
    public final static String V2_STATISTICS = "/v2/log/client";                  //统计信息接口路由(都是同一路由)

}
