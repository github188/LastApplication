package com.iermu.client;

import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.cms.iermu.cms.cmsNative;
import com.iermu.client.business.api.ApiEngine;
import com.iermu.client.business.dao.AccountWrapper;
import com.iermu.client.business.dao.DaoEngine;
import com.iermu.client.business.impl.event.OnLoginSuccessEvent;
import com.iermu.client.business.impl.event.OnLogoutEvent;
import com.iermu.client.config.AppConfig;
import com.iermu.client.model.Account;
import com.iermu.client.util.Logger;
import com.iermu.eventobj.BusObject;

import java.util.List;

import im.fir.sdk.FIR;

/**
 * App业务运行环境(初始化)
 * 1.
 * 2.
 * 3.
 * <p>
 * Created by wcy on 15/6/19.
 */
public class ErmuApplication extends Application implements OnLogoutEvent, OnLoginSuccessEvent {

    private static final Handler mHandler = new Handler();
    private static ErmuApplication instance;
    private static Application mContext;
    public static final String INTENT_NEW_ALARM = "intent_new_alarm";
    public static final String INTENT_NOTICATION_PUSH = "intent_notication_push";
    public static final String INTENT_NOTICATION_PUSH_CLEAR = "intent_notication_push_clear";

    static {
        try {
            System.loadLibrary("bdpush_V2_5");
        } catch (UnsatisfiedLinkError e) {
            Logger.e("load library failed", e);
        }
    }

    public ErmuApplication() {
        this.instance = this;
    }

    /**
     * 初始化CoreBusiness
     */
    public static void init(Application context) {
        if (context == null) throw new RuntimeException("Context can not be null!");
        mContext = context;
        initInstance().onCreate();
    }

    public static Application getContext() {
        return mContext;
    }

    private static ErmuApplication initInstance() {
        if (instance == null) {
            synchronized (ErmuApplication.class) {
                if (instance == null) {
                    instance = new ErmuApplication();
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i(">>>>>> onCreate");
        if (mContext == null) {
            this.mContext = this;
        }
        initLoaderContext();
    }


    /**
     * 获取网络连接状态
     *
     * @return
     */
    public static boolean isConnected() {
        ConnectivityManager cManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cManager.getActiveNetworkInfo();
        boolean connected = false;
        if (nInfo != null && nInfo.isConnected()) {
            connected = true;
        }
        return connected;
    }

    //初始化加载环境
    private void initLoaderContext() {
        Logger.i(">>>>>> initLoaderContext");
        Application context = getContext();
        if (AppConfig.DEV_MODE) {
            /* 全局异常崩溃处理 */
            CrashHandler catchExcep = new CrashHandler(context);
            Thread.setDefaultUncaughtExceptionHandler(catchExcep);
            //LoggerFactory.enableLoggingUncaughtException(catchExcep);
        }

        Logger.init("com.iermu");   //初始化日志环境
        ApiEngine.init(this);       //初始化Api
        DaoEngine.init(this);       //初始化数据库
        new ErmuBusiness();         //初始化Business(预加载数据)
        new BusObject();            //初始化业务事件驱动
        new cmsNative();            //xx

        Logger.i(">>>>>> initLoaderContext FIR");
        FIR.init(this);             //Fir SDK
        //startPushWork();            //初始化百度推送SDK
        startMonitorNetWork();      //开始网络监测
        BusObject.subscribeEvent(OnLoginSuccessEvent.class, this);
        BusObject.subscribeEvent(OnLogoutEvent.class, this);
        Logger.i(">>>>>> initLoaderContext end");
    }

    /**
     * 判断当前应用程序是否处于后台
     * @param context
     * @return
     */
    public static boolean isRunInBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public static int getVersionBuild() {
        try {
            PackageManager manager = getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getContext().getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 获取versoin_name
     * @return 当前应用的版本号
     */
    public static String getVersionName() {
        try {
            PackageManager manager = getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getContext().getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Toast消息
     *
     * @param message
     */
    public static void toast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 执行工作线程
     *
     * @param runnable
     */
    public static void execBackground(Runnable runnable) {
        new Thread(runnable).start();
    }

    /**
     * 执行主线程
     *
     * @param runnable
     */
    public static void execMainThread(Runnable runnable) {
        mHandler.post(runnable);
    }

    /**
     * 执行主线程
     *
     * @param runnable
     * @param delayMillis
     */
    public static void execMainThread(Runnable runnable, long delayMillis) {
        mHandler.postDelayed(runnable, delayMillis);
    }

    /**
     * 执行后台线程
     *
     * @param runnable
     * @param delayMillis
     */
    public static void execBackground(final Runnable runnable, final long delayMillis) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    execBackground(runnable);
                }
            }
        }).start();
    }

    @Override
    public void OnLogoutEvent() {
        com.baidu.android.pushservice.PushManager.unbind(this);
    }

    @Override
    public void onLoginSuccess() {
        //startPushWork();
    }

    // 打开百度推送
    public static void startPushWork() {
        Account account = AccountWrapper.queryAccount();
        Logger.d("account:" + account);
        if (account != null) {
            Logger.d("startPushWork accountUid:" + account.getUid());
            Logger.d("startPushWork accountUname:" + account.getUname());
            Logger.d("startPushWork lyyUToken:" + account.getLyyUToken());
            Logger.d("startPushWork lyyUConfig:" + account.getLyyUConfig());
            Logger.d("startPushWork baiduak:" + account.getBaiduAK());
            com.baidu.android.pushservice.PushManager.startWork(getContext(), PushConstants.LOGIN_TYPE_ACCESS_TOKEN, account.getBaiduAK());    // 百度推送
            //if(!PushManager.isConnected(getContext())) {
            //    PushManager.startWork(getContext(), PushConstants.LOGIN_TYPE_ACCESS_TOKEN, account.getBaiduAK());    // 百度推送
            //} else if(!PushManager.isPushEnabled(getContext())) {
            //    PushManager.resumeWork(getContext());
            //}
            com.igexin.sdk.PushManager.getInstance().initialize(getContext());
            String clientid = com.igexin.sdk.PushManager.getInstance().getClientid(getContext());
            Logger.i("startPushWork: startPushWork end. Getui:cid"+clientid);
        }
    }
    //开始网络监测
    private void startMonitorNetWork() {
        //NetworkStatusReceiver receiver = new NetworkStatusReceiver();
        //IntentFilter intent = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        //registerReceiver(receiver, intent);
    }

    class NetworkStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                synchronized (ErmuApplication.class) {
                    NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                }
            } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if (info.getDetailedState() == NetworkInfo.DetailedState.DISCONNECTED) {
                }
            }
        }
    }
}
