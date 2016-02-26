package com.iermu.client.business.impl;

import com.iermu.client.ErmuApplication;
import com.iermu.client.IStatisticsBusiness;
import com.iermu.client.business.impl.stat.StatCode;
import com.iermu.client.model.CamDev;
import com.iermu.client.model.CamDevConf;

/**
 * Created by zhangxq on 15/11/24.
 */
public class StatisticsBusStrategy extends BaseBusinessStrategy implements IStatisticsBusiness {

    private IStatisticsBusiness mbusiness;

    public StatisticsBusStrategy(IStatisticsBusiness business) {
        super(business);
        mbusiness = business;
    }

    @Override
    public void statStartPlay(final String deviceId, final int connectType, final int connectRet, final int status) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mbusiness.statStartPlay(deviceId, connectType, connectRet, status);
            }
        });
    }

    @Override
    public void statCollectLog(StatCode code, String key, String value) {

    }

    @Override
    public void pushSetupDevLog() {

    }

    @Override
    public void statStartPlay(final String deviceId, final int connectType, final int connectRet
            , final int status, final int playerErrorCode) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mbusiness.statStartPlay(deviceId, connectType, connectRet, status, playerErrorCode);
            }
        });
    }

    @Override
    public void statSetupDev(final CamDev dev, final CamDevConf conf, final boolean manualMode, final int errorCode) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mbusiness.statSetupDev(dev, conf, manualMode, errorCode);
            }
        });
    }

    @Override
    public void statPushFail(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mbusiness.statPushFail(deviceId);
            }
        });
    }
}
