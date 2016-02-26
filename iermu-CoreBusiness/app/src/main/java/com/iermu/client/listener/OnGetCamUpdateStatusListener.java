package com.iermu.client.listener;

import com.iermu.client.model.Business;
import com.iermu.client.model.viewmodel.CamUpdateStatus;

/**
 * 获取摄像机升级状态
 *
 * Created by zsj on 16/1/4.
 */
public interface OnGetCamUpdateStatusListener {

    public void OnGetCamUpdateStatus(CamUpdateStatus status, Business business);

}
