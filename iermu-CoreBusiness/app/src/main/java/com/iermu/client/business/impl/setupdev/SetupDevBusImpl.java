package com.iermu.client.business.impl.setupdev;

import android.app.Application;
import android.net.wifi.ScanResult;
import android.text.TextUtils;

import com.cms.iermu.cms.CmsUtil;
import com.cms.iermu.cms.WifiType;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IMimeCamBusiness;
import com.iermu.client.ISetupDevBusiness;
import com.iermu.client.business.api.CamDeviceApi;
import com.iermu.client.business.api.PubCamApi;
import com.iermu.client.business.api.response.CamMetaResponse;
import com.iermu.client.business.api.response.RegisterDevResponse;
import com.iermu.client.business.impl.BaseBusiness;
import com.iermu.client.business.impl.event.OnAccountTokenEvent;
import com.iermu.client.business.impl.event.OnSetupDevEvent;
import com.iermu.client.business.impl.setupdev.setup.HiwifiConnectRunnable;
import com.iermu.client.business.impl.setupdev.setup.ISetupDevStep;
import com.iermu.client.business.impl.setupdev.setup.SetupBaiduDev;
import com.iermu.client.business.impl.setupdev.setup.SetupHiWifiDev;
import com.iermu.client.business.impl.setupdev.setup.SetupLyyDev;
import com.iermu.client.business.impl.setupdev.setup.SetupProgressListener;
import com.iermu.client.business.impl.setupdev.setup.SetupStatus;
import com.iermu.client.business.impl.setupdev.setup.SetupStatusListener;
import com.iermu.client.business.impl.setupdev.setup.SetupStepScheduler;
import com.iermu.client.listener.OnRegisterDevListener;
import com.iermu.client.listener.OnSetupDevListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamDev;
import com.iermu.client.model.CamDevConf;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.BusinessMsg;
import com.iermu.client.model.constant.CamDevType;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.model.constant.ScanDevMode;
import com.iermu.client.util.LanguageUtil;
import com.iermu.client.util.Logger;
import com.iermu.client.util.WifiUtil;
import com.iermu.eventobj.BusObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * 添加(安装)摄像机设备业务
 *  1.AP扫描
 *  2.upnp扫描
 *
 * 扫描设备流程:(V3.0.0)
 * ------------------------------
 * 1.获取当前手机连接的Wifi
 *   没有连接Wifi, 则提示用户需要使用Wifi
 * 2.获取Wifi列表
 * 3.开始配置设备
 *   校验密码格式
 *   保存Wifi数据到数据库
 *   开始扫描设备
 * 4.1AP扫描
 *   WifiDirect扫描
 *   获取Wifi列表
 *   检查是否是IPC AP加密方式
 *       重新配置设备
 *       新增设备
 * 返回设备列表
 * 4.2Upnp扫描
 *   扫描Upno设备
 *   过滤正常状态的摄像头设备
 *   检查摄像头状态(配置状态、工作状态、切换状态)
 *       重新配置设备
 *       新增设备
 * 返回设备列表
 *
 * 扫描设备流程:(V3.1.0)
 * ------------------------------
 * 1.扫描设备
 * 2.设备列表: 选择要连接的设备
 * 3.获取Wifi列表
 * 4.输入Wifi配置(密码), 开始配置设备
 * 5.连接摄像机: 注册设备、配置设备、设备关联云服务器
 *
 *
 * 连接设备:(V3.0.0)
 * ------------------------------
 * 1. connType==1
 *  eht: startEthSet (upnp)
 *      1.创建与设备通信的环境(devIp、password、type、)
 *      2.注册设备(regDev): pcs注册
 *      3.成功:(连接pcs成功)
 *      4.设置设备参数(setDevParams)
 *      5.设备注册成功
 *
 *  wifi: startSetDev(ap)
 *      自动配置模式
 *      1.检测设备是否激活(没有激活,提示)
 *      2.m_iConnType==0 (判断设备连接类型: devType)
 *          获取SSID
 *      3.注册设备(regDev): pcs注册
 *      4.成功:(连接pcs成功)
 *          连接Ap模式Wifi(connAp)
 *          设置接收Wifi连接状态广播
 *      5.接收Wifi广播连接成功
 *          AP模式设置固定IP
 *      6.设置设备参数(setDevParams)
 *      7.设备注册成功
 *          AP模式将手机Wifi配置回来
 *
 *      手动配置模式
 *
 * 百度云设备添加流程:
 *  1.扫描设备列表
 *  2.选择设备
 *  3.选择Wifi信息
 *  4.注册设备(百度云)
 *  5.配置设备参数(Token、StreamId、uid、wifi)
 *  6.设备自动上线
 *
 * 羚羊云设备添加流程:
 *  1.扫描设备列表
 *  2.选择设备
 *  3.选择Wifi信息
 *  4.配置设备参数(Wifi)
 *  5.设备自动注册羚羊云
 *  6.绑定设备, 绑定重试
 *  7.绑定成功后, 注册设备(服务器\羚羊云)
 *
 * 完整添加流程:(百度云、羚羊云)
 *  1.扫描设备列表
 *  2.选择设备
 *  3.选择Wifi信息
 *
 *  4.判断设备平台类型
 *  4.1 百度云设备
 *      1.注册设备(百度云)
 *      2.配置设备参数(Token、StreamId、uid、wifi)
 *      3.设备自动上线
 *  4.2 羚羊云设备
 *      1.配置设备参数(Wifi)
 *      2.设备自动注册羚羊云start
 *      3.绑定设备, 绑定重试
 *      4.绑定成功后, 注册设备(服务器\羚羊云)
 *
 * Created by wcy on 15/6/21.
 */
