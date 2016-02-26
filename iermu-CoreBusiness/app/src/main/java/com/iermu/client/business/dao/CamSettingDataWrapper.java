package com.iermu.client.business.dao;

import android.text.TextUtils;
import android.util.Log;

import com.iermu.client.business.api.converter.CamInfoConverter;
import com.iermu.client.business.dao.generator.CamSettingDataDao;
import com.iermu.client.business.dao.generator.DaoSession;
import com.iermu.client.model.CamInfo;
import com.iermu.client.model.CamSettingData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by zhangxq on 15/9/14.
 */
public class CamSettingDataWrapper {

    @Inject
    static DaoSession daoSession;

    /**
     * 获取设备信息
     *
     * @param uid
     * @param deviceId
     * @return 获取到的设备信息，获取失败返回null
     */
    public static CamSettingData getCamSettingData(String uid, String deviceId) {
        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(deviceId)) {
            return null;
        }

        CamSettingDataDao dao = daoSession.getCamSettingDataDao();
        QueryBuilder<CamSettingData> builder = dao.queryBuilder();
        builder.where(CamSettingDataDao.Properties.Uid.eq(uid));
        builder.where(CamSettingDataDao.Properties.DeviceId.eq(deviceId));
        CamSettingData d = builder.unique();
        Log.i("CamSettingData", "get:" + d);
        return builder.unique();
    }

    /**
     * 获取某个用户下的设备信息列表
     *
     * @param uid
     * @return
     */
    public static List<CamSettingData> getCamSettingDataList(String uid) {
        if (TextUtils.isEmpty(uid)) {
            return null;
        }

        CamSettingDataDao dao = daoSession.getCamSettingDataDao();
        QueryBuilder<CamSettingData> builder = dao.queryBuilder();
        builder.where(CamSettingDataDao.Properties.Uid.eq(uid));
        List<CamSettingData> camSettingDatas = builder.list();

        return camSettingDatas;
    }

    /**
     * 获取设置Info信息
     *
     * @param uid
     * @param deviceId
     * @return
     */
    public static CamInfo getCamSettingInfo(String uid, String deviceId) {
        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(deviceId)) {
            return null;
        }
        CamSettingData data = getCamSettingData(uid, deviceId);
        if (data == null) {
            return null;
        }
        String json = data.getInfoJson();
        if(json == null) return null;
        try {
            JSONObject object = new JSONObject(json);
            if (object == null) {
                return null;
            }
            return CamInfoConverter.fromJson(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 更新设备信息
     *
     * @param uid
     * @param deviceid
     * @param isAlarmOpen
     * @return 返回更新完成后的对象，更新失败返回null
     */
    public void updateCamSettingData(String uid, String deviceid, boolean isAlarmOpen) {
        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(deviceid)) {
            return;
        }
        CamSettingDataDao dao = daoSession.getCamSettingDataDao();
        QueryBuilder<CamSettingData> builder = dao.queryBuilder();
        builder.where(CamSettingDataDao.Properties.Uid.eq(uid)
                , CamSettingDataDao.Properties.DeviceId.eq(deviceid));
        CamSettingData settingData = builder.unique();
        if (settingData == null) {
            settingData = new CamSettingData();
        }
        //uniqueId: uid_deviceId
        String uniqueId = uid + "_" + deviceid;
        settingData.setUniqueId(uniqueId);
        settingData.setUid(uid);
        settingData.setDeviceId(deviceid);
        settingData.setIsAlarmOpen(isAlarmOpen ? 1 : 0);
        long s = dao.insertOrReplace(settingData);
        Log.i("CamSettingData", "updateisAlarmOpen:" + s + "--deviceid:" + deviceid);
    }


    /**
     * 更新设备信息
     *
     * @param uid
     * @param deviceid
     * @param infoJson
     * @return 返回更新完成后的对象，更新失败返回null
     */
    public void updateCamSettingInfor(String uid, String deviceid, String infoJson) {
        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(deviceid)) {
            return;
        }
        CamSettingDataDao dao = daoSession.getCamSettingDataDao();
        QueryBuilder<CamSettingData> builder = dao.queryBuilder();
        builder.where(CamSettingDataDao.Properties.Uid.eq(uid)
                , CamSettingDataDao.Properties.DeviceId.eq(deviceid));
        CamSettingData settingData = builder.unique();
        if (settingData == null) {
            settingData = new CamSettingData();
        }
        //uniqueId: uid_deviceId
        String uniqueId = uid + "_" + deviceid;
        settingData.setUniqueId(uniqueId);
        settingData.setUid(uid);
        settingData.setDeviceId(deviceid);
        settingData.setInfoJson(infoJson);
        long s = dao.insertOrReplace(settingData);
        Log.i("CamSettingData", "updateinfoJson:" + s + "--deviceid:" + deviceid + "===" + infoJson);
    }

    /**
     * 获取已打开报警开关的总数
     * @return
     * @param uid
     */
    public static long getAlarmOpenCount(String uid) {
        CamSettingDataDao dao = daoSession.getCamSettingDataDao();
        QueryBuilder<CamSettingData> builder = dao.queryBuilder();
        builder.where(CamSettingDataDao.Properties.Uid.eq(uid));
        builder.where(CamSettingDataDao.Properties.IsAlarmOpen.eq(1));
        return builder.count();
    }

    /**
     * 查询摄像机报警开关是否已打开
     * @param uid
     * @param deviceId
     * @return
     */
    public static boolean isOpenAlarmPush(String uid, String deviceId) {
        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(deviceId)) {
            return false;
        }
        CamSettingDataDao dao = daoSession.getCamSettingDataDao();
        QueryBuilder<CamSettingData> builder = dao.queryBuilder();
        builder.where(CamSettingDataDao.Properties.Uid.eq(uid));
        builder.where(CamSettingDataDao.Properties.DeviceId.eq(deviceId));
        builder.where(CamSettingDataDao.Properties.IsAlarmOpen.eq(1));
        return builder.unique()!=null;
    }

}
