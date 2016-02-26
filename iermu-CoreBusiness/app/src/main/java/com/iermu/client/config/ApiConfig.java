package com.iermu.client.config;

import android.text.TextUtils;

import com.cms.iermu.cms.cmsNative;
import com.iermu.apiservice.ApiRoute;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.util.LanguageUtil;
import com.iermu.client.util.MD5;
import com.squareup.okhttp.internal.Util;

import java.util.Map;
import java.util.TimeZone;

/**
 * Api接口配置
 *
 * Created by wcy on 15/6/21.
 */
public class ApiConfig {

    public static final String IP_HTTPS	= "https://";
    //public static final String IP_HTTP	= "http://";
    public static final String IERMU_HOST   = ApiRoute.HOST;
    public static final String BAIDU_HOST   = "openapi.baidu.com";
    public static final String IERMU_IP     = ApiRoute.IP;              //爱耳目服务器IP
    public static final String BAIDU_IP     = IP_HTTPS + BAIDU_HOST;    //百度开发者中心IP

    public static final String iermu_APPKEY             = AppConfig.AK;               //iermu AppKey
    public static final String iermu_CODE_REDIRECT      = "iermuconnect://code/success";        //iermu 重定向地址
    public static final String iermu_DEVTOKEN_REDIRECT  = "iermuconnect://device_code/success"; //iermu 重定向地址
    public static final String pcs_REDIRECT             = "bdconnect://success";                //百度授权认证重定向地址
    public static final String BAIDU_URLDEV = "https://pcs.baidu.com/rest/2.0/pcs/device";

    //public static final String MESSAGE_DOWNLOAD         = "http://123.57.4.235:8081/rest/2.0/pcs/device?method=downloadalarmpic"; // 报警图片下载地址
    public static final String CVR_BUG                  = "http://kankan.baidu.com/mobile/#/buy/access_token";//购买云录制
    public static final String CVR_DEV                  = "http://www.iermu.com/";
    public static final String QUESTION_NOEMAL          = "http://www.iermu.com/question.php";//常见问题
    public static final String OFFICAL                  = "http://www.iermu.com";//官网
    public static final String REPORT                   = "http://www.iermu.com/wap/report_camera.php";//举报
    public static final String PUBLIC_LIVE              = "http://www.iermu.com/fenxiangxieyi.html";//公共直播协议
    public static final String SOLVE                    = "http://www.iermu.com/question4.php";//查看如何解决
    public static final String PCS_URL_DEV              = "https://pcs.baidu.com/rest/2.0/pcs/device";
    public static final String PCS_URL_FILE             = "https://pcs.baidu.com/rest/2.0/pcs/file";
    public static final String BUY_CAM                  = "http://weidian.com";//购买摄像机

    public static final String DELETE_CLIP              = "https://pcs.baidu.com/rest/2.0/pcs/thumbnail";
    //access_token=21.8412bbc947958f7a624661271937cb27.2592000.1454120403.3662873766-1508471&
    //&path=/apps/iermu/clip/1453364067.mp4
    public static final String UPDATE_PASSWORD            = "https://passport.iermu.com/resetpwd";//找回密码
    public static final String USER_AGREEMENT           = "http://www.iermu.com";//官网
    public static final String BIND_BAIDU               = "https://passport.iermu.com/connect/baidu/bind";//绑定百度账号>>>>>>> 添加百度绑定、修改密码、更新数据库
    public static final String USER_DEAL                = "http://www.iermu.com/private";//用户协议
    public static final String UPLOAD_AVATAR            = "http://upload.iermu.com/user/avatar";//上传头像

    /* 爱耳目服务器路由 */
    //private static final String iermu_REGISTER_DEV = "/rest/2.0/pcs/device";    //注册设备
    /* 百度 pcs */
    private static final String pcs_GETACCESSTOKEN  = "/oauth/2.0/token";       //获取AccessToken

    static final String TAG = ApiConfig.class.getSimpleName();

    public static int iTzOffset = TimeZone.getDefault().getRawOffset()/1000;//8 * 60 * 60; // 校正设备端本地时间戳为标准时间戳

