package com.iermu.client.business.impl.setupdev.setup;

import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import com.cms.iermu.cms.devSet;
import com.iermu.client.model.CamDev;
import com.iermu.client.model.constant.CamDevType;
import com.iermu.client.util.Logger;

/**
 * 配置设备参数线程
 *
 * Created by wcy on 15/8/10.
 */
public class ConfigDevRunnable extends Thread {

    private Context mContext;
    private devSet mDevSet;
    private String deviceId;            //设备ID
    private boolean isResetDev = true; //是否重置设备参数
    private int TIMEOUT = 60000;    //最大超时时间
    private int DELAYTIME = 100;    //重试间隔时间

    //Token 信息配置
    private String accessToken;         //第三方平台AccessToken(百度、羚羊云)
    private String streamId;            //摄像头直播流ID

    //配置百度用户UID
    private String baiduUID;                 //百度用户ID

    //Wifi 信息配置
    private String ssid;                //Wifi SSID
    private String password;            //Wifi 密码
    private String user;                //Wifi 用户名(企业级)
    private String encryption;          //Wifi 加密类型

    //DHCP 手动配置IP
    private String dhcpIp;
    private String dhcpNetmask;
    private String dhcpGateway;
    private boolean isDHCP = true;      //true:自动获取IP false:手动配置IP

    private OnConfigDevListener listener;
    private CamDev camDev;
    private boolean interrupted = false;

    public ConfigDevRunnable(Context context, String devId) {
        this.mContext = context;
        this.deviceId = devId;
    }

    /**
     * 配置摄像机绑定信息
     * @param accessToken   第三方平台AccessToken(百度、羚羊云)
     * @param streamId      摄像头直播流ID
     */
    public ConfigDevRunnable setCamBindInfo(String accessToken, String streamId) {
        this.accessToken = accessToken;
        this.streamId    = streamId;
        return this;
    }

    /**
     * 配置百度UID
     * @param uid
     * @return
     */
    public ConfigDevRunnable setBaiduUID(String uid) {
        this.baiduUID = uid;
        return this;
    }

    /**
     * 配置摄像机Wifi信息
     * @param ssid          ssid
     * @param pwd           密码
     * @param user          用户名
     * @param encryption    Wifi加密类型
     */
    public ConfigDevRunnable setCamWifiInfo(String ssid, String pwd, String user, String encryption) {
        this.ssid = ssid;
        this.password = pwd;
        this.user = user;
        this.encryption = encryption;
        return this;
    }

    /**
     * 配置摄像机IP地址信息
     * @param dhcpIp
     * @param dhcpNetmask
     * @param dhcpGateway
     * @return
     */
    public ConfigDevRunnable setDHCP(String dhcpIp, String dhcpNetmask, String dhcpGateway) {
        if(TextUtils.isEmpty(dhcpIp)
                || TextUtils.isEmpty(dhcpNetmask)
                || TextUtils.isEmpty(dhcpGateway)) {
            //TODO 校验格式问题
            return this;
        }
        this.isDHCP = false;
        this.dhcpIp = dhcpIp;
        this.dhcpNetmask = dhcpNetmask;
        this.dhcpGateway = dhcpGateway;
        return this;
    }

    /**
     * 设备类型(Ap、Upnp)
     * @param camDev
     * @return
     */
    public ConfigDevRunnable setCamDev(CamDev camDev) {
        this.camDev = camDev;
        return this;
    }

    /**
     * 结束配置摄像机参数
     */
    public ConfigDevRunnable stopConfigCam() {
        if(mDevSet != null) {
            mDevSet.stopSet();
        }
        return this;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        this.interrupted = true;
        stopConfigCam();
    }

