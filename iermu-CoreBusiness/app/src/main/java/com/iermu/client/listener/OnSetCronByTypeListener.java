package com.iermu.client.listener;

import com.iermu.client.model.Business;
import com.iermu.client.model.constant.CronType;

/**
 *  设置定时信息 (开关机定时、报警定时、录像定时)
 *
 * Created by wcy on 15/7/7.
 */
public interface OnSetCronByTypeListener {

    public void onSetCron(CronType type, Business business);

}
