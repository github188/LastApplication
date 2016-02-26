package com.iermu.client;

import com.iermu.client.model.CamAlarm;
import com.iermu.client.model.CamCron;
import com.iermu.client.model.CamCvr;
import com.iermu.client.model.CamInfo;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.CamStatus;
import com.iermu.client.model.CronRepeat;
import com.iermu.client.model.Email;
import com.iermu.client.model.viewmodel.CamUpdateStatus;

import java.util.Date;
import java.util.List;

/**
 * 摄像机设置(配置信息)业务
 *  1.授权管理
 *  2.云录制
 *  3.设备信息
 *  4.设置
 *
 * Created by wcy on 15/6/21.
 */
public interface ICamSettingBusiness extends IBaseBusiness {

    /**
     * 同步摄像机配置信息
     *
     * 事件函数:
     *      @see com.iermu.client.listener.OnCamSettingListener
     *
     * @param deviceId 设备ID
     */
//    public void syncCamSetting(String deviceId);

    /**
     * 获取摄像机设备信息
     * @param deviceId
     */
    public void syncCamInfo(String deviceId);

    /**
     * 获取摄像机状态信息
     * @param deviceId
     */
    public void syncCamStatus(String deviceId);

    /**
     * 获取摄像机开关机定时信息
     * @param deviceId
     */
    public void syncCamPowerCron(String deviceId);


    /**
     * 获取摄像机云录制信息
     * @param deviceId
     */
    public void syncCamCvr(String deviceId);

    /**
     * 获取摄像机报警通知
     * @param deviceId
     */
    public void syncCamAlarm(String deviceId);

    /**
     * 获取云服务状态
     */
    public void syncCamCloud();

    /**
     * 固件升级并返回审计状态
     */
    public void checkCamFirmware(String deviceId);

    /**
     * 退出升级状态查询
     */
    public void exitExitcheckCamFirmware();

    /**
     * 查询最新版本信息
     */
    public void checkUpgradeVersion(String deviceId);

    /**
     * 重启摄像机
     * @param deviceId 设备ID
     */
    public void restartCamDev(String deviceId);

    /**
     * 注销摄像机
     * @param deviceId  设备ID
     */
    public void dropCamDev(String deviceId);

    /**
     * 更新摄像机名称
     * @param deviceId 设备ID
     * @param camName  摄像机名称
     */
    public void updateCamName(String deviceId, String camName);

    /**
     * 摄像机开关
     * @param deviceId  设备ID
     * @param powerSwitch    是否打开
     */
    public void powerCamDev(String deviceId, boolean powerSwitch);

    /**
     * 设置摄像机最大上行带宽
     * @param deviceId
     * @param maxSpeed
     */
    public void setCamMaxspeed(String deviceId, int maxSpeed);

    /**
     * 设置摄像机指示灯开关
     * @param deviceId
     * @param light
     */
    public void setDevLight(String deviceId, boolean light);

    /**
     * 设置摄像机画面是否翻转180度
     * @param deviceId
     * @param invert
     */
    public void setDevInvert(String deviceId, boolean invert);

    /**
     * 设置摄像机云录制开关
     * @param deviceId
     * @param isCvr
     */
    public void setDevCvr(String deviceId, boolean isCvr);

    /**
     * 设置摄像机静音
     * @param deviceId
     * @param audio
     */
    public void setDevAudio(String deviceId, boolean audio);

    /**
     * 设置摄像机场景
     *
     * 事件函数:
     *      @see com.iermu.client.listener.OnSetDevSceneListener
     *
     * @param deviceId
     * @param scene
     */
    public void setDevScene(String deviceId, boolean scene);

    /**
     * 设置摄像机夜视模式
     * @param deviceId
     * @param nightmode
     */
    public void setDevNightMode(String deviceId, int nightmode);

    /**
     * 设置摄像机曝光模式
     * @param deviceId
     * @param exposemode
     */
    public void setDevExposeMode(String deviceId, int exposemode);

    /**
     * 设置摄像机开关定时
     * @param deviceId
     * @param isCron
     * @param begin
     * @param end
     * @param repeat
     */
    public void setCamCron(String deviceId, boolean isCron, Date begin, Date end, CronRepeat repeat);

    /**
     * 设置摄像机开关
     * @param deviceId
     * @param isCron
     */
//    public void setCamCron(String deviceId, boolean isCron);

