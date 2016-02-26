package com.iermu.client.business.dao.upgrade;

import android.database.sqlite.SQLiteDatabase;

import com.iermu.client.business.dao.generator.CamSettingDataDao;

/**
 * Created by wcy on 15/9/9.
 */
public class DBMigrationHelper18 extends AbstractMigratorHelper {

    @Override
    public void onUpgrade(SQLiteDatabase db) {
        CamSettingDataDao.dropTable(db, true);
        CamSettingDataDao.createTable(db, true);
    }
}
