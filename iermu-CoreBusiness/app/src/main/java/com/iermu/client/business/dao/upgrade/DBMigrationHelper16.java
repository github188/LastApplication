package com.iermu.client.business.dao.upgrade;

import android.database.sqlite.SQLiteDatabase;

import com.iermu.client.business.dao.generator.CamLiveDao;
import com.iermu.client.business.dao.generator.CloudPositionDao;

/**
 * Created by wcy on 15/9/9.
 */
public class DBMigrationHelper16 extends AbstractMigratorHelper {

    @Override
    public void onUpgrade(SQLiteDatabase db) {
        CloudPositionDao.dropTable(db, true);
        CloudPositionDao.createTable(db, true);
    }
}
