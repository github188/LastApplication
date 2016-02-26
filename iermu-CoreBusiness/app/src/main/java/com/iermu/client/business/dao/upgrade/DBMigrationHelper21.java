package com.iermu.client.business.dao.upgrade;

import android.database.sqlite.SQLiteDatabase;

import com.iermu.client.business.dao.generator.CamLiveDao;

/**
 * Created by wcy on 15/9/9.
 */
public class DBMigrationHelper21 extends AbstractMigratorHelper {

    @Override
    public void onUpgrade(SQLiteDatabase db) {
        MigrationHelper.getInstance().migrate(db, CamLiveDao.class);
    }
}