public class SetupDevBusImpl extends BaseBusiness implements ISetupDevBusiness, OnAccountTokenEvent {

    private Application mContext;
    private String accessToken;
    private String uid;
    private String baiduUID;
    private Map<String, CamDev> mCamDevMap; //{devId : CamdDev}
    private Map<String, ScanResult> mWifiList;//{SSID:ScanResult}
    private Thread mApScan;
    private Thread mUpnpScan;
    private SmartScanRunnable mSmartScan;
    private HiWifiScanRunnable mHiWifiScanRunnable;
    private VoiceScanRunnable mVoiceScan;
    private WifiDirectScanRunnable mWifiDirectScan;
    private Thread mWifiScan;
    private OnSetupDevListener mListener;
    private SetupStepScheduler setupDevScheduler;

    private HiwifiConnectRunnable hiWifiRunnable;    //hiWifi设备上线拿ip

    public SetupDevBusImpl() {
        super();
        mContext = ErmuApplication.getContext();
        mCamDevMap = new HashMap<String, CamDev>();
        mWifiList = new HashMap<String, ScanResult>();
        baiduUID = ErmuBusiness.getAccountAuthBusiness().getBaiduUid();//TODO 获取百度用户UID
        BusObject.subscribeEvent(OnAccountTokenEvent.class, this);
    }

    @Override
    public void addSetupDevListener(OnSetupDevListener listener) {
        synchronized (SetupDevBusImpl.class) {
            this.mListener = listener;
        }
    }

    @Override
    public void removeSetupDevListener(OnSetupDevListener listener) {
        synchronized (SetupDevBusImpl.class) {
            this.mListener = null;
        }
    }

