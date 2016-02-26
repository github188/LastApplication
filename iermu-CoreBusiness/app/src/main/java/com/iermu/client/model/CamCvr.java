package com.iermu.client.model;

import java.util.Date;

/**
 *  云录制
 *
 * Created by wcy on 15/7/1.
 */
public class CamCvr {

    private boolean isCvr;  //云录制开关
    private CamCron cron;   //云录制定时


    public boolean isCvr() {
        return isCvr;
    }

    public void setCvr(boolean isCvr) {
        this.isCvr = isCvr;
    }

    public CamCron getCron() {
        return cron;
    }

    public void setCron(CamCron cron) {
        this.cron = cron;
    }
}
