package com.iermu.client.business.dao;

import com.iermu.client.business.dao.generator.DaoSession;
import com.iermu.client.business.dao.generator.WifiInfoDao;
import com.iermu.client.model.WifiInfo;

import javax.inject.Inject;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by zhoushaopei on 15/9/22.
 */
public class WifiInfoWrapper {

    @Inject static DaoSession daoSession;

    public static WifiInfo queryWifiInfo(String ssid) {
        WifiInfoDao wifiInfoDao = daoSession.getWifiInfoDao();
        QueryBuilder<WifiInfo> builder = wifiInfoDao.queryBuilder();
        builder.where(WifiInfoDao.Properties.Ssid.eq(ssid));
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        WifiInfo wifiInfo = builder.unique();
        return wifiInfo;
    }

    public static void insert(String ssid, String wifiAccount, String pass) {
        WifiInfoDao dao = daoSession.getWifiInfoDao();
        QueryBuilder<WifiInfo> builder = dao.queryBuilder();
        WifiInfo wifiInfo = builder.where(WifiInfoDao.Properties.Ssid.eq(ssid)).unique();
        if (wifiInfo == null) wifiInfo = new WifiInfo();
        wifiInfo.setSsid(ssid);
        wifiInfo.setAccount(wifiAccount);
        wifiInfo.setPass(pass);
        dao.insertOrReplace(wifiInfo);
    }

}