    @Override
    public void scanCam(CamDevConf camDevConf) {
        //TODO 优化代码
        WifiNetworkManager wifiManager = WifiNetworkManager.getInstance(ErmuApplication.getContext());
        if (!wifiManager.isWifiEnabled()) wifiManager.openWifi();
        stopScanRunnable();
        synchronized (SetupDevBusImpl.class) {
            Map<String, CamDev> map = getCamDevMap();
            Collection<CamDev> values = map.values();
            Iterator<CamDev> iterator = values.iterator();
            while (iterator.hasNext()) {
                CamDev next = iterator.next();
                int devType = next.getDevType();
                long scanTime = next.getScanTime();
                long millis = System.currentTimeMillis();
                boolean timeout = millis-scanTime>80000;
                if( (devType==CamDevType.TYPE_SMART && timeout) //smart && 超时   //smart比较IP
                    || devType!=CamDevType.TYPE_SMART) {        //不等于smart
                    iterator.remove();
                }
            }
        }

//        if("".equals(camDevConf.getWifiSSID())){
            //HiWifiApi 极路由扫描
//            mHiWifiScanRunnable = new HiWifiScanRunnable()
//                    .setListener(mScanCamDev);
//            mHiWifiScanRunnable.start();
//        }else{


            //处理不同类型wifi传值问题
        int wifiType        = camDevConf.getWifiType();
        String wifiSSID     = camDevConf.getWifiSSID();
        String wifiPwd      = camDevConf.getWifiPwd();
        String wifiAccount  = "";
        if(wifiType == WifiType.EAP) {
            wifiAccount  = camDevConf.getWifiAccount();
        }else if (wifiType == WifiType.OPEN){
            wifiPwd = "";
        }



        //Wifi有线直连 扫描
//        mWifiDirectScan = new WifiDirectScanRunnable(mContext);
//        mWifiDirectScan.start();

//        //发送声波
//        mVoiceScan = new VoiceScanRunnable(mContext, wifiSSID, wifiPwd, 0, wifiAccount);
//        mVoiceScan.start();

            //Smart 扫描
            if(mSmartScan == null) {//smart模式扫描: 情况特殊, 一次添加流程最好创建一次Socket对象
                mSmartScan = new SmartScanRunnable(mContext, wifiSSID, wifiPwd, wifiType, wifiAccount)
                        .setListener(mScanCamDev);
                mSmartScan.start();
            } else {
                mSmartScan.setConfigWifi(wifiSSID, wifiPwd, wifiType, wifiAccount);
                mSmartScan.restart();
            }

            //Ap 扫描
            mApScan = new ApScanRunnable(mContext, baiduUID)
            .setListener(mScanCamDev);
            mApScan.start();

            //Upnp 扫描
            mUpnpScan = new UpnpScanRunnable(baiduUID, mContext)
            .setListener(mScanCamDev);
            mUpnpScan.start();
//
//            }


    }

    @Override
    public void scanSpecifiedCam(String camDevID) {
        stopScanRunnable();
        //Ap 扫描
        mApScan = new ApScanRunnable(mContext, baiduUID)
        .setFindDevID(camDevID)
        .setListener(mScanCamDev);
        mApScan.start();

        //Upnp 扫描
        mUpnpScan = new UpnpScanRunnable(baiduUID, mContext)
        .setFindDevID(camDevID)
        .setListener(mScanCamDev);
        mUpnpScan.start();
    }

    @Override
    public void scanWifi() {
        //TODO 优化代码
        WifiNetworkManager wifiManager = WifiNetworkManager.getInstance(ErmuApplication.getContext());
        if (!wifiManager.isWifiEnabled()) wifiManager.openWifi();

        stopScanWifi();
        //Wifi 扫描
        mWifiScan = new WifiScanRunnable(mContext)
                .setListener(mScanCamDev);
        mWifiScan.start();
    }

