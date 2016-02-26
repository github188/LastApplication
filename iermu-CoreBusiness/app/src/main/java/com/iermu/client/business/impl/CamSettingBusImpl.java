package com.iermu.client.business.impl;

import android.content.Context;
import android.text.TextUtils;

import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.IPreferenceBusiness;
import com.iermu.client.business.api.CamSettingApi;
import com.iermu.client.business.api.MimeCamApi;
import com.iermu.client.business.api.response.CamAlarmResponse;
import com.iermu.client.business.api.response.CamCvrResponse;
import com.iermu.client.business.api.response.CamInfoResponse;
import com.iermu.client.business.api.response.CamPowerCronResponse;
import com.iermu.client.business.api.response.CamStatusResponse;
import com.iermu.client.business.api.response.CamUpdateStatusResponse;
import com.iermu.client.business.api.response.CapsuleResponse;
import com.iermu.client.business.api.response.MimeCamListResponse;
import com.iermu.client.business.api.response.Response;
import com.iermu.client.business.api.response.UpgradeVersionResponse;
import com.iermu.client.business.dao.CamSettingDataWrapper;
import com.iermu.client.business.impl.event.OnAccountTokenEvent;
import com.iermu.client.business.impl.event.OnDropCamEvent;
import com.iermu.client.business.impl.event.OnLogoutEvent;
import com.iermu.client.business.impl.event.OnPowerCamEvent;
import com.iermu.client.business.impl.event.OnUpdateCamNameEvent;
import com.iermu.client.listener.OnBitLevelChangeListener;
import com.iermu.client.listener.OnCamSettingListener;
import com.iermu.client.listener.OnCapsuleListener;
import com.iermu.client.listener.OnCheckCamFirmwareListener;
import com.iermu.client.listener.OnCheckUpgradeVersionListener;
import com.iermu.client.listener.OnDevCloudListener;
import com.iermu.client.listener.OnDropCamListener;
import com.iermu.client.listener.OnGetCamUpdateStatusListener;
import com.iermu.client.listener.OnPowerCamListener;
import com.iermu.client.listener.OnRestartCamListener;
import com.iermu.client.listener.OnSetAlarmNoticeListener;
import com.iermu.client.listener.OnSetCamAlarmListener;
import com.iermu.client.listener.OnSetCamEmailCronListener;
import com.iermu.client.listener.OnSetCamMaxspeedListener;
import com.iermu.client.listener.OnSetCronByTypeListener;
import com.iermu.client.listener.OnSetDevAudioListener;
import com.iermu.client.listener.OnSetDevCronListener;
import com.iermu.client.listener.OnSetDevCvrListener;
import com.iermu.client.listener.OnSetDevEmailListener;
import com.iermu.client.listener.OnSetDevExposeModeListener;
import com.iermu.client.listener.OnSetDevInvertListener;
import com.iermu.client.listener.OnSetDevLevelListener;
import com.iermu.client.listener.OnSetDevLightListener;
import com.iermu.client.listener.OnSetDevNightModeListener;
import com.iermu.client.listener.OnSetDevSceneListener;
import com.iermu.client.listener.OnUpdateCamNameListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamAlarm;
import com.iermu.client.model.CamCapsule;
import com.iermu.client.model.CamCron;
import com.iermu.client.model.CamCvr;
import com.iermu.client.model.CamInfo;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.CamStatus;
import com.iermu.client.model.CronRepeat;
import com.iermu.client.model.Email;
import com.iermu.client.model.UpgradeVersion;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.CamSettingType;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.model.constant.CronType;
import com.iermu.client.model.constant.PushType;
import com.iermu.client.model.viewmodel.CamUpdateStatus;
import com.iermu.client.util.DateUtil;
import com.iermu.client.util.EncryptUtil;
import com.iermu.client.util.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 摄像机设置(配置信息)业务
 * <p/>
 * Created by wcy on 15/7/2.
 */
