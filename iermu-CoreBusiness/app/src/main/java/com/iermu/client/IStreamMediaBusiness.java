package com.iermu.client;

import com.iermu.client.model.LiveMedia;
import com.iermu.lan.model.CamRecord;

/**
 * 流媒体相关业务接口
 *
 *  直播流信息
 *  录像播放列表
 *  播放录像接口
 *  查找录像起止时间
 *  录像剪辑
 *  录像剪辑任务状态
 *
 * Created by wcy on 15/7/29.
 */
public interface IStreamMediaBusiness extends IBaseBusiness {

    /**
     * 重新刷新流媒体直播信息
     * @param deviceId
     */
    public void reloadLiveMedia(String deviceId);

    /**
     * 开启我的摄像机直播信息
     * @param deviceId
     */
    public void openLiveMedia(String deviceId);

    /**
     * 开启公共摄像机直播信息
     * @param deviceId
     * @param shareId   公开分享ID
     * @param uk        用户ID
     */
    public void openPubLiveMedia(String deviceId, String shareId, String uk);

    /**
     * 关闭摄像机直播信息
     * @param deviceId
     */
    public void closeLiveMedia(String deviceId);

    /**
     * 开始播放录像
     *  @see com.iermu.client.listener.OnOpenPlayRecordListener
     *
     * @param deviceId
     */
    public void openPlayRecord(String deviceId, int startTime, int endTime, CamRecord record);


    public void openCardPlayRecord(String deviceId, int startTime, int endTime);

    /**
     * 停止播放录像
     *
     * @param deviceId
     */
    public void closeRecord(String deviceId);

    /**
     * 获取直播信息
     * @param deviceId
     * @return
     */
    public LiveMedia getLiveMedia(String deviceId);

    /**
     * 校准播放开始时间
     *
     * @param deviceId
     * @param oldStartTime
     */
    public void vodSeek(String deviceId, int oldStartTime, int num);
}
