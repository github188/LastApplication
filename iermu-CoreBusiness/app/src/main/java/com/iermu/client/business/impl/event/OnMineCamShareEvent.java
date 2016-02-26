package com.iermu.client.business.impl.event;

import com.iermu.client.model.CamLive;

/**
 * 我的摄像机分享事件（1.私密分享 2.申请公开直播）
 *
 * Created by zhoushaopei on 15/8/19.
 */
public interface OnMineCamShareEvent {

    /**
     * 修改分享类型
     *
     * @param live
     *
     */
    public void onMineCamShare(CamLive live);

}
