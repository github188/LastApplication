package com.iermu.lan.test;

import android.app.Application;
import android.os.SystemClock;
import java.lang.Thread.UncaughtExceptionHandler;

import jcifs.smb.SmbException;

/**
 * 异常崩溃处理类
 *  当程序发生未捕获异常时，由该类来接管程序并记录发送错误报告。
 *
 * Created by wcy on 15/6/22.
 */
public class TestCrashHandler implements UncaughtExceptionHandler {

    /** 错误日志文件名称 */
    static final String LOG_NAME = "crash_%s.txt";

    /** 系统默认的UncaughtException处理类 */
    private UncaughtExceptionHandler mDefaultHandler;

    Application application;

    /**
     * 构造函数
     *  获取系统默认的UncaughtException处理器，设置该CrashHandler为程序的默认处理器 。
     *
     * @param application 上下文
     */
    public TestCrashHandler(Application application) {
        if(application == null) throw new RuntimeException("Application can not be null!");
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        this.application = application;
    }

    /** 
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // 如果用户没有处理则让系统默认的异常处理器来处理
        SystemClock.sleep(3000);
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    /** 
     * 自定义错误处理，收集错误信息
     *  发送错误报告等操作均在此完成
     * @param ex 异常
     * @return true：如果处理了该异常信息；否则返回false。
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return true;
        }
        ex.printStackTrace();
        return true;
    }

}
