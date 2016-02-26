package com.iermu.client.listener;

import com.iermu.client.model.CamLive;

/**
 * Created by zhangxq on 15/8/13.
 */
public interface OnCamLiveFindListener {
    /**
     * 获取到制定摄像机事件
     *
     * @param camLive
     */
    public void onFindCamLive(CamLive camLive);
}
