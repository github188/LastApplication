package com.iermu.client.business.impl;

import com.iermu.client.ErmuApplication;
import com.iermu.client.IPubCamBusiness;
import com.iermu.client.model.CamLive;

import java.util.List;

/**
 *
 * Created by wcy on 15/6/22.
 */
public class PubCamStrategy extends BaseBusinessStrategy implements IPubCamBusiness {

    private IPubCamBusiness mBusiness;

    public PubCamStrategy(IPubCamBusiness business) {
        super(business);
        mBusiness = business;
    }


    @Override
    public void syncNewCamList(final String orderby) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.syncNewCamList(orderby);
            }
        });
    }

    @Override
    public void syncOldCamList(final String orderby) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.syncOldCamList(orderby);
            }
        });
    }

    @Override
    public void viewCam(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.viewCam(deviceId);
            }
        });
    }

    @Override
    public List<CamLive> getCamList(String orderby) {
        return mBusiness.getCamList(orderby);
    }

    @Override
    public CamLive getCamLive(String shareId, String uk) {
        return mBusiness.getCamLive(shareId, uk);
    }

    @Override
    public int getNextPageNum(String orderby) {
        return mBusiness.getNextPageNum(orderby);
    }

}