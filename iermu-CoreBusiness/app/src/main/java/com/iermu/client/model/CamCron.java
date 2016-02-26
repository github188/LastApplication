package com.iermu.client.model;

import java.util.Date;

/**
 *  摄像机定时模型 | 云录制定时模型 | 报警定时
 *
 * Created by wcy on 15/7/1.
 */
public class CamCron {

    private boolean     isCron; //定时开关
    private Date        start;  //起始时间
    private Date        end;    //结束时间
    private CronRepeat  repeat; //生效日期

    public boolean isCron() {
        return isCron;
    }

    public void setCron(boolean isCron) {
        this.isCron = isCron;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public CronRepeat getRepeat() {
        return repeat;
    }

    public void setRepeat(CronRepeat repeat) {
        this.repeat = repeat;
    }
}
