package com.iermu.client.listener;

import com.iermu.client.model.Business;
import com.iermu.client.model.constant.CronType;

/**
 *  设置摄像机定时
 *
 * Created by wcy on 15/7/7.
 */
public interface OnSetDevCronListener {

    public void onSetCamCron(CronType type, Business business,boolean isCron);

}
