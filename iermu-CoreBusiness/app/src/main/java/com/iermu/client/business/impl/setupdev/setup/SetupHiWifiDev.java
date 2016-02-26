package com.iermu.client.business.impl.setupdev.setup;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.cms.iermu.cms.CmsUtil;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.business.impl.event.OnSetupDevEvent;
import com.iermu.client.business.impl.setupdev.CheckDevOnLineRunnable;
import com.iermu.client.business.impl.setupdev.WifiNetworkManager;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamDev;
import com.iermu.client.model.CamDevConf;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.BusinessMsg;
import com.iermu.client.model.constant.CamDevType;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.util.Logger;
import com.iermu.eventobj.BusObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 添加(安装)HiWifi设备
 *
 * Created by wcy on 15/8/10.
 */
public class SetupHiWifiDev extends BaseSetupDev implements ISetupDevStep {

    private SetupStatus status = SetupStatus.SETUP_INITNG;
    private WifiConnectRunnable devWifiRunnable;    //连接设备Wifi线程
    private HiwifiConnectRunnable hiWifiRunnable;    //hiWifi设备上线拿ip
    private WifiConnectRunnable wifiRunnable;       //连接手机Wifi线程
    private ConfigDevRunnable   confDevRunable;     //配置设备参数线程
    private WifiNetworkManager  wifiNetManager;
    private CamDev dev;                             //连接的设备
    private CamDevConf conf;                        //配置摄像机参数
    private String accessToken;                     //服务器AccessToken
    private String baiduUID;                        //百度UID
    private String baiduAccessToken;                //百度AccessToken
    private String streamId;
    private Context context;
    private Timer progressTimer;
    private TimerTask progressTask;
    private CheckDevOnLineRunnable checkDevOnLineRunnabe;
    private RegisterDevRunnable registerDevRunnable;

    public SetupHiWifiDev() {
        this.accessToken      = ErmuBusiness.getAccountAuthBusiness().getAccessToken();
        this.baiduUID         = ErmuBusiness.getAccountAuthBusiness().getBaiduUid();
        this.baiduAccessToken = ErmuBusiness.getAccountAuthBusiness().getBaiduAccessToken();
        this.context          = ErmuApplication.getContext();
        this.wifiNetManager   = WifiNetworkManager.getInstance(context);
    }

    @Override
    protected synchronized void onSetupChanged(SetupStatus s) {
        if(status == SetupStatus.SETUP_STOPTED) {
            return;
        }
        super.onSetupChanged(s);
        synchronized (SetupHiWifiDev.class) {
            this.status = s;
        }
    }

    @Override
    public void init(CamDev dev, CamDevConf conf) {
        this.dev = dev;
        this.conf = conf;
    }

    @Override
    public void start() {
        onSetupChanged(SetupStatus.SETUP_INITNG);
        this.progressTimer  = new Timer();
        this.progressTask   = new TimerTask() {
            @Override
            public void run() {
                onUpdateProgress();
            }
        };
        int period;
        if(dev.getDevType()==CamDevType.TYPE_AP)
            period = 250;
        else if(dev.getDevType()==CamDevType.TYPE_HIWIFI)
            period = 200;
        else
            period = 150;
//        int period = (dev.getDevType()==CamDevType.TYPE_AP)?300:200;
        progressTimer.schedule(progressTask, 0, period);
        //TODO 检查Wifi是否正常打开
        //重新打开Wifi®®
        //并且连接正常
        int devType = dev.getDevType();
        if(devType == CamDevType.TYPE_HIWIFI) {
            hiWifiRunnable = new HiwifiConnectRunnable(dev.getBSSID());
            hiWifiRunnable.setOnHiWifiConnectListener(new HiwifiConnectRunnable.HiWifiConnectListener() {
                @Override
                public void onHiWifiConnected(CamDev devce) {
                    Log.i("hiwifitimeout4",""+devce.getConnectType());
                    if (devce.getConnectType()==-1) {
                        Logger.i("hiwifi连接失败");
                        int busCode = BusinessCode.HIWIFI_CONNECT_FAIL;
                        String busMsg = BusinessMsg.matchMessage(busCode);
                        onSetupChanged(SetupStatus.CHECK_ENV_HIWIFI_CONNEVCT_TIMEOUT);
                    } else {
                        Logger.i("hiwifi连接成功");
                        Log.i("hiwifiIp", devce.getDevIP());
                        dev.setDevIP(devce.getDevIP());
                        dev.setConnectType(devce.getConnectType());
                        onSetupChanged(SetupStatus.SETUP_INITED);
                    }
                }
            });
            hiWifiRunnable.start();
        } else {
            onSetupChanged(SetupStatus.SETUP_INITED);
        }
    }

