package com.iermu.client.business.dao.upgrade;

import android.database.sqlite.SQLiteDatabase;

import com.iermu.client.business.dao.generator.DaoMaster;

/**
 * Created by wcy on 16/1/26.
 */
public class DBDowngradeHelper {

    /**
     * 数据库降级操作
     *  因为我们无法预知未来版本的表结构，向下兼容时最稳妥的方法就是将该版本自己需要的表重构一次
     * @param db
     */
    public void onDowngrade(SQLiteDatabase db) {
        DaoMaster.dropAllTables(db, true);
        DaoMaster.createAllTables(db, false);
    }


}
