package com.iermu.lan;

import android.content.Context;

import com.iermu.lan.model.Result;

/**
 * Created by zsj on 15/10/12.
 */
public interface LanDevPlatformApi {
    /**
     * 开启云台自动平扫
     * @param context
     * @param devId 设备号
     * @param uid 百度账号登录后的uid
     */
    public Result openDevRotate(Context context,String devId,String uid);

    /**
     * 关闭云台自动平扫
     * @param context
     * @param devId 设备号
     * @param uid 百度账号登录后的uid
     *
     */
    public Result closeDevRotate(Context context,String devId,String uid);


    /**
     * 添加云台预置点
     * @param context
     * @param devId 设备号
     * @param uid 百度账号登录后的uid
     * @param point 预置点序号
     */
    public Result addDevPresetPoint(Context context,String devId,String uid,int point);

    /**
     * 执行云台预置点
     * @param context
     * @param devId 设备号
     * @param uid 百度账号登录后的uid
     */
    public Result toDevPresetPoint(Context context,String devId,String uid,int number);

    /**
     * 云台按指定坐标移动
     * @param context
     * @param devId 设备号
     * @param uid 百度账号登录后的uid
     * @param xSrc 起始点X坐标
     * @param ySrc 起始点y坐标
     * @param xDest 终点X坐标
     * @param yDest 终点y坐标
     * @return Result.resultInt 0:没有到边界；1：左边界；2右边界，3，上边界，4下边界；
     */
    public Result setDevMovePoint(Context context,String devId,String uid,int xSrc,int ySrc,int xDest,int yDest);

    /**
     * 检测云台是否处于平扫状态
     * @param context
     * @param devId 设备号
     * @param uid 百度账号登录后的uid
     * @return Result.resultBoolean true:平扫状态；false：非平扫状态
     */
    public Result isRotate(Context context,String devId,String uid);

    /**
     * 检测云台是否支持坐标位移
     * @param context
     * @param devId 设备号
     * @param uid 百度账号登录后的uid
     * @return Result.resultBoolean true:支持坐标位移；false：不支持坐标位移
     */
    public Result isSupportXYMove(Context context,String devId,String uid);

    /**
     * 检测是否有云台
     * @param context
     * @param devId 设备号
     * @param uid 百度账号登录后的uid
     * @return Result.resultBoolean true:支持云台；false：不支持云台
     */
    //public Result isSupportPlatform(Context context,String devId,String uid);

    /**
     * 获取局域网直播路径
     * @param context
     * @param devId 设备号
     * @param uid 百度账号登录后的uid
     * @param version 固件版本号(例如: 6.123_100_20_2015-12-1  前面的6.123为固件版本号)
     * @return Result.resultString 局域网播放路径
     */
    public Result getLanPlayUrl(Context context, String devId, String uid, String version);




}
