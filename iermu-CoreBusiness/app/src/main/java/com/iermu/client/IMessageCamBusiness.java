package com.iermu.client;

import com.iermu.client.model.AlarmImageData;

import java.util.List;

/**
 * 消息摄像头业务
 *  1.消息
 *  2.消息信息
 *
 * Created by zhoushaopei on 15/6/21.
 */
public interface IMessageCamBusiness extends IBaseBusiness {

    /**
     * 获取我的摄像头个数
     *
     * @return
     */
    public int getMineCamLiveCount();

    /**
     * 获取报警设备列表
     */
    public void getMineCamList();

    /**
     * 下拉刷新告警gridView数据
     */
    public void syncNewImageDatas(String deviceId);

    /**
     * 上拉刷新告警gridView数据
     */
    public void syncOldImageDatas(String deviceId);

    /**
     * 获取告警消息gridView数据
     *
     * @return
     */
    public List<AlarmImageData> getImageDatas();

    /**
     * 更具id列表删除图片数据
     *
     * @param ids
     */
    public void deleteImageDatas(List<Long> ids);

    /**
     * 获取打开报警的设备个数
     *
     * @return
     */
    public long getOpendAlarmCamCount();
}