    /**
     * 设置报警信息 (默认打开推送、打开移动报警、打开报警定时开关)
     * @param deviceId
     * @param level     移动报警级别
     * @param begin
     * @param end
     * @param repeat
     */
    public void setCamAlarm(String deviceId,int level, Date begin, Date end, CronRepeat repeat);

//    /**
//     * 设置摄像机定时时间
//     * @param deviceId
//     * @param begin     开始时间
//     * @param end       结束时间
//     */
//    public void setCamCronTime(String deviceId, Date begin, Date end);
//
//    /**
//     * 设置摄像机重复周期
//     * @param deviceId
//     * @param repeat    重复周期
//     */
    public void setCamCronRepeat(String deviceId, CronRepeat repeat);

    /**
     * 设置云录制开关
     * @param deviceId
     * @param isCvr
     */
    public void setCvr(String deviceId, boolean isCvr);

    /**
     * 设置云录制定时开关时间
     * @param deviceId
     * @param isCron
     */
    public void setCvrCron(String deviceId, boolean isDevCvr, boolean isCron, Date begin, Date end, CronRepeat repeat);

//
//    /**
//     * 设置云录制重复周期
//     * @param deviceId
//     * @param repeat
//     */
//    public void setCvrCronRepeat(String deviceId, CronRepeat repeat);

    /**
     * 设置报警通知开关
     * @param deviceId
     * @param isNotice
     */
//    public void setAlarmNotice(String deviceId, boolean isNotice);

//    /*
//     * 设置消息报警定时开关
//     * @param deviceId
//     * @param isCron
//     */
//    public void setAlarmCron(String deviceId, boolean isCron);

    /**
     * 设置消息报警定时时间
     * @param deviceId
     * @param begin
     * @param end
     */
    public void setAlarmCronTime(String deviceId, Date begin, Date end);

    /**
     * 设置消息报警重复周期
     * @param deviceId
     * @param repeat
     */
    public void setAlarmCronRepeat(String deviceId, CronRepeat repeat);

    /**
     * 设置消息报警"邮件通知"开关
     * @param deviceId
     * @param isCron
     */
    public void setCamEamilCron(String deviceId, boolean isCron);

    /**
     * 设置摄像机消息报警邮箱配置
     * @param deviceId  设备ID
     * @param from     发件人
     * @param to        收件人
     * @param cc        抄送
     * @param server    发件服务器
     * @param port      端口
     * @param user      用户名
     * @param passwd      密码
     * @param isSSL     是否开启SSL
     */
    public void setDevEmail(String deviceId, String to, String cc, String server
            , String port, String from, String user, String passwd, boolean isSSL);

    /**
     * 设置报警灵敏度高低
     * @param deviceId
     * @param level
     */
    public void setAlarmMoveLevel(String deviceId, int level);

    /**
     * 设置百度推送通道
     *
     * @param udId
     * @param pushId
     * @param userId
     * @param channelId
     */
//    public void setAlertPushChannel(String udId, int pushId, String userId, String channelId);

    /**
     * 启动注册百度推送配置信息
     * @param userId
     * @param channelId
     */
    public void startRegisterBaiduPush(String userId, String channelId);

    /**
     * 启动注册个推推送配置信息
     * @param clientId
     */
    public void startRegisterGetuiPush(String clientId);

    /**
     * 停止报警推送
     * @param deviceId
     */
    public void stopAlarmPush(String deviceId);

    /**
     * 设置清晰度
     *
     * @param deviceId
     * @param bitLevel
     */
    public void setBitLevel(String deviceId, int bitLevel);

    /**
     * 获取空气胶囊温湿度
     *
     * @param deviceId
     */
    public void getCapsule(String deviceId);

    /**
     * 获取摄像机设备信息
     * @param deviceId
     * @return
     */
    public CamInfo getCamInfo(String deviceId);

    /**
     * 获取消息报警定时信息
     * @param deviceId
     * @return
     */
    public CamCron getAlarmCron(String deviceId);

    /**
     * 获取摄像机定时信息
     * @param deviceId
     * @return
     */
    public CamCron getCamCron(String deviceId);

    /**
     * 获取云录制定时信息
     * @param deviceId
     * @return
     */
    public CamCron getCvrCron(String deviceId);

    /**
     * 获取摄像机更多状态信息
     * @param deviceId
     */
    public CamStatus getCamStatus(String deviceId);

    /**
     * 获取摄像机邮件配置信息
     * @param deviceId
     * @return
     */
    public Email getCamEmail(String deviceId);

    /**
     * 获取摄像机消息报警设置信息
     * @param deviceId
     */
    public CamAlarm getCamAlarm(String deviceId);

    /**
     * 获取摄像机云录制信息
     * @param deviceId
     * @return
     */
    public CamCvr getCamCvr(String deviceId);

    /**
     * 获取摄像机与服务状态
     */
    public List<CamLive> getCamCloud();

    /**
     * 获取摄像机升级状态
     * @param deviceId
     * @return
     */
    public void getCamUpdateStatus(String deviceId);

}
