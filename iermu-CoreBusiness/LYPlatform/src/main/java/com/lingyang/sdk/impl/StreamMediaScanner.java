package com.lingyang.sdk.impl;

import android.os.SystemClock;

import com.lingyang.sdk.MediaScannerListener;
import com.lingyang.sdk.util.CLog;

/**
 * 羚羊云设备线程扫描私有流媒体(直播地址、直播状态)
 *
 * 开始扫描摄像机状态, 连接摄像头
 *  0. 判断当前是否断开连接状态
 *  1. 获取摄像机状态
 *  2. 状态为 1
 *      可以连接摄像头
 *    其他:
 *      返回摄像机状态、提示用户不同的状态信息
 *      等待500毫秒, 重新获取摄像机状态
 *
 * 停止扫描摄像机状态, 断开连接摄像头
 *
 * 监听羚羊云断开摄像机连接状态
 *
 * Created by wcy on 15/9/22.
 */
public class StreamMediaScanner {

    private static StreamMediaScanner mInstance;
    private static MediaScannerListener mListener;
    private static boolean stopScanner = false;

    public static StreamMediaScanner getInstance() {
        if (mInstance == null) {
            synchronized (StreamMediaScanner.class) {
                if (mInstance == null) {
                    mInstance = new StreamMediaScanner();
                }
            }
        }
        return mInstance;
    }

    public void startPrivate(String devToken, String trackIp, int trackPort) {
        if(!stopScanner) {
            stop();
        }
        if(mListener == null) {
            return;
        }
        synchronized (StreamMediaScanner.class) {
            long startTime  = System.currentTimeMillis();
            int connect     = -1;   //connect==0 表示连接成功
            this.stopScanner = false;
            while( !stopScanner
                    && connect!=0
                    && System.currentTimeMillis()-startTime<=3000 ) {
                CLog.i("StreamMediaScanner startPrivate connect=" + connect);
                connect = IErmuSDK.getInstance().ConnectPrivateMedia(devToken, trackIp, trackPort);
                if(connect == 0) {
                    mListener.mediaScanner(connect, devToken);
                    break;
                }
                SystemClock.sleep(300);
            }
            if(System.currentTimeMillis()-startTime>3000) {
                mListener.mediaScanner(connect, devToken);
            }
        }
    }

    public void startPublic(String rtmpURL) {
        if(!stopScanner) {
            stop();
        }
        if(mListener == null) {
            return;
        }
        synchronized (StreamMediaScanner.class) {
            long startTime   = System.currentTimeMillis();
            int connect      = -1;   //connect==0 表示连接成功
            this.stopScanner = false;
            while( !stopScanner && connect!=0
                    && System.currentTimeMillis()-startTime<=3000 ) {
                connect = IErmuSDK.getInstance().ConnectPublicMedia(rtmpURL);
                CLog.i("StreamMediaScanner startPublic connect=" + connect);
                if(connect == 0) {
                    mListener.mediaScanner(connect, rtmpURL);
                    break;
                }
                SystemClock.sleep(300);
            }
            if(System.currentTimeMillis()-startTime>3000) {
                mListener.mediaScanner(connect, rtmpURL);
            }
        }
    }

    public void startRecordMedia(String diskInfo, String devToken, int fromTime, int toTime, int playTime) {
        CLog.i("StreamMediaScanner startRecordMedia="+devToken);
        if(!stopScanner) {
            stop();
        }
        if(mListener == null) {
            return;
        }
        synchronized (StreamMediaScanner.class) {
            long startTime   = System.currentTimeMillis();
            int connect = -1;   //connect==0 表示连接成功
            this.stopScanner = false;
            while( !stopScanner && connect!=0
                    && System.currentTimeMillis()-startTime<=3000 ) {
                connect = IErmuSDK.getInstance().ConnectRecordSource(diskInfo, devToken, fromTime, toTime, playTime);
                CLog.i("StreamMediaScanner startRecordMedia connect=" + connect);
                if(connect == 0) {
                    mListener.mediaScanner(connect, devToken);
                    break;
                }
                SystemClock.sleep(300);
            }
            //超时 & 连接失败 & 非主动结束(stop)
            if(System.currentTimeMillis()-startTime>3000 && connect!=0 && !stopScanner) {
                mListener.mediaScanner(connect, devToken);
            }
        }
    }

    public void stop() {
        stopScanner = true;
        synchronized (StreamMediaScanner.class) {
            IErmuSDK.getInstance().DisconnectMediaSource();
        }
    }

    public void addMediaScannerListener(MediaScannerListener listener) {
        this.mListener = listener;
    }

}
