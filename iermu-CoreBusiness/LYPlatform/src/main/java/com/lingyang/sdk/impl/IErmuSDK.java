package com.lingyang.sdk.impl;

import android.os.SystemClock;

import com.lingyang.sdk.util.CLog;
import com.lyy.lyyapi;

/**
 * 羚羊云平台Api封装
 *
 * Created by wcy on 15/8/18.
 */
public class IErmuSDK {

    private static IErmuSDK lyyPlatform;
    private static lyyapi lyyapi;
    private int loginStatus = -1;       //status==0 表示登录成功
    private boolean stopCloud = false;  //停止SDK服务
    private String userToken;           //羚羊UserToken
    private String userConfig;          //羚羊配置信息

    public static IErmuSDK getInstance() {
        if(lyyPlatform == null) {
            synchronized (IErmuSDK.class) {
                if (lyyPlatform == null) {
                    lyyPlatform = new IErmuSDK();
                }
            }
        }
        return lyyPlatform;
    }

    public IErmuSDK() {
        this.lyyapi = getLYYApi();
    }

    public void StartCloudService(String userToken, String userConfig, com.lingyang.sdk.LoginListener listener) {
        CLog.v("StartCloudService userToken=" + userToken + " userConfig=" + userConfig);
        if(listener == null) {
            throw new RuntimeException("LoginListener not be null.");
        }
        synchronized (IErmuSDK.class) {
            this.userToken  = userToken;
            this.userConfig = userConfig;
            this.stopCloud  = false;
            long sTime = System.currentTimeMillis();
            long eTime = sTime;
            while (!stopCloud && loginStatus!=0 && eTime-sTime<1000 ) { //status==0 表示登录成功
                loginStatus = getLYYApi().loginLyy(userToken, userConfig, null);
                eTime = System.currentTimeMillis();
                CLog.v("StartCloudService status="+loginStatus+" userToken="+userToken+" userConfig="+userConfig+" "+(eTime-sTime));
                SystemClock.sleep(100);
            }
        }

        if( stopCloud ) return; //登录|连接的过程中, 产生中断SDK服务
        if(loginStatus == 0) {
            listener.Online();
        } else {
            listener.OffOnline();
        }
    }

    public void StopCloudService() {
        CLog.v("StopCloudService");
        //PlatformAPI.StopCloudService();
        synchronized (IErmuSDK.class) {
            this.loginStatus = -1;
            this.stopCloud = true;
            getLYYApi().lyyStop();
        }
    }

    public void StartConnectPrivateMedia(String devToken, String trackIp, int trackPort) {
        CLog.v("StartConnectPrivateMedia");
        checkCloudServiceStatus();
        if(loginStatus == 0) {
            StreamMediaScanner.getInstance().startPrivate(devToken, trackIp, trackPort);
        }
    }

    public void StartConnectPublicMedia(String rtmpURL) {
        CLog.v("StartConnectPublicMedia");
        checkCloudServiceStatus();
        if(loginStatus == 0) {
            StreamMediaScanner.getInstance().startPublic(rtmpURL);
        }
    }

    public void StopConnectMedia() {
        CLog.v("StopConnectMedia");
        //if(loginStatus == 0) {
            StreamMediaScanner.getInstance().stop();
        //}
    }

    public void StartRecordMedia(String diskInfo, String devToken, int fromTime, int toTime, int playTime) {
        CLog.v("StartRecordMedia");
        checkCloudServiceStatus();
        if(loginStatus == 0) {
            StreamMediaScanner.getInstance().startRecordMedia(diskInfo, devToken, fromTime, toTime, playTime);
        }
    }

    public void StopRecordMedia() {
        CLog.v("StopRecordMedia");
        //if(loginStatus == 0) {
        StreamMediaScanner.getInstance().stop();
        //}
    }

    public int ConnectPrivateMedia(String devToken, String trackIp, int trackPort) {
        checkCloudServiceStatus();
        synchronized (IErmuSDK.class) {
            int i = getLYYApi().connP2PCam(devToken, trackIp, trackPort);
            CLog.v("ConnectPrivateMedia status="+i+" devToken="+devToken+" trackIp="+trackIp+" trackPort="+trackPort);
            return i;
        }
    }

    public int ConnectPublicMedia(String rtmpURL) {
        //checkCloudServiceStatus();
        synchronized (IErmuSDK.class) {
            int i = getLYYApi().connRtmpCam(rtmpURL);
            CLog.v("ConnectPublicMedia status="+i+" rtmpURL="+rtmpURL);
            return i;
        }
    }

    public void DisconnectMediaSource() {
        synchronized (IErmuSDK.class) {
            getLYYApi().lyyDisConn();
            CLog.v("DisconnectMediaSource");
        }
    }

    public int ConnectRecordSource(String diskInfo, String devToken, int fromTime, int toTime, int playTime) {
        checkCloudServiceStatus();
        int vod = getLYYApi().openVod(diskInfo, devToken, fromTime, toTime, playTime);
        CLog.v("ConnectRecordSource status="+vod+" devToken="+devToken+" from="+fromTime+" to="+toTime+" diskInfo="+diskInfo);
        return vod;
    }

    public String GetPlayPath(String devToken) {
        String playPath = getLYYApi().getLyyPlayPath();
        CLog.v("GetPlayPath playPath="+playPath+" devToken="+devToken);
        return playPath;
    }

    public String GetRecordPlayPath(String devToken) {
        String vodPath = getLYYApi().getLyyVodPath();
        CLog.v("GetRecordPlayPath vodPath="+vodPath+" devToken="+devToken);
        return vodPath;
    }

    public void addMediaScannerListener(com.lingyang.sdk.MediaScannerListener listener) {
        StreamMediaScanner.getInstance().addMediaScannerListener(listener);
    }

    //检测云服务登录状态
    private void checkCloudServiceStatus() {
        synchronized (IErmuSDK.class) {
            long sTime = System.currentTimeMillis();
            long eTime = sTime;
            if (!stopCloud && loginStatus!=0) { //status==0 表示登录成功
                loginStatus = getLYYApi().loginLyy(userToken, userConfig, null);
                eTime = System.currentTimeMillis();
                CLog.v("loginCloudService status="+loginStatus+" userToken="+userToken+" userConfig="+userConfig+" "+(eTime-sTime));
            }
        }
    }

    public lyyapi getLYYApi() {
        if(lyyapi == null) {
            synchronized (IErmuSDK.class) {
                if(lyyapi == null) {
                    lyyapi = new lyyapi();
                    lyyapi.setOnInfoListener(new lyyapi.OnInfoListener() {
                        @Override
                        public boolean onInfo(int what, String msg) {
                            CLog.v("onInfo what=" + what + " msg="+msg);
                            return false;
                        }
                    });
                }
            }
        }
        return lyyapi;
    }

}
