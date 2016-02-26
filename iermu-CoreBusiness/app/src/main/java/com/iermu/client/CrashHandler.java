package com.iermu.client;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.iermu.client.config.PathConfig;
import com.iermu.client.util.FileUtil;
import com.iermu.client.util.Logger;
import com.iermu.client.util.StringUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 异常崩溃处理类
 *  当程序发生未捕获异常时，由该类来接管程序并记录发送错误报告。
 *
 * Created by wcy on 15/6/22.
 */
public class CrashHandler implements UncaughtExceptionHandler {

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
    public CrashHandler(Application application) {
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
        Logger.e("应用异常", ex);
        SystemClock.sleep(3000);
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // 等待会后结束程序
            try {
            	Log.i(LOG_NAME,"exit start");
                Thread.sleep(3000);
//            	Intent intent = new Intent(application.getApplicationContext(), MainActivity.class);
//                PendingIntent restartIntent = PendingIntent.getActivity(
//                        application.getApplicationContext(), 0, intent,
//                        Intent.FLAG_ACTIVITY_NEW_TASK);
                //退出程序
//                AlarmManager mgr = (AlarmManager)application.getContext().getSystemService(Context.ALARM_SERVICE);
//                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
//                        restartIntent); // 1秒钟后重启应用
                exitApp();
                Log.i(LOG_NAME,"exit end");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void exitApp() {
        try {
            int currentVersion = android.os.Build.VERSION.SDK_INT;
            if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
                System.exit(0);
            } else {
                ActivityManager activityManager = (ActivityManager)application.getSystemService(Context.ACTIVITY_SERVICE);
                activityManager.restartPackage(application.getPackageName());
                System.exit(0);
            }
        } catch(Exception e) {}
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
        // 提示错误消息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(application.getApplicationContext(), "应用发生异常，即将退出！", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        // 保存错误报告文件
        saveCrashInfoToFile(ex);
        return true;
    }

    /** 
     * 保存错误信息到文件中
     *
     * @param ex 异常
     */
    private void saveCrashInfoToFile(Throwable ex) {
        final StackTraceElement[] stack = ex.getStackTrace();
        final String message = ex.getMessage();
        String currentTime = StringUtil.currentTime(StringUtil.FORMAT_YMDHMS2).toString();
        /* 准备错误日志文件 */
        String filePath= PathConfig.APP_LOG_PATH + String.format(LOG_NAME, currentTime);
        File logFile = new File(filePath);
        if (!logFile.getParentFile().exists()) {
        	FileUtil.createDipPath(filePath);
            //logFile.getParentFile().mkdirs();
        }
        /* 写入错误日志 */
        FileWriter fw = null;
        final String lineFeed = "\r\n";
        try {
            fw = new FileWriter(logFile, true);
            fw.write(currentTime + lineFeed + lineFeed);
            fw.write(message + lineFeed);
            for (int i = 0; i < stack.length; i++) {
                fw.write(stack[i].toString() + lineFeed);
            }
            fw.write(lineFeed);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fw)
                    fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
