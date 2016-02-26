package com.iermu.client.business.impl;

import com.iermu.client.ErmuApplication;
import com.iermu.client.IUserCenterBusiness;
import com.iermu.client.model.ScreenClip;

import java.util.List;
import java.util.Map;

/**
 * Created by zhoushaopei on 16/1/7.
 *
 * 个人中心关联截图、剪辑
 */
public class UserCenterBusStrategy extends BaseBusinessStrategy implements IUserCenterBusiness {

    private IUserCenterBusiness mBusiness;

    public UserCenterBusStrategy(IUserCenterBusiness business) {
        super(business);
        mBusiness = business;
    }

    @Override
    public void getScreenClip() {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.getScreenClip();
            }
        });
    }

    @Override
    public void getUserScreen() {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.getUserScreen();
            }
        });
    }

    @Override
    public void getUserClip() {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.getUserClip();
            }
        });
    }

    @Override
    public void deleteScreenClip(final Map<String, ScreenClip> map) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.deleteScreenClip(map);
            }
        });
    }

    @Override
    public List<ScreenClip> getScreenClip(int type) {
        return mBusiness.getScreenClip(type);
    }
}
