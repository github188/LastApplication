package com.iermu.client.business.dao.upgrade;

import android.database.sqlite.SQLiteDatabase;

import com.iermu.client.business.dao.generator.AccountDao;
import com.iermu.client.business.dao.generator.WifiInfoDao;
import com.iermu.client.model.WifiInfo;

/**
 * Created by wcy on 15/9/9.
 */
public class DBMigrationHelper12 extends AbstractMigratorHelper {

    @Override
    public void onUpgrade(SQLiteDatabase db) {
        WifiInfoDao.dropTable(db, true);
        WifiInfoDao.createTable(db, true);
    }
}
