package com.iermu.client;

import com.iermu.client.model.CamLive;
import com.iermu.client.model.viewmodel.MimeCamItem;

import java.util.List;

/**
 * 我的摄像头
 *  1.我的摄像机信息
 *  2.我的摄像机录像数据(时间轴)
 *  3.注销设备
 *  4.注册设备
 *  5.检查设备是否注册
 *  6.设置(修改)摄像头名称
 *
 *  7.我的某个摄像头已经授权访问的用户列表
 *  8.取消某用户授权
 *  9.关闭授权摄像头
 *  10.创建/查询分享摄像头
 *  11.获取分享摄像头的url
 *  12.第三方应用获取耳目授权接口
 *
 *  是否分离业务关注?
 *  3.我的摄像头分享管理
 *  4.我的摄像头语音
 *  5.我的摄像头录像
 *
 * Created by wcy on 15/6/21.
 */
public interface IMimeCamBusiness extends IBaseBusiness {

    /**
     * 同步更新最新一页摄像机列表
     *
     * 事件函数:
     *      @see com.iermu.client.listener.OnMimeCamChangedListener
     */
    public void syncNewCamList();

    /**
     * 同步更新最老一页摄像机列表
     *
     * 事件函数:
     *      @see com.iermu.client.listener.OnMimeCamChangedListener
     */
    public void syncOldCamList();

    /**
     * 查找指定设备
     * @param deviceId 设备ID
     */
    public void findCamLive(String deviceId);

    /**
     * 更新指定设备的状态
     * @param deviceId
     */
    public void syncLiveStatus(String deviceId);

    /**
     * 获取摄像机
     * @param deviceId
     * @return
     */
    public CamLive getCamLive(String deviceId);

    /**
     * 获取我的摄像机列表
     * @return
     */
    public List<CamLive> getCamList();

    /**
     * 获取我的摄像机Item列表
     * @return
     */
    public List<MimeCamItem> getCamItemList();

    /**
     * 获取我的摄像机计数
     * @return
     */
    public void getMineCamCount();

    /**
     * 获取我的摄像机列表
     *
     * @return
     */
    public List<CamLive> getMineCamList();

    /**
     * 删除收藏设备
     *
     * @param deviceId
     */
    public void deleteCamLive(String deviceId);

    /**
     * 获取当前添加设备的名称
     *
     * @param deviceId
     */
    public String getCamDescription(String deviceId);

    /**
     * 获取下一页页数
     */
    public int getNextPageNum();
}
