package com.iermu.client.listener;

import com.iermu.client.model.Business;
import com.iermu.client.model.CamCapsule;

/**
 * 获取摄像机的温湿度
 *
 * Created by zhoushaopei on 15/11/10.
 */
public interface OnCapsuleListener {

    /**
     * 接口回调 成功｜失败
     * @param capsule
     * @param bus
     */
    public void onCapsule(CamCapsule capsule, Business bus);
}