    @Override
    public void checkCamEnvironment(CamDev camDev, boolean manualMode, CamDevConf conf) {
        String SSID     = this.scanConnectSSID();
        String wifiSSID = conf.getWifiSSID();
        int devType     = camDev.getDevType();
        long scanTime   = camDev.getScanTime();

        long millis = System.currentTimeMillis();
//        if(devType == CamDevType.TYPE_SMART && !SSID.equals(wifiSSID)) {
//            onSetupDevStatus(SetupStatus.CHECK_ENV_SMART_WIFI_NOMATCH);
//        } else
        if(devType == CamDevType.TYPE_SMART && millis-scanTime>80000) {
            //Smart模式配置摄像机, 最大超时60秒(摄像机会自动回复红蓝交替闪烁状态)
            onSetupDevStatus(SetupStatus.CHECK_ENV_SMART_TIMEOUT);
        } else if (devType == CamDevType.TYPE_AP && manualMode) {
            //先注册服务器,成功1、手动配置页面2、停止动画（成功或者失败）
            this.registerCamStep(camDev);
        } else {
            onSetupDevStatus(SetupStatus.CHECK_ENV_OK);
        }
    }

    @Override
    public void connectCam(final CamDev dev, final boolean manualMode, final CamDevConf conf) {
        stopScanRunnable();
        if(mSmartScan != null) {
            mSmartScan.interrupt();
            mSmartScan = null;
        }
        //检查设备ID是否有效. (设备是否激活)
        if(CmsUtil.DEV_NO_ACTIVATED.equals(dev.getDevID())) { //提示设备未激活
            int busCode = BusinessCode.DEV_NOTACTIVE;
            String busMsg = BusinessMsg.matchMessage(busCode);
            onScanFail(busCode, busMsg);
            return;
        }

        int devType = dev.getDevType();
        //manualMode = (devType==CamDevType.TYPE_AP) && manualMode;

//        if( !((devType==CamDevType.TYPE_AP) && manualMode) && setupDevScheduler != null) { //手动模式: 不需要清除之前的配置流程
        if(devType!=CamDevType.TYPE_HIWIFI){
            String wifiSSID = conf.getWifiSSID();
            WifiNetworkManager manager = WifiNetworkManager.getInstance(mContext);
            ScanResult scanResult = manager.findScanResult(wifiSSID);
            conf.setScanWifi(scanResult);
        }


        if( !manualMode && setupDevScheduler != null) { //手动模式: 不需要清除之前的配置流程
            setupDevScheduler.stopSetup();
            setupDevScheduler = null;
        }
        stopScanWifi();
//        CamDevConf conf = new CamDevConf();
//        conf.setScanWifi(scanResult);
//        conf.setWifiAccount(wifiAccount);
//        conf.setWifiPwd(wifiPwd);
//        conf.setDhcpIP(dhcpIP）                                                                                                         );
//        conf.setDhcpNetmask(dhcpNetmask);
//        conf.setDhcpGateway(dhcpGateway);

        if( !((devType==CamDevType.TYPE_AP) && manualMode) ) { //手动模式: 继续引用之前的流程
            ISetupDevStep setupDevStep = null;
            int deviceType = dev.getConnectType();  //检测设备平台类型
            if(devType == CamDevType.TYPE_HIWIFI) { //TODO HIWIFI 特殊处理逻辑
                setupDevStep = new SetupHiWifiDev();
            } else if(deviceType == ConnectType.BAIDU) {
                setupDevStep = new SetupBaiduDev();
            } else if (deviceType == ConnectType.LINYANG) {  //新版羚羊云流程 20150917
                setupDevStep = new SetupLyyDev();
            }
            setupDevStep.addSetupStatusListener(new SetupStatusListener(){
                @Override
                public void onSetupStatusChange(SetupStatus status) {
                    onSetupDevStatus(status);
                    switch (status) {
                    case REGISTER_NOTPERMISSION: //没有权限(该设备不是当前用户的)
                        ErmuBusiness.getStatisticsBusiness().statSetupDev(dev, conf, manualMode, 4001);
                        break;
                    case REGISTER_FAIL:          //注册设备失败
                        ErmuBusiness.getStatisticsBusiness().statSetupDev(dev, conf, manualMode, 4002);
                        break;
                    case CONNECT_DEV_FAIL:       //连接设备Wifi失败(Ap模式)
                        ErmuBusiness.getStatisticsBusiness().statSetupDev(dev, conf, manualMode, 4004);
                        break;
                    case CONF_DEV_FAIL:          //配置设备参数失败
                        ErmuBusiness.getStatisticsBusiness().statSetupDev(dev, conf, manualMode, 4004);
                        break;
                    case CONF_CONNECTDEV_FAIL:   //连接摄像头失败|超时
                        ErmuBusiness.getStatisticsBusiness().statSetupDev(dev, conf, manualMode, 4004);
                        break;
                    case CONF_PERMISSION_DENIED: //配置设备: 没有设备权限，第三方定制机
                        ErmuBusiness.getStatisticsBusiness().statSetupDev(dev, conf, manualMode, 6001);
                        break;
                    case CONNECT_WIFI_FAIL:      //重连Wifi失败
                        ErmuBusiness.getStatisticsBusiness().statSetupDev(dev, conf, manualMode, 6002);
                        break;
                    case SETUP_FAIL:             //安装设备失败(提示:网络不稳定)
                        ErmuBusiness.getStatisticsBusiness().statSetupDev(dev, conf, manualMode, 4005);
                        break;
                    case SETUP_SUCCESS:
                        ErmuBusiness.getStatisticsBusiness().statSetupDev(dev, conf, manualMode, 0);
                        break;
                    }
                }
            });
            setupDevStep.addSetupProgressListener(new SetupProgressListener() {
                @Override
                public void onProgress(int progress) {
                    onUpdateSetupProgress(progress);
                }
            });
            setupDevScheduler = new SetupStepScheduler(setupDevStep);
        }
        setupDevScheduler.startSetup(dev, conf);
    }

