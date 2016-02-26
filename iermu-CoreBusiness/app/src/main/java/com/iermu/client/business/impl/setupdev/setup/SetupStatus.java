package com.iermu.client.business.impl.setupdev.setup;

/**
 * 连接(配置)设备流程状态
 *
 * Created by wcy on 15/8/10.
 */
public enum SetupStatus {

    //checkCamEnvironment
    CHECK_ENV_SMART_WIFI_NOMATCH,   //检测安装摄像机环境:Smart模式手机连接Wifi与摄像机配置Wifi不一致
    CHECK_ENV_SMART_TIMEOUT,        //检测安装摄像机环境:Smart模式超时
    CHECK_ENV_HIWIFI_CONNEVCT_TIMEOUT,        //检测安装摄像机环境:HIWIFI模式超时
    CHECK_ENV_OK,                   //检测安装摄像机环境:OK

    SETUP_INITNG,           //安装设备初始化环境
    SETUP_INITED,           //安装设备初始化完成

    //注册设备
    REGISTER_ING,           //正在注册设备
    REGISTER_SUCCESS,       //注册设备到云服务器成功
    REGISTED,               //设备已注册
    REGISTER_NOTPERMISSION, //没有权限(该设备不是当前用户的)
    REGISTER_FAIL,          //注册设备失败

    //连接设备Ap (Ap模式需要连接; Upnp默认与设备在同一网段)
    CONNECT_DEV_ING,        //正在连接设备Wifi
    CONNECT_DEV_SUCCESS,    //连接设备Wifi成功(Ap模式|Upnp模式)
    CONNECT_DEV_FAIL,       //连接设备Wifi失败(Ap模式)

    //配置设备参数
    CONF_DEV_ING,           //正在配置设备参数
    CONF_DEV_SUCCESS,       //配置设备参数成功
    CONF_DEV_FAIL,          //配置设备参数失败
    CONF_CONNECTDEV_FAIL,   //连接摄像头失败|超时
    CONF_PERMISSION_DENIED, //配置设备: 没有设备权限，第三方定制机

    //重连手机Wifi
    CONNECT_WIFI_ING,       //正在重连Wifi
    CONNECT_WIFI_SUCCESS,   //重连Wifi成功
    CONNECT_WIFI_FAIL,      //重连Wifi失败

    //连接(配置)设备完成
    SETUP_SUCCESS,          //安装设备完成
    SETUP_FAIL,             //安装设备失败(提示:网络不稳定)

    SETUP_STOPTED,          //终止安装设备

    GET_HIWIFI_IP_SUCCESS,  //hiwifi获取ip成功
    GET_HIWIFI_IP_FAIL      //hiwifi获取ip失败
}
