package com.iermu.client.listener;

import com.iermu.client.model.Business;

/**
 *  设置摄像机云录制开关
 *
 * Created by wcy on 15/7/7.
 */
public interface OnSetDevCvrListener {

    public void onSetDevCvr(Business business,boolean isDevCvr);

}