    @Override
    public void registerDev() {
        if( !TextUtils.isEmpty(streamId)) {
            onSetupChanged(SetupStatus.REGISTER_SUCCESS);           //注册设备成功
            return;
        }
        onSetupChanged(SetupStatus.REGISTER_ING);
        final String devID    = dev.getDevID();
        final int connectType = ConnectType.BAIDU;
        registerDevRunnable = new RegisterDevRunnable(devID, connectType, accessToken);
        registerDevRunnable.setRegisterDevListener(new RegisterDevRunnable.RegisterDevListener() {
            @Override
            public void onRegisterDev(Business bus, String connectId) {
                int code = bus.getCode();
                if(!TextUtils.isEmpty(connectId)
                        && (code==BusinessCode.DEV_REGISTED || code==BusinessCode.SUCCESS)) {//注册成功
                    streamId = connectId;
                    if(code == BusinessCode.DEV_REGISTED) {
                        onSetupChanged(SetupStatus.REGISTED);
                    } else {
                        onSetupChanged(SetupStatus.REGISTER_SUCCESS);
                    }
                    return;
                }
                if(code == BusinessCode.DEV_REGISTED || code==BusinessCode.NO_PERMISSION) {
                    onSetupChanged(SetupStatus.REGISTER_NOTPERMISSION);
                } else {
                    onSetupChanged(SetupStatus.REGISTER_FAIL);
                }
            }
        });
        registerDevRunnable.start();
    }

    @Override
    public void connectDev() {
        onSetupChanged(SetupStatus.CONNECT_DEV_ING);
        int devType = dev.getDevType();
        if(devType == CamDevType.TYPE_AP) {         //Ap模式设置
            final String ssid = dev.getSSID();
            String bssid    = dev.getBSSID();
            String pwd      = dev.getDevPwd();
            int wifiType    = dev.getWifiType();
            devWifiRunnable = new WifiConnectRunnable(ssid, bssid, pwd, wifiType);
            devWifiRunnable.setOnWifiConnectListener(new WifiConnectRunnable.WifiConnectListener() {
                @Override
                public void onWifiConnected(boolean success) {
                    if(status == SetupStatus.CONNECT_DEV_SUCCESS
                            || status == SetupStatus.CONNECT_DEV_FAIL) {
                        //防止重复调用
                        Logger.i("Ap重复调用");
                        return;
                    }
                    //判断连接当前Wifi SSID 是否是设备SSID
                    String connect_ssid = wifiNetManager.getSSID();
                    if (success && connect_ssid.indexOf(ssid) >= 0) {
                        Logger.i("Ap连接成功");
                        onSetupChanged(SetupStatus.CONNECT_DEV_SUCCESS);
                    } else {
                        Logger.i("Ap连接失败");
                        onSetupChanged(SetupStatus.CONNECT_DEV_FAIL);
                    }
                }
            });
            devWifiRunnable.start();
        } else if(devType == CamDevType.TYPE_ETH||devType == CamDevType.TYPE_SMART||devType == CamDevType.TYPE_HIWIFI) { //Upnp模式设置
            onSetupChanged(SetupStatus.CONNECT_DEV_SUCCESS);
        } else if(devType == CamDevType.TYPE_WIFI_DIRECT) { //有线
            //v3.0.0废弃的
        }
//        else if(devType == CamDevType.TYPE_HIWIFI) { //hiwifi重新拿ip
//            onSetupChanged(SetupStatus.CONNECT_DEV_SUCCESS);;
//            hiWifiRunnable = new HiwifiConnectRunnable(dev.getBSSID());
//            hiWifiRunnable.setOnHiWifiConnectListener(new HiwifiConnectRunnable.HiWifiConnectListener() {
//                @Override
//                public void onHiWifiConnected(CamDev devce) {
////                    //判断连接当前Wifi SSID 是否是设备SSID
////                    String connect_ssid = wifiNetManager.getSSID();
//                    if (!"".equals(devce.getDevIP())) {
//                        Logger.i("hiwifi连接成功");
//                        Log.i("hiwifiIp", devce.getDevIP());
//                        dev.setDevIP(devce.getDevIP());
//                        dev.setConnectType(devce.getConnectType());
//                        onSetupChanged(SetupStatus.CONNECT_DEV_SUCCESS);
//                    } else {
//                        Logger.i("hiwifi连接失败");
//                        Log.i("hiwifiIp", "ip失败");
//                        onSetupChanged(SetupStatus.CONNECT_DEV_FAIL);
//                    }
//                }
//            });
//            hiWifiRunnable.start();
//        }
    }

