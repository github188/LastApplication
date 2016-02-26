package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 * 手动注册设备
 *
 * Created by zsp on 15/7/23.
 */
public interface OnRegisterDevListener {

    /**
     * 手动注册
     * @param business
     */
    public void onRegisterDev(Business business);

}
