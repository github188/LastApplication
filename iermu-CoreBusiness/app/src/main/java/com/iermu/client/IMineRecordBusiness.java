package com.iermu.client;

import android.content.Context;

import com.iermu.client.model.CamDate;
import com.iermu.client.model.CamThumbnail;
import com.iermu.lan.model.CamRecord;
import com.iermu.lan.model.CardInforResult;
import com.iermu.lan.model.NasParamResult;
import com.iermu.lan.model.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取录像信息
 * 1.获取录像列表
 * 2.获取录像缩略图列表
 * 3.获取录像天数
 * <p>
 * Created by zhangxq on 15/8/13.
 */
public interface IMineRecordBusiness extends IBaseBusiness {

    /**
     * 获取录像列表
     *
     * @param deviceId
     */
    public void findRecordList(String deviceId);

    /**
     * 获取卡录列表
     *
     * @param deviceId
     */
    public void findCardRecordList(String deviceId);

    /**
     * 获取nas录像
     *
     * @param deviceId
     */
    public void findNasRecordList(String deviceId, long startTime, long endTime);

    /**
     * 获取缩略图列表
     *
     * @param deviceId
     */
    public void findThumbnailList(String deviceId, int dayNum);

    /**
     * 获取卡录信息
     *
     * @param deviceId
     */
    public void findCardInfo(String deviceId);

    /**
     * 获取卡录信息
     *
     * @param deviceId
     * @return
     */
    public CardInforResult getCardInfo(String deviceId);

    /**
     * 获取日期列表，用于日期选择面板
     *
     * @return
     */
    public List<CamDate> getDayTimeList(String deviceId);

    /**
     * 获取某个设备下的所有录像
     *
     * @param deviceId
     * @return
     */
    public List<CamRecord> getRecordList(String deviceId);

    /**
     * 获取卡录录像
     *
     * @param deviceId
     * @return
     */
    public List<CamRecord> getCardRecordList(String deviceId);

    /**
     * 获取某个设备下的所有缩略图
     *
     * @param deviceId
     * @return
     */
    public List<CamThumbnail> getThumbnailList(String deviceId);

    /**
     * 获取accessToken
     *
     * @return
     */
    public String getAccessToken();

    /**
     * 初始化日期数据
     */
    public void initData(String deviceId, int dayNum);

    /**
     * 卡录初始化日期数据
     *
     * @param deviceId
     * @param dayNum
     * @param endTime
     */
    public void initDate(String deviceId, int dayNum, int endTime);

    /**
     * 停止数据加载
     *
     * @param deviceId
     */
    public void stopLoadData(String deviceId);

    /**
     * 剪辑
     */
    public void startClipRec(long st, long et, String deviceId, String strFilename);

    /**
     * 结束剪辑线程
     */
    public void setStartClipRecExit();

    /**
     * 获取samba nas盘共享目录
     *
     * @param strIP
     * @param username
     * @param pwd
     * @return
     */
    public void getSmbFolder(String strIP, String username, String pwd);

    /**
     * 获取nas设置
     *
     * @param c
     * @param strDevID
     * @param uid
     * @return NasParamResult.map
     * ip：nas设置的ip
     * uName：nas用户名
     * pwd：nas密码
     * nasPath：nas存储路径
     * size：nas存储容量（G）
     */
    public void getNasParam(Context c, String strDevID, String uid);

    /**
     * @param c
     * @param strDevID   设备id
     * @param uid        userid
     * @param swNasCheck nas开关
     * @param etUser     路由器用户名
     * @param etPwd      路由器密码
     * @param spPath     视屏存储路径名
     * @param etIp       路由器ip
     * @param etSize     存储目录空间大小（G）
     * @return
     */
    public void setNasParam(Context c, String strDevID, String uid, boolean swNasCheck, String etUser, String etPwd, String spPath, String etIp, int etSize,boolean isSmb);

    /**
     * 获取Nfs nas盘共享目录
     */
    public void getNfsPath(Context c, String strDevID, String uid, String nasIp);

    /*
     * 获取后台是否正在剪辑
     *
     * @return
     */
    public boolean getIsCliping();
}
