package com.iermu.client.business.impl;

import com.iermu.client.ErmuApplication;
import com.iermu.client.IMessageCamBusiness;
import com.iermu.client.model.AlarmDeviceItem;
import com.iermu.client.model.AlarmImageData;

import java.util.List;

/**
 *
 * Created by wcy on 15/6/22.
 */
public class MessageCamStrategy extends BaseBusinessStrategy implements IMessageCamBusiness {

    private IMessageCamBusiness mBusiness;

    public MessageCamStrategy(IMessageCamBusiness business) {
        super(business);
        mBusiness = business;
    }

    @Override
    public int getMineCamLiveCount() {
        return mBusiness.getMineCamLiveCount();
    }

    @Override
    public void getMineCamList() {
        mBusiness.getMineCamList();
    }

    @Override
    public void syncNewImageDatas(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.syncNewImageDatas(deviceId);
            }
        });
    }

    @Override
    public void syncOldImageDatas(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.syncOldImageDatas(deviceId);
            }
        });
    }

    @Override
    public List<AlarmImageData> getImageDatas() {
        return mBusiness.getImageDatas();
    }

    @Override
    public void deleteImageDatas(List<Long> ids) {
        mBusiness.deleteImageDatas(ids);
    }

    @Override
    public long getOpendAlarmCamCount() {
        return mBusiness.getOpendAlarmCamCount();
    }
}