package com.iermu.client;

/**
 * 我的摄像头报警业务
 *  1.查看报警消息
 *  2.删除报警消息
 *  3.报警业务设置(配置信息)
 *
 * Created by wcy on 15/6/21.
 */
public interface ICamAlarmBusiness extends IBaseBusiness {

    public void setBaiduPushMessage(String message);

    public void setGetuiPushMessage(String payload, String taskId, String messageId);

    /**
     * 报警推送是否打开状态
     * @param deviceId
     * @return
     */
    public boolean isOpenAlarmPush(String deviceId);



}
