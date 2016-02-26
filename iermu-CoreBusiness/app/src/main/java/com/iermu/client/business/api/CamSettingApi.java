package com.iermu.client.business.api;

import com.iermu.apiservice.service.CamSettingService;
import com.iermu.client.business.api.converter.CamCronConverter;
import com.iermu.client.business.api.response.CamAlarmResponse;
import com.iermu.client.business.api.response.CamCvrResponse;
import com.iermu.client.business.api.response.CamInfoResponse;
import com.iermu.client.business.api.response.CamPowerCronResponse;
import com.iermu.client.business.api.response.CamSettingResponse;
import com.iermu.client.business.api.response.CamStatusResponse;
import com.iermu.client.business.api.response.CamUpdateStatusResponse;
import com.iermu.client.business.api.response.CapsuleResponse;
import com.iermu.client.business.api.response.Response;
import com.iermu.client.business.api.response.UpgradeVersionResponse;
import com.iermu.client.model.CronRepeat;
import com.iermu.client.model.UpgradeVersion;
import com.iermu.client.util.LanguageUtil;
import com.iermu.client.util.LoggerUtil;

import org.json.JSONObject;

import java.util.Date;

import javax.inject.Inject;

/**
 *  摄像机设置(配置信息)Api接口
 *
 * Created by wcy on 15/7/2.
 */
public class CamSettingApi extends BaseHttpApi {

    @Inject static CamSettingService mApiService;

