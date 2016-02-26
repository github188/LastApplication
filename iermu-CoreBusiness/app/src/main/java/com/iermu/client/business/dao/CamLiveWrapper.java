package com.iermu.client.business.dao;

import com.iermu.client.business.dao.generator.CamLiveDao;
import com.iermu.client.business.dao.generator.DaoSession;
import com.iermu.client.model.CamLive;
import com.iermu.client.util.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by zhoushaopei on 15/10/22.
 */
public class CamLiveWrapper {

    @Inject static DaoSession daoSession;

    public static CamLive queryCamLive(String deviceId, String uid) {
        CamLiveDao camLiveDao = daoSession.getCamLiveDao();
        QueryBuilder<CamLive> builder = camLiveDao.queryBuilder();
        builder.where(CamLiveDao.Properties.Uid.eq(uid));
        builder.where(CamLiveDao.Properties.DeviceId.eq(deviceId));
        CamLive camLive = builder.unique();
        return camLive;
    }

    public static List<CamLive> queryCamLive(String uid) {
        CamLiveDao camLiveDao = daoSession.getCamLiveDao();
        QueryBuilder<CamLive> builder = camLiveDao.queryBuilder();
        builder.where(CamLiveDao.Properties.Uid.eq(uid));
        List<CamLive> list = builder.list();
        return (list!=null) ? list : new ArrayList<CamLive>();
    }

    public static void insert(String uid, List<CamLive> list) {
        try {
            for (int i=0;i<list.size();i++) {
                CamLive live = list.get(i);
                //UID + DeviceID + DataType(同一台设备可能会出现被自己订阅的情况.)
                live.setUniqueId(uid+"_"+live.getDeviceId()+"_"+live.getDataType());
                live.setUid(uid);
            }
            CamLive[] objects = list.toArray(new CamLive[list.size()]);
            CamLiveDao dao = daoSession.getCamLiveDao();
            dao.insertInTx(objects);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("CamLiveWrapper", e);
        }
    }

    public static void clearCamLive(String uid) {
        CamLiveDao camLiveDao = daoSession.getCamLiveDao();
        QueryBuilder<CamLive> builder = camLiveDao.queryBuilder();
        builder.where(CamLiveDao.Properties.Uid.eq(uid));
        List<CamLive> list = builder.list();
        if (list!=null) camLiveDao.deleteAll();
    }
}