    /**
     * 获取Pcs服务器AccessToken接口
     * @return
     */
    public static String getPcsAccessToken() {
        return splacePcsURL(pcs_GETACCESSTOKEN);
    }

    /**
     * 获取iermu服务器跳转Baidu Web登录页地址
     * @return
     */
    public static String getIermuAuthorizeUrl() {
        //绑定百度账号逻辑需要重写, 不能沿用这个URL
//        String uid = ErmuBusiness.getAccountAuthBusiness().getUid();
//        if (TextUtils.isEmpty(uid)) {
//            uid = "";
//        }
        StringBuilder sb = new StringBuilder();
        sb.append(ApiRoute.v2_AUTHORIZE)
                .append("?response_type=code")
                .append("&client_id=")
                .append(iermu_APPKEY)
                .append("&redirect_uri=")
                .append(iermu_CODE_REDIRECT)
                .append("&scope=netdisk")
                .append("&display=mobile")
                .append("&connect_type=1");
                //.append("&bind_uid="+uid);
        return splaceIermuURL(sb.toString());
    }

    /**
     * Web登录页地址(青岛联通)
     * @param connectUid 青岛联通账号,手机号
     * @param expire    签名过期时间,单位为秒,当前时间戳+60秒
     * @param sign      算法为MD5(appid + expire + client_id + client_secret + connect_uid),其中client_id为应用API Key,client_secret为应用Secret Key
     * @return
     */
    public static String getQDLTAuthorizeUrl(String connectUid, long expire, String sign) {
        StringBuilder sb = new StringBuilder();
        sb.append(ApiRoute.v2_AUTHQDLT)
                .append("?response_type=code")
                .append("&client_id=")
                .append(iermu_APPKEY)
                .append("&redirect_uri=")
                .append(iermu_CODE_REDIRECT)
                .append("&scope=netdisk")
                .append("&display=mobile")
                //.append("&connect_type=1")
                //.append("&bind_uid="+uid)
                .append("&connect_uid=" + connectUid)
                .append("&expire="+expire)
                .append("&sign="+sign);
        return splaceIermuURL(sb.toString());
    }

    /**
     * 获取Baidu Web登录页地址
     * @return
     */
    public static String getBaiduAuthorizeUrl() {
        String clientId = cmsNative.getAppInfoA("a");
        StringBuilder sb = new StringBuilder();
        sb.append("/oauth/2.0/authorize?")
                .append("response_type=code")
                .append("&client_id=")
                .append(clientId)
                .append("&redirect_uri=")
                .append(pcs_REDIRECT)
                .append("&scope=netdisk")
                .append("&display=mobile");
        return splacePcsURL(sb.toString());
    }

    /**
     * 获取设备AccessToken (百度)
     * @return
     */
    public static String getDeviceToken() {
        StringBuilder sb = new StringBuilder();
        sb.append(ApiRoute.v2_AUTHORIZE)
                .append("?response_type=device_token")
                .append("&client_id=")
                .append(iermu_APPKEY)
                .append("&redirect_uri=")
                .append(iermu_DEVTOKEN_REDIRECT)
                .append("&scope=netdisk")
                .append("&display=mobile");
        return splaceIermuURL(sb.toString());
    }

    /**
     * 判断链接地址是否是百度授权页面URL
     * @param url
     * @return
     */
    public static boolean isBaiduAuthorizeUrl(String url) {
        return TextUtils.isEmpty(url)
                ? false
                : (url.contains(BAIDU_HOST + "/oauth/2.0/authorize?")
                && url.contains("response_type=code") );
    }

    /**
     * 判断链接地址是否是Iermu授权页面URL
     * @param url
     * @return
     */
    public static boolean isIermuAuthorizeUrl(String url) {
        return TextUtils.isEmpty(url)
                ? false
                : (url.contains(IERMU_HOST + ApiRoute.v2_AUTHORIZE)
                && url.contains("response_type=device_token") );
    }