    @Override
    public void registerCamStep(CamDev camDev) {
        if(setupDevScheduler != null) {
            setupDevScheduler.stopSetup();
            setupDevScheduler = null;
        }
        stopScanRunnable();
        if(mSmartScan != null) {    //smart模式退出扫描
            mSmartScan.interrupt();
            mSmartScan = null;
        }
        stopScanWifi();

        ISetupDevStep setupDevStep = null;
        int deviceType = camDev.getConnectType();  //检测设备平台类型
        if(deviceType == ConnectType.BAIDU) {
            setupDevStep = new SetupBaiduDev();
        } else if (deviceType == ConnectType.LINYANG) {  //新版羚羊云流程 20150917
            setupDevStep = new SetupLyyDev();
        }
        setupDevStep.addSetupStatusListener(new SetupStatusListener(){
            @Override
            public void onSetupStatusChange(SetupStatus status) {
                onSetupDevStatus(status);
            }
        });
        setupDevStep.addSetupProgressListener(new SetupProgressListener() {
            @Override
            public void onProgress(int progress) {
                onUpdateSetupProgress(progress);
            }
        });
        setupDevScheduler = new SetupStepScheduler(setupDevStep);
        setupDevScheduler.registerDev(camDev);
    }

    @Override
    public void _registerCam(CamDev camDev) {
//        String desc     = "我的摄像机";
        String devID    = camDev.getDevID();
        String desc = ErmuBusiness.getMimeCamBusiness().getCamDescription(devID);
        String language = LanguageUtil.getLanguage();
        if (TextUtils.isEmpty(desc)) desc = language.equals("zh") ? "我的摄像机" : "My Camera";
        int connectType = camDev.getConnectType();
        String token            = ErmuBusiness.getAccountAuthBusiness().getAccessToken();
        RegisterDevResponse res = CamDeviceApi.registerDevice(devID, 1, connectType, desc, token);
        Business bus = res.getBusiness();
        int code     = bus.getCode();
//        streamId     = res.getStreamId();
        if(code == BusinessCode.DEV_REGISTED ) {    //设备已注册
            IMimeCamBusiness business = ErmuBusiness.getMimeCamBusiness();
            CamLive camLive = business.getCamLive(devID);
            if(camLive == null) {
                CamMetaResponse metaRes = PubCamApi.apiCamMeta(devID, accessToken, "", "");
                bus     = metaRes.getBusiness();
                camLive = metaRes.getCamLive();
//                streamId     = (camLive!=null) ? camLive.getStreamId() : "";
            }
            if(camLive != null) BusObject.publishEvent(OnSetupDevEvent.class, camLive);//注册成功 | 已注册成功
        }
        sendListener(OnRegisterDevListener.class, bus);
    }

