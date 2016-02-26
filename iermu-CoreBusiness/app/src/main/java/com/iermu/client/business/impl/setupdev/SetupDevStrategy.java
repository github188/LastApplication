package com.iermu.client.business.impl.setupdev;

import android.net.wifi.ScanResult;

import com.iermu.client.ErmuApplication;
import com.iermu.client.ISetupDevBusiness;
import com.iermu.client.business.impl.BaseBusinessStrategy;
import com.iermu.client.listener.OnSetupDevListener;
import com.iermu.client.model.CamDev;
import com.iermu.client.model.CamDevConf;

import java.util.List;

/**
 * 添加(安装)摄像机设备业务模块
 *  1.参数格式校验
 *  2.集合数组复制
 *  3.异步执行
 *
 *  4.函数调用日志
 *  5.函数执行时长
 *
 * Created by wcy on 15/6/22.
 */
public class SetupDevStrategy extends BaseBusinessStrategy implements ISetupDevBusiness {

    private ISetupDevBusiness mBusiness;

    public SetupDevStrategy(ISetupDevBusiness business) {
        super(business);
        mBusiness = business;
    }

    @Override
    public void addSetupDevListener(OnSetupDevListener listener) {
        mBusiness.addSetupDevListener(listener);
    }

    @Override
    public void removeSetupDevListener(OnSetupDevListener listener) {
        mBusiness.removeSetupDevListener(listener);
    }

    @Override
    public void scanCam(final CamDevConf camDevConf) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.scanCam(camDevConf);
            }
        });
    }

    @Override
    public void scanSpecifiedCam(final String camDevID) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.scanSpecifiedCam(camDevID);
            }
        });
    }

    @Override
    public void scanWifi() {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.scanWifi();
            }
        });
    }

    @Override
    public void checkCamEnvironment(final CamDev camDev, final boolean manualMode, final CamDevConf conf) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.checkCamEnvironment(camDev, manualMode, conf);
            }
        });
    }

    @Override
    public void connectCam(final CamDev camDev, final boolean manualMode, final CamDevConf conf) {
        if(camDev == null) {
            throw new RuntimeException("");
        }
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.connectCam(camDev, manualMode, conf);
            }
        });
    }

    @Override
    public void registerCamStep(final CamDev camDev) {
        if(camDev == null) {
            throw new RuntimeException("");
        }
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.registerCamStep(camDev);
            }
        });
    }

//    @Override
//    public void connectCam(final CamDev camDev, final String wifiSSID, final String pwd) {
//        if(camDev == null) {
//            throw new RuntimeException("");
//        }
//        ErmuApplication.execBackground(new Runnable() {
//            @Override
//            public void run() {
//                mBusiness.connectCam(camDev, wifiSSID, pwd);
//            }
//        });
//    }

    @Override
    public void _registerCam(final CamDev camDev) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness._registerCam(camDev);
            }
        });
    }

    @Override
    public void quitSetupDev() {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.quitSetupDev();
            }
        });
    }

    @Override
    public void quitScanCam() {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.quitScanCam();
            }
        });
    }

    @Override
    public void quitScanWifi() {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.quitScanWifi();
            }
        });
    }

    @Override
    public String scanConnectSSID() {
        return mBusiness.scanConnectSSID();
    }

    @Override
    public String scanConnectBSSID() {
        return mBusiness.scanConnectBSSID();
    }

    @Override
    public int getConnectWifiType() {
        return mBusiness.getConnectWifiType();
    }

    @Override
    public List<CamDev> getScanCamDev() {
        return mBusiness.getScanCamDev();
    }

    @Override
    public boolean existedSmartCamDev() {
        return mBusiness.existedSmartCamDev();
    }

    @Override
    public List<ScanResult> getScanWifi() {
        return mBusiness.getScanWifi();
    }
}