    /**
     * 判断链接地址是否是Iermu 获取DeviceToken页面URL
     * @param url
     * @return
     */
    public static boolean isIermuDeviceTokenUrl(String url) {
        return TextUtils.isEmpty(url)
                ? false
                : (url.contains(IERMU_HOST + ApiRoute.v2_AUTHORIZE)
                && url.contains("response_type=code") );
        //: (url.contains(getIermuAuthorizeUrl()) );
    }

    /**
     * 判断链接地址是否是Iermu 获取授权码重定向URL
     * @param url
     * @return
     */
    public static boolean isIermuCodeRedirectUrl(String url) {
        return TextUtils.isEmpty(url)
                ? false
                : url.startsWith(iermu_CODE_REDIRECT) && url.contains("code");
    }

    /**
     * 判断链接地址是否是Iermu 获取DeviceToken重定向URL
     * @param url
     * @return
     */
    public static boolean isIermuDeviceTokenRedirectUrl(String url) {
        return TextUtils.isEmpty(url)
                ? false
                : url.startsWith(iermu_DEVTOKEN_REDIRECT);
    }

    /**
     * 判断链接地址是否是百度 获取授权码重定向URL
     * @param url
     * @return
     */
    public static boolean isBaiduCodeRedirectUrl(String url) {
        return TextUtils.isEmpty(url)
                ? false
                : url.startsWith(pcs_REDIRECT) && url.contains("code");
    }

    /**
     * 判断当前播放地址是否无效
     * @param playUrl
     * @return
     */
    public static boolean invalidPlayUrl(String playUrl) {
        return playUrl.contains("rtmp://invalidurl.com/live/");
    }

    /**
     * 获取分享链接
     * @param uk
     * @param shareId
     * @param shareType
     * @return
     */
    public static String getShareLink(String uk, String shareId, int shareType) {
        String url = "http://www.iermu.com/view_share.php?shareid=" + shareId + "&uk="
                + uk + "&share=" + shareType;
        return url;
    }

    /**
     * 获取百度录像Url
     * @param accessToken
     * @param deviceId
     * @param startTime
     * @param endTime
     * @return
     */
    public static String getBaiduRecordUrl(String accessToken, String deviceId, int startTime, int endTime) {
        String url = IERMU_IP + ApiRoute.v2_VOD
                + "&access_token=" + accessToken
                + "&deviceid=" + deviceId
                + "&st=" + startTime
                + "&et=" + endTime;
        return url;
    }
    /**
     *
     *  获取羚羊云的分享链接地址
     * @param uk
     * @param shareId
     * @param shareType
     * @return
     */
    public static String getLYYShareLink(String uk, String shareId, int shareType) {
        String url = "http://www.iermu.com/view_share.php?shareid=" + shareId + "&uk="
                + uk + "&share=" + shareType + "&l=1";
        return url;
    }

    /**
     * 获取报警图片URL地址
     * @param accessToken
     * @param deviceId      设备ID
     * @param alarmTime     报警时间
     * @return
     */
    public static String getAlarmPicUrl(String accessToken, String deviceId, String alarmTime) {
        String url = IERMU_IP + ApiRoute.V2_DOWNALARMPIC
                + "&access_token=" + accessToken
                + "&deviceid=" + deviceId
                + "&path=/apps/iermu/alarmjpg/" + deviceId
                + "/" + alarmTime
                + ".jpg";
        return url;
    }