public class CamSettingBusImpl extends BaseBusiness implements ICamSettingBusiness, OnAccountTokenEvent
        , OnLogoutEvent
        //, OnSetupDevEvent, OnMimeCamChangedEvent
        , OnDropCamEvent {

    private Map<String, CamInfo> mInfoMap = new HashMap<String, CamInfo>();
    private Map<String, CamCron> mPowerCronMap = new HashMap<String, CamCron>();
    private Map<String, CamCvr> mCamCvrMap = new HashMap<String, CamCvr>();
    private Map<String, CamAlarm> mCamAlarmMap = new HashMap<String, CamAlarm>();
    private Map<String, CamStatus> mCamStatusMap = new HashMap<String, CamStatus>();
    private Map<String, CamLive> mCamCloudMap = new HashMap<String, CamLive>();
    //    private List<String> mPreloadCache = new ArrayList<String>();
    private CamSettingDataWrapper camSettingDataWrapper;

    private Context mContext;
    private String accessToken;
    private String refreshToken;
    private String uid;
    private IPreferenceBusiness mPreferenceBus;
    private int count = 0;          //我的摄像机列表总数
    private int page = 1;           //分页加载页数

    // 注册推送相关参数
    private String udId;
    private String userId;
    private String channelId;
    private int pushId;
    private boolean isRegiste;

    private  boolean upgradeflag = true;

    public CamSettingBusImpl() {
        super();
        this.mContext = ErmuApplication.getContext();
        this.camSettingDataWrapper = new CamSettingDataWrapper();
        mPreferenceBus = ErmuBusiness.getPreferenceBusiness();
        this.accessToken = ErmuBusiness.getAccountAuthBusiness().getAccessToken();
        uid = ErmuBusiness.getAccountAuthBusiness().getUid();
        subscribeEvent(OnAccountTokenEvent.class, this);
        subscribeEvent(OnLogoutEvent.class, this);
        //subscribeEvent(OnSetupDevEvent.class, this);
        //subscribeEvent(OnDropCamEvent.class, this);
        //subscribeEvent(OnMimeCamChangedEvent.class, this);
    }

    @Override
    public void syncCamInfo(String deviceId) {
        CamInfoResponse response = CamSettingApi.apiGetCamInfo(deviceId, accessToken, refreshToken);
        Business bus = response.getBusiness();
        if (bus.isSuccess()) {
            CamInfo info = response.getCamInfo();
            addCamInfoMap(deviceId, info);
            camSettingDataWrapper.updateCamSettingInfor(uid, deviceId, info.toJsonString());
        }
        sendListener(OnCamSettingListener.class, CamSettingType.INFO, deviceId, bus);
    }

    @Override
    public void syncCamCvr(String deviceId) {
        CamCvrResponse response = CamSettingApi.apiGetCloudCvr(deviceId, accessToken, refreshToken);
        Business bus = response.getBusiness();
        if (bus.isSuccess()) {
            CamCvr camCvr = response.getCamCvr();
            if (camCvr != null) addCamCvrMap(deviceId, camCvr);
        }
        sendListener(OnCamSettingListener.class, CamSettingType.CVR_CRON, deviceId, bus);
    }

    @Override
    public void syncCamAlarm(String deviceId) {
        CamAlarmResponse response = CamSettingApi.apiGetCamAlarm(deviceId, accessToken, refreshToken);
        Business bus = response.getBusiness();
        if (bus.isSuccess()) {
            CamAlarm alarm = response.getAlarm();
            addCamAlarmMap(deviceId, alarm);
            camSettingDataWrapper.updateCamSettingData(uid, deviceId, alarm.isNotice());
        }
        sendListener(OnCamSettingListener.class, CamSettingType.ALARM, deviceId, bus);

    }

    @Override
    public void syncCamStatus(String deviceId) {
        CamStatusResponse response = CamSettingApi.apiGetCamStatus(deviceId, accessToken);
        Business bus = response.getBusiness();
        if (bus.isSuccess()) {
            CamStatus status = response.getCamStatus();
            addCamStatusMap(deviceId, status);
        }
//        else {
//            getPreloadCache().remove(deviceId);//清除预加载缓存Key
//        }
        sendListener(OnCamSettingListener.class, CamSettingType.STATUS, deviceId, bus);
    }

    @Override
    public void syncCamPowerCron(String deviceId) {
        CamPowerCronResponse response = CamSettingApi.apiGetCamPowerCron(deviceId, accessToken, refreshToken);
        Business bus = response.getBusiness();
        if (bus.isSuccess()) {
            CamCron powerCron = response.getPowerCron();
            addPowerCronMap(deviceId, powerCron);
        }
        sendListener(OnCamSettingListener.class, CamSettingType.CAM_CRON, deviceId, bus);
    }

    //TODO 获取云服务状态
    @Override
    public void syncCamCloud() {
        this.page = 1;
        MimeCamListResponse response = MimeCamApi.apiDevCloud(ConnectType.BAIDU, page, accessToken);
        Business business = response.getBusiness();
        switch (business.getCode()) {
            case BusinessCode.SUCCESS:
                addCamCloudMap(response.getList());
                sendListener(OnDevCloudListener.class, business);
                break;
            default:
                sendListener(OnDevCloudListener.class, business);
                break;
        }
    }

    /**
     * 检测固件版本是否需要升级，如果需要，触发升级
     * @param deviceId
     * 0：初始状态
     *  1：开始升级
        2：开始下载
        3：下载成功
        4：正在升级
        5：升级成功
        -1：下载失败
        -2：升级失败
     */
    @Override
    public void checkCamFirmware(String deviceId) {
        CamUpdateStatusResponse camUpdateStatusResponse = null;
        Business business = new Business();
        CamUpdateStatus status = null;
        status = new CamUpdateStatus();
        status.setDeviceid(deviceId);
        status.setIntStatus(1);
        sendListener(OnGetCamUpdateStatusListener.class, status, business);

        Response response = CamSettingApi.apiCamFirmware(deviceId, accessToken);
        Business bus = response.getBusiness();
//        sendListener(OnCheckCamFirmwareListener.class, bus);
        if(bus.isSuccess()){
            int statusint = 0;
            upgradeflag = true;
            long time = System.currentTimeMillis();
            while (upgradeflag){
                if(System.currentTimeMillis()-time>300*1000){
                    statusint = -2;
                    status.setIntStatus(statusint);
                }
                if(ErmuBusiness.getAccountAuthBusiness().getAccessToken()==null||ErmuBusiness.getAccountAuthBusiness().getAccessToken().equals(""))break;//如果注销登录，停止
                camUpdateStatusResponse = CamSettingApi.apiGetCamUpdateStatus(deviceId, accessToken);
                if(camUpdateStatusResponse == null||camUpdateStatusResponse.getUpdateStatus()==null){
                    statusint = -2;
                    status.setDeviceid(deviceId);
                    status.setIntStatus(statusint);
                }else{
                    status = camUpdateStatusResponse.getUpdateStatus();
                    business = camUpdateStatusResponse.getBusiness();
                    statusint = status.getIntStatus();
                }
                sendListener(OnGetCamUpdateStatusListener.class, status, business);
                if(statusint ==5||statusint ==-1||statusint ==-2){
                    upgradeflag = false;
                    break;
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }else {
            status = new CamUpdateStatus();
            status.setIntStatus(-2);
            status.setDeviceid(deviceId);
            sendListener(OnGetCamUpdateStatusListener.class, status, bus);
        }

    }

    /**
     * 查询最新版本信息
     * @param deviceId
     */
    @Override
    public void checkUpgradeVersion(String deviceId) {
        UpgradeVersionResponse response = CamSettingApi.checkUpgradeVersion(deviceId, accessToken);
        Business bus = response.getBusiness();
        UpgradeVersion upgradeVersion = response.getUpdateStatus();
        sendListener(OnCheckUpgradeVersionListener.class, upgradeVersion, bus);
    }

    @Override
    public void exitExitcheckCamFirmware(){
        upgradeflag = false;
    };

    @Override
    public void restartCamDev(String deviceId) {
        Response response = CamSettingApi.apiRestartCamDev(deviceId, accessToken);
        Business bus = response.getBusiness();
        sendListener(OnRestartCamListener.class, bus);
    }

    @Override
    public void dropCamDev(String deviceId) {
        Response response = CamSettingApi.apiDropCamDev(deviceId, accessToken);
        Business bus = response.getBusiness();
        sendListener(OnDropCamListener.class, bus);
        publishEvent(OnDropCamEvent.class, deviceId, bus);
    }

    @Override
    public void powerCamDev(String deviceId, boolean powerSwitch) {
        Response response = CamSettingApi.apiPowerCam(deviceId, accessToken, powerSwitch);
        Business bus = response.getBusiness();
        //if (bus.getCode() == BusinessCode.SUCCESS || powerSwitch) {
        //    //摄像机关机时, 同时关闭定时
        //    setCamCron(deviceId, false);
        //}
        powerSwitch = (bus.getCode() == BusinessCode.SUCCESS) ? powerSwitch : !powerSwitch;
        try {
            Thread.sleep(200);
            publishEvent(OnPowerCamEvent.class, deviceId);
            sendListener(OnPowerCamListener.class, bus, powerSwitch);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateCamName(String deviceId, String camName) {
        Response response = CamSettingApi.apiUpdateCamName(deviceId, accessToken, camName);
        Business bus = response.getBusiness();
        boolean success = (bus.getCode() == BusinessCode.SUCCESS);
        publishEvent(OnUpdateCamNameEvent.class, deviceId, camName, success);
        sendListener(OnUpdateCamNameListener.class, bus);
    }

    @Override
    public void setCamMaxspeed(String deviceId, int maxSpeed) {
        Response response = CamSettingApi.apiSetMaxUpspeed(deviceId, accessToken, maxSpeed);
        Business bus = response.getBusiness();
        if (bus.isSuccess()) {
            CamStatus camStatus = getCamStatus(deviceId);
            if (camStatus != null) camStatus.setMaxspeed(String.valueOf(maxSpeed));
        }
        sendListener(OnSetCamMaxspeedListener.class, bus);
    }

    @Override
    public void setDevLight(String deviceId, boolean light) {
        CamStatus status = getCamStatusMap().get(deviceId);
        if (status != null) {
            status.setLight(light);
        }
        Response response = CamSettingApi.apiSetDevLight(deviceId, accessToken, light);
        Business bus = response.getBusiness();
        light = (bus.getCode() == BusinessCode.SUCCESS) ? light : !light;
        sendListener(OnSetDevLightListener.class, bus, light);
    }

    @Override
    public void setDevInvert(String deviceId, boolean invert) {
        CamStatus status = getCamStatusMap().get(deviceId);
        if (status != null) {
            status.setInvert(invert);
        }
        Response response = CamSettingApi.apiSetDevInvert(deviceId, accessToken, invert);
        Business bus = response.getBusiness();
        invert = (bus.getCode() == BusinessCode.SUCCESS) ? invert : !invert;
        sendListener(OnSetDevInvertListener.class, bus, invert);
    }


    @Override
    public void setDevCvr(String deviceId, boolean isDevCvr) {
        CamCvr camCvr = getCamCvrMap().get(deviceId);
        if (camCvr != null) {
            camCvr.setCvr(isDevCvr);
        }
        Response response = CamSettingApi.apiSetDevCvr(deviceId, accessToken, isDevCvr);
        Business bus = response.getBusiness();
        isDevCvr = (bus.getCode() == BusinessCode.SUCCESS) ? isDevCvr : !isDevCvr;
        sendListener(OnSetDevCvrListener.class, bus, isDevCvr);
    }

    @Override
    public void setDevAudio(String deviceId, boolean audio) {
        CamStatus status = getCamStatusMap().get(deviceId);
        if (status != null) {
            status.setAudio(audio);
        }
        Response response = CamSettingApi.apiSetDevAudio(deviceId, accessToken, audio);
        Business bus = response.getBusiness();
        audio = (bus.getCode() == BusinessCode.SUCCESS) ? audio : !audio;
        sendListener(OnSetDevAudioListener.class, bus, audio);
    }

    @Override
    public void setDevScene(String deviceId, boolean scene) {
        CamStatus status = getCamStatusMap().get(deviceId);
        if (status != null) {
            status.setScene(scene ? 1 : 0);
        }

        Response response = CamSettingApi.apiSetDevScene(deviceId, accessToken, scene);
        Business bus = response.getBusiness();
        sendListener(OnSetDevSceneListener.class, bus);
    }

    @Override
    public void setDevNightMode(String deviceId, int nightmode) {
        CamStatus status = getCamStatusMap().get(deviceId);
        if (status != null) {
            status.setNightmode(nightmode);
        }
        Response response = CamSettingApi.apiSetDevNightMode(deviceId, accessToken, nightmode);
        Business bus = response.getBusiness();
        sendListener(OnSetDevNightModeListener.class, bus);
    }

    @Override
    public void setDevExposeMode(String deviceId, int exposemode) {
        CamStatus status = getCamStatusMap().get(deviceId);
        if (status != null) {
            status.setExposemode(exposemode);
        }
        Response response = CamSettingApi.apiSetDevExposeMode(deviceId, accessToken, exposemode);
        Business bus = response.getBusiness();
        sendListener(OnSetDevExposeModeListener.class, bus);
    }

    @Override
    public void setCamCron(String deviceId, boolean isCron, Date begin, Date end, CronRepeat repeat) {
        if (repeat == null) {
            repeat = new CronRepeat();
            repeat.setDefault();
        }
        boolean invalid = isInvalid(begin, end);
        if (invalid) {  //判断当前摄像机定时的时间值是否无效, 如果用户有设置过有效值, 则使用用户上次设置的值
            begin = DateUtil.getBeginToday();
            end = DateUtil.getEndToday();
        }
        Response response = CamSettingApi.apiSetCamCron(deviceId, accessToken, isCron, begin, end, repeat);
        Business bus = response.getBusiness();
        if (bus.isSuccess()) {
            CamCron camCron = getPowerCronMap().get(deviceId);
            if (camCron != null) {//更新缓存
                camCron.setCron(isCron);
                camCron.setStart(begin);
                camCron.setEnd(end);
                camCron.setRepeat(repeat);
            }
        }
        sendListener(OnSetCronByTypeListener.class, CronType.POWER, bus);
    }

//    @Override
//    public void setCamCron(String deviceId, boolean isCron) {
//        CamCron camCron = getPowerCronMap().get(deviceId);
//        Date beginDate = DateUtil.getBeginToday();
//        Date endDate = DateUtil.getEndToday();
//        CronRepeat repeat = new CronRepeat();
//        repeat.setDefault();
//        if (camCron != null) {
//            Date start = camCron.getStart();
//            Date end = camCron.getEnd();
//            boolean invalid = isInvalid(start, end);
//            if (!invalid) {  //判断当前摄像机定时的时间值是否无效, 如果用户有设置过有效值, 则使用用户上次设置的值
//                beginDate = start;
//                endDate = end;
//            }
//            camCron.setCron(isCron);
//            camCron.setStart(beginDate);
//            camCron.setEnd(endDate);
//            camCron.setRepeat(repeat);
//        }
//        Response response = CamSettingApi.apiSetCamCron(deviceId, accessToken, isCron, beginDate, endDate, repeat);
//        Business bus = response.getBusiness();
//        isCron = (bus.getCode() == BusinessCode.SUCCESS) ? isCron : !isCron;
//        sendListener(OnSetDevCronListener.class, CronType.POWER, bus, isCron);
//    }

    public void startRegisterBaiduPush(String userId, String channelId) {
        IPreferenceBusiness pref = ErmuBusiness.getPreferenceBusiness();
        pref.setBaiduPushConfig(userId, channelId);
        startRegisterPush("", PushType.BAIDU);
    }

    public void startRegisterGetuiPush(String clientId) {
        IPreferenceBusiness pref = ErmuBusiness.getPreferenceBusiness();
        pref.setGetuiPushConfig(clientId);
        startRegisterPush("", PushType.GETUI);
    }

    @Override
    public void stopAlarmPush(String deviceId) {
        Response response = CamSettingApi.apiSetAlarmPush(deviceId, accessToken, false);
        Business bus = response.getBusiness();
        if (bus.getCode() == BusinessCode.SUCCESS) {
            CamAlarm alarm = getCamAlarmMap().get(deviceId);
            if (alarm != null) {
                alarm.setNotice(false);
            }
            camSettingDataWrapper.updateCamSettingData(uid, deviceId, false);
        }
        sendListener(OnSetAlarmNoticeListener.class, bus, !bus.isSuccess());
    }

    /**
     //检测推送服务注册状态(百度、个推)
     //0. 检测服务注册状态,未注册则:
     //1. 推送服务未启动
     //   重新启动推送服务
     //2. 注册推送服务
     //   注册成功 | 注册失败
     * @param deviceId
     * @param pushType
     * @return
     */
    public boolean startRegisterPush(String deviceId, int pushType) {
        IPreferenceBusiness preBus  = ErmuBusiness.getPreferenceBusiness();
        //boolean registed    = preBus.isRegistedPush(uid, pushType);
        //if(registed) return true;//服务已经注册成功

        Business bus = new Business();
        if(!preBus.existPushConfig(pushType)) {
            Logger.i("推送服务启动失败.");
            ErmuApplication.startPushWork();
            bus.setCode(BusinessCode.PUSH_FAILED);
            sendListener(OnSetCamAlarmListener.class, deviceId, bus);
            return false;
        }
        String udId = android.os.Build.SERIAL;
        Map<String, String> map = preBus.getPushConfig(pushType);
        if(pushType == PushType.GETUI) {
            String clientId = map.get("clientId");
            Response response = CamSettingApi.apiRegisterGetuiPush(accessToken, udId, clientId);
            bus = response.getBusiness();
        } else {
            String userId = map.get("userId");
            String channelId = map.get("channelId");
            Response response = CamSettingApi.apiRegisterBaiduPush(accessToken, udId, userId, channelId);
            bus = response.getBusiness();
        }

        if (bus.isSuccess()) {
            preBus.setRegisterPush(uid, pushType, true);
        } else {
            preBus.setRegisterPush(uid, pushType, false);
            bus.setErrorMsg(bus.getErrorCode() + "");
            bus.setCode(BusinessCode.REGISTE_FILED);
            sendListener(OnSetCamAlarmListener.class, deviceId, bus);
        }
        return bus.isSuccess();
    }

    @Override
    public void setCamAlarm(String deviceId, int level, Date begin, Date end, CronRepeat repeat) {//TODO 报警灵敏度与时间设置
        Business bus = new Business();
//        if (!isRegiste) {   //检测到: 当前推送通道未注册成功.
//            if (userId == null) {
//                Logger.d("绑定百度推送失败");
//                bus.setCode(BusinessCode.PUSH_FAILED);
//                ErmuApplication.startPushWork();
//                sendListener(OnSetCamAlarmListener.class, deviceId, bus);
//                return;
//            }
//            Response response = CamSettingApi.apiRegisterBaiduPush(accessToken, udId, userId, channelId);
//            bus = response.getBusiness();
//            if (bus.getCode() == BusinessCode.SUCCESS) {
//                this.isRegiste = true;// 注册成功之后记录成功状态
//            } else {
//                this.isRegiste = false;
//                bus.setErrorMsg(bus.getErrorCode() + "");
//                bus.setCode(BusinessCode.REGISTE_FILED);
//                sendListener(OnSetCamAlarmListener.class, deviceId, bus);
//                return; //推送通道打开失败
//            }
//        }
        CamLive live = ErmuBusiness.getMimeCamBusiness().getCamLive(deviceId);
        int connectType = (live!=null) ? live.getConnectType() : ConnectType.BAIDU;
        int pushType = /*(connectType==ConnectType.LINYANG) ? PushType.GETUI : */PushType.BAIDU;
        boolean b = startRegisterPush(deviceId, pushType);
        if(!b) return;  //注册推送服务失败

        if (repeat == null) {
            repeat = new CronRepeat();
            repeat.setDefault();
        }
        boolean invalid = isInvalid(begin, end);
        if (invalid) {  //判断当前摄像机定时的时间值是否无效, 如果用户有设置过有效值    , 则使用用户上次设置的值
            begin = DateUtil.getBeginToday();
            end = DateUtil.getEndToday();
        }
        Response response = CamSettingApi.apiSetCamAlarm(deviceId, accessToken, level, begin, end, repeat);
        bus = response.getBusiness();
        if (bus.isSuccess()) {
            CamCron camCron = getAlarmCron(deviceId);
            CamAlarm camAlarm = getCamAlarmMap().get(deviceId);
            if (camCron != null) {
                camCron.setCron(true);
                camCron.setStart(begin);
                camCron.setEnd(end);
                camCron.setRepeat(repeat);
            }
            if (camAlarm != null) {
                camAlarm.setMoveLevel(level);
            }
            camSettingDataWrapper.updateCamSettingData(uid, deviceId, true);
        }
        sendListener(OnSetCamAlarmListener.class, deviceId, bus);
    }

    @Override
    public void setCamCronRepeat(String deviceId, CronRepeat repeat) {
        if (repeat == null) throw new RuntimeException("Parameter can not be null.");

        CamCron camCron = getPowerCronMap().get(deviceId);
        Date beginTime = DateUtil.getBeginToday();
        Date endTime = DateUtil.getEndToday();
        if (camCron != null) {
            beginTime = camCron.getStart();
            endTime = camCron.getEnd();
            camCron.setRepeat(repeat);
        }
        Response response = CamSettingApi.apiSetCamCron(deviceId, accessToken, true, beginTime, endTime, repeat);
        Business bus = response.getBusiness();
        sendListener(OnSetDevCronListener.class, CronType.POWER, bus);
    }

    @Override
    public void setCvr(String deviceId, boolean isCvr) {
        Response response = CamSettingApi.apiSetCvr(deviceId, accessToken, isCvr);
        Business bus = response.getBusiness();
        if (bus.isSuccess()) {
            CamCvr camCvr = getCamCvrMap().get(deviceId);
            if (camCvr != null) {
                camCvr.setCvr(isCvr);
            }
        }
        sendListener(OnSetCronByTypeListener.class, CronType.CVR, bus);
    }

    @Override
    public void setCvrCron(String deviceId, boolean isDevCvr, boolean isCron, Date begin, Date end, CronRepeat repeat) {
        if (repeat == null) {
            repeat = new CronRepeat();
            repeat.setDefault();
        }
        boolean invalid = isInvalid(begin, end);
        if (invalid) {  //判断云录制定时的时间值是否无效, 如果用户有设置过有效值, 则使用用户上次设置的值
            begin = DateUtil.getBeginToday();
            end = DateUtil.getEndToday();
        }
        Response response = CamSettingApi.apiSetCvrCron(deviceId, accessToken, isDevCvr, isCron, begin, end, repeat);
        Business bus = response.getBusiness();
        if (bus.isSuccess()) {
            CamCron cvrCron = getCvrCron(deviceId);
            CamCvr camCvr = getCamCvrMap().get(deviceId);
            if (cvrCron != null) {
                cvrCron.setCron(isCron);
                cvrCron.setStart(begin);
                cvrCron.setEnd(end);
                cvrCron.setRepeat(repeat);
            }
            if (camCvr != null) {
                camCvr.setCvr(isDevCvr);
            }
        }
        sendListener(OnSetCronByTypeListener.class, CronType.CVR, bus);
    }

//    @Override
//    public void setCvrCronRepeat(String deviceId, CronRepeat repeat) {
//        if (repeat == null) throw new RuntimeException("Parameter can not be null.");
//
//        CamCron cron = getCvrCron(deviceId);
//        Date beginTime = DateUtil.getBeginToday();
//        Date endTime = DateUtil.getEndToday();
//        if (cron != null) {
//            beginTime = cron.getStart();
//            endTime = cron.getEnd();
//            cron.setRepeat(repeat);
//        }
//        Response response = CamSettingApi.apiSetCvrCron(deviceId, accessToken, true, beginTime, endTime, repeat);
//        Business bus = response.getBusiness();
//        sendListener(OnSetDevCronListener.class, CronType.CVR, bus);
//    }

//    @Override
//    public void setAlarmNotice(String deviceId, boolean isNotice) {
//        if (!isRegiste) {
//            if (userId == null) {
//                Logger.d("绑定百度推送失败");
//                sendListener(OnRegisteListener.class, null, isNotice, "绑定百度推送失败");
//                return;
//            }
//            Response response = CamSettingApi.apiRegisterBaiduPush(accessToken, udId, pushId, userId, channelId);
//            Business bus = response.getBusiness();
//            if (bus.getCode() == BusinessCode.SUCCESS) {
//                // 注册成功之后记录成功状态
//                this.isRegiste = true;
//                setAlarmNoticeOnly(deviceId, isNotice);
//            } else {
//                this.isRegiste = false;
//                sendListener(OnRegisteListener.class, bus, isNotice, null);
//            }
//        } else {
//            setAlarmNoticeOnly(deviceId, isNotice);
//        }
//    }
//
//    /**
//     * 设置报警开关
//     *
//     * @param deviceId
//     * @param isNotice
//     */
//    private void setAlarmNoticeOnly(String deviceId, boolean isNotice) {
//        Response response = CamSettingApi.apiSetAlarmNotice(deviceId, accessToken, isNotice);
//        Business bus = response.getBusiness();
//        if (bus.getCode() == BusinessCode.SUCCESS) {
//            CamAlarm alarm = getCamAlarmMap().get(deviceId);
//            if (alarm != null) {
//                alarm.setNotice(isNotice);
//            }
//            camSettingDataWrapper.updateCamSettingData(uid, deviceId, isNotice);
//        }
//        isNotice = (bus.getCode() == BusinessCode.SUCCESS) ? isNotice : !isNotice;
//        sendListener(OnSetAlarmNoticeListener.class, bus, isNotice);
//    }
//
//TODO
//    @Override
//    public void setAlarmCron(String deviceId, boolean isCron) {
//        CamCron camCron = getAlarmCron().get(deviceId);
//        String accessToken  = ErmuApplication.getAccessToken();
//        Date beginDate      = DateUtil.getBeginToday();
//        Date endDate        = DateUtil.getEndToday();
//        CronRepeat repeat   = new CronRepeat();
//        repeat.setDefault();
//        if(camCron != null) {
//            Date start = camCron.getStart();
//            Date end = camCron.getEnd();
//            boolean invalid = isInvalid(start, end);
//            if(!invalid) {  //判断云录制定时的时间值是否无效, 如果用户有设置过有效值, 则使用用户上次设置的值
//                beginDate = start;
//                endDate   = end;
//            }
//            camCron.setCron(isCron);
//            camCron.setStart(beginDate);
//            camCron.setEnd(endDate);
//            camCron.setRepeat(repeat);
//        } else {
//            syncAlarmCron(deviceId);
//        }
//        Response response = CamSettingApi.apiSetAlarmCron(deviceId, accessToken, isCron, beginDate, endDate, repeat);
//        int businessCode = response.getBusinessCode();
//        sendListener(OnSetDevCronListener.class, CronType.ALARM, (businessCode==BusinessCode.SUCCESS));
//    }

    @Override
    public void setAlarmCronTime(String deviceId, Date begin, Date end) {
        if (begin == null || end == null) throw new RuntimeException("Parameter can not be null.");
        CamAlarm alarm = getCamAlarmMap().get(deviceId);
        if (alarm != null) {
            CamCron camCron = alarm.getAlarmCron();
            CronRepeat repeat = new CronRepeat();
            synchronized (deviceId) {
                repeat.setDefault();
                boolean invalid = isInvalid(begin, end);
                if (invalid) {  //判断当前摄像机定时的时间值是否无效, 如果用户有设置过有效值, 则使用用户上次设置的值
                    begin = DateUtil.getBeginToday();
                    end = DateUtil.getEndToday();
                }

                if (camCron != null) {
                    camCron.setStart(begin);
                    camCron.setEnd(end);
                    repeat = camCron.getRepeat();
                }
            }
            Response response = CamSettingApi.apiSetAlarmCron(deviceId, accessToken, true, begin, end, repeat);
            Business bus = response.getBusiness();
            sendListener(OnSetDevCronListener.class, CronType.ALARM, bus);
        }
    }

    @Override
    public void setAlarmCronRepeat(String deviceId, CronRepeat repeat) {
        if (repeat == null) throw new RuntimeException("Parameter can not be null.");
        CamAlarm alarm = getCamAlarmMap().get(deviceId);
        if (alarm != null) {
            CamCron camCron = alarm.getAlarmCron();
            Date beginTime = DateUtil.getBeginToday();
            Date endTime = DateUtil.getEndToday();
            if (camCron != null) {
                beginTime = camCron.getStart();
                endTime = camCron.getEnd();
                camCron.setRepeat(repeat);
            }
            Response response = CamSettingApi.apiSetAlarmCron(deviceId, accessToken, true, beginTime, endTime, repeat);
            Business bus = response.getBusiness();
            sendListener(OnSetDevCronListener.class, CronType.ALARM, bus);
        }
    }

    @Override
    public void setCamEamilCron(String deviceId, boolean isCron) {
        CamAlarm alarm = getCamAlarmMap().get(deviceId);
        if (alarm != null) {
            alarm.setMail(isCron);
        }
        Response response = CamSettingApi.apiSetCamEmailCron(deviceId, accessToken, isCron);
        Business business = response.getBusiness();
        isCron = (business.getCode() == BusinessCode.SUCCESS) ? isCron : !isCron;
        sendListener(OnSetCamEmailCronListener.class, business, isCron);
    }

    @Override
    public void setDevEmail(String deviceId, String to, String cc, String server, String port
            , String from, String user, String passwd, boolean isSSL) {
        if (TextUtils.isEmpty(from) || TextUtils.isEmpty(to) || TextUtils.isEmpty(server)
                || TextUtils.isEmpty(user) || TextUtils.isEmpty(passwd)
                ) throw new RuntimeException("Parameter can not be null.");
        Map<String, CamStatus> map = getCamStatusMap();
        CamStatus status = map.get(deviceId);
        Email email = status.getEmail();
        if (email != null) {
            email.setFrom(from);
            email.setTo(to);
            email.setCc(cc);
            email.setServer(server);
            email.setPort(port);
            email.setUser(user);
            email.setIsSSL(isSSL);
            email.setPasswd(passwd);
            mPreferenceBus.setDevEmailSSL(deviceId, isSSL);
        }
        passwd = EncryptUtil.encryptPass(passwd);
        Response response = CamSettingApi.apiSetDevEmail(deviceId, accessToken, to, cc, server
                , port, from, user, passwd);
        Business business = response.getBusiness();
        sendListener(OnSetDevEmailListener.class, business);
    }

    @Override
    public void setAlarmMoveLevel(String deviceId, int level) {
        Response response = CamSettingApi.apiSetAlarmMoveLevel(deviceId, accessToken, level);
        Business business = response.getBusiness();
        if (business.getCode() == BusinessCode.SUCCESS) {
            CamAlarm alarm = getCamAlarmMap().get(deviceId);
            if (alarm != null) {
                alarm.setMoveLevel(level);
            }
        }
        sendListener(OnSetDevLevelListener.class, business);
    }

//    @Override
//    public void setAlertPushChannel(String udId, int pushId, String userId, String channelId) {
//        this.udId = udId;
//        this.userId = userId;
//        this.channelId = channelId;
//        this.pushId = pushId;
//        if (TextUtils.isEmpty(accessToken)) {
//            Logger.e("AccessToken can not be null.");
//            return;
//        }
//        Response response = CamSettingApi.apiRegisterBaiduPush(accessToken, udId, pushId, userId, channelId);
//        Business bus = response.getBusiness();
//        if (bus.getCode() == BusinessCode.SUCCESS) {
//            // 注册成功之后记录成功状态
//            this.isRegiste = true;
//        } else {
//            this.isRegiste = false;
//        }
//    }

    @Override
    public void setBitLevel(String deviceId, int bitLevel) {
        Response response = CamSettingApi.apiSetBitLevel(deviceId, accessToken, bitLevel);
        Business business = response.getBusiness();
        if (business.getCode() == BusinessCode.SUCCESS) {
            CamStatus camStatus = getCamStatus(deviceId);
            if (camStatus != null) {
                camStatus.setBitlevel(bitLevel);
            }
        }
        sendListener(OnBitLevelChangeListener.class, business, bitLevel);
    }

    @Override
    public void getCapsule(String deviceId) {
        CapsuleResponse response = CamSettingApi.apiGetCapsule(deviceId, accessToken);
        CamCapsule camCapsule = response.getCamCapsule();
        Business business = response.getBusiness();
        sendListener(OnCapsuleListener.class, camCapsule, business);
    }

    @Override
    public CamInfo getCamInfo(String deviceId) {
        return getCamInfoMap().get(deviceId);
    }

    @Override
    public CamCron getAlarmCron(String deviceId) {
        synchronized (deviceId) {
            CamAlarm camAlarm = getCamAlarmMap().get(deviceId);
            return (camAlarm != null) ? camAlarm.getAlarmCron() : null;
        }
    }

    @Override
    public CamCron getCamCron(String deviceId) {
        synchronized (deviceId) {
            return getPowerCronMap().get(deviceId);
        }
    }

    @Override
    public CamCron getCvrCron(String deviceId) {
        synchronized (deviceId) {
            CamCvr cvr = getCamCvrMap().get(deviceId);
            return (cvr != null) ? cvr.getCron() : null;
        }
    }

    @Override
    public CamStatus getCamStatus(String deviceId) {
        return getCamStatusMap().get(deviceId);
    }

    @Override
    public Email getCamEmail(String deviceId) {
        CamStatus status = getCamStatusMap().get(deviceId);
        return (status != null) ? status.getEmail() : null;
    }

    @Override
    public CamAlarm getCamAlarm(String deviceId) {
        synchronized (deviceId) {
            return getCamAlarmMap().get(deviceId);
        }
    }

    @Override
    public CamCvr getCamCvr(String deviceId) {
        synchronized (deviceId) {
            return getCamCvrMap().get(deviceId);
        }
    }

    @Override
    public List<CamLive> getCamCloud() {
        Collection<CamLive> values = getCamCloudMap().values();
        ArrayList<CamLive> cams = new ArrayList<CamLive>(values);
        List<CamLive> list = new ArrayList<CamLive>(Arrays.asList(new CamLive[cams.size()]));
        Collections.copy(list, cams);
        return list;
    }

    @Override
    public void getCamUpdateStatus(String deviceId) {
        CamUpdateStatusResponse response = null;
        Business business = null;
        CamUpdateStatus status = null;
        for (int i = 0; i < 20; i++) {//容错处理
            response = CamSettingApi.apiGetCamUpdateStatus(deviceId, accessToken);
            status = response.getUpdateStatus();
            business = response.getBusiness();
            sendListener(OnGetCamUpdateStatusListener.class, status, business);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            if (business.isSuccess()) {
//                break;
//            }
        }

    }

    @Override
    public void onTokenChanged(String uid, String accessToken, String refreshToken) {
        this.uid = uid;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    //    @Override
//    public void onSetupDevEvent(CamLive live) {
//        if (live == null) return;
//        String deviceId = live.getDeviceId();
//        //syncCamSetting(deviceId);
//        syncCamStatus(deviceId);
//    }
//
//    @Override
//    public void onMimeCamChanged(List<CamLive> list) {
//        synchronized (CamSettingBusImpl.class) {
//            Logger.i(">>>>> onMimeCamChanged");
//            for (int i = 0; i < list.size(); i++) {
//                CamLive live = list.get(i);
//                String deviceId = live.getDeviceId();
//                int dataType = live.getDataType();
//                if (getPreloadCache().contains(deviceId)) {
//                    continue;
//                } else {
//                    getPreloadCache().add(deviceId);
//                    if (dataType == LiveDataType.MIME && !live.isOffline()) {//if (live.getDataType() == 0 || live.getDataType() == 1) {   //TODO 优化
//                        Logger.i(">>>>> onMimeCamChanged deviceId:" + deviceId);
//                        //syncCamSetting(deviceId);
//                        syncCamStatus(deviceId);
//                    } else if (dataType == LiveDataType.AUTHORIZE) {
//                        syncCamStatus(deviceId);
//                    }
//                    if ((dataType == LiveDataType.MIME || dataType == LiveDataType.AUTHORIZE) && !live.isOffline()) {
//                        syncCamInfo(deviceId);
//                    }
//                }
//            }
//        }
//    }
//
    @Override
    public void onDropCamEvent(String deviceId, Business bus) {
        synchronized (CamSettingBusImpl.class) {
            Map<String, CamLive> map = getCamCloudMap();
            map.remove(deviceId);
        }
    }

    @Override
    public void OnLogoutEvent() {
        getCamInfoMap().clear();
        getPowerCronMap().clear();
        getCamCvrMap().clear();
        getCamAlarmMap().clear();
        getCamStatusMap().clear();
        getCamCloudMap().clear();
    }

    /**
     * 转化时间为 HHmmss 格式
     *
     * @param date
     * @return
     */
    private static String fromCronTime(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("HHmm");//TODO 默认忽略秒数'ss'
        return df.format(date) + "00";
    }

    //判断摄像机开关机|云录制定时的无效值
    private static boolean isInvalid(Date begin, Date end) {
        Date beginDate = DateUtil.getBeginToday();
        if (begin == null || end == null) {
            return true;
        }
        String beginDateStr = fromCronTime(beginDate);
        String beginStr = fromCronTime(begin);
        String endStr = fromCronTime(end);

        return (beginDateStr.equals(beginStr)) && (beginDateStr.equals(endStr));
    }

    //摄像机信息
    private void addCamInfoMap(String deviceId, CamInfo camInfo) {
        if (camInfo == null || TextUtils.isEmpty(deviceId)) return;
        synchronized (CamSettingBusImpl.class) {
            getCamInfoMap().put(deviceId, camInfo);
        }
    }

    //摄像机定时
    private void addPowerCronMap(String deviceId, CamCron camCron) {
        if (camCron == null || TextUtils.isEmpty(deviceId)) return;
        synchronized (CamSettingBusImpl.class) {
            getPowerCronMap().put(deviceId, camCron);
        }
    }

    //录像定时
    private void addCamCvrMap(String deviceId, CamCvr camCvr) {
        if (camCvr == null || TextUtils.isEmpty(deviceId)) return;
        synchronized (CamSettingBusImpl.class) {
            getCamCvrMap().put(deviceId, camCvr);
        }
    }

    //摄像机状态
    private void addCamStatusMap(String deviceId, CamStatus status) {
        if (status == null || TextUtils.isEmpty(deviceId)) return;
        synchronized (CamSettingBusImpl.class) {
            getCamStatusMap().put(deviceId, status);
        }
    }

    //摄像机报警配置信息
    private void addCamAlarmMap(String deviceId, CamAlarm alarm) {
        if (alarm == null || TextUtils.isEmpty(deviceId)) return;
        synchronized (CamSettingBusImpl.class) {
            getCamAlarmMap().put(deviceId, alarm);
        }
    }

    //TODO    //摄像机云服务状态
    private void addCamCloudMap(List<CamLive> list) {
        if (list == null) return;
        synchronized (CamSettingBusImpl.class) {
            for (int i = 0; i < list.size(); i++) {
                CamLive cloud = list.get(i);
                String deviceId = cloud.getDeviceId();
                getCamCloudMap().put(deviceId, cloud);
            }
        }
    }

//    private List<String> getPreloadCache() {
//        if (mPreloadCache == null) {
//            mPreloadCache = new ArrayList<String>();
//        }
//        return mPreloadCache;
//    }

    //录像定时
    private Map<String, CamCvr> getCamCvrMap() {
        if (mCamCvrMap == null) {
            mCamCvrMap = new HashMap<String, CamCvr>();
        }
        return mCamCvrMap;
    }

    //摄像机定时
    private Map<String, CamCron> getPowerCronMap() {
        if (mPowerCronMap == null) {
            mPowerCronMap = new HashMap<String, CamCron>();
        }
        return mPowerCronMap;
    }

    //摄像机信息
    private Map<String, CamInfo> getCamInfoMap() {
        if (mInfoMap == null) {
            mInfoMap = new HashMap<String, CamInfo>();
        }
        return mInfoMap;
    }

    //摄像机报警信息
    private Map<String, CamAlarm> getCamAlarmMap() {
        if (mCamAlarmMap == null) {
            mCamAlarmMap = new HashMap<String, CamAlarm>();
        }
        return mCamAlarmMap;
    }

    private Map<String, CamStatus> getCamStatusMap() {
        if (mCamStatusMap == null) {
            mCamStatusMap = new HashMap<String, CamStatus>();
        }
        return mCamStatusMap;
    }

    private Map<String, CamLive> getCamCloudMap() {
        if (mCamCloudMap == null) {
            mCamCloudMap = new HashMap<String, CamLive>();
        }
        return mCamCloudMap;
    }
}
