package com.iermu.client.business.impl;

import android.content.Context;
import android.os.Message;

import com.cms.iermu.cms.CmsErr;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.IMineRecordBusiness;
import com.iermu.client.IPreferenceBusiness;
import com.iermu.client.business.api.ClipApi;
import com.iermu.client.business.api.MimeCamApi;
import com.iermu.client.business.api.response.CamRecordListResponse;
import com.iermu.client.business.api.response.CamThumbnailListResponse;
import com.iermu.client.business.impl.event.OnAccountTokenEvent;
import com.iermu.client.business.impl.event.OnMimeCamChangedEvent;
import com.iermu.client.business.impl.event.OnSetupDevEvent;
import com.iermu.client.listener.OnCardInfoChangeListener;
import com.iermu.client.listener.OnCardRecordChangedListener;
import com.iermu.client.listener.OnCardRecordDayChangedListener;
import com.iermu.client.listener.OnClipListener;
import com.iermu.client.listener.OnGetNasParamListener;
import com.iermu.client.listener.OnGetSmbFolderListener;
import com.iermu.client.listener.OnNasRecordChangedListener;
import com.iermu.client.listener.OnRecordChangedListener;
import com.iermu.client.listener.OnSetNasParamListener;
import com.iermu.client.listener.OnThumbnailChangedListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamDate;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.CamThumbnail;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.util.DateUtil;
import com.iermu.client.util.Logger;
import com.iermu.lan.CardVidioApi;
import com.iermu.lan.NasDevApi;
import com.iermu.lan.cardVidio.impl.CareVidioImpl;
import com.iermu.lan.model.CamRecord;
import com.iermu.lan.model.CardInforResult;
import com.iermu.lan.model.ErrorCode;
import com.iermu.lan.model.NasParamResult;
import com.iermu.lan.model.NasPlayListResult;
import com.iermu.lan.model.Result;
import com.iermu.lan.nas.impl.NasDevImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangxq on 15/8/13.
 */
