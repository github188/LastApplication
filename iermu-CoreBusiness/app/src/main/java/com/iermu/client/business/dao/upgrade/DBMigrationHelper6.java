package com.iermu.client.business.dao.upgrade;

import android.database.sqlite.SQLiteDatabase;

import com.iermu.client.business.dao.generator.AccountDao;

/**
 * 版本号: 6
 * 更新内容:
 *  1. 更新Account表增加字段'LyyAppId'
 *
 * Created by wcy on 15/9/9.
 */
public class DBMigrationHelper6 extends AbstractMigratorHelper {

    @Override
    public void onUpgrade(SQLiteDatabase db) {
        MigrationHelper.getInstance().migrate(db, AccountDao.class);
    }
}
