package com.iermu.client.business.impl.event;

/**
 * 修改摄像机名称事件
 *
 * Created by wcy on 15/8/13.
 */
public interface OnUpdateCamNameEvent {

    /**
     * 摄像机名称修改事件
     * @param deviceId
     * @param name
     * @param success
     */
    public void onUpdateCamName(String deviceId, String name, boolean success);

}
