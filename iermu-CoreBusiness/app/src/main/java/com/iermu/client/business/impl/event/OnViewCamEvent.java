package com.iermu.client.business.impl.event;

/**
 * 观看摄像机事件(统计计数)
 *
 * Created by wcy on 15/9/8.
 */
public interface OnViewCamEvent {

    public void onViewCamEvent(String deviceId, int num);

}
