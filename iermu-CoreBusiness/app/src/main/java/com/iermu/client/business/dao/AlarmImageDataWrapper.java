package com.iermu.client.business.dao;

import com.iermu.client.business.dao.generator.AlarmImageDataDao;
import com.iermu.client.business.dao.generator.DaoSession;
import com.iermu.client.model.AlarmImageData;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by zhangxq on 15/9/15.
 */
public class AlarmImageDataWrapper {

    @Inject
    static DaoSession daoSession;

    /**
     * 获取设备消息数量
     *
     * @param deviceId
     * @return
     */
    public static long getCountByDeviceId(String deviceId) {
        AlarmImageDataDao dao = daoSession.getAlarmImageDataDao();
        QueryBuilder<AlarmImageData> builder = dao.queryBuilder();
        builder.where(AlarmImageDataDao.Properties.DeviceId.eq(deviceId));
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        return builder.count();
    }

    /**
     * 获取最近一条消息
     *
     * @param deviceId
     * @return
     */
    public static AlarmImageData getLastImageDatasByDeviceId(String deviceId) {
        AlarmImageDataDao dao = daoSession.getAlarmImageDataDao();
        QueryBuilder<AlarmImageData> builder = dao.queryBuilder();
        builder.where(AlarmImageDataDao.Properties.DeviceId.eq(deviceId));
        builder.orderDesc(AlarmImageDataDao.Properties.Id);
        builder.limit(1);
        return builder.list().get(0);
    }

    /**
     * 分页获取消息列表
     *
     * @param deviceId
     * @param beginPosition
     * @param num
     * @return
     */
    public static List<AlarmImageData> getImageDatasByDeviceId(String deviceId, int beginPosition, int num) {
        AlarmImageDataDao dao = daoSession.getAlarmImageDataDao();
        QueryBuilder<AlarmImageData> builder = dao.queryBuilder();
        builder.where(AlarmImageDataDao.Properties.DeviceId.eq(deviceId));
        builder.orderDesc(AlarmImageDataDao.Properties.Alarmtime);
        builder.offset(beginPosition);
        builder.limit(num);
        return builder.list();
    }

    /**
     * 删除一条消息
     *
     * @param id
     */
    public static void deleteById(long id) {
        AlarmImageDataDao dao = daoSession.getAlarmImageDataDao();
        dao.deleteByKey(id);
    }

    /**
     * 保存一条消息
     *
     * @param title
     * @param description
     * @param deviceId
     * @param recdatetime
     * @param alarmtime
     */
    public static void save(String title, String description, String deviceId, String recdatetime, String alarmtime) {
        AlarmImageDataDao dao = daoSession.getAlarmImageDataDao();
        AlarmImageData data = new AlarmImageData(title, description, deviceId, recdatetime, alarmtime);
        dao.insertWithoutSettingPk(data);
    }
}
