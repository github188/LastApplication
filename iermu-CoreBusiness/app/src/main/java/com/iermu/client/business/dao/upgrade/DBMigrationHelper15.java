package com.iermu.client.business.dao.upgrade;

import android.database.sqlite.SQLiteDatabase;

import com.iermu.client.business.dao.generator.AccountDao;
import com.iermu.client.business.dao.generator.CamLiveDao;
import com.iermu.client.model.CamLive;

/**
 * Created by wcy on 15/9/9.
 */
public class DBMigrationHelper15 extends AbstractMigratorHelper {

    @Override
    public void onUpgrade(SQLiteDatabase db) {
        CamLiveDao.dropTable(db, true);
        CamLiveDao.createTable(db, true);
    }
}
