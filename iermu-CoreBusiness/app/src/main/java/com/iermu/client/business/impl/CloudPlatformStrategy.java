package com.iermu.client.business.impl;

import com.iermu.client.ErmuApplication;
import com.iermu.client.ICamShareBusiness;
import com.iermu.client.ICloudPaltformBusiness;
import com.iermu.client.model.GrantUser;

import java.util.List;

/**
 * Created by zhoushaopei on 15/7/13.
 */
public class CloudPlatformStrategy extends BaseBusinessStrategy implements ICloudPaltformBusiness {

    private ICloudPaltformBusiness mBusiness;

    public CloudPlatformStrategy(ICloudPaltformBusiness business) {
        super(business);
        this.mBusiness = business;
    }


    @Override
    public void cloudMove(final String deviceId, final int x1, final int y1, final int x2, final int y2) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.cloudMove(deviceId, x1, y1, x2, y2);
            }
        });
    }

    @Override
    public void cloudMovePreset(final String deviceId, final int preset) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.cloudMovePreset(deviceId, preset);
            }
        });
    }

    @Override
    public void startCloudRotate(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.startCloudRotate(deviceId);
            }
        });
    }

    @Override
    public void stopCloudRotate(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.stopCloudRotate(deviceId);
            }
        });
    }

    @Override
    public void addPreset(final String deviceId, final int preset, final String title) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.addPreset(deviceId, preset, title);
            }
        });
    }

    @Override
    public void dropPreset(final String deviceId, final int preset) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.dropPreset(deviceId, preset);
            }
        });
    }

    @Override
    public void getListPreset(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.getListPreset(deviceId);
            }
        });
    }

    @Override
    public void checkCloudPlatForm(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
               mBusiness.checkCloudPlatForm(deviceId);
            }
        });
    }

    @Override
    public void checkIsRotate(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.checkIsRotate(deviceId);
            }
        });
    }
}
