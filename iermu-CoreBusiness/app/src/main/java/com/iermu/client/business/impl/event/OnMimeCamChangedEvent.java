package com.iermu.client.business.impl.event;

import com.iermu.client.model.CamLive;

import java.util.List;

/**
 * 我的摄像机列表更新事件
 *
 * Created by wcy on 15/8/4.
 */
public interface OnMimeCamChangedEvent {

    /**
     * 我的摄像机列表更新事件
     * @param list
     */
    public void onMimeCamChanged(List<CamLive> list);

}