    @Override
    public void configDev() {
        onSetupChanged(SetupStatus.CONF_DEV_ING);
        String devID      = dev.getDevID();
        String ssid     = conf.getSSID();
        String account  = conf.getWifiAccount();
        String pwd      = conf.getWifiPwd();
        String dhcpIP   = conf.getDhcpIP();
        String dhcpNetmask = conf.getDhcpNetmask();
        String dhcpGateway = conf.getDhcpGateway();
        String encryption = String.valueOf(dev.getWifiType());
        confDevRunable = new ConfigDevRunnable(context, devID);
        confDevRunable.setCamDev(dev)                           //连接的设备
            .setBaiduUID(baiduUID)                              //配置百度UID
            .setCamBindInfo(baiduAccessToken, streamId)         //配置百度AccessToken
            .setCamWifiInfo(ssid, pwd, account, encryption)  //配置Wifi信息
            .setDHCP(dhcpIP, dhcpNetmask, dhcpGateway)
            .setListener(new ConfigDevRunnable.OnConfigDevListener() {  //事件
                @Override
                public void onConfigDev(ConfigDevRunnable.ConfigStatus status) {
                    switch (status) {
                    case SUCCESS:
                        onSetupChanged(SetupStatus.CONF_DEV_SUCCESS);
                        break;
                    case CONNECT_TIMEOUT:
                        onSetupChanged(SetupStatus.CONF_CONNECTDEV_FAIL);
                        break;
                    //case DEVID_NOMATCH:
                    //    break;
                    case PERMISSION_DENIED:
                        onSetupChanged(SetupStatus.CONF_PERMISSION_DENIED);
                        break;
                    //case TOKEN_FAILED:
                    //    break;
                    //case DHCP_FAILED:
                    //    break;
                    //case WIFI_FAILED:
                    //    break;
                    //case STOP:
                    //    break;
                    default:
                        onSetupChanged(SetupStatus.CONF_DEV_FAIL);
                        break;
                    }
                }
            })
            .start();
    }

    @Override
    public void resetWifiNetwork() {
        if( conf==null ) return;

        onSetupChanged(SetupStatus.CONNECT_WIFI_ING);
        String ssid         = wifiNetManager.getSSID();
        String wifiPwd      = conf.getWifiPwd();
        final String wifiSSID = conf.getSSID();
        String wifiBSSID    = conf.getBSSID();
        String capabilities = conf.getCapabilities();
        int wifiType        = CmsUtil.getWifiType(capabilities);
        int devType         = dev.getDevType();

        boolean connectWifi = ssid.indexOf(wifiSSID) < 0;

        if(devType == CamDevType.TYPE_AP && connectWifi) {
            wifiNetManager.deletedWifi(dev.getSSID());
            wifiNetManager.startScan(); //清除扫描结果(自动清除设备Wifi)
        } else if((devType == CamDevType.TYPE_ETH || devType == CamDevType.TYPE_SMART|| devType == CamDevType.TYPE_HIWIFI)
                && connectWifi) {
        } else if(devType == CamDevType.TYPE_WIFI_DIRECT && connectWifi) {
        }
        if(!connectWifi && status!=SetupStatus.SETUP_STOPTED
                && status!=SetupStatus.CONNECT_WIFI_SUCCESS|| devType == CamDevType.TYPE_HIWIFI) {//当前连接Wifi 为手机Wifi
            Logger.i("Wifi已经连接成功");
            onSetupChanged(SetupStatus.CONNECT_WIFI_SUCCESS);
            return;
        }

        wifiRunnable = new WifiConnectRunnable(wifiSSID, wifiBSSID, wifiPwd, wifiType);
        wifiRunnable.setOnWifiConnectListener(new WifiConnectRunnable.WifiConnectListener() {
            @Override
            public void onWifiConnected(boolean success) {
                if (status == SetupStatus.SETUP_STOPTED
                        || status == SetupStatus.CONNECT_WIFI_SUCCESS
                        || status == SetupStatus.CONNECT_WIFI_FAIL
                        || status == SetupStatus.SETUP_SUCCESS
                        || status == SetupStatus.SETUP_FAIL) {
                    //防止重复调用
                    Logger.i("Wifi重复调用");
                    return;
                }
                //判断连接当前Wifi SSID 是否是手机SSID
                String connect_ssid = wifiNetManager.getSSID();
                if (success && connect_ssid.indexOf(wifiSSID) >= 0) {
                    onSetupChanged(SetupStatus.CONNECT_WIFI_SUCCESS);
                } else {
                    Logger.i("提示Wifi连接失败");
                    onSetupChanged(SetupStatus.CONNECT_WIFI_FAIL);
                }
            }
        });
        wifiRunnable.start();
    }

