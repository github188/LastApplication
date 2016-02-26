package com.iermu.apiservice.service;

import com.iermu.apiservice.ApiRoute;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * 统计相关接口
 * <p/>
 * Created by zhangxq on 15/11/24.
 */
public interface StatisticsService {

    /**
     * 统计打开视频是否成功
     *
     * @param method        固定为startplay
     * @param deviceId      设备id
     * @param accessToken   爱耳目平台access_token
     * @param info          具体内容，描述如下:
        “app”:      app平台-版本，比如Android的4.4.4就为”Android-4.4.4”。
        “cloud”:    摄像机使用的云平台，百度为baidu,羚羊为lingyang,新增的类似。
        “sdk”:      使用的云平台sdk的版本。
        “error_code”:   如果为羚羊，是ConnectPrivate的返回值。
        “device_status”:如果为羚羊，是GetDeviceStatus的返回值。
        “server_status”:如果为羚羊，是GetOnlineStatus的返回值

        上传时机:每次打开成功时都上传，每次app需要提示连接摄像机失败时上传。
        备注:该接口目前只需要查看羚羊云设备时调用
     * @return
     */
    @POST(ApiRoute.V2_STATISTICS)
    @FormUrlEncoded
    String statStartPlay(@Field("method") String method, @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("info") String info);

    /**
     * 视频播放中实时数据
     * @param method        固定为playinginfo
     * @param deviceId      设备id
     * @param accessToken   爱耳目平台access_token
     * @param info          具体内容，描述如下:
        “app”:      app平台-版本，比如Android的4.4.4就为”Android-4.4.4”。
        “cloud”:    摄像机使用的云平台，百度为baidu,羚羊为lingyang,新增的类似。
        “sdk”:      使用的云平台sdk的版本。
        “width”:    分辨率宽，需要播放器支持。
        “height”:   分辨率高，需要播放器支持。
        “opentime”: 从开始播放到第一帧数据出来的时间，如果是百度，是把直播url送进播放器开始计算，到收到第一帧数据的通知；如果是羚羊，是调用ConnectPrivate前开始，到收到第一帧数据的通知，单位ms。
        “delay”:    当前帧的本地延迟时间，需要播放器支持，单位ms。
        “upload_bitrate”:   仅羚羊，设备上传码率，单位KB/s，需要播放器支持。
        “download_bitrate”: 数据下载码率，单位KB/s，需要播放器支持。
        “device_status”:    仅羚羊，是GetDeviceStatus的返回值。
        “server_status”:    仅羚羊，是GetOnlineStatus的返回值。
        “transmit”:         仅羚羊，传输方式(p2p,rtmp),通过GetStreamMode判断。
        “rtmp”:             仅羚羊，如果transmit为rtmp,此处为摄像机连接的服务器地址，通过GetRTMPURL获取。

        上传时机:在播放成功后，每间隔5秒上传一次
     * @return
     */
    @POST(ApiRoute.V2_STATISTICS)
    @FormUrlEncoded
    String statPlayInfo(@Field("method") String method, @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("info") String info);

    /**
     * app上传日志文件
     * @param method        固定为upload
     * @param accessToken   爱耳目平台access_token
     * @return
     */
    @POST(ApiRoute.V2_STATISTICS)
    @FormUrlEncoded
    String statUploadLog(@Field("method") String method, @Field("access_token") String accessToken);

    /**
     * app自定义接口
     * @param method        固定为customize
     * @param accessToken   爱耳目平台access_token
     * @param info          用户自定义
     * @return
     */
    @POST(ApiRoute.V2_STATISTICS)
    @FormUrlEncoded
    String statCustomize(@Field("method") String method, @Field("access_token") String accessToken, @Field("info") String info);

    /**
     * 统计添加设备流程
     * @param method        固定为adddevice
     * @param deviceId      设备id,如果为smartconfig或者audioconfig，在没有找到设备时不用填写
     * @param accessToken   爱耳目平台access_token
     * @param info          具体内容，描述如下:
        “app”:      app平台-版本，比如Android的4.4.4就为”Android-4.4.4”。
        “cloud”:    摄像机使用的云平台，百度为baidu,羚羊为lingyang,新增的类似。
        “sdk”:      使用的云平台sdk的版本。
        “ssid”:     设备需要连接的WiFi ssid。
        “pwd”:      设备需要连接的WiFi密码。
        “author”:   路由器安全认证类型(wpa2/wep/802.1x)。
        “encrypt”:  路由器加密类型(AES/TKIP),仅Android端提供。
        “mode”:     WiFi模式(2.4G/5G),仅Android端提供。
        “type”:     添加方式(smartconfig, audioconfig, autoap, manualap)。
        “userid”:   用户在我们平台的用户id。
        “error_msg”:额外的错误信息，app自定义，可描述不在error_code中定义的情况。
        “error_code”:
                    0:成功。
                    4001:设备已经被别人绑定。
                    4002:调用后台接口时网络出错。
                    4003:注册的设备id和实际写入的设备id不匹配。
                    4004:与设备建立socket通信超时。
                    4005:等待摄像机上线超时。
                    5001:smartconfig或者audioconfig在超时时间内没有找到设备。
                    600x:Android自定义
     * @return
     */
    @POST(ApiRoute.V2_STATISTICS)
    @FormUrlEncoded
    String statSetupDev(@Field("method") String method, @Field("deviceid") String deviceId
            , @Field("access_token") String accessToken, @Field("info") String info);

}
