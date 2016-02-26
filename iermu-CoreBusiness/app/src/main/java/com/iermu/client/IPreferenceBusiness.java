package com.iermu.client;

import com.iermu.client.config.PreferenceConfig;

import java.util.Map;

/**
 * 用户偏好设置(设置中心)
 * 1.用户名、Token
 * <p>
 * Created by wcy on 15/6/21.
 */
public interface IPreferenceBusiness extends IBaseBusiness {

    /**
     * 保存摄像机消息报警邮件配置SSL
     *
     * @param isSSL
     */
    public void setDevEmailSSL(String deviceId, boolean isSSL);

    /**
     * 摄像机消息报警邮件配置是否开启SSL
     *
     * @param deviceId 设备ID
     */
    public boolean isDevEmailSSL(String deviceId);

    /**
     * 保存摄像机手动配置开关开启
     *
     * @param b
     */
    public void setAdvanceConfig(boolean b);

    /**
     * 摄像机手动配置开关是否开启 (manualMode)
     *
     * @return
     */
    public boolean getAdvancedConfig();

    /**
     * 保存声音开关状态
     *
     * @param deviceId
     * @param soundType
     * @param isOpen
     */
    public void setSoundStatus(String deviceId, PreferenceConfig.SoundType soundType, boolean isOpen);

    /**
     * 获取声音开关状态
     *
     * @param deviceId
     * @param soundType
     * @return
     */
    public boolean getSoundStatus(String deviceId, PreferenceConfig.SoundType soundType);

    /**
     * 保存版本是否升级
     *
     * @return
     */
    public void setAlertedUpdateVersion(int newVersionCode);

    /**
     * 获取版本是否升级
     *
     * @return
     */
    public int getAlertedUpdateVersion();

    /**
     * 空气胶囊dialog显示
     *
     * @param deviceId
     */
    public void setCapsuleDialogShow(String deviceId);

    /**
     * 判断空气胶囊dialog是否显示
     */
    public boolean isCapsuleDialogShow(String deviceId);

    /**
     * 活动页图片地址
     *
     * @param imgUrl
     */
    public void setPosterImgUrl(String imgUrl);

    /**
     * 获取活动页图片地址
     *
     * @return
     */
    public String getPosterImgUrl();

    /**
     * 活动页网页
     *
     * @param webUrl
     */
    public void setPosterWebUrl(String webUrl);

    /**
     * 获取活动页网页
     *
     * @return
     */
    public String getPosterWebUrl();

    /**
     * 活动开始时间
     *
     * @param startTime
     */
    public void setPosterStartTime(long startTime);

    /**
     * 获取活动开始时间
     *
     * @return
     */
    public long getPosterStartTime();

    /**
     * 活动结束时间
     *
     * @param endTime
     */
    public void setPosterEndTime(long endTime);

    /**
     * 获取活动结束时间
     *
     * @return
     */
    public long getPosterEndTime();

    /**
     * 红蓝灯交替闪烁
     */
    public void setRebBlueLight(boolean isFirst);

    /**
     * 获取红蓝灯交替闪烁
     */
    public boolean getRebBlueLight();

    /**
     * 输入wifi密码
     */
    public void setInputWifiPa(boolean isInput);

    /**
     * 获取输入wifi密码
     */
    public boolean getInputWifiPa();

    /**
     * 存我的摄像机数量
     *
     * @return
     */
    public void setMyCamCount(int count);

    /**
     * 获取我的摄像机数量
     *
     * @return
     */
    public int getMyCamCount();

    /**
     * 获取录像剪辑提示是否显示过
     *
     * @return
     */
    public boolean getFilmEditIsShowd();

    /**
     * 设置录像剪辑提示是否显示过
     *
     * @param isShowd
     */
    public void setFilmEditIsShowd(boolean isShowd);

    /**
     * 设置上一次录像剪辑开始时间和结束时间
     *
     * @param startTime
     * @param endTime
     */
    public void setFilmEditTime(long startTime, long endTime);

    /**
     * 获取录像剪辑开始时间
     */
    public long getFilmEditStartTime();

    /**
     * 获取录像剪辑结束时间
     */
    public long getFilmEditEndTime();

    /**
     * 设置上次剪辑是否失败
     *
     * @param isFaild
     */
    public void setFilmEditIsFaild(boolean isFaild);

    /**
     * 获取上次剪辑是否失败
     */
    public boolean getFilmEditIsFaild();

    /**
     * 保存摄像机手动获取IP地址
     *
     * @param ipModeJson
     */
    public void setIpMode(String ipModeJson);

    /**
     * 获取摄像机手动获取IP地址
     *
     */
    public String getIpMode();

    /**
     * 获取推送服务注册状态
     * @param uid       用户ID
     * @param pushType  Push类型
     * @return
     */
    public boolean isRegistedPush(String uid, int pushType);

    /**
     * 保存推送服务注册状态
     * @param uid       用户ID
     * @param pushType  Push类型
     * @param registed  是否注册成功
     */
    public void setRegisterPush(String uid, int pushType, boolean registed);

    /**
     * 获取推送服务配置信息
     * @param pushType  Push类型
     * @return
     */
    public Map<String,String> getPushConfig(int pushType);

    /**
     * 是否存在推送服务配置信息
     * @param pushType
     * @return
     */
    public boolean existPushConfig(int pushType);

    /**
     * 保存百度推送配置信息
     * @param userId
     * @param channelId
     */
    public void setBaiduPushConfig(String userId, String channelId);

    /**
     * 保存个推推送配置信息
     * @param clientId
     */
    public void setGetuiPushConfig(String clientId);

    /**
     * 保存设备添加错误次数
     * @param times
     */
    public void setDevAddErrorTimes(int times);

    /**
     * 获取设备添加错误次数
     */
    public int getDevAddErrorTimes();
}
