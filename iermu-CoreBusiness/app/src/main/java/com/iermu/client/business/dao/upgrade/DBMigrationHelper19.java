package com.iermu.client.business.dao.upgrade;

import android.database.sqlite.SQLiteDatabase;

import com.iermu.client.business.dao.generator.CamSettingDataDao;

/**
 * Created by wcy on 15/9/9.
 */
public class DBMigrationHelper19 extends AbstractMigratorHelper {

    @Override
    public void onUpgrade(SQLiteDatabase db) {
        MigrationHelper.getInstance().migrate(db, CamSettingDataDao.class);
    }
}