    /**
     * 获取摄像机信息接口
     * @param deviceId
     * @param accessToken
     * @param refreshToken
     * @return
     */
    @Deprecated
    public static CamSettingResponse apiGetCamSetting(String deviceId, String accessToken, String refreshToken) {
        CamSettingResponse response;
        String method = "setting";
        String type = "all";
        try {
            String str = mApiService.getCamSettings(method, type, deviceId, accessToken, refreshToken);
            response = CamSettingResponse.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiGetCamSetting", e);
            response = CamSettingResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 获取摄像机信息接口
     * @param deviceId
     * @param accessToken
     * @param refreshToken
     * @return
     */
    public static CamInfoResponse apiGetCamInfo(String deviceId, String accessToken, String refreshToken) {
        CamInfoResponse response;
        String method = "setting";
        String type = "info";
        try {
            String str = mApiService.getCamSettings(method, type, deviceId, accessToken, refreshToken);
            response = CamInfoResponse.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiGetCamInfo", e);
            response = CamInfoResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 获取摄像机信息接口
     * @param deviceId
     * @param accessToken
     * @return
     */
    public static CamStatusResponse apiGetCamStatus(String deviceId, String accessToken) {
        CamStatusResponse response;
        String method = "setting";
        String type = "status";
        try {
            String str = mApiService.getCamSettings(method, type, deviceId, accessToken, "");
            response = CamStatusResponse.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiGetCamStatus", e);
            response = CamStatusResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 摄像机开关机定时
     * @param deviceId
     * @param accessToken
     * @return
     */
    public static CamPowerCronResponse apiGetCamPowerCron(String deviceId, String accessToken, String refreshToken) {
        CamPowerCronResponse response;
        String method = "setting";
        String type = "power";
        try {
            String str = mApiService.getCamSettings(method, type, deviceId, accessToken, refreshToken);
            response = CamPowerCronResponse.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiGetCamPower", e);
            response = CamPowerCronResponse.parseResponseError(e);
        }
        return response;
    }

    //获取摄像机云录制定时信息
    public static CamCvrResponse apiGetCloudCvr(String deviceId, String accessToken, String refreshToken) {
        CamCvrResponse response;
        String method = "setting";
        String type = "cvr";
        try {
            String str = mApiService.getCamSettings(method, type, deviceId, accessToken, refreshToken);
            response = CamCvrResponse.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiGetCamCvr", e);
            response = CamCvrResponse.parseResponseError(e);
        }
        return response;
    }

    //获取摄像机报警级别配置信息
    public static CamAlarmResponse apiGetCamAlarm(String deviceId, String accessToken, String refreshToken) {
        CamAlarmResponse response;
        String method = "setting";
        String type = "alarm";
        try {
            String str = mApiService.getCamSettings(method, type, deviceId, accessToken, refreshToken);
            response = CamAlarmResponse.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiGetCamAlarm", e);
            response = CamAlarmResponse.parseResponseError(e);
        }
        return response;
    }

    //检测固件升级
    public static Response apiCamFirmware(String deviceId, String accessToken) {
        Response response;
        String method = "upgrade";
        String language = LanguageUtil.getLanguage();
        try {
            String str = mApiService.checkCamFirmware(method, deviceId, accessToken, language);
            response = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiCamFirmware", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    //查询最新版本信息
    public static UpgradeVersionResponse checkUpgradeVersion(String deviceId, String accessToken) {
        UpgradeVersionResponse response;
        String method = "checkupgrade";
        try {
            String str = mApiService.checkUpgradeVersion(method, deviceId, accessToken);
            response = UpgradeVersionResponse.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("checkupgrade", e);
            response = UpgradeVersionResponse.parseResponseError(e);
        }
        return response;
    }

    //检测固件升级状态
    public static CamUpdateStatusResponse apiGetCamUpdateStatus(String deviceId, String accessToken) {
        CamUpdateStatusResponse response;
        String method = "upgradestatus";
        try {
            String str = mApiService.getCamUpdateStatus(method, deviceId, accessToken);
            response = CamUpdateStatusResponse.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("upgradestatus", e);
            response = CamUpdateStatusResponse.parseResponseError(e);
        }
        return response;
    }

    /**
     * 重启摄像机
     * @param deviceId
     * @param accessToken
     * @return
     */
    public static Response apiRestartCamDev(String deviceId, String accessToken) {
        Response response;
        String method = "updatesetting";
        try {
            JSONObject json = new JSONObject();
            json.put("restart", 1);
            String str  = mApiService.restartCamDev(method, deviceId, accessToken, json.toString());
            response    = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiRestartCamDev", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    /**
     * 注销摄像机
     * @param deviceId
     * @param accessToken
     * @return
     */
    public static Response apiDropCamDev(String deviceId, String accessToken) {
        Response response;
        String method = "drop";
        try {
            String str = mApiService.dropCamDev(method, deviceId, accessToken);
            response = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiDropCamDev", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    /**
     * 摄像机开关
     * @param deviceId
     * @param accessToken
     * @param power         0关 1开
     * @return
     */
    public static Response apiPowerCam(String deviceId, String accessToken, boolean power) {
        Response response;
        String method = "updatesetting";
        try {
            JSONObject json = new JSONObject();
            json.put("power", power ? 1:0);
            if(!power) {//关机的同时关闭定时开关
                json.put("power_cron", 0);
                json.put("power_start", "000000");
                json.put("power_end",   "235900");
                json.put("power_repeat", "1111111");
            }
            String str = mApiService.powerCam(method, deviceId, accessToken, json.toString());
            response = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiPowerCam", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    /**
     * 更新摄像机名称
     * @param deviceId
     * @param accessToken
     * @param camName
     * @return
     */
    public static Response apiUpdateCamName(String deviceId, String accessToken, String camName) {
        Response response;
        String method = "update";
        try {
            String str  = mApiService.updateCamName(method, deviceId, accessToken, camName);
            response    = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiUpdateCamName", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    /**
     * 设置最大上行带宽
     * @param deviceId
     * @param accessToken
     * @param maxSpeed
     * @return
     */
    public static Response apiSetMaxUpspeed(String deviceId, String accessToken, int maxSpeed) {
        Response response;
        String method = "updatesetting";
        try {
            JSONObject json = new JSONObject();
            json.put("maxspeed", maxSpeed);
            json.put("minspeed", "50");
            String str  = mApiService.setMaxUpspeed(method, deviceId, accessToken, json.toString());
            response    = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiSetMaxUpspeed", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    //设置摄像机指示灯
    public static Response apiSetDevLight(String deviceId, String accessToken, boolean light) {
        Response response;
        String method = "updatesetting";
        try {
            JSONObject json = new JSONObject();
            json.put("light", light ? 1:0);
            String str = mApiService.setDevLight(method, deviceId, accessToken, json.toString());
            response = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiSetDevLight", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    //设置摄像机画面翻转
    public static Response apiSetDevInvert(String deviceId, String accessToken, boolean invert) {
        Response response;
        String method = "updatesetting";
        try {
            JSONObject json = new JSONObject();
            json.put("invert", invert ? 1:0);
            String str  = mApiService.setDevInvert(method, deviceId, accessToken, json.toString());
            response    = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiSetDevInvert", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    //设置摄像机录像定时
    public static Response apiSetDevCvr(String deviceId, String accessToken, boolean isCvr) {
        Response response;
        String method = "updatesetting";
        try {
            JSONObject json = new JSONObject();
            json.put("cvr",isCvr ? 1 : 0);
            String str  = mApiService.setDevCvr(method, deviceId, accessToken, json.toString());
            response    = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiSetDevCvr", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    //设置摄像机静音
    public static Response apiSetDevAudio(String deviceId, String accessToken, boolean audio) {
        Response response;
        String method = "updatesetting";
        try {
            JSONObject json = new JSONObject();
            json.put("audio",audio ? 1 : 0);
            String str  = mApiService.setDevAudio(method, deviceId, accessToken, json.toString());
            response    = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiSetDevAudio", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    //设置摄像机场景
    public static Response apiSetDevScene(String deviceId, String accessToken, boolean scene) {
        Response response;
        String method = "updatesetting";
        try {
            JSONObject json = new JSONObject();
            json.put("scene", scene ? 1: 0);
            String str  = mApiService.setDevScene(method, deviceId, accessToken, json.toString());
            response    = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiSetDevScene", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    //设置摄像机夜视模式
    public static Response apiSetDevNightMode(String deviceId, String accessToken, int nightmode) {
        Response response;
        String method = "updatesetting";
        try {
            JSONObject json = new JSONObject();
            json.put("nightmode", nightmode);
            String str  = mApiService.setDevNightMode(method, deviceId, accessToken, json.toString());
            response    = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiSetDevNightMode", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    //设置摄像机曝光模式
    public static Response apiSetDevExposeMode(String deviceId, String accessToken, int exposemode) {
        Response response;
        String method = "updatesetting";
        try {
            JSONObject json = new JSONObject();
            json.put("exposemode", exposemode);
            String str = mApiService.setDevExposeMode(method, deviceId, accessToken, json.toString());
            response = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiSetDevExposeMode", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    @Deprecated
    public static Response apiSetCvr(String deviceId, String accessToken, boolean isCvr) {
        Response response;
        String method = "updatesetting";
        try {
            JSONObject json = new JSONObject();
            json.put("cvr", isCvr? 1 : 0);

            String str = mApiService.setDevCron(method, deviceId, accessToken, json.toString());
            response = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiSetCvr", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    //设置录像定时
    public static Response apiSetCvrCron(String deviceId, String accessToken, boolean isDevCvr, boolean isCron
            , Date begin, Date end, CronRepeat repeat) {
        Response response;
        String method = "updatesetting";
        try {
            String beginTime = CamCronConverter.fromCronTime(begin);
            String endTime   = CamCronConverter.fromCronTime(end);
            String converter = CamCronConverter.fromCronRepeat(repeat);

            JSONObject json = new JSONObject();
            json.put("cvr", isDevCvr? 1 : 0);
            json.put("cvr_cron", isCron? 1 : 0);
            json.put("cvr_start", beginTime);
            json.put("cvr_end", endTime);
            json.put("cvr_repeat", converter);

            String str = mApiService.setDevCron(method, deviceId, accessToken, json.toString());
            response = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiSetDevCron", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    //设置摄像机开关机定时
    public static Response apiSetCamCron(String deviceId, String accessToken, boolean isCron
            , Date begin, Date end, CronRepeat repeat) {
        Response response;
        String method = "updatesetting";

        try {
            String beginTime = CamCronConverter.fromCronTime(begin);
            String endTime   = CamCronConverter.fromCronTime(end);
            String converter = CamCronConverter.fromCronRepeat(repeat);

            JSONObject json = new JSONObject();
            json.put("power_cron", isCron? 1 : 0);
            json.put("power_start", beginTime);
            json.put("power_end", endTime);
            json.put("power_repeat", converter);

            String str = mApiService.setDevCron(method, deviceId, accessToken, json.toString());
            response = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiSetCamCron", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }


    public static Response apiSetCamAlarm(String deviceId, String accessToken, int level, Date begin, Date end, CronRepeat repeat) {
        Response response;
        String method = "updatesetting";

        try {
            String beginTime = CamCronConverter.fromCronTime(begin);
            String endTime   = CamCronConverter.fromCronTime(end);
            String repeatstr = CamCronConverter.fromCronRepeat(repeat);

            JSONObject json = new JSONObject();
            json.put("alarm_push", 1);          //默认打开推送
            json.put("alarm_cron", 1);          //默认打开推送定时开关
            json.put("alarm_move", 1);          //默认打开移动报警开关
            json.put("alarm_move_level", level);
            json.put("alarm_start", beginTime);
            json.put("alarm_end", endTime);
            json.put("alarm_repeat", repeatstr);

            String str = mApiService.setDevCron(method, deviceId, accessToken, json.toString());
            response = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiSetCamAlarmCron", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    //设置摄像机报警定时开关
    public static Response apiSetAlarmPush(String deviceId, String accessToken, boolean push) {
        Response response;
        String method = "updatesetting";
        try {
            JSONObject json = new JSONObject();
            json.put("alarm_push", push ? 1 : 0);
            String str = mApiService.setDevAlarm(method, deviceId, accessToken, json.toString());
            response = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiSetAlarmPush", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    //设置摄像机报警定时
    @Deprecated
    public static Response apiSetAlarmCron(String deviceId, String accessToken, boolean isCron
            , Date begin, Date end, CronRepeat repeat) {
        Response response;
        String method = "updatesetting";

        try {
            String beginTime = CamCronConverter.fromCronTime(begin);
            String endTime   = CamCronConverter.fromCronTime(end);
            String converter = CamCronConverter.fromCronRepeat(repeat);

            JSONObject json = new JSONObject();
            json.put("alarm_cron", isCron? 1 : 0);
            json.put("alarm_start", beginTime);
            json.put("alarm_end", endTime);
            json.put("alarm_repeat", converter);

            String str = mApiService.setDevCron(method, deviceId, accessToken, json.toString());
            response = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiSetAlarmCron", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    //设置摄像机邮件信息
    @Deprecated
    public static Response apiSetDevEmail(String deviceId, String accessToken, String to, String cc
            , String server, String port, String from, String user, String passwd) {
        Response response;
        String method = "updatesetting";
        try {
            JSONObject json = new JSONObject();
            json.put("mail_to", to);
            json.put("mail_cc", cc);
            json.put("mail_server", server);
            json.put("mail_port", port);
            json.put("mail_from", from);
            json.put("mail_user", user);
            json.put("mail_passwd", passwd);

            String str = mApiService.setDevEmail(method, deviceId, accessToken, json.toString());
            response = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiSetDevEmail", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    //设置摄像机"邮件报警"开关
    @Deprecated
    public static Response apiSetCamEmailCron(String deviceId, String accessToken, boolean isCron) {
        Response response;
        String method = "updatesetting";
        try {
            JSONObject json = new JSONObject();
            json.put("alarm_mail", isCron ? 1 : 0);
            String str = mApiService.setAlarmMail(method, deviceId, accessToken, json.toString());
            response = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiSetAlarmMail", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    //设置报警灵敏度高低
    @Deprecated
    public static Response apiSetAlarmMoveLevel(String deviceId, String accessToken, int level) {
        Response response;
        String method = "updatesetting";
        try {
            JSONObject json = new JSONObject();
            json.put("alarm_move", 1);
            json.put("alarm_move_level", level);
            String str = mApiService.setAlarmMoveLevel(method, deviceId, accessToken, json.toString());
            response = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiSetAlarmMail", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    // 设置百度推送通道
    public static Response apiRegisterBaiduPush(String accessToken, String udId, String userId, String channelId) {
        Response response;
        String method = "register";
        int pushId = 1;
        try {
            JSONObject json = new JSONObject();
            json.put("channel_id", channelId);
            json.put("user_id", userId);
            String str = mApiService.registerPush(method, accessToken, udId, pushId, json.toString());
            response = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiRegisterBaiduPush", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    /**
     * 注册个推推送服务
     * @param accessToken
     * @param udId
     * @param clientId
     * @return
     */
    public static Response apiRegisterGetuiPush(String accessToken, String udId, String clientId) {
        Response response;
        String method = "register";
        int pushId = 2;
        try {
            JSONObject json = new JSONObject();
            json.put("client_id", clientId);
            String str = mApiService.registerPush(method, accessToken, udId, pushId, json.toString());
            response = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiRegisterGetuiPush", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }

    // 设置清晰度
    public static Response apiSetBitLevel(String deviceId, String accessToken, int bitlevel) {
        Response response;
        String method = "updatesetting";
        try {
            JSONObject json = new JSONObject();
            json.put("bitlevel", bitlevel);
            String str = mApiService.setBitLevel(method, deviceId, accessToken, json.toString());
            response = Response.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiSetBitLevel", e);
            response = Response.parseResponseError(e);
        }
        return response;
    }
    // 空气胶囊
    public static CapsuleResponse apiGetCapsule(String deviceId, String accessToken) {
        CapsuleResponse response;
        String method = "setting";
        String type = "capsule";
        try {
            String str = mApiService.getCamCapsule(method, type, deviceId, accessToken);
            response = CapsuleResponse.parseResponse(str);
        } catch (Exception e) {
            LoggerUtil.e("apiGetCapsule", e);
            response = CapsuleResponse.parseResponseError(e);
        }
        return response;
    }

//TODO    //设置邮件报警开关
//    public static Response apiSetDevEamilCron(String deviceId, String accessToken, boolean isCron) {
//        Response response;
//        String method = "set_dev_alarm_mail";
//        try {
//            String str = mApiService.setDevEmailCron(method, deviceId, accessToken, isCron ? 1 : 0);
//            response = Response.parseResponse(str);
//        } catch (Exception e) {
//            LoggerUtil.e("apiSetDevEamilCron", e);
//            response = Response.parseResponseError(e);
//        }
//        return response;
//    }

//TODO    //设置与服务状态
//    public static CamCloudResponse apiGetDevCloud(String accessToken) {
//        CamCloudResponse response;
//        String method = "list";
//        try {
//            String str = mApiService.getDevCloud(method, accessToken);
//            response = CamCloudResponse.parseResponse(str);
//        } catch (Exception e) {
//            LoggerUtil.e("apiGetDevCloud", e);
//            response = CamCloudResponse.parseResponseError(e);
//        }
//        return response;
//    }

}
