package com.iermu.client.business.impl;

import com.iermu.client.ErmuApplication;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.model.CamAlarm;
import com.iermu.client.model.CamCron;
import com.iermu.client.model.CamCvr;
import com.iermu.client.model.CamInfo;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.CamStatus;
import com.iermu.client.model.CronRepeat;
import com.iermu.client.model.Email;

import java.util.Date;
import java.util.List;

/**
 * Created by wcy on 15/7/2.
 */
public class CamSettingStrategy extends BaseBusinessStrategy implements ICamSettingBusiness {

    private ICamSettingBusiness mBusiness;

    public CamSettingStrategy(ICamSettingBusiness business) {
        super(business);
        this.mBusiness = business;
    }

//    @Override
//    public void syncCamSetting(final String deviceId) {
//        ErmuApplication.execBackground(new Runnable() {
//            @Override
//            public void run() {
//                mBusiness.syncCamSetting(deviceId);
//            }
//        });
//    }

    @Override
    public void syncCamInfo(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.syncCamInfo(deviceId);
            }
        });
    }

    @Override
    public void syncCamStatus(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.syncCamStatus(deviceId);
            }
        });
    }

    @Override
    public void syncCamPowerCron(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.syncCamPowerCron(deviceId);
            }
        });
    }

    @Override
    public void syncCamCvr(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.syncCamCvr(deviceId);
            }
        });
    }

    @Override
    public void syncCamAlarm(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.syncCamAlarm(deviceId);
            }
        });
    }

    @Override
    public void syncCamCloud() {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.syncCamCloud();
            }
        });
    }

    @Override
    public void checkCamFirmware(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.checkCamFirmware(deviceId);
            }
        });
    }

    @Override
    public void exitExitcheckCamFirmware() {
        mBusiness.exitExitcheckCamFirmware();
    }

    @Override
    public void checkUpgradeVersion(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.checkUpgradeVersion(deviceId);
            }
        });
    }

    @Override
    public void restartCamDev(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.restartCamDev(deviceId);
            }
        });
    }

    @Override
    public void dropCamDev(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.dropCamDev(deviceId);
            }
        });
    }

    @Override
    public void updateCamName(final String deviceId, final String camName) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.updateCamName(deviceId, camName);
            }
        });
    }

    @Override
    public void powerCamDev(final String deviceId, final boolean powerSwitch) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.powerCamDev(deviceId, powerSwitch);
            }
        });
    }

    @Override
    public void setCamMaxspeed(final String deviceId, final int maxSpeed) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.setCamMaxspeed(deviceId, maxSpeed);
            }
        });
    }

    @Override
    public void setDevLight(final String deviceId, final boolean light) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.setDevLight(deviceId, light);
            }
        });
    }

    @Override
    public void setDevInvert(final String deviceId, final boolean invert) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.setDevInvert(deviceId, invert);
            }
        });
    }

    @Override
    public void setDevCvr(final String deviceId, final boolean isCvr) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.setDevCvr(deviceId, isCvr);
            }
        });
    }

    @Override
    public void setDevAudio(final String deviceId, final boolean audio) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.setDevAudio(deviceId, audio);
            }
        });
    }

    @Override
    public void setDevScene(final String deviceId, final boolean scene) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.setDevScene(deviceId, scene);
            }
        });
    }

    @Override
    public void setDevNightMode(final String deviceId, final int nightmode) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.setDevNightMode(deviceId, nightmode);
            }
        });
    }

    @Override
    public void setDevExposeMode(final String deviceId, final int exposemode) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.setDevExposeMode(deviceId, exposemode);
            }
        });
    }

    @Override
    public void setCamCron(final String deviceId, final boolean isCron, final Date begin, final Date end, final CronRepeat repeat) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.setCamCron(deviceId, isCron, begin, end, repeat);
            }
        });
    }

//    @Override
//    public void setCamCron(final String deviceId, final boolean isCron) {
//        ErmuApplication.execBackground(new Runnable() {
//            @Override
//            public void run() {
//                mBusiness.setCamCron(deviceId, isCron);
//            }
//        });
//    }

    @Override
    public void setCamAlarm(final String deviceId, final int level, final Date begin, final Date end, final CronRepeat repeat) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.setCamAlarm(deviceId, level, begin, end, repeat);
            }
        });
    }

//    @Override
//    public void setCamAlarmCron(final String deviceId, final int level, final boolean isCron, final Date begin, final Date end, final CronRepeat repeat) {
//        ErmuApplication.execBackground(new Runnable() {
//            @Override
//            public void run() {
//                mBusiness.setCamAlarmCron(deviceId, level, isCron, begin, end, repeat);
//            }
//        });
//    }
//
//    @Override
//    public void setCamCronTime(final String deviceId, final Date begin, final Date end) {
//        ErmuApplication.execBackground(new Runnable() {
//            @Override
//            public void run() {
//                mBusiness.setCamCronTime(deviceId, begin, end);
//            }
//        });
//    }

    @Override
    public void setCamCronRepeat(final String deviceId, final CronRepeat repeat) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.setCamCronRepeat(deviceId, repeat);
            }
        });
    }

    @Override
    public void setCvr(final String deviceId, final boolean isCvr) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.setCvr(deviceId, isCvr);
            }
        });
    }

    @Override
    public void setCvrCron(final String deviceId, final boolean isDevCvr, final boolean isCron, final Date begin, final Date end, final CronRepeat repeat) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.setCvrCron(deviceId,isDevCvr, isCron, begin, end, repeat);
            }
        });
    }

