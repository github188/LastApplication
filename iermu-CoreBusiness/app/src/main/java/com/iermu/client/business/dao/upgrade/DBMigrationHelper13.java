package com.iermu.client.business.dao.upgrade;

import android.database.sqlite.SQLiteDatabase;

import com.iermu.client.business.dao.generator.CloudPositionDao;
import com.iermu.client.business.dao.generator.WifiInfoDao;
import com.iermu.client.model.CloudPosition;

/**
 * Created by wcy on 15/9/9.
 */
public class DBMigrationHelper13 extends AbstractMigratorHelper {

    @Override
    public void onUpgrade(SQLiteDatabase db) {
        CloudPositionDao.createTable(db, true);
    }
}
