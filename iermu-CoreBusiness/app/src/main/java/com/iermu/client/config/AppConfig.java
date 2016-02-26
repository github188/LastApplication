package com.iermu.client.config;

import com.iermu.client.util.StringUtil;

/**
 * 应用全局配置
 *  1.设置中心默认配置
 *  2.发布、测试模式
 *  3.Logger级别
 *
 * Created by wcy on 15/6/21.
 */
public class AppConfig {

    public static final boolean DEV_MODE = true;   //开发模式|发布模式
    public static final boolean INTERNATIONAL = false; //国际版为true ｜ 国内版为false
    public static final String CONTENTTYPE	= "application/x-www-form-urlencoded";
    //public static final String CONTENTTYPE	= "application/json";
    public static final String PACKAGE_NAME = "com.cms.iermu";

    public static final String APPID = "1";             //平台ID
    public static final String AK    = "lTlpBDk0eviYJ7MyC3OG";    //客户端用户名
    public static final String SK    = "2gfRh7dg53ffZLt5664Q";    //客户端密码
    public static final String QDLT_TOKEN = "5A0172766A99F68C184CC1E7722AEA29";//字串“iermu_android_qdlt”，经MD5加密后的字串为“5A0172766A99F68C184CC1E7722AEA29”
    public static final int EXPIRE   = 60000*60*24;               //签名串的过期时间 单位：秒
    public static final int REGISTER_EXPIRE   = 60*1000;      //注册的签名串过期时间 单位：秒
    public static final int QDLT_AUTH_EXPIRE  = 60*1000;      //青岛联通登录验证串过期时间 单位：秒(当前时间戳+60秒)


    /**
     * 获取青岛联通登录授权签名
     * @param expire        签名串过期时间
     * @param connectUid    手机号
     * @return
     */
    public static String getQDLTAuthSign(long expire, String connectUid) {
        String appid = AppConfig.APPID;
        String ak = AppConfig.AK;
        String sk = AppConfig.SK;

        String realSignStr = appid + expire + ak + sk + connectUid;
        String realSignMd5 = StringUtil.string2MD5(realSignStr);
        String sign = appid + "-" + ak + "-" + realSignMd5;
        return sign;
    }

    /**
     * 获取青岛联通登录授权签名过期时间
     * @return
     */
    public static long getQDLTSignExpire() {
        return System.currentTimeMillis()/1000 + AppConfig.QDLT_AUTH_EXPIRE;
    }

}
