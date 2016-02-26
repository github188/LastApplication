package com.iermu.client.config;

/**
 * 用户偏好设置(设置中心)
 *
 * Created by zhoushaopei on 15/7/20.
 */
public class PreferenceConfig {

    public static final String DEV_EMAIL_SSL    = "dev_email_ssl";  //摄像机消息报警邮件配置是否开启SSL
    public static final String ADVANCED_CONFIG  = "advanced_config";//设置手动配置模式
    public static final String SOUNG_CONFIG     = "sound_config";   //设置播放器的声音开关
    public static final String ALERT_UPDATEVERSION= "alert_update"; //检测版本升级
    public static final String CAPSULE_DIALOG   = "capsule_dialog"; //检测空气胶囊dialog是否显示
    public static final String POSTER_IMG_URL   = "img_url"; //活动页图片地址
    public static final String POSTER_WEB_URL   = "web_url"; //活动页网页
    public static final String POSTER_START_TIME   = "start_time"; //活动开始时间
    public static final String POSTER_END_TIME     = "end_time"; //活动结束时间
    public static final String REB_BLUE_LIGHT      = "reb_blue_light";//红蓝灯交替闪烁
    public static final String INPUT_WIFI_PASSWORD = "input_wifi_password";//输入wifi密码
    public static final String MY_CAM_COUNT     = "my_cam_count"; //我的摄像机数量
    public static final String FILM_EDIT_TIP_SHOW = "film_edit_tip_show"; //是否显示过录像剪辑提示
    public static final String SETUP_IP_MODE = "setup_ip_mode"; //手动配置ip地址
    public static final String FILM_EDIT_START_TIME = "film_edit_start_time"; // 剪辑开始时间
    public static final String FILM_EDIT_END_TIME = "film_edit_end_time"; // 剪辑结束时间
    public static final String FILM_EDIT_IS_FAILD = "film_edit_is_faild"; // 剪辑是否失败
    public static final String REGISTED_PUSH    = "registed_push"; //是否注册成功推送服务(指:向服务器注册推送服务的配置信息)
    public static final String BAIDU_PUSHCONF   = "baidu_pushconf"; //百度推送服务的配置信息
    public static final String GETUI_PUSHCONF   = "getui_pushconf"; //个推推送服务的配置信息
    public static final String DEV_ADD_ERROR_TIMES   = "dev_add_error_times"; //个推推送服务的配置信息

    // 声音开关类型，直播，录像，公共
    public enum SoundType {
        mineCam,
        record,
        pubCam
    }
}
