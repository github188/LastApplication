package com.iermu.client.business.dao;

import com.iermu.client.business.dao.generator.AlarmImageDataDao;
import com.iermu.client.business.dao.generator.CloudPositionDao;
import com.iermu.client.business.dao.generator.DaoSession;
import com.iermu.client.model.CloudPosition;
import com.iermu.client.util.Logger;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by zhangxq on 15/10/20.
 */
public class CloudPositionWrapper {

    @Inject
    static DaoSession daoSession;

    /**
     * 获取预置位个数
     *
     * @param uid
     * @param deviceId
     * @return
     */
    public static long getCount(String uid, String deviceId) {
        if (uid == null || deviceId == null) {
            return 0;
        }

        CloudPositionDao dao = daoSession.getCloudPositionDao();
        QueryBuilder<CloudPosition> builder = dao.queryBuilder();
        builder.where(CloudPositionDao.Properties.Uid.eq(uid));
        builder.where(CloudPositionDao.Properties.DeviceId.eq(deviceId));
        return builder.count();
    }

    /**
     * 获取预置位列表
     *
     * @param uid
     * @param deviceId
     * @return
     */
    public static List<CloudPosition> getCloudPositions(String uid, String deviceId) {
        if (uid == null || deviceId == null) {
            return null;
        }

        CloudPositionDao dao = daoSession.getCloudPositionDao();
        QueryBuilder<CloudPosition> builder = dao.queryBuilder();
        builder.where(CloudPositionDao.Properties.Uid.eq(uid));
        builder.where(CloudPositionDao.Properties.DeviceId.eq(deviceId));
        builder.orderAsc(CloudPositionDao.Properties.AddDate);
        return builder.list();
    }

    public static void saveOrUpdatePosition(String uid, String deviceid, int preset, String title, String imagePath) {
        if (uid == null || deviceid == null) {
            Logger.d("用户id或deviceId为空");
            return;
        }

        CloudPositionDao dao = daoSession.getCloudPositionDao();
        QueryBuilder<CloudPosition> builder = dao.queryBuilder();
        builder.where(CloudPositionDao.Properties.Uid.eq(uid),
                CloudPositionDao.Properties.DeviceId.eq(deviceid),
                CloudPositionDao.Properties.Preset.eq(preset));
        CloudPosition cloudPosition = builder.unique();
        if (cloudPosition == null) {
            cloudPosition = new CloudPosition();
            cloudPosition.setAddDate(new Date().getTime());
        }
        //uniqueId: uid_deviceId
        String uniqueId = uid + "_" + deviceid + "_" + preset;
        cloudPosition.setUniqueId(uniqueId);
        cloudPosition.setUid(uid);
        cloudPosition.setDeviceId(deviceid);
        cloudPosition.setTitle(title);
        cloudPosition.setPreset(preset);
        cloudPosition.setImagePath(imagePath);
        dao.insertOrReplace(cloudPosition);
    }

    /**
     * 删除一个预置点
     */
    public static void deletePosition(String uid, String deviceId, int preset) {
        CloudPositionDao dao = daoSession.getCloudPositionDao();
        QueryBuilder<CloudPosition> builder = dao.queryBuilder();
        builder.where(CloudPositionDao.Properties.Uid.eq(uid),
                CloudPositionDao.Properties.DeviceId.eq(deviceId),
                CloudPositionDao.Properties.Preset.eq(preset));
        CloudPosition cloudPosition = builder.unique();
        if (cloudPosition != null) {
            dao.delete(cloudPosition);
            Logger.d("delete:" + cloudPosition.getTitle() + "-" + cloudPosition.getPreset());
        }
    }
}