//    @Override
//    public void setCvrCronTime(final String deviceId, final Date begin, final Date end) {
//        ErmuApplication.execBackground(new Runnable() {
//            @Override
//            public void run() {
//                mBusiness.setCvrCronTime(deviceId, begin, end);
//            }
//        });
//    }
//
//    @Override
//    public void setCvrCronRepeat(final String deviceId, final CronRepeat repeat) {
//        ErmuApplication.execBackground(new Runnable() {
//            @Override
//            public void run() {
//                mBusiness.setCvrCronRepeat(deviceId, repeat);
//            }
//        });
//    }

//    @Override
//    public void setAlarmNotice(final String deviceId, final boolean isNotice) {
//        ErmuApplication.execBackground(new Runnable() {
//            @Override
//            public void run() {
//                mBusiness.setAlarmNotice(deviceId, isNotice);
//            }
//        });
//    }

//    @Override
//    public void setAlarmCron(final String deviceId, final boolean isCron) {
//        ErmuApplication.execBackground(new Runnable() {
//            @Override
//            public void run() {
//                mBusiness.setAlarmCron(deviceId, isCron);
//            }
//        });
//    }

    @Override
    public void setAlarmCronTime(final String deviceId, final Date begin, final Date end) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.setAlarmCronTime(deviceId, begin, end);
            }
        });
    }

    @Override
    public void setAlarmCronRepeat(final String deviceId, final CronRepeat repeat) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.setAlarmCronRepeat(deviceId, repeat);
            }
        });
    }

    @Override
    public void setCamEamilCron(final String deviceId, final boolean isCron) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.setCamEamilCron(deviceId, isCron);
            }
        });
    }

    @Override
    public void setDevEmail(final String deviceId, final String to, final String cc, final String server, final String port
            , final String from, final String user, final String passwd, final boolean isSSL) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.setDevEmail(deviceId, to, cc, server, port, from, user, passwd, isSSL);
            }
        });
    }

    @Override
    public void setAlarmMoveLevel(final String deviceId, final int level) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.setAlarmMoveLevel(deviceId, level);
            }
        });
    }

    @Override
    public void startRegisterBaiduPush(final String userId, final String channelId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.startRegisterBaiduPush(userId, channelId);
            }
        });
    }

    @Override
    public void startRegisterGetuiPush(final String clientId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.startRegisterGetuiPush(clientId);
            }
        });
    }

    @Override
    public void stopAlarmPush(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.stopAlarmPush(deviceId);
            }
        });
    }

//    @Override
//    public void setAlertPushChannel(final String udId, final int pushId, final String userId, final String channelId) {
//        ErmuApplication.execBackground(new Runnable() {
//            @Override
//            public void run() {
//                mBusiness.setAlertPushChannel(udId, pushId, userId, channelId);
//            }
//        });
//    }

    @Override
    public void setBitLevel(final String deviceId, final int bitLevel) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.setBitLevel(deviceId, bitLevel);
            }
        });
    }

    @Override
    public void getCapsule(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.getCapsule(deviceId);
            }
        });
    }

    @Override
    public CamInfo getCamInfo(String deviceId) {
        return mBusiness.getCamInfo(deviceId);
    }

    @Override
    public CamCron getCamCron(String deviceId) {
        return mBusiness.getCamCron(deviceId);
    }

    @Override
    public CamCron getCvrCron(String deviceId) {
        return mBusiness.getCvrCron(deviceId);
    }

    @Override
    public CamCron getAlarmCron(String deviceId) {
        return mBusiness.getAlarmCron(deviceId);
    }

    @Override
    public CamStatus getCamStatus(String deviceId) {
        return mBusiness.getCamStatus(deviceId);
    }

    @Override
    public Email getCamEmail(String deviceId) {
        return mBusiness.getCamEmail(deviceId);
    }

    @Override
    public CamAlarm getCamAlarm(String deviceID) {
        return mBusiness.getCamAlarm(deviceID);
    }

    @Override
    public CamCvr getCamCvr(String deviceId) {
        return mBusiness.getCamCvr(deviceId);
    }

    @Override
    public List<CamLive> getCamCloud() {
        return mBusiness.getCamCloud();
    }

    @Override
    public void getCamUpdateStatus(final String deviceId) {
        ErmuApplication.execBackground(new Runnable() {
            @Override
            public void run() {
                mBusiness.getCamUpdateStatus(deviceId);
            }
        });
    }

}
