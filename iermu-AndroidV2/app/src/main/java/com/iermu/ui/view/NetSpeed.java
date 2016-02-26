package com.iermu.ui.view;

/**
 * Created by xjy on 15/11/2.
 */
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cms.media.widget.VideoView;
import com.iermu.client.util.Logger;

public class NetSpeed {
    private final static String TAG = "NetSpeed";
    private long preRxBytes = 0;
    private Timer mTimer = null;
    private Context mContext;
    private static NetSpeed mNetSpeed;
    private Handler mHandler;
    private VideoView mVideoView;


    private NetSpeed(Context mContext, Handler mHandler, VideoView videoView) {
        this.mContext = mContext;
        this.mHandler = mHandler;
        this.mVideoView = videoView;
    }

    public static NetSpeed getInstant(Context mContext, Handler mHandler, VideoView videoView) {
        if (mNetSpeed == null) {
            mNetSpeed = new NetSpeed(mContext, mHandler, videoView);
        }
        return mNetSpeed;
    }

    private long getNetworkRxBytes() {
        int currentUid = getUid();
        if (currentUid < 0) {
            return 0;
        }
        long rxBytes = TrafficStats.getUidRxBytes(currentUid);
        if (rxBytes == TrafficStats.UNSUPPORTED) {
            rxBytes = TrafficStats.getTotalRxBytes();
        }
        return rxBytes;
    }

    public int getNetSpeed() {
        long curRxBytes = getNetworkRxBytes();
//        long bytes = curRxBytes - preRxBytes;
//        preRxBytes = curRxBytes;
        long netbps = mVideoView.getNetbps();
        Logger.i("netbps "  + netbps);
        int kb = (int) Math.floor(netbps / 1024 + 0.5);
        String kB = String.valueOf(kb);
        if (kB.length() > 3){
            kb = 0;
        }
        return kb;
    }

    public void startCalculateNetSpeed() {
        preRxBytes = getNetworkRxBytes();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = 1;
                    msg.arg1 = getNetSpeed();
                    mHandler.sendMessage(msg);
                }
            }, 1000, 1000);
        }
    }

    public void stopCalculateNetSpeed() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private int getUid() {
        try {
            PackageManager pm = mContext.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(
                    mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            return ai.uid;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
