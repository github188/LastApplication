package com.iermu.client.business.dao.upgrade;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by wcy on 15/9/9.
 */
public abstract class AbstractMigratorHelper {

    /**
     * 数据库升级操作
     * @param db
     */
    public abstract void onUpgrade(SQLiteDatabase db);


}