    @Override
    public void quitSetupDev() {
        ErmuBusiness.getPreferenceBusiness().setAdvanceConfig(false);
        ErmuBusiness.getPreferenceBusiness().setIpMode("");
        ErmuBusiness.getPreferenceBusiness().setDevAddErrorTimes(0);
        if(setupDevScheduler != null) {
            setupDevScheduler.stopSetup();
            setupDevScheduler = null;
        }
        synchronized (SetupDevBusImpl.class) {
            mListener = null;
            if(getCamDevMap() != null) {
                getCamDevMap().clear();
            }
        }
        this.quitScanCam();//退出扫描设备程序指令
        //stopScanRunnable();
        stopScanWifi();
        //if(mSmartScan != null) {
        //    mSmartScan.interrupt();
        //    mSmartScan = null;
        //}
    }

    //TODO 退出扫描设备程序: 会清除扫描设备缓存
    @Override
    public void quitScanCam() {
        stopScanRunnable();
        if(mSmartScan != null) {    //smart模式退出扫描
            mSmartScan.interrupt();
            mSmartScan = null;
        }
        synchronized (SetupDevBusImpl.class) {
            mListener = null;
            getCamDevMap().clear();
        }
    }

    @Override
    public void quitScanWifi() {
        stopScanWifi();
    }

    @Override
    public String scanConnectSSID() {
        WifiNetworkManager manager = WifiNetworkManager.getInstance(mContext);
        String ssid = manager.getSSID();
        if(ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid = ssid.substring(1, ssid.length()-1);
        }
        return ssid;
    }

    @Override
    public String scanConnectBSSID() {
        WifiNetworkManager manager = WifiNetworkManager.getInstance(mContext);
        return manager.getBSSID();
    }

    @Override
    public int getConnectWifiType() {
        WifiNetworkManager manager = WifiNetworkManager.getInstance(mContext);
        String capabilities = manager.getCapabilities();
        return CmsUtil.getWifiType(capabilities);
    }

    @Override
    public List<CamDev> getScanCamDev() {
        synchronized (SetupDevBusImpl.class) {
            Collection<CamDev> values = getCamDevMap().values();
            ArrayList<CamDev> camDevs = new ArrayList<CamDev>(values);
            List<CamDev> list = new ArrayList<CamDev>(Arrays.asList(new CamDev[camDevs.size()]));
            Collections.copy(list, camDevs);
            return list;
        }
    }

