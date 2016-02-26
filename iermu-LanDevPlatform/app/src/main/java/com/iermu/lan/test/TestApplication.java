package com.iermu.lan.test;

import android.app.Application;

/**
 * Created by richard on 15/10/16.
 */
public class TestApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /* 全局异常崩溃处理 */
        TestCrashHandler catchExcep = new TestCrashHandler(this);
        Thread.setDefaultUncaughtExceptionHandler(catchExcep);
    }


}
