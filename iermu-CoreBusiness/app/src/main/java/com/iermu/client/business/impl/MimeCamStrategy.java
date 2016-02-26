package com.iermu.client.business.impl;

import com.iermu.client.ErmuApplication;
import com.iermu.client.IMimeCamBusiness;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.viewmodel.MimeCamItem;

import java.util.List;

/**
 * Created by wcy on 15/6/22.
 */
public class MimeCamStrategy extends BaseBusinessStrategy implements IMimeCamBusiness {

    private IMimeCamBusiness mBusiness;

    public MimeCamStrategy(IMimeCamBusiness business) {
        super(business);
        mBusiness = business;
    }

    @Override
    public void syncNewCamList() {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.syncNewCamList();
            }
        });
    }

    @Override
    public void syncOldCamList() {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.syncOldCamList();
            }
        });
    }

    @Override
    public void findCamLive(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.findCamLive(deviceId);
            }
        });
    }

    @Override
    public void syncLiveStatus(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.syncLiveStatus(deviceId);
            }
        });
    }

    @Override
    public CamLive getCamLive(String deviceId) {
        return mBusiness.getCamLive(deviceId);
    }

    @Override
    public List<CamLive> getCamList() {
        return mBusiness.getCamList();
    }

    @Override
    public List<MimeCamItem> getCamItemList() {
        return mBusiness.getCamItemList();
    }

    @Override
    public void getMineCamCount() {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.getMineCamCount();
            }
        });
    }

    @Override
    public List<CamLive> getMineCamList() {
        return mBusiness.getMineCamList();
    }

    @Override
    public void deleteCamLive(String deviceId) {
        mBusiness.deleteCamLive(deviceId);
    }

    @Override
    public String getCamDescription(String deviceId) {
        return mBusiness.getCamDescription(deviceId);
    }

    @Override
    public int getNextPageNum() {
        return mBusiness.getNextPageNum();
    }
}