    @Override
    public boolean existedSmartCamDev() {
        synchronized (SetupDevBusImpl.class) {
            Map<String, CamDev> map = getCamDevMap();
            Collection<CamDev> values = map.values();
            Iterator<CamDev> iterator = values.iterator();
            while (iterator.hasNext()) {
                CamDev next = iterator.next();
                int devType = next.getDevType();
                if(devType==CamDevType.TYPE_SMART) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public List<ScanResult> getScanWifi() {
        synchronized (SetupDevBusImpl.class) {
            Collection<ScanResult> values = getScanWifiMap().values();
            ArrayList<ScanResult> list = new ArrayList<ScanResult>(values);
            //List<ScanResult> list = new ArrayList<ScanResult>(Arrays.asList(new ScanResult[wifiList.size()]));
            //Collections.copy(list, wifiList);

            //TODO 优化代码
            WifiNetworkManager manager = WifiNetworkManager.getInstance(mContext);
            String ssid = manager.getSSIDStr();
            ListIterator<ScanResult> iterator = list.listIterator();
            ScanResult temp = null;
            List<ScanResult> temList = new ArrayList<ScanResult>();
            while(iterator.hasNext()) {
                ScanResult next = iterator.next();
                if(ssid.equals(next.SSID)) {
                    temp = next;
                    iterator.remove();
                } else if(WifiUtil.is5GHz(next.frequency)){
                    iterator.remove();
                    temList.add(next);
                }
            }
            Collections.sort(list, new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult s1, ScanResult s2) {
                    return s1.SSID.compareToIgnoreCase(s2.SSID);
                }
            });
            if(temp != null) {
                list.add(0, temp);
            }
            list.addAll(temList);
            //TODO end
            return list;
        }
    }

    @Override
    public void onTokenChanged(String uid, String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.uid = uid;
        this.baiduUID = ErmuBusiness.getAccountAuthBusiness().getBaiduUid();
    }

    private void stopScanRunnable() {
        if(mVoiceScan!=null) {
            mVoiceScan.interrupt();
            mVoiceScan = null;
        }
        if(mWifiDirectScan!=null) {
            mWifiDirectScan.interrupt();
            mWifiDirectScan = null;
        }
        if(mApScan != null) {
            mApScan.interrupt();
            mApScan = null;
        }
        if(mUpnpScan != null) {
            mUpnpScan.interrupt();
            mUpnpScan = null;
        }
        if(mHiWifiScanRunnable != null) {
            mHiWifiScanRunnable.interrupt();
            mHiWifiScanRunnable = null;
        }
        //synchronized (SetupDevBusImpl.class) {
        //    getCamDevMap().clear();
        //}
        //smart模式扫描: 情况特色, 一次添加流程最好创建一次Socket对象
        //if(mSmartScan != null) {
        //    mSmartScan.interrupt();
        //    mSmartScan = null;
        //}
    }

    private void stopScanWifi() {
        if(mWifiScan != null) {
            mWifiScan.interrupt();
            mWifiScan = null;
        }
        synchronized (SetupDevBusImpl.class) {
            if(getScanWifiMap() != null) {
                getScanWifiMap().clear();
            }
        }
    }

    /**
     * 添加新的扫描设备列表
     *  1.过滤重复
     *  2.线程同步(数据访问锁)
     * @param list
     */
    //protected void appendCamDevList(List<CamDev> list) {
    //    synchronized (SetupDevBusImpl.class) {
    //        Map<String, CamDev> map = getCamDevMap();
    //        for(int i=0; i<list.size(); i++) {
    //            CamDev dev = list.get(i);
    //            String devID = dev.getDevID();
    //            if(TextUtils.isEmpty(devID)) {
    //                Logger.oError("扫描的设备Id为null.");
    //            }
    //            boolean b = map.containsKey(devID);
    //            if( !b ) {
    //                map.put(devID, dev);
    //            }
    //        }
    //    }
    //}

    /**
     * 添加新的Wifi扫描列表
     *  1.过滤重复
     *  2.线程同步(数据访问锁)
     * @param list
     */
    protected void appendWifiList(List<ScanResult> list) {
        synchronized (SetupDevBusImpl.class) {
            for(int i=0; i<list.size(); i++) {
                ScanResult result = list.get(i);
                Map<String, ScanResult> map = getScanWifiMap();
                if(!map.containsKey(result.SSID)) {
                    map.put(result.SSID, result);
                }
            }
        }
    }

    protected void onScanCamList() {
        execMainThread(new Runnable() {
            @Override
            public void run() {
                if(mListener == null) return;
                synchronized (SetupDevBusImpl.class) {
                    if(mListener == null) return;
                    Collection<CamDev> values = getCamDevMap().values();
                    ArrayList<CamDev> camDevs = new ArrayList<CamDev>(values);
                    List<CamDev> list = new ArrayList<CamDev>(Arrays.asList(new CamDev[camDevs.size()]));
                    Collections.copy(list, camDevs);
                    mListener.onScanCamList(list);
                }
            }
        });
    }

    protected void onScanWifiList() {
        execMainThread(new Runnable() {
            @Override
            public void run() {
                if(mListener == null) return;
                synchronized (SetupDevBusImpl.class) {
                    if(mListener == null) return;
                    mListener.onScanWifiList();
                }
            }
        });
    }

    private void onScanFail(final int businessCode, final String message) {
        execMainThread(new Runnable() {
            @Override
            public void run() {
                if(mListener == null) return;
                mListener.onScanFail(businessCode, message);
            }
        });
    }

    private void onUpdateSetupProgress(final int progress) {
        execMainThread(new Runnable() {
            @Override
            public void run() {
                if(mListener == null) return;
                mListener.onUpdateProgress(progress);
            }
        });
    }

    private void onSetupDevStatus(final SetupStatus status) {
        execMainThread(new Runnable() {
            @Override
            public void run() {
                if(mListener == null) return;
                mListener.onSetupStatus(status);
            }
        });
    }

    private Map<String, ScanResult> getScanWifiMap() {
        if(mWifiList == null) {
            mWifiList = new HashMap<String, ScanResult>();
        }
        return mWifiList;
    }

    private Map<String, CamDev> getCamDevMap() {
        if(mCamDevMap == null) {
            mCamDevMap = new HashMap<String, CamDev>();
        }
        return mCamDevMap;
    }

    //扫描摄像机设备事件回调
    OnScanCamDevListener mScanCamDev = new OnScanCamDevListener() {
        @Override
        public void onDevList(ScanDevMode mode, List<CamDev> list) {
            super.onDevList(mode, list);
            Logger.i("OnScanCamDevListener onDevList "+list.size());
            synchronized (SetupDevBusImpl.class) {
                Map<String, CamDev> map = getCamDevMap();
                for(int i=0; i<list.size(); i++) {
                    CamDev dev   = list.get(i);
                    String devID = dev.getDevID();
                    if(TextUtils.isEmpty(devID)) {
                        Logger.oError("扫描的设备Id为null.");
                        continue;
                    }
                    map.put(devID, dev);
                }
            }
            //1. 控制返回扫描设备列表最少时长: 20秒
            //2. 找不到设备: 模式为Smart 并且设备列表为0 (扫描时间 Upnp:10秒 Ap:60000 Smart:100000)
            //3. 当前设备列表已经有Smart设备:则5秒后返回
            //4. 20秒以内: 已经有Smart设备,则立即返回(最小5秒后)
            //appendCamDevList(list);
            Map<String, CamDev> map = getCamDevMap();
            if(mApScan!=null && mUpnpScan!=null && mSmartScan!=null
                && mode==ScanDevMode.AP      //该处应为Smart模式时间最长
                && map.size()<=0 ) {
                int busCode   = BusinessCode.NOTFIND_SCANDEV;
                String busMsg = BusinessMsg.matchMessage(busCode);
                onScanFail(busCode, busMsg);
            } else if(map.size()>0) {
                onScanCamList();
            }
            //if(getCamDevMap().size()<=0 && mode==ScanDevMode.AP && mApScan != null && mUpnpScan!=null) {    //检索失败, 没有找到设备
            //    int busCode = BusinessCode.NOTFIND_SCANDEV;
            //    String busMsg = BusinessMsg.matchMessage(busCode);
            //    onScanFail(busCode, busMsg);
            //} else {    //发送设备检索完成事件
            //    onScanCamList();
            //}
        }
        @Override
        public void onWifiList(List<ScanResult> list) {
            super.onWifiList(list);
            appendWifiList(list);
            //发送Wifi检索完成事件
            onScanWifiList();
        }
        @Override
        public void onWifiClose() {
            super.onWifiClose();
            //提示Wifi已关闭
            int busCode = BusinessCode.WIFI_CLOSE;
            String busMsg = BusinessMsg.matchMessage(busCode);
            onScanFail(busCode, busMsg);
        }
    };

}
