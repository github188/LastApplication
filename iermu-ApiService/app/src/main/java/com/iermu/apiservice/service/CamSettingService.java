package com.iermu.apiservice.service;

import com.iermu.apiservice.ApiRoute;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * 摄像机设置API相关接口
 *
 * Created by wcy on 15/7/1.
 */
public interface CamSettingService {

    /**
     * 获取摄像机设置信息接口
     * @param method        请求函数名
     * @param type          请求接口类型
     * @param deviceId      设备ID
     * @param accessToken   accessToken
     * @param refreshToken  refreshToken
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_SETTING)
    String getCamSettings(@Field("method") String method, @Field("type") String type
            , @Field("deviceid") String deviceId, @Field("access_token") String accessToken
            , @Field("refresh_token") String refreshToken);

    /**
     * 注销摄像机设备
     * @param method
     * @param deviceId
     * @param accessToken
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_DROP)
    String dropCamDev(@Field("method") String method , @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken);

    /**
     * 重启摄像机
     * @param method
     * @param deviceId
     * @param accessToken
     * @param fileds        {restart: 1}
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_UPDATESETTING)
    String restartCamDev(@Field("method") String method , @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("fileds") String fileds);

    /**
     * 修改摄像机名称
     * @param method
     * @param deviceId
     * @param accessToken
     * @param name          摄像机名称
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_UPDATECAM)
    String updateCamName(@Field("method") String method , @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("desc") String name);

    /**
     * 摄像机开关
     * @param method
     * @param deviceId
     * @param accessToken
     * @param fileds         {power: 0关 1开}
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_UPDATESETTING)
    String powerCam(@Field("method") String method , @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("fileds") String fileds);

    /**
     * 设置摄像机最大上行带宽限制
     * @param method
     * @param deviceId
     * @param accessToken
     * @param fileds        {maxspeed:50}
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_UPDATESETTING)
    String setMaxUpspeed(@Field("method") String method , @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("fileds") String fileds);

    /**
     * 设置摄像机指示灯开关
     * @param method
     * @param deviceId
     * @param accessToken
     * @param fileds        {light: 0关 1开}
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_UPDATESETTING)
    String setDevLight(@Field("method") String method , @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("fileds") String fileds);

    /**
     * 设置摄像机画面是否旋转180度
     * @param method
     * @param deviceId
     * @param accessToken
     * @param fileds        {invert: 0关 1开}
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_UPDATESETTING)
    String setDevInvert(@Field("method") String method , @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("fileds") String fileds);

    /**
     * 设置摄像机云录制开关
     * @param method
     * @param deviceId
     * @param accessToken
     * @param fileds
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_UPDATESETTING)
    String setDevCvr(@Field("method") String method , @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("fileds") String fileds);

    /**
     * 设置摄像机是否静音
     * @param method
     * @param deviceId
     * @param accessToken
     * @param fileds        {}
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_UPDATESETTING)
    String setDevAudio(@Field("method") String method , @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("fileds") String fileds);

    /**
     * 设置摄像机场景
     * @param method
     * @param deviceId
     * @param accessToken
     * @param fileds        {scene: 0关 1开}
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_UPDATESETTING)
    String setDevScene(@Field("method") String method , @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("fileds") String fileds);

    /**
     * 设置摄像机夜视模式
     * @param method
     * @param deviceId
     * @param accessToken
     * @param fileds        {nightmode: 0关 1开}
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_UPDATESETTING)
    String setDevNightMode(@Field("method") String method , @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("fileds") String fileds);

    /**
     * 设置摄像机曝光
     * @param method
     * @param deviceId
     * @param accessToken
     * @param fileds        {exposemode: 0关 1开}
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_UPDATESETTING)
    String setDevExposeMode(@Field("method") String method , @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("fileds") String fileds);

    /**
     * 设置定时任务
     * @param method        函数名
     * @param deviceId      设备ID
     * @param accessToken
     * @param fileds        {cvr_cron: 0关 1开, cvr_start:开始时间, cvr_end:结束时间, cvr_repeat:一二三四}
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_UPDATESETTING)
    String setDevCron(@Field("method") String method, @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("fileds") String fileds);

    /**
     * 设置摄像机“报警定时”开关
     * @param method        函数名
     * @param deviceId      设备ID
     * @param accessToken
     * @param fileds        {alarm_notice 1:开  0:关}
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_UPDATESETTING)
    String setDevAlarm(@Field("method") String method, @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("fileds") String fileds);

    /**
     * 设置邮件信息
     * @param method        函数名
     * @param deviceId      设备ID
     * @param accessToken
     * @param fileds        {mail_to:收件人, mail_cc:抄送, mail_server:发件服务器, mail_port:端口号, mail_from:发件人, mail_user:用户名, mail_passwd:密码}
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_UPDATESETTING)
    String setDevEmail(@Field("method") String method, @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("fileds") String fileds);

    /**
     * 设置摄像机邮件报警开关
     * @param method        函数名
     * @param deviceId      设备ID
     * @param accessToken
     * @param fileds        {mail_to:收件人, mail_cc:抄送, mail_server:发件服务器, mail_port:端口号, mail_from:发件人, mail_user:用户名, mail_passwd:密码}
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_UPDATESETTING)
    String setAlarmMail(@Field("method") String method, @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("fileds") String fileds);

    /**
     * 设置报警灵敏度高低
     * @param method
     * @param deviceId
     * @param accessToken
     * @param fileds        {alarm_move_level: 0:低 1:正常 2:高}
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_UPDATESETTING)
    String setAlarmMoveLevel(@Field("method") String method, @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("fileds") String fileds);

    /**
     * 设置|注册推送通道
     * @param method
     * @param udId          设备序列号
     * @param accessToken
     * @param fileds        {userId, channelId}
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_PUSHREGISTER)
    String registerPush(@Field("method") String method, @Field("access_token") String accessToken
            , @Field("udid") String udId, @Field("pushid") int pushId,  @Field("config") String fileds);

    /**
     * 设置清晰度
     * @param method
     * @param deviceId
     * @param accessToken
     * @param fileds        {bitlevel 0 1 2}
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_UPDATESETTING)
    String setBitLevel(@Field("method") String method, @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("fileds") String fileds);

    /**
     * 检测摄像机固件
     * @param method
     * @param deviceId
     * @param accessToken
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_UPDATESTATUS)
    String checkCamFirmware(@Field("method") String method, @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("lang") String language);

    /**
     * 查询最新版本信息
     * @param method
     * @param deviceId
     * @param accessToken
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_UPDATESTATUS)
    String checkUpgradeVersion(@Field("method") String method, @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken);



    /**
     * 检测摄像机固件升级状态
     * @param method
     * @param deviceId
     * @param accessToken
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_UPDATESTATUS)
    String getCamUpdateStatus(@Field("method") String method, @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken);


    /**
     * 空气胶囊的温湿度
     * @param method        请求函数名
     * @param type          请求接口类型
     * @param deviceId      设备ID
     * @param accessToken   accessToken
     * @return
     */
    @FormUrlEncoded
    @POST(ApiRoute.v2_SETTING)
    String getCamCapsule(@Field("method") String method, @Field("type") String type
            , @Field("deviceid") String deviceId, @Field("access_token") String accessToken);

//    /**
//     * 设置获取云服务状态
//     * @param method
//     * @param accessToken
//     * @return
//     */
//    @FormUrlEncoded
//    @POST(ApiConfig.v2_UPDATESETTING)
//    String getDevCloud(@Query("method") String method, @Query("access_token") String accessToken);

//     * 设置消息报警邮箱配置
//     * @param method
//     * @param deviceId      设备ID
//     * @param accessToken
//     * @param from          发件人
//     * @param to            收件人
//     * @param cc            抄送
//     * @param server        服务器
//     * @param port          端口
//     * @param user          用户名
//     * @param passwd        密码
//     * @return
//     */
//    @GET(ApiConfig.BASEURI)
//    String setDevEmail(@Query("method") String method , @Query("dev_id") String deviceId
//            , @Query("access_token") String accessToken, @Query("mail_from") String from, @Query("mail_to") String to
//            , @Query("mail_cc") String cc, @Query("mail_server") String server, @Query("mail_port") String port
//            , @Query("mail_user") String user, @Query("mail_passwd") String passwd);
//
//    /**
//     * 设置邮件通知开启
//     * @param method
//     * @param deviceId      设备ID
//     * @param accessToken
//     * @param isCron        邮件通知是否开启
//     * @return
//     */
//    @GET(ApiConfig.BASEURI)
//    String setDevEmailCron(@Query("method") String method , @Query("dev_id") String deviceId
//                , @Query("access_token") String accessToken,  @Query("alarm_mail") int isCron);
//
//    /**
//     * 设置报警灵敏度高低
//     * @param method
//     * @param deviceId      设备ID
//     * @param accessToken
//     * @param moveLevel    灵敏度
//     * @return
//     */
//    @GET(ApiConfig.BASEURI)
//    String setDevLevel(@Query("method") String method , @Query("dev_id") String deviceId
//            , @Query("access_token") String accessToken , @Query("alarm_move") int move
//            ,  @Query("alarm_move_level") int moveLevel);


}
