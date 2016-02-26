package com.iermu.client.business.impl.event;

import com.iermu.client.model.CamLive;

import java.util.List;

/**
 * 公共摄像头列表更新事件
 *
 * Created by wcy on 15/8/14.
 */
public interface OnPubCamChangedEvent {


    /**
     * 公共摄像头列表更新事件
     * @param list
     */
    public void onPubCamChanged(List<CamLive> list);


}
