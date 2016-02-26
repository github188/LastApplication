package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 *  设置摄像机状态灯开关
 *
 * Created by wcy on 15/7/7.
 */
public interface OnSetDevLightListener {

    public void onSetDevLight(Business business,boolean light);

}