public class MineRecordBusImpl extends BaseBusiness implements IMineRecordBusiness, OnAccountTokenEvent
        , OnMimeCamChangedEvent, OnSetupDevEvent {

    private String accessToken;
    private Context context;
    private IPreferenceBusiness preBusiness = ErmuBusiness.getPreferenceBusiness();

    private Map<String, CamLive> mLiveMap;    //{deviceId, CamLive}
    private Map<String, List<CamDate>> timeMap; // 时间列表，用于日期面板
    private Map<String, Integer> endTimeMap; // 结束时间
    private Map<String, Integer> startTimeMap; // 开始时间
    private Map<String, List<CamRecord>> recordListMap; // 录像列表，记录每个摄像机的录像
    private Map<String, List<CamThumbnail>> thumbnailMap; // 缩略图列表，记录每个摄像机的缩略图
    private Map<String, Boolean> stopMap; // 停止列表，记录页面是否关闭，如果关闭，停止对应的数据加载循环

    private Map<String, CardInforResult> cardInfoMap; // 卡录信息
    private Map<String, Map<Integer, List<CamRecord>>> cardRecordListMap; // 卡录列表, deviceid, daystartTime, 录像列表

    private boolean isRecrodLoaded; // 记录录像是否加载完成
    private boolean isThumbnailLoaded; // 记录缩略图是否加载完成

    private boolean clipFlag = false;

    public MineRecordBusImpl() {
        this.context = ErmuApplication.getContext();
        timeMap = new HashMap<String, List<CamDate>>();
        endTimeMap = new HashMap<String, Integer>();
        startTimeMap = new HashMap<String, Integer>();
        recordListMap = new HashMap<String, List<CamRecord>>();
        thumbnailMap = new HashMap<String, List<CamThumbnail>>();
        stopMap = new Hashtable<String, Boolean>();
        cardInfoMap = new HashMap<String, CardInforResult>();
        cardRecordListMap = new Hashtable<String, Map<Integer, List<CamRecord>>>();
        accessToken = ErmuBusiness.getAccountAuthBusiness().getAccessToken();
        subscribeEvent(OnAccountTokenEvent.class, this);
        subscribeEvent(OnMimeCamChangedEvent.class, this);
        subscribeEvent(OnSetupDevEvent.class, this);
    }

    @Override
    public void initData(String deviceId, int dayNum) {
        Logger.d("currentTime::" + DateUtil.endTime(new Date()).getTime() / 1000 / 60 / 60);
        endTimeMap.put(deviceId, (int) (DateUtil.endTime(new Date()).getTime() / 1000));
        startTimeMap.put(deviceId, endTimeMap.get(deviceId) - DateUtil.DAY_SECOND_NUM * dayNum);
        if (thumbnailMap.get(deviceId) == null) {
            thumbnailMap.put(deviceId, new ArrayList<CamThumbnail>());
        }
        if (timeMap.get(deviceId) == null) {
            timeMap.put(deviceId, new ArrayList<CamDate>());
            List<CamDate> timeList = timeMap.get(deviceId);
            for (int i = 0; i < dayNum; i++) {
                int dayStartTime = endTimeMap.get(deviceId) - DateUtil.DAY_SECOND_NUM * (i + 1);
                timeList.add(new CamDate(dayStartTime, false));
            }
        }
    }

    @Override
    public void initDate(String deviceId, int dayNum, int endTime) {
        endTimeMap.put(deviceId, endTime + 1);
        startTimeMap.put(deviceId, endTimeMap.get(deviceId) - DateUtil.DAY_SECOND_NUM * dayNum);
        if (timeMap.get(deviceId) == null) {
            timeMap.put(deviceId, new ArrayList<CamDate>());
            List<CamDate> timeList = timeMap.get(deviceId);
            for (int i = 0; i < dayNum; i++) {
                int dayStartTime = endTimeMap.get(deviceId) - DateUtil.DAY_SECOND_NUM * (i + 1);
                timeList.add(new CamDate(dayStartTime, false));
            }
        }
    }

    @Override
    public void stopLoadData(String deviceId) {
        stopMap.put(deviceId, true);
    }

    @Override
    public void startClipRec(long st, long et, String deviceId, String strFilename) {
        CmsErr pcserr = new CmsErr(-1, "");
        String strAccessToken = ErmuBusiness.getAccountAuthBusiness().getBaiduAccessToken();
        String strClipStatus = ClipApi.getClipStatus(strAccessToken, deviceId, true, pcserr);
        if (pcserr.getErrCode() == 200 && strClipStatus.indexOf("status") >= 0) { // 检查是否有进行中的任务
            JSONObject retMsg;
            try {
                retMsg = new JSONObject(strClipStatus);
                int iStatus = retMsg.getInt("status");
                if (iStatus == 1) { // 有进行中的任务
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = retMsg.getInt("progress");
                    sendListener(OnClipListener.class, msg);
                    return;
                }
            } catch (JSONException e) {
                Message msg = new Message();
                msg.what = -1;
                msg.obj = pcserr;
                preBusiness.setFilmEditIsFaild(true);
                sendListener(OnClipListener.class, msg);
                e.printStackTrace();
            }
        }

        boolean bIsexistFile = ClipApi.isExistFile(strAccessToken, strFilename, pcserr);
        String strRecname = strFilename;
        if (pcserr.getErrCode() == 0 && bIsexistFile) { // 文件名重复
            Message msg = new Message();
            msg.what = 2;
            msg.obj = pcserr;
            sendListener(OnClipListener.class, msg);
            return;
        }

        boolean bOk = ClipApi.startClip(strAccessToken, deviceId, st, et, strRecname, pcserr);
        if (bOk) {
            clipFlag = true;
            while (clipFlag) {
                String clipProgress = ClipApi.getClipStatus(strAccessToken, deviceId, true, pcserr);
                if (pcserr.getErrCode() == 200 && clipProgress.indexOf("status") >= 0) { // 查询进度
                    try {
                        JSONObject retMsg = new JSONObject(clipProgress);
                        int iStatus = retMsg.getInt("status");
                        if (iStatus == 1) {
                            Message msg = new Message();
                            msg.what = 0;
                            msg.obj = retMsg.getInt("progress");
                            sendListener(OnClipListener.class, msg);
                        } else if (iStatus == 0) {
                            clipFlag = false;
                            Message msg = new Message();
                            msg.what = 3;
                            sendListener(OnClipListener.class, msg);
                            preBusiness.setFilmEditIsFaild(false);
                        } else {
                            clipFlag = false;
                            Message msg = new Message();
                            msg.what = -1;
                            msg.obj = pcserr;
                            preBusiness.setFilmEditIsFaild(true);
                            sendListener(OnClipListener.class, msg);
                        }

                    } catch (JSONException e) {
                        Message msg = new Message();
                        msg.what = -1;
                        msg.obj = pcserr;
                        preBusiness.setFilmEditIsFaild(true);
                        sendListener(OnClipListener.class, msg);
                        clipFlag = false;
                        e.printStackTrace();
                    }
                } else {
                    Message msg = new Message();
                    msg.what = -1;
                    msg.obj = "剪辑失败";
                    preBusiness.setFilmEditIsFaild(true);
                    sendListener(OnClipListener.class, msg);
                    clipFlag = false;
                }

            }
//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        } else {
            Message msg = new Message();
            msg.what = -1;
            msg.obj = pcserr;
            preBusiness.setFilmEditIsFaild(true);
            sendListener(OnClipListener.class, msg);
        }
    }

    @Override
    public void setStartClipRecExit() {
        clipFlag = false;
    }

    @Override
    public boolean getIsCliping() {
        return clipFlag;
    }

    @Override
    public void findRecordList(String deviceId) {
        Map<String, CamLive> map = getCamLiveMap();
        if (!map.containsKey(deviceId)) {
            throw new RuntimeException("Not find the device.");
        }
        CamLive live = map.get(deviceId);
        int connectType = live.getConnectType();
        int startTime = startTimeMap.get(deviceId);
        int endTime = endTimeMap.get(deviceId);
        CamRecordListResponse res;
        if (connectType == ConnectType.LINYANG) {
            res = MimeCamApi.apiGetLyyRecordList(deviceId, accessToken, startTime, endTime);
        } else {
            res = MimeCamApi.apiGetBaiduRecordList(deviceId, accessToken, startTime + DateUtil.TIME_ZONE_OFFECT, endTime + DateUtil.TIME_ZONE_OFFECT);
        }

        Business busRecord = res.getBusiness();
        switch (busRecord.getCode()) {
            case BusinessCode.SUCCESS:
                List<CamRecord> records = res.getRecords();
                setIsExistRecord(records, deviceId);
                recordListMap.put(deviceId, records);
                break;
            default:
                //TODO 通知界面错误
                break;
        }

        sendListener(OnRecordChangedListener.class, busRecord);
        isRecrodLoaded = true;
        if (isThumbnailLoaded) {
            filteThumbnail(deviceId);
            sendListener(OnThumbnailChangedListener.class);
        }
    }

    @Override
    public void findCardRecordList(String deviceId) {
        stopMap.put(deviceId, false);
        NasDevApi nasDevApi = new NasDevImpl();
        CardInforResult cardInforResult = getCardInfo(deviceId);
        if (cardInforResult == null) {
            Logger.d("cardInfoResutl = null");
            return;
        }
        String uid = ErmuBusiness.getAccountAuthBusiness().getBaiduUid();

        List<CamDate> camDates = timeMap.get(deviceId + "card");
        Map<Integer, List<CamRecord>> cardMap = cardRecordListMap.get(deviceId);
        if (cardMap == null) {
            cardMap = new HashMap<Integer, List<CamRecord>>();
            cardRecordListMap.put(deviceId, cardMap);
        }

        int dayNoRecordNum = 0;
        for (int i = 0; i < camDates.size(); i++) {
            if (stopMap.get(deviceId)) {
                Logger.d("findCardListStop");
                return;
            }
            CamDate camDate = camDates.get(i);
            if (cardMap.get(camDate.getDayStartTime()) == null || cardMap.get(camDate.getDayStartTime()).size() == 0) {
                String startTime = DateUtil.dateToString(new Date(camDate.getDayStartTime() * 1000l), DateUtil.FORMAT_ONE);
                String endTime = DateUtil.dateToString(new Date((camDate.getDayStartTime() + DateUtil.DAY_SECOND_NUM - 1) * 1000l), DateUtil.FORMAT_ONE);
                NasPlayListResult result = nasDevApi.getPlayList(context, deviceId, uid, startTime, endTime, null);
                ErrorCode errorCode = result.getErrorCode();
                if (errorCode.getIndex() == ErrorCode.SUCCESS.ordinal()) {
                    List<CamRecord> records = result.getList();
                    if (records != null && records.size() > 0) {
                        cardMap.put(camDate.getDayStartTime(), records);
                        camDate.setIsExistRecord(true);
                    } else {
                        cardMap.put(camDate.getDayStartTime(), new ArrayList<CamRecord>());
                        camDate.setIsExistRecord(false);
                    }
                    Logger.d("onSuccess >>>>>>>>" + "day" + i + ":" + cardMap.get(camDate.getDayStartTime()).size());
                    sendListener(OnCardRecordDayChangedListener.class, errorCode, i);
                } else {
                    if (cardMap.get(camDate.getDayStartTime()) == null) {
                        dayNoRecordNum++;
                    }
                    Logger.d("onFail >>>>>>>>" + "day" + i);
                }
            }
        }

        sendListener(OnCardRecordChangedListener.class, dayNoRecordNum);
    }

    @Override
    public void findNasRecordList(String deviceId, long startTime, long endTime) {
        String uid = ErmuBusiness.getAccountAuthBusiness().getBaiduUid();
        String startTimeStr = DateUtil.dateToString(new Date(startTime), DateUtil.FORMAT_ONE);
        String endTimeStr = DateUtil.dateToString(new Date(endTime), DateUtil.FORMAT_ONE);
        NasDevApi nasDevApi = new NasDevImpl();
        NasPlayListResult result = nasDevApi.getPlayList(context, deviceId.substring(0, deviceId.length() - 3), uid, startTimeStr, endTimeStr, null);

        ErrorCode errorCode = result.getErrorCode();
        if (errorCode.getIndex() == ErrorCode.SUCCESS.ordinal()) {
            List<CamRecord> records = result.getList();
            if (records != null && records.size() > 0) {
                int startTimeInt = records.get(0).getStartTime();
                String startTimeBeginStr = DateUtil.formatDate(new Date(startTimeInt * 1000l), DateUtil.FORMAT_ONE).substring(0, 10) + " 00:00:00";
                String endTimeEndStr = DateUtil.formatDate(new Date(), DateUtil.FORMAT_ONE).substring(0, 10) + " 23:59:59";
                int st = (int) (DateUtil.stringtoDate(startTimeBeginStr, DateUtil.FORMAT_ONE).getTime() / 1000);
                int et = (int) (DateUtil.stringtoDate(endTimeEndStr, DateUtil.FORMAT_ONE).getTime() / 1000);
                int dayNum = (et - st + 10) / DateUtil.DAY_SECOND_NUM; // +10是为了容错
                initDate(deviceId, dayNum, et);
                setIsExistRecord(records, deviceId);
                recordListMap.put(deviceId, records);
            }
        }

        sendListener(OnNasRecordChangedListener.class, errorCode);
    }

    @Override
    public void findThumbnailList(String deviceId, int dayNum) {
        stopMap.put(deviceId, false);
        Map<String, CamLive> map = getCamLiveMap();
        if (!map.containsKey(deviceId)) {
            throw new RuntimeException("Not find the device.");
        }
        CamLive live = map.get(deviceId);
        int connectType = live.getConnectType();
        for (int i = 0; i < dayNum; i++) {
            if (stopMap.get(deviceId)) {
                Logger.d("findThumbnailListStop");
                return;
            }
            int dayEndTime = endTimeMap.get(deviceId) - DateUtil.DAY_SECOND_NUM * i;
            findThumbnailListByDay(deviceId, connectType == ConnectType.LINYANG, dayEndTime, i == 0);
        }

        isThumbnailLoaded = true;
        if (isRecrodLoaded) {
            filteThumbnail(deviceId);
            sendListener(OnThumbnailChangedListener.class);
        }
    }

    @Override
    public void findCardInfo(String deviceId) {
        CardVidioApi cardVidioApi = new CareVidioImpl();
        String uid = ErmuBusiness.getAccountAuthBusiness().getBaiduUid();
        CardInforResult cardInforResult = cardVidioApi.getCardInfor(context, deviceId, uid);
        ErrorCode errorCode = cardInforResult.getErrorCode();
        if (errorCode.getIndex() == ErrorCode.SUCCESS.ordinal()) {
            cardInfoMap.put(deviceId, cardInforResult);
        } else {
            // TODO 处理错误
        }
        sendListener(OnCardInfoChangeListener.class, errorCode);
    }

    @Override
    public CardInforResult getCardInfo(String deviceId) {
        return cardInfoMap.get(deviceId);
    }

    @Override
    public List<CamDate> getDayTimeList(String deviceId) {
        return timeMap.get(deviceId) == null ? new ArrayList<CamDate>() : timeMap.get(deviceId);
    }

    @Override
    public List<CamRecord> getRecordList(String deviceId) {
        return recordListMap.get(deviceId) == null ? new ArrayList<CamRecord>() : recordListMap.get(deviceId);
    }

    @Override
    public List<CamRecord> getCardRecordList(String deviceId) {
        List<CamRecord> list = new ArrayList<CamRecord>();
        List<CamDate> camDates = timeMap.get(deviceId + "card");
        Map<Integer, List<CamRecord>> cardMap = cardRecordListMap.get(deviceId);
        if (camDates != null && camDates.size() > 0 && cardMap != null) {
            for (int i = 0; i < camDates.size(); i++) {
                CamDate camDate = camDates.get(i);
                List<CamRecord> records = cardMap.get(camDate.getDayStartTime());
                if (records != null) {
                    List<CamRecord> recordsTemp = new ArrayList<CamRecord>(records);
                    recordsTemp.addAll(list);
                    list = recordsTemp;
                }
            }
        }
        Logger.d("getCardRecordList >>>>>>>>" + list.size());
        return list;
    }

    @Override
    public List<CamThumbnail> getThumbnailList(String deviceId) {
        return thumbnailMap.get(deviceId) == null ? new ArrayList<CamThumbnail>() : new ArrayList<CamThumbnail>(thumbnailMap.get(deviceId));
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    @Override
    public void onTokenChanged(String uid, String accessToken, String refreshToken) {
        this.accessToken = accessToken;
    }

    @Override
    public void onMimeCamChanged(List<CamLive> list) {
        if (list == null) return;
        synchronized (StreamMediaBusiness.class) {
            Iterator<CamLive> iterator = list.iterator();
            while (iterator.hasNext()) {
                CamLive next = iterator.next();
                String deviceId = next.getDeviceId();
                getCamLiveMap().put(deviceId, next);
            }
        }
    }

    @Override
    public void onSetupDevEvent(CamLive live) {
        if (live == null) return;
        String deviceId = live.getDeviceId();
        getCamLiveMap().put(deviceId, live);
    }

    /**
     * 遍历设置每天有没有录像
     *
     * @param records
     * @param deviceid
     */
    private void setIsExistRecord(List<CamRecord> records, String deviceid) {
        List<CamDate> camDates = timeMap.get(deviceid);
        int recordBeginIndex = records.size() - 1;
        for (int i = 0; i < camDates.size(); i++) {
            CamDate camDate = camDates.get(i);
            int dayStartTime = camDate.getDayStartTime();
            int dayEndTime = camDate.getDayStartTime() + DateUtil.DAY_SECOND_NUM;
            for (int j = recordBeginIndex; j >= 0; j--) {
                CamRecord camRecord = records.get(j);
                int recordStartTime = camRecord.getStartTime();
                int recordEndTime = camRecord.getEndTime();
                if (recordStartTime < dayStartTime && recordEndTime > dayStartTime ||
                        recordStartTime > dayStartTime && recordEndTime < dayEndTime ||
                        recordStartTime < dayEndTime && recordEndTime > dayEndTime ||
                        recordStartTime < dayStartTime && recordEndTime > dayEndTime) {
                    camDate.setIsExistRecord(true);
                    recordBeginIndex = j;
                    break;
                }
            }
        }
    }

    private Map<String, CamLive> getCamLiveMap() {
        if (mLiveMap == null) {
            mLiveMap = new HashMap<String, CamLive>();
        }
        return mLiveMap;
    }

    /**
     * 循环获取缩略图
     */
    private void findThumbnailListByDay(String deviceId, boolean islyy, int endTime, boolean isFirst) {
        int startTime = endTime - DateUtil.DAY_SECOND_NUM;
        if (!islyy) {
            startTime += DateUtil.TIME_ZONE_OFFECT;
            endTime += DateUtil.TIME_ZONE_OFFECT;
        }
        CamThumbnailListResponse responseThumbnail = MimeCamApi.apiGetThumbnailList(deviceId, accessToken, startTime, endTime, islyy);
        Business busThumbnail = responseThumbnail.getBusiness();
        switch (busThumbnail.getCode()) {
            case BusinessCode.SUCCESS:
                List<CamThumbnail> thumbnails = responseThumbnail.getList();
                if (!isFirst) {
                    thumbnails.addAll(thumbnailMap.get(deviceId));
                }
                thumbnailMap.put(deviceId, thumbnails);
                break;
            default:
                //TODO 通知界面错误
                break;
        }
    }

    /**
     * 过滤没用的缩略图
     */
    private synchronized void filteThumbnail(String deviceId) {
        List<CamRecord> records = getRecordList(deviceId);
        List<CamThumbnail> thumbnails = getThumbnailList(deviceId);
        List<CamThumbnail> thumbnailsFilte = new ArrayList<CamThumbnail>(thumbnails);
        int recordIndex = 0;
        for (int i = 0; i < thumbnails.size(); i++) {
            for (int j = recordIndex; j < records.size(); j++) {
                CamThumbnail camThumbnail = thumbnails.get(i);
                CamRecord camRecord = records.get(j);
                if (camThumbnail.getTime() > camRecord.getStartTime() && camThumbnail.getTime() < camRecord.getEndTime()) {
                    recordIndex = j;
                    break;
                } else if (camThumbnail.getTime() < camRecord.getStartTime()) {
                    recordIndex = j;
                    thumbnailsFilte.remove(camThumbnail);
                    break;
                }
            }
        }
        thumbnailMap.put(deviceId, thumbnailsFilte);
    }

    /**
     * 获取samba nas盘共享目录
     *
     * @param strIP
     * @param username
     * @param pwd
     * @return
     */
    @Override
    public void getSmbFolder(String strIP, String username, String pwd) {
        Result result = new NasDevImpl().getSmbFolder(strIP, username, pwd);
        sendListener(OnGetSmbFolderListener.class, result);

    }

    /**
     * 获取Nfs nas盘共享目录
     */
    public void getNfsPath(Context c, String strDevID, String uid, String nasIp) {
        Result result = new NasDevImpl().getNfsPath(c, strDevID, uid, nasIp);
        sendListener(OnGetSmbFolderListener.class, result);
    }


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
    public void getNasParam(Context c, String strDevID, String uid) {
        NasParamResult result = null;
        for (int i = 0; i < 3; i++) {
            result = new NasDevImpl().getNasParam(c, strDevID, uid);
            if(result.getErrorCode()==ErrorCode.SUCCESS)break;
        }
        sendListener(OnGetNasParamListener.class, result);
    }

    /**
     * 设置nas参数
     *
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
    public void setNasParam(Context c, String strDevID, String uid, boolean swNasCheck, String etUser, String etPwd, String spPath, String etIp, int etSize,boolean isSmb) {
        Result result = new NasDevImpl().setNasParam(c, strDevID, uid, swNasCheck, etUser, etPwd, spPath, etIp, etSize,isSmb);
        sendListener(OnSetNasParamListener.class, result);
    }
}
