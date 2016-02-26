package com.iermu.client;

import com.iermu.client.business.impl.stat.StatCode;
import com.iermu.client.model.CamDev;
import com.iermu.client.model.CamDevConf;

/**
 * 统计业务接口
 *
 * Created by zhangxq on 15/11/24.
 */
public interface IStatisticsBusiness extends IBaseBusiness {

    /**
     * 收集模块统计日志
     * @param code   统计模块Code码
     * @param key    键值
     * @param value  键/值
     */
    public void statCollectLog(StatCode code, String key, String value);

    /**
     * 上传添加流程统计日志
     */
    public void pushSetupDevLog();

    /**
     * 统计打开视频是否成功
     * @param deviceId
     * @param connectType
     * @param status
     * @param playerErrorCode
     */
    public void statStartPlay(String deviceId, int connectType, int connectRet, int status, int playerErrorCode);

    /**
     * 统计打开视频是否成功
     * @param deviceId
     * @param connectType
     * @param connectRet
     * @param status
     */
    public void statStartPlay(String deviceId, int connectType, int connectRet, int status);

    /**
     * 统计添加设备日志信息
     * @param dev
     * @param conf
     * @param manualMode 手动配置模式
     * @param errorCode  错误状态码
     */
    public void statSetupDev(CamDev dev, CamDevConf conf, boolean manualMode, int errorCode);

    /**
     * 统计绑定百度推送失败
     *
     * @param deviceId
     */
    public void statPushFail(String deviceId);

}
