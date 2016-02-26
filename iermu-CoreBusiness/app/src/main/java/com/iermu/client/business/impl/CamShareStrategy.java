package com.iermu.client.business.impl;

import com.iermu.client.ErmuApplication;
import com.iermu.client.ICamShareBusiness;
import com.iermu.client.model.GrantUser;

import java.util.List;

/**
 * Created by zhoushaopei on 15/7/13.
 */
public class CamShareStrategy extends BaseBusinessStrategy implements ICamShareBusiness {

    private ICamShareBusiness mBusiness;

    public CamShareStrategy(ICamShareBusiness business) {
        super(business);
        this.mBusiness = business;
    }

    @Override
    public void syncGrantUsers(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.syncGrantUsers(deviceId);
            }
        });

    }

    @Override
    public void createShare(final String deviceId, final String introduce, final int shareType) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.createShare(deviceId, introduce, shareType);
            }
        });
    }

    @Override
    public void cancleShare(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.cancleShare(deviceId);
            }
        });
    }

    @Override
    public void getGrantCode(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.getGrantCode(deviceId);
            }
        });
    }

    @Override
    public void grantShare(final String code) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.grantShare(code);
            }
        });
    }

    @Override
    public String getShareLink(String deviceId) {
        return mBusiness.getShareLink(deviceId);
    }

    @Override
    public List<GrantUser> getGrantUser(String deviceId) {
        return mBusiness.getGrantUser(deviceId);
    }

    @Override
    public void dropGrantUser(final String deviceId, final String uk) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.dropGrantUser(deviceId, uk);
            }
        });
    }

    @Override
    public void dropGrantDevice(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.dropGrantDevice(deviceId);
            }
        });
    }
}
