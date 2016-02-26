package com.iermu.client.business.dao.upgrade;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.iermu.client.business.dao.generator.DaoMaster.OpenHelper;
import com.iermu.client.util.Logger;

/**
 * Created by wcy on 15/9/9.
 */
public class UpgradeHelper extends OpenHelper {

    public static final String TAG = "UpgradeHelper";

    public UpgradeHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logger.i(">>>>>>>> onUpgrade oldVersion:"+oldVersion+" newVersion:"+newVersion);
        /* i represent the version where the user is now and the class named with this number implies that is upgrading from i to i++ schema */
        if(oldVersion > newVersion) {
            DBDowngradeHelper downgradeHelper = new DBDowngradeHelper();
            downgradeHelper.onDowngrade(db);
            return;
        }
        for (int i = oldVersion; i < newVersion; i++) {
            try {
                /* New instance of the class that migrates from i version to i++ version named DBMigratorHelper{version that the db has on this moment} */
                AbstractMigratorHelper migratorHelper = (AbstractMigratorHelper) Class.forName("com.iermu.client.business.dao.upgrade.DBMigrationHelper" + i).newInstance();

                if (migratorHelper != null) {

                    /* Upgrade de db */
                    migratorHelper.onUpgrade(db);
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
                Log.e(TAG, "Could not migrate from schema from schema: " + i + " to " + i++);
                /* If something fail prevent the DB to be updated to future version if the previous version has not been upgraded successfully */
                break;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                Log.e(TAG, "Could not migrate from schema from schema: " + i + " to " + i++);
                /* If something fail prevent the DB to be updated to future version if the previous version has not been upgraded successfully */
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, "Could not migrate from schema from schema: " + i + " to " + i++);
                /* If something fail prevent the DB to be updated to future version if the previous version has not been upgraded successfully */
                break;
            } catch (ClassCastException e) {
                e.printStackTrace();
                Log.e(TAG, "Could not migrate from schema from schema: " + i + " to " + i++);
                /* If something fail prevent the DB to be updated to future version if the previous version has not been upgraded successfully */
                break;
            }


        }
    }

}