    private static String splacePcsURL(String uri) {
        if(TextUtils.isEmpty(uri)) {
            throw new IllegalArgumentException("uri may not be null.");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(BAIDU_IP)
                .append(uri);
        return sb.toString();
    }

    private static String splaceIermuURL(String uri) {
        if(TextUtils.isEmpty(uri)) {
            throw new IllegalArgumentException("uri may not be null.");
        }
        StringBuilder sb = new StringBuilder();
        sb.append(IERMU_IP)
                .append(uri);
        return sb.toString();
    }

    /**
     *
     * @return debug: Host else null
     */
    public static Map<String, String> getHeaders() {
        return null; //"accept:application/json";
    }


    /**
     * 检查是否有进行中的任务api url
     */
    public static  String getClipStatusUrl(String accessToken, String deviceId, boolean bTask){
        String url = PCS_URL_DEV
                +"?method=infoclip"
                + "&access_token=" + accessToken
                + "&deviceid=" + deviceId
                + "&type=" + (bTask? "task" : "quota");
        return url;
    }

    /**
     * 检测文件名是否重复api url
     */
    public static  String getIsExistFileUrl(String accessToken, String strName){
        String url = PCS_URL_FILE
                +"?method=meta"
                + "&access_token=" + accessToken
                + "&path=" + "/apps/iermu/clip/" + strName + ".mp4";
        return url;
    }

    /**
     * 获取开始剪辑录像api url
     */
    public static  String getStartClipUrl(String accessToken, String strDevID, long lSt, long lEt, String strClipName){
        String strSt = Long.toString(lSt+iTzOffset);
        String strEt = Long.toString(lEt+iTzOffset);

        String url = PCS_URL_DEV
                +"?method=clip"
                + "&access_token=" + accessToken
                + "&deviceid=" + strDevID
                + "&name=" + strClipName
                + "&st=" + strSt
                + "&et=" + strEt;
        return url;
    }

    /**
     * 获取举报页面URL
     * @param uid           当前登录用户的UID
     * @param accessToken   当前登录用户AccessToken
     * @param deviceId      举报设备ID
     * @return
     */
    public static String getReportUrl(String uid, String accessToken, String deviceId) {
        String language = LanguageUtil.getLanguage();
        return ApiConfig.REPORT
                +"?uk="+ uid
                + "&deviceid=" + deviceId
                + "&access_token="+accessToken
                + "&lang="+language;
    }

    /**
     * 购买摄像机
     *
     * @return
     */
    public static String getCvrDev() {
        String language = LanguageUtil.getLanguage();
        return ApiConfig.CVR_DEV + "?lang=" + language;
    }

    /**
     * 购买云录制
     *
     * @return
     */
    public static String getCvrBug() {
        String language = LanguageUtil.getLanguage();
        return ApiConfig.CVR_BUG + "=" + ErmuBusiness.getAccountAuthBusiness().getBaiduAccessToken() + "&lang=" + language;
    }

    /**
     * 常见问题
     * @return
     */
    public static String getQuestionNoemal() {
        String language = LanguageUtil.getLanguage();
        return ApiConfig.QUESTION_NOEMAL + "?lang=" + language;
    }

    /**
     * 官网
     * @return
     */
    public static String getOffical() {
        String language = LanguageUtil.getLanguage();
        return ApiConfig.OFFICAL + "?lang=" + language;
    }

    /**
     * 公共直播协议
     * @return
     */
    public static String getPublicLive() {
        String language = LanguageUtil.getLanguage();
        return ApiConfig.PUBLIC_LIVE + "?lang=" + language;
    }

    /**
     * 查看如果解决问题
     * @return
     */
    public static String getSolve() {
        String language = LanguageUtil.getLanguage();
        return ApiConfig.SOLVE + "?lang=" + language;
    }

    /**
     * 购买摄像机
     * @return
     */
    public static String getBuyCam() {
        return ApiConfig.BUY_CAM+"?userid=335320008&wfr=c";
    }

    /**
     * 找回密码
     * @return
     */
    public static String getUpdatePassword() {
        String language = LanguageUtil.getLanguage();
        return ApiConfig.UPDATE_PASSWORD + "?display=mobile&lang=" + language;
    }

    /**
     * 用户协议
     * @return
     */
    public static String getUserDeal() {
        String language = LanguageUtil.getLanguage();
        return ApiConfig.USER_DEAL + "?lang=" + language;
    }

    public static String getBindBaidu(String token) {
        String language = LanguageUtil.getLanguage();
        return ApiConfig.BIND_BAIDU +"?access_token="+token+"&display=mobile&lang=" + language;
    }

    public static String getDeleteClip(String baiduToken, String path) {
        return ApiConfig.DELETE_CLIP +"?method=generate&width=320&height=180&access_token="+baiduToken+"&path="+path;
    }
}
