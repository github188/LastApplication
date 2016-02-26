package com.iermu.client;

import com.iermu.client.model.CamLive;

import java.util.List;

/**
 * 公开摄像头业务
 *  1.公共频道
 *  2.公开摄像头信息
 *  3.获取指定设备的弹幕信息
 *
 *  是否分离业务关注?
 *  3.公开摄像头评论
 *  4.公开摄像头举报
 *  5.公开摄像头分享
 *  6.公开摄像头收藏
 *
 * Created by wcy on 15/6/21.
 */
public interface IPubCamBusiness extends IBaseBusiness {

    // 取新的一页
    public void syncNewCamList(String orderby);

    // 取老的一页
    public void syncOldCamList(String orderby);

    /**
     * 观看摄像机(统计计数)
     * @param deviceId 设备ID
     */
    public void viewCam(String deviceId);

    // 获取列表
    public List<CamLive> getCamList(String orderby);

    // 获取一个视频信息
    public CamLive getCamLive(String shareId, String uk);

    /**
     * 获取下一页页数
     *
     * @param orderby
     * @return
     */
    public int getNextPageNum(String orderby);
}
