package com.iermu.client.business.api;

import com.iermu.apiservice.service.StatisticsService;
import com.iermu.client.util.Logger;

import org.json.JSONObject;

import java.util.Map;

import javax.inject.Inject;

/**
 * 统计信息相关接口
 * 1. 打开视频是否成功
 * 2. 视频播放中实时数据
 * 3. app自定义接口
 * 4. app上传日志文件
 * 5. 设备添加是否成功
 * <p/>
 * Created by zhangxq on 15/11/24.
 */
public class StatisticsApi {

    @Inject
    static StatisticsService mApiService;

    /**
     * 统计打开视频是否成功
     *
     * @param deviceId
     * @param accessToken
     * @param app
     * @param cloud
     * @param sdk
     * @param errorCode
     * @param deviceStatus
     * @param serverStatus “app”:      app平台-版本，比如Android的4.4.4就为”Android-4.4.4”。
     *                     “cloud”:    摄像机使用的云平台，百度为baidu,羚羊为lingyang,新增的类似。
     *                     “sdk”:      使用的云平台sdk的版本。
     *                     “error_code”:   如果为羚羊，是ConnectPrivate的返回值。
     *                     “device_status”:如果为羚羊，是GetDeviceStatus的返回值。
     *                     “server_status”:如果为羚羊，是GetOnlineStatus的返回值
     *                     <p/>
     *                     上传时机:每次打开成功时都上传，每次app需要提示连接摄像机失败时上传。
     *                     备注:该接口目前只需要查看羚羊云设备时调用
     */
    public static void apiStartPlay(String deviceId, String accessToken, String app, String cloud
            , String sdk, int errorCode, int deviceStatus, int serverStatus, int playerErrorCode) {
        String method = "startplay";
        JSONObject json = new JSONObject();
        try {
            json.put("app", app);
            json.put("cloud", cloud);
            json.put("sdk", sdk);
            json.put("error_code", errorCode);
            json.put("device_status", deviceStatus);
            json.put("server_status", serverStatus);
            json.put("player_error_code", playerErrorCode);
            Logger.i("StatisticsApi", "app:" + app + " cloud:" + cloud + " sdk:" + sdk + " error_code:" + errorCode + " device_status:" + deviceStatus
                    + " server_status:" + serverStatus + " player_error_code:" + playerErrorCode);
            mApiService.statStartPlay(method, deviceId, accessToken, json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 统计添加设备
     *
     * @param deviceId
     * @param accessToken
     * @param userId
     * @param type
     * @param app
     * @param cloud
     * @param ssid
     * @param pwd
     * @param errorCode   “app”:      app平台-版本，比如Android的4.4.4就为”Android-4.4.4”。
     *                    “cloud”:    摄像机使用的云平台，百度为baidu,羚羊为lingyang,新增的类似。
     *                    “sdk”:      使用的云平台sdk的版本。
     *                    “ssid”:     设备需要连接的WiFi ssid。
     *                    “pwd”:      设备需要连接的WiFi密码。
     *                    “author”:   路由器安全认证类型(wpa2/wep/802.1x)。
     *                    “encrypt”:  路由器加密类型(AES/TKIP),仅Android端提供。
     *                    “mode”:     WiFi模式(2.4G/5G),仅Android端提供。
     *                    “type”:     添加方式(smartconfig, audioconfig, autoap, manualap)。
     *                    “userid”:   用户在我们平台的用户id。
     *                    “error_msg”:额外的错误信息，app自定义，可描述不在error_code中定义的情况。
     *                    “error_code”:
     *                    0:成功。
     *                    4001:设备已经被别人绑定。
     *                    4002:调用后台接口时网络出错。
     *                    4003:注册的设备id和实际写入的设备id不匹配。
     *                    4004:与设备建立socket通信超时。
     *                    4005:等待摄像机上线超时。
     *                    5001:smartconfig或者audioconfig在超时时间内没有找到设备。
     *                    600x:Android自定义
     */
    public static void apiSetupDev(String deviceId, String accessToken, String userId, String type
            , String app, String cloud, String ssid, String pwd, int errorCode) {
        String method = "adddevice";
        JSONObject json = new JSONObject();
        try {
            json.put("userid", userId);
            json.put("type", type);
            json.put("app", app);
            json.put("cloud", cloud);
            json.put("ssid", ssid);
            json.put("pwd", pwd);
            json.put("error_code", errorCode);
            mApiService.statSetupDev(method, deviceId, accessToken, json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 统计绑定百度推送失败信息
     *
     * @param deviceId
     * @param accessToken
     * @param phoneModel         手机型号
     * @param phoneBrand         手机品牌
     * @param phoneSdkVersion    手机sdk版本
     * @param phoneSystemVersion 手机系统版本
     */
    public static void apiBindBaiduPush(String deviceId, String accessToken, String phoneModel
            , String phoneBrand, String phoneSdkVersion, String phoneSystemVersion, String versionName, String versionCode) {
        String method = "customize";
        JSONObject json = new JSONObject();
        try {
            json.put("deviceId", deviceId);
            json.put("phoneModel", phoneModel);
            json.put("phoneBrand", phoneBrand);
            json.put("phoneSdkVersion", phoneSdkVersion);
            json.put("phoneSystemVersion", phoneSystemVersion);
            json.put("appVersion", versionName);
            json.put("appVersionCode", versionCode);
            mApiService.statCustomize(method, accessToken, json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void apiCustomize(String accessToken, Map<String, String> map) {
        String method = "customize";
        JSONObject json = new JSONObject();
        try {

            mApiService.statCustomize(method, accessToken, json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
