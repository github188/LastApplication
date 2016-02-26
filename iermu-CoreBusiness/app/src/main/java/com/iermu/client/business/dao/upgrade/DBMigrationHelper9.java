package com.iermu.client.business.dao.upgrade;

import android.database.sqlite.SQLiteDatabase;

import com.iermu.client.business.dao.generator.AccountDao;
import com.iermu.client.business.dao.generator.CamSettingDataDao;

/**
 * Created by wcy on 15/9/9.
 */
public class DBMigrationHelper9 extends AbstractMigratorHelper {

    @Override
    public void onUpgrade(SQLiteDatabase db) {
        AccountDao.dropTable(db, true);
        AccountDao.createTable(db, true);
    }
}
