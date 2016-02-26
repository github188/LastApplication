package com.iermu.client.model;

/**
 * Created by zhoushaopei on 15/11/10.
 */
public class CamCapsule {

    private double temperature; //温度
    private double humidity;    //湿度

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
}
