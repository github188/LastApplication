package com.iermu.client.model;

/**
 * 最新版本信息
 * Created by jack on 16/1/14.
 */
public class UpgradeVersion {

    private int needUpgrade;
    private String version;
    private String desc;

    public int getNeedUpgrade() {
        return needUpgrade;
    }

    public void setNeedUpgrade(int needUpgrade) {
        this.needUpgrade = needUpgrade;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "UpgradeVersion{" +
                "needUpgrade=" + needUpgrade +
                ", version='" + version + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
