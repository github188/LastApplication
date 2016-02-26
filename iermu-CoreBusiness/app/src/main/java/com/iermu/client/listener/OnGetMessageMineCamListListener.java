package com.iermu.client.listener;

import com.iermu.client.model.AlarmDeviceItem;

import java.util.List;

/**
 * 报警消息页面获取摄像机列表
 * <p>
 * Created by zhangxq on 15/12/24.
 */
public interface OnGetMessageMineCamListListener {
    /**
     * 获取报警消息摄像机列表完成
     *
     * @param alarmDeviceItems
     */
    public void onGetMineCamList(List<AlarmDeviceItem> alarmDeviceItems);
}
