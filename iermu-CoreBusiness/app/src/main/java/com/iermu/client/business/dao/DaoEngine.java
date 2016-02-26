package com.iermu.client.business.dao;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.iermu.client.ErmuApplication;
import com.iermu.client.business.dao.generator.DaoMaster;
import com.iermu.client.business.dao.generator.DaoSession;
import com.iermu.client.business.dao.upgrade.UpgradeHelper;
import com.iermu.client.util.Logger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * 数据库层引擎
 *
 * Created by wcy on 15/9/9.
 */
public class DaoEngine {

    private static final String DB_NAME = "iermu-db";
    private static ObjectGraph mObjectGraph;

    public static void init(ErmuApplication app) {
        if(mObjectGraph == null) {
            synchronized (DaoEngine.class) {
                if (mObjectGraph == null) {
                    Application context = app.getContext();
                    mObjectGraph = ObjectGraph.create(new DaoMoudle(context));
                    mObjectGraph.inject(app);
                    mObjectGraph.injectStatics();
                    Logger.i("DaoEngine init end.");
                }
            }
        }
    }

    public static ObjectGraph getObjectGraph() {
        return mObjectGraph;
    }

    @Module(
    injects = {
        ErmuApplication.class, Application.class
    },
    staticInjections = {
        AccountWrapper.class, AlarmImageDataWrapper.class, CamSettingDataWrapper.class
        , WifiInfoWrapper.class, CloudPositionWrapper.class, CamLiveWrapper.class
    })
    static class DaoMoudle {
        private final Context appContext;

        public DaoMoudle(Context appContext) {
            this.appContext = appContext;
        }

        @Provides
        @Singleton
        DaoSession provideDaoSession() {
            Logger.i(">>>>>>>> provideDaoSession");
            UpgradeHelper helper = new UpgradeHelper(appContext, DB_NAME, null);
            //DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(appContext, DB_NAME, null);
            SQLiteDatabase db = helper.getWritableDatabase();
            DaoMaster daoMaster = new DaoMaster(db);
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
            return daoMaster.newSession();
        }

    }

}