    /**
     *
     * 状态码定义：
     * 	 0 成功
     * 	-1 连接摄像头失败，网络连接失败/获取设备信息出错
     * 	-2 设备ID不匹配
     *  -3 没有设备权限，第三方定制机
     *  -4 同-3
     *  -5 设置token失败
     *  -6 设置固定IP出错
     *  -7 设置wifi出错
     *  -100 用户主动中断配置过程
     *
     * @return 状态码
     */
    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        int devret = -1;
        while ( (System.currentTimeMillis()-startTime < TIMEOUT)
                && !interrupted
                && devret!=0 && devret!=-2 && devret!=-3 && devret!=-4 && devret!=-100
                ) {
            int devType = camDev.getDevType();
            Logger.i("ConfigDevRunnable", "isDHCP="+isDHCP+" dhcpIP="+dhcpIp+" net="+dhcpNetmask+" gate="+dhcpGateway
                    +"\r\n" + "ssid="+ssid+" password="+password+" user="+user+" encryption="+encryption
                    +"\r\n" + "accessToken="+accessToken+" streamId="+streamId
                    +"\r\n" + "devType="+devType+" deviceid="+deviceId+" devIP="+camDev.getDevIP()+" baiduUID="+baiduUID+" devPwd="+camDev.getDevPwd());
            if(devType == CamDevType.TYPE_AP) {
                mDevSet = new devSet(mContext, deviceId, isResetDev, baiduUID);
            } else if(devType == CamDevType.TYPE_ETH) {
                mDevSet = new devSet(mContext, deviceId, isResetDev, camDev.getDevIP(), baiduUID);
            }else if(devType == CamDevType.TYPE_SMART) {
                mDevSet = new devSet(mContext, deviceId, isResetDev, camDev.getDevIP(), baiduUID, camDev.getDevPwd());
            }else if(devType == CamDevType.TYPE_HIWIFI) {
                mDevSet = new devSet(mContext, deviceId, isResetDev, camDev.getDevIP(), baiduUID);
            }
            mDevSet.setBindInfo(accessToken, streamId);
            mDevSet.setwifi(ssid, password, user, encryption);
            mDevSet.setNet(isDHCP, dhcpIp, dhcpNetmask, dhcpGateway);
            devret  = mDevSet.startSetDev();
            Logger.i("ConfigDevRunnable", " result="+devret);
            switch(devret) {
            case 0:     //成功
                onConfigCamDev(ConfigStatus.SUCCESS);
                break;
            case -1:    //连接摄像头失败，网络连接失败/获取设备信息出错
                SystemClock.sleep(DELAYTIME);
                break;
            case -2:    //设备ID不匹配
                onConfigCamDev(ConfigStatus.DEVID_NOMATCH);
                break;
            case -3:    //没有设备权限，第三方定制机
                onConfigCamDev(ConfigStatus.PERMISSION_DENIED);
                break;
            case -4:    //同-3
                onConfigCamDev(ConfigStatus.PERMISSION_DENIED);
                break;
            case -5:    //设置token失败 |(可能streamId为null)
                //onConfigCamDev(ConfigStatus.TOKEN_FAILED);
                SystemClock.sleep(DELAYTIME);
                break;
            case -6:    //设置固定IP出错
                //onConfigCamDev(ConfigStatus.DHCP_FAILED);
                SystemClock.sleep(DELAYTIME);
                break;
            case -7:    //设置wifi出错
                //onConfigCamDev(ConfigStatus.WIFI_FAILED);
                SystemClock.sleep(DELAYTIME);
                break;
            case -100:  //用户主动中断配置过程
                onConfigCamDev(ConfigStatus.STOP);
                break;
            }
        }
        if(System.currentTimeMillis()-startTime>= TIMEOUT
                && (devret==-1 || devret==-5 || devret==-6 || devret==-7)) {
            //连接摄像头失败，网络连接失败/获取设备信息出错
            //重试连接摄像头超时
            onConfigCamDev(ConfigStatus.CONNECT_TIMEOUT);
        }
    }

    public ConfigDevRunnable setListener(OnConfigDevListener listener) {
        this.listener = listener;
        return this;
    }

    private void onConfigCamDev(ConfigStatus status) {
        this.interrupt();
        if(listener == null) return;
        listener.onConfigDev(status);
    }


    /**
     * 配置摄像机参数返回状态码
     *
     * Created by wcy on 15/7/22.
     */
    public static enum ConfigStatus {
        //    *  0 成功
        //    *  -1 连接摄像头失败，网络连接失败/获取设备信息出错
        //    *  -2 设备ID不匹配
        //    *  -3 没有设备权限，第三方定制机
        //    *  -4 同-3
        //    *  -5 设置token失败
        //    *  -6 设置固定IP出错
        //    *  -7 设置wifi出错
        //    *  -100 用户主动中断配置过程
        SUCCESS,            //成功
        CONNECT_TIMEOUT,    //配置摄像机超时
        CONNECT_FAIL,       //连接摄像头失败
        DEVID_NOMATCH,      //设备ID不匹配
        PERMISSION_DENIED,  //没有设备权限，第三方定制机
        TOKEN_FAILED,       //设置token失败
        DHCP_FAILED,        //设置固定IP出错
        WIFI_FAILED,        //设置wifi出错
        STOP                //用户主动中断配置过程

    }

    /**
     * 配置摄像机设备事件监听器
     *
     * Created by wcy on 15/7/22.
     */
    public static interface OnConfigDevListener {

        /**
         * 配置摄像机设备参数过程事件回调
         * @param status
         */
        public void onConfigDev(ConfigStatus status);

    }

}