    @Override
    public void checkDevOnline() {
        wifiNetManager.startScan();//扫描Ap设备(几秒之后自动清除已注册设备Wifi)
        if(checkDevOnLineRunnabe != null) {
            checkDevOnLineRunnabe.interrupt();
            checkDevOnLineRunnabe = null;
        }
        checkDevOnLineRunnabe = new CheckDevOnLineRunnable(dev);
        Logger.i("----checkDevOnLineRunnabe");
        checkDevOnLineRunnabe.setOnDevOnLine(new CheckDevOnLineRunnable.DevOnLineListener() {
            @Override
            public void onDevOnLine(CamLive camLive) {
                BusObject.publishEvent(OnSetupDevEvent.class, camLive);
                onSetupChanged(SetupStatus.SETUP_SUCCESS);
            }
            @Override
            public void onTimeout() {
                onSetupChanged(SetupStatus.SETUP_FAIL);
            }
        });
        checkDevOnLineRunnabe.start();
    }

    @Override
    public void stop() {
        //String deviceId = dev.getDevID();
        //CamSettingApi.apiDropCamDev(deviceId, accessToken);
        onSetupChanged(SetupStatus.SETUP_STOPTED);
        if(registerDevRunnable!=null){
            registerDevRunnable.interrupt();
            registerDevRunnable = null;
        }
        if(devWifiRunnable != null) {
            devWifiRunnable.interrupt();
            devWifiRunnable = null;
        }
        if(wifiRunnable != null) {
            wifiRunnable.interrupt();
            wifiRunnable = null;
        }
        if(confDevRunable != null) {
            confDevRunable.interrupt();
            confDevRunable = null;
        }
        if(checkDevOnLineRunnabe != null) {
            checkDevOnLineRunnabe.interrupt();
            checkDevOnLineRunnabe = null;
        }
        if(progressTask != null) {
            progressTask.cancel();
            progressTask = null;
        }
        if(progressTimer != null) {
            progressTimer.cancel();
            progressTimer = null;
        }
        resetWifiNetwork();
    }

    @Override
    public void stopProgress() {
        if(progressTask != null) {
            progressTask.cancel();
            progressTask = null;
        }
        if(progressTimer != null) {
            progressTimer.cancel();
            progressTimer = null;
        }
    }

    private int progress = 0;
    private void onUpdateProgress() {
        if(status==SetupStatus.REGISTER_NOTPERMISSION
                || status==SetupStatus.REGISTER_FAIL
                || status==SetupStatus.CONNECT_DEV_FAIL
                || status==SetupStatus.CONF_DEV_FAIL
                || status==SetupStatus.CONF_CONNECTDEV_FAIL
                || status==SetupStatus.CONF_PERMISSION_DENIED
                || status==SetupStatus.CONNECT_WIFI_FAIL
                || status==SetupStatus.SETUP_FAIL) {
            progressTask.cancel();
            progressTimer.cancel();
            return;
        }
        if(status == SetupStatus.SETUP_SUCCESS) {
            progress = 100;
        } else if(progress<99){
            progress++;
        }
        super.onSetupProgress(progress);
    }
}
