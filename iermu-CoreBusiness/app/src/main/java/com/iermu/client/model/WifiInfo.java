package com.iermu.client.model;

/**
 * Created by zhoushaopei on 15/9/22.
 */
public class WifiInfo {
    private String ssid;
    private String account;
    private String pass;
    private long id;

    public WifiInfo() {}

    public WifiInfo(String ssid, String account, String pass) {
        this.ssid = ssid;
        this.account = account;
        this.pass = pass;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public boolean hasId() {
        return false;
    }

    public boolean hasSsid() {
        if (ssid != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasAccount() {
        if (account != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasPass() {
        if (pass != null) {
            return true;
        } else {
            return false;
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }



    public static class Builder{
        private String ssid;
        private String account;
        private String pass;
        private long id;

        public Builder() {
        }

        public String getSsid() {
            return ssid;
        }

        public void setSsid(String ssid) {
            this.ssid = ssid;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getPass() {
            return pass;
        }

        public void setPass(String pass) {
            this.pass = pass;
        }

        public WifiInfo build() {
            WifiInfo wifiInfo = new WifiInfo();
            wifiInfo.setSsid(ssid);
            wifiInfo.setAccount(account);
            wifiInfo.setPass(pass);
            return wifiInfo;
        }

        public void setId(long id) {
            this.id = id;
        }


    }
}


