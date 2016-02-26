package com.iermu.lan;

import android.content.Context;

import com.cms.iermu.cms.CmsErr;
import com.iermu.lan.model.NasParamResult;
import com.iermu.lan.model.NasPlayListResult;
import com.iermu.lan.model.Result;

/**
 * Created by zsj on 15/10/16.
 */
public interface NasDevApi {
    /**
     * 获取 nas盘存储目录
     * @param strIP   路由器设置IP
     * @param username  路由器用户名
     * @param pwd  路由器密码
     * @return Result.resultString nas盘存储目录
     */
    public Result getSmbFolder(String strIP, String username, String pwd);


    /**
     *获取nas录像列表
     * @param c
     * @param strDevID 设备ID
     * @param uid 百度账号登录后的uid
     * @param strSt startTime YYYY-MM-DD HH:mm:SS
     * @param strEt endTime YYYY-MM-DD HH:mm:SS
     * @param cmserr
     * @return NasPlayListResult ArrayList<NasRec>  录像信息列表
     */
    public NasPlayListResult getPlayList(Context c, String strDevID, String uid, String strSt, String strEt, CmsErr cmserr);


    /**
     * 获取nas录像播放路径
     * @param c
     * @param strDevID 设备id
     * @param uid  百度账号登录后的uid
     * @param iSt   开始时间YYYY-MM-DD HH:mm:SS
     * @param iEt   结束时间YYYY-MM-DD HH:mm:SS
     * @return Result.resultString  播放url
     */
    public Result startNasPlay(Context c, String strDevID, String uid, String iSt, String iEt);

    /**
     * 停止nas播放服务
     * @param c
     * @param strDevID
     * @param uid
     * @return
     */
    //public Result stopNasPlay(Context c, String strDevID, String uid);

    /**
     * 获取nas设置
     * @param c
     * @param strDevID
     * @param uid
     * @return NasParamResult.map
     *                      ip：nas设置的ip
     *                      uName：nas用户名
     *                      pwd：nas密码
     *                      nasPath：nas存储路径
     *                      size：nas存储容量（G）
     */
    public NasParamResult getNasParam( Context c, String strDevID,String uid) ;

    /**设置nas参数
     *
     * @param c
     * @param strDevID  设备id
     * @param uid    userid
     * @param swNasCheck nas开关
     * @param etUser    路由器用户名
     * @param etPwd    路由器密码
     * @param spPath    视屏存储路径名
     * @param etIp    路由器ip
     * @param etSize    存储目录空间大小（G）
     * @return
     */
    public Result setNasParam(Context c, String strDevID, String uid, boolean swNasCheck, String etUser, String etPwd, String spPath,String etIp,int etSize,boolean isSmb);

    /**
     * 获取Nfs nas盘共享目录
     *
     */
    public Result getNfsPath(Context c, String strDevID, String uid ,String nasIp);
}
