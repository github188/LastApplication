package com.iermu.client.business.impl;

import android.content.Context;

import com.iermu.client.ErmuApplication;
import com.iermu.client.IMineRecordBusiness;
import com.iermu.client.listener.OnGetSmbFolderListener;
import com.iermu.client.model.CamDate;
import com.iermu.client.model.CamThumbnail;
import com.iermu.lan.model.CamRecord;
import com.iermu.lan.model.CardInforResult;
import com.iermu.lan.model.NasParamResult;
import com.iermu.lan.model.Result;
import com.iermu.lan.nas.impl.NasDevImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxq on 15/8/13.
 */
public class MineRecordStrategy extends BaseBusinessStrategy implements IMineRecordBusiness {

    private IMineRecordBusiness business;

    public MineRecordStrategy(IMineRecordBusiness business) {
        super(business);
        this.business = business;
    }

    @Override
    public void findRecordList(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                business.findRecordList(deviceId);
            }
        });
    }

    @Override
    public void findCardRecordList(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                business.findCardRecordList(deviceId);
            }
        });
    }

    @Override
    public void findNasRecordList(final String deviceId, final long startTime, final long endTime) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                business.findNasRecordList(deviceId, startTime, endTime);
            }
        });
    }

    @Override
    public void findThumbnailList(final String deviceId, final int dayNum) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                business.findThumbnailList(deviceId, dayNum);
            }
        });
    }

    @Override
    public void findCardInfo(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                business.findCardInfo(deviceId);
            }
        });
    }

    @Override
    public CardInforResult getCardInfo(String deviceId) {
        return business.getCardInfo(deviceId);
    }

    @Override
    public List<CamDate> getDayTimeList(String deviceId) {
        return business.getDayTimeList(deviceId);
    }

    @Override
    public List<CamRecord> getRecordList(String deviceId) {
        return business.getRecordList(deviceId);
    }

    @Override
    public List<CamRecord> getCardRecordList(String deviceId) {
        return business.getCardRecordList(deviceId);
    }

    @Override
    public List<CamThumbnail> getThumbnailList(String deviceId) {
        return business.getThumbnailList(deviceId);
    }

    @Override
    public String getAccessToken() {
        return business.getAccessToken();
    }

    @Override
    public void initData(String deviceId, int dayNum) {
        business.initData(deviceId, dayNum);
    }

    @Override
    public void initDate(String deviceId, int dayNum, int endTime) {
        business.initDate(deviceId, dayNum, endTime);
    }

    @Override
    public void stopLoadData(String deviceId) {
        business.stopLoadData(deviceId);
    }

    @Override
    public void startClipRec(final long st, final long et, final String deviceId, final String strFilename) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                business.startClipRec(st, et, deviceId, strFilename);
            }
        });
    }

    @Override
    public void setStartClipRecExit() {
        business.setStartClipRecExit();
    }

    @Override
    public void getSmbFolder(final String strIP, final String username, final String pwd) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                business.getSmbFolder(strIP, username, pwd);
            }
        });
    }

    @Override
    public void getNasParam(final Context c, final String strDevID, final String uid) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                business.getNasParam(c, strDevID, uid);
            }
        });
    }

    @Override
    public void setNasParam(final Context c, final String strDevID, final String uid, final boolean swNasCheck, final String etUser, final String etPwd, final String spPath, final String etIp, final int etSize,final boolean isSmb) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                business.setNasParam(c, strDevID, uid, swNasCheck, etUser, etPwd, spPath, etIp, etSize,isSmb);
            }
        });
    }

    @Override
    public void getNfsPath(final Context c, final String strDevID, final String uid, final String nasIp) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                business.getNfsPath(c, strDevID, uid, nasIp);
            }
        });
    }

    @Override
    public boolean getIsCliping() {
        return business.getIsCliping();
    }
}
