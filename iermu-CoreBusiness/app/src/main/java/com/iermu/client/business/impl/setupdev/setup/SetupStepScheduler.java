package com.iermu.client.business.impl.setupdev.setup;

import com.iermu.client.ErmuApplication;
import com.iermu.client.model.CamDev;
import com.iermu.client.model.CamDevConf;

import retrofit.android.MainThreadExecutor;

/**
 * 添加(安装)设备流程调度器
 *
 * Created by wcy on 15/8/10.
 */
public class SetupStepScheduler implements SetupStatusListener, SetupProgressListener {

    private ISetupDevStep mSetupStep;
    private boolean stepAuto = true;    //自动执行流程

    public SetupStepScheduler(ISetupDevStep setupStep) {
        if(setupStep == null) throw new RuntimeException("SetupStep not be null.");

        this.mSetupStep = setupStep;
        setupStep.addSetupStatusListener(this);
        setupStep.addSetupProgressListener(this);
    }

    /**
     * 开始添加(安装)设备
     */
    public void startSetup(CamDev dev, CamDevConf conf) {
        this.stepAuto = true;
        mSetupStep.init(dev, conf);
        mSetupStep.start();
    }

    /**
     * 执行指定的添加(安装)设备步骤
     */
    public void registerDev(CamDev dev) {
        this.stepAuto = false;
        mSetupStep.init(dev, null);
        mSetupStep.registerDev();
    }

    /**
     * 结束添加(安装)设备
     */
    public void stopSetup() {
        mSetupStep.stop();
    }

    @Override
    public void onSetupStatusChange(SetupStatus status) {
        if( !stepAuto ) {
            mSetupStep.stopProgress();
            return; //不是自动执行流程
        }
        switch(status) {//新版羚羊云流程跟百度流程一样
        case SETUP_INITED:
            mSetupStep.registerDev();
            break;
        case REGISTER_SUCCESS:      //注册设备到云服务器成功
        case REGISTED:              //设备已注册(自己的设备)
            mSetupStep.connectDev();
            break;
        case CONNECT_DEV_SUCCESS:   //连接设备Wifi成功(Ap模式|Upno模式)
            mSetupStep.configDev();
            break;
        case CONF_DEV_SUCCESS:      //配置设备参数成功
            mSetupStep.resetWifiNetwork();
            break;
        case CONNECT_WIFI_SUCCESS:  //重连Wifi成功
            mSetupStep.checkDevOnline();
            break;
        case SETUP_SUCCESS:         //安装设备完成
            break;
        }
    }

    @Override
    public void onProgress(int progress) {

    }
}
