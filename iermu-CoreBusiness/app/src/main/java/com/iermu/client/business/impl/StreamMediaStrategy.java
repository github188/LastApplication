package com.iermu.client.business.impl;

import com.iermu.client.IStreamMediaBusiness;
import com.iermu.client.model.LiveMedia;
import com.iermu.lan.model.CamRecord;

/**
 * Created by wcy on 15/6/22.
 */
public class StreamMediaStrategy extends BaseBusinessStrategy implements IStreamMediaBusiness {

    private IStreamMediaBusiness mBusiness;

    public StreamMediaStrategy(IStreamMediaBusiness business) {
        super(business);
        mBusiness = business;
    }

    @Override
    public void reloadLiveMedia(final String deviceId) {
        execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.reloadLiveMedia(deviceId);
            }
        });
    }

    @Override
    public void openLiveMedia(final String deviceId) {
        execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.openLiveMedia(deviceId);
            }
        });
    }

    @Override
    public void openPubLiveMedia(final String deviceId, final String shareId, final String uk) {
        execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.openPubLiveMedia(deviceId, shareId, uk);
            }
        });
    }

    @Override
    public void closeLiveMedia(final String deviceId) {
        execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.closeLiveMedia(deviceId);
            }
        });
    }

    @Override
    public void openPlayRecord(final String deviceId, final int startTime, final int endTime, final CamRecord record) {
        execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.openPlayRecord(deviceId, startTime, endTime, record);
            }
        });
    }

    @Override
    public void openCardPlayRecord(final String deviceId, final int startTime, final int endTime) {
        execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.openCardPlayRecord(deviceId, startTime, endTime);
            }
        });
    }

    @Override
    public void closeRecord(final String deviceId) {
        execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.closeRecord(deviceId);
            }
        });
    }

    @Override
    public LiveMedia getLiveMedia(String deviceId) {
        return mBusiness.getLiveMedia(deviceId);
    }

    @Override
    public void vodSeek(final String deviceId, final int oldStartTime, final int num) {
        execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.vodSeek(deviceId, oldStartTime, num);
            }
        });
    }
}