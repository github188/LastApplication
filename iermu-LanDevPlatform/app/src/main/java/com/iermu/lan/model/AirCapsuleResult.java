package com.iermu.lan.model;

import java.util.HashMap;

/**
 * Created by richard on 15/11/9.
 */
public class AirCapsuleResult {

    private ErrorCode errorCode;
    private boolean isSport;//是否支持空气胶囊
    private double temperature;//温度
    private double humidity;//湿度

    public AirCapsuleResult(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isSport() {
        return isSport;
    }

    public void setIsSport(boolean isSport) {
        this.isSport = isSport;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    @Override
    public String toString() {
        return "AirCapsuleResult{" +
                "errorCode=" + errorCode +
                ", isSport=" + isSport +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                '}';
    }
}
