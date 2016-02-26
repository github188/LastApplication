package com.iermu.client.business.impl.setupdev.setup;

import com.iermu.client.model.CamDev;
import com.iermu.client.model.CamDevConf;

/**
 * 添加(安装)设备流程接口
 *
 * Created by wcy on 15/8/10.
 */
public interface ISetupDevStep {

    public void addSetupStatusListener(SetupStatusListener listener);

    public void addSetupProgressListener(SetupProgressListener listener);

    /**
     * 初始化配置参数
     * @param dev
     * @param conf
     */
    public void init(CamDev dev, CamDevConf conf);

    /**
     * 开启连接(配置)过程
     *
     * 事件函数:
     *  @see com.iermu.client.business.impl.setupdev.setup.SetupStatusListener
     */
    public void start();

    /**
     * 注册设备
     *
     * 事件函数:
     *  @see com.iermu.client.business.impl.setupdev.setup.SetupStatusListener
     *
     */
    public void registerDev();

    /**
     * 连接设备(Ap模式| Upnp默认与设备在同一个网段)
     *
     * 事件函数:
     *  @see com.iermu.client.business.impl.setupdev.setup.SetupStatusListener
     */
    public void connectDev();

    /**
     * 配置设备信息
     *
     * 事件函数:
     *  @see com.iermu.client.business.impl.setupdev.setup.SetupStatusListener
     */
    public void configDev();

    /**
     * 重置Wifi网络
     *
     * 事件函数:
     *  @see com.iermu.client.business.impl.setupdev.setup.SetupStatusListener
     */
    public void resetWifiNetwork();

//    /**
//     * 绑定设备到云服务器(羚羊云)
//     *
//     * 事件函数:
//     *  @see com.iermu.client.business.impl.setupdev.setup.SetupStatusListener
//     */
//    public void boundDev();

    /**
     * 检测设备上线成功(刷新到该摄像头数据)
     *
     * 事件函数:
     *  @see com.iermu.client.business.impl.setupdev.setup.SetupStatusListener
     */
    public void checkDevOnline();

    /**
     * 终止连接(配置)过程
     *
     * 事件函数:
     *  @see com.iermu.client.business.impl.setupdev.setup.SetupStatusListener
     */
    public void stop();

    /**
     * 终止连接(配置)过程进度
     *
     * 事件函数:
     *  @see com.iermu.client.business.impl.setupdev.setup.SetupStatusListener
     */
    public void stopProgress();


}
