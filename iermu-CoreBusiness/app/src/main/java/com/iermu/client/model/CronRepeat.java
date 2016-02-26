package com.iermu.client.model;

import java.io.Serializable;

/**
 * 定时周期模型
 *
 * Created by wcy on 15/7/11.
 */
public class CronRepeat implements Serializable {

    private boolean isMonday;       //星期一
    private boolean isTuesday;      //星期二
    private boolean isWednesday;    //星期三
    private boolean isThursday;     //星期四
    private boolean isFriday;       //星期五
    private boolean isSaturday;     //星期六
    private boolean isSunday;       //星期日


    /**
     * 判断是否有无效值 (至少选择一个)
     * @return
     */
    public boolean isInValid() {
        return !isMonday && !isTuesday && !isWednesday && !isThursday && !isFriday && !isSaturday && !isSunday;
    }

    /**
     * 设置默认值
     */
    public void setDefault() {
        this.isMonday    = true;
        this.isTuesday   = true;
        this.isWednesday = true;
        this.isThursday  = true;
        this.isFriday    = true;
        this.isSaturday  = true;
        this.isSunday    = true;
    }

    public boolean isMonday() {
        return isMonday;
    }

    public void setMonday(boolean isMonday) {
        this.isMonday = isMonday;
    }

    public boolean isTuesday() {
        return isTuesday;
    }

    public void setTuesday(boolean isTuesday) {
        this.isTuesday = isTuesday;
    }

    public boolean isWednesday() {
        return isWednesday;
    }

    public void setWednesday(boolean isWednesday) {
        this.isWednesday = isWednesday;
    }

    public boolean isThursday() {
        return isThursday;
    }

    public void setThursday(boolean isThursday) {
        this.isThursday = isThursday;
    }

    public boolean isFriday() {
        return isFriday;
    }

    public void setFriday(boolean isFriday) {
        this.isFriday = isFriday;
    }

    public boolean isSaturday() {
        return isSaturday;
    }

    public void setSaturday(boolean isSaturday) {
        this.isSaturday = isSaturday;
    }

    public boolean isSunday() {
        return isSunday;
    }

    public void setSunday(boolean isSunday) {
        this.isSunday = isSunday;
    }

    public boolean isLastOne() {
        int monday = isMonday == true ? 1 : 0;
        int tuesday = isTuesday == true ? 1 : 0;
        int wednesday = isWednesday == true ? 1 : 0;
        int thursday = isThursday == true ? 1 : 0;
        int friday = isFriday == true ? 1 : 0;
        int saturday = isSaturday == true ? 1 : 0;
        int sunday = isSunday == true ? 1 : 0;
        boolean select = (monday+tuesday+wednesday+thursday+friday+saturday+sunday) == 1 ? true : false;
        if (select) {
            return true;
        } else {
            return false;
        }
    }

}
