package com.iermu.client.business.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.iermu.client.ErmuApplication;
import com.iermu.client.IPreferenceBusiness;
import com.iermu.client.config.PreferenceConfig;
import com.iermu.client.model.constant.PushType;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户偏好设置(设置中心)
 * Created by zhoushaopei on 15/7/20.
 */
public class PreferenceBusImpl extends BaseBusiness implements IPreferenceBusiness {

    private static final String PREFERENCE_NAME = "iermu_config";
    private SharedPreferences mPreferences;

    public PreferenceBusImpl() {
        mPreferences = ErmuApplication.getContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void setDevEmailSSL(String deviceId, boolean isSSL) {
        setPreference(PreferenceConfig.DEV_EMAIL_SSL + "_" + deviceId, isSSL);
    }

    @Override
    public boolean isDevEmailSSL(String deviceId) {
        return getBoolean(PreferenceConfig.DEV_EMAIL_SSL + "_" + deviceId);
    }

    @Override
    public void setAdvanceConfig(boolean b) {
        setPreference(PreferenceConfig.ADVANCED_CONFIG, b);
    }

    @Override
    public boolean getAdvancedConfig() {
        return getBoolean(PreferenceConfig.ADVANCED_CONFIG);
    }

    public void setSoundStatus(String deviceId, PreferenceConfig.SoundType soundType, boolean isOpen) {
        String key = PreferenceConfig.SOUNG_CONFIG + "_" + soundType.name() + "_" + deviceId;
        setPreference(key, isOpen);
    }

    @Override
    public boolean getSoundStatus(String deviceId, PreferenceConfig.SoundType soundType) {
        String key = PreferenceConfig.SOUNG_CONFIG + "_" + soundType.name() + "_" + deviceId;
        return mPreferences.getBoolean(key, true);
    }

    @Override
    public void setAlertedUpdateVersion(int newVersionCode) {
        setPreference(PreferenceConfig.ALERT_UPDATEVERSION, newVersionCode);
    }

    @Override
    public int getAlertedUpdateVersion() {
        return mPreferences.getInt(PreferenceConfig.ALERT_UPDATEVERSION, 0);
    }

    @Override
    public void setCapsuleDialogShow(String deviceId) {
        setPreference(PreferenceConfig.CAPSULE_DIALOG + deviceId, true);
    }

    @Override
    public boolean isCapsuleDialogShow(String deviceId) {
        return mPreferences.getBoolean(PreferenceConfig.CAPSULE_DIALOG + deviceId, false);
    }

    @Override
    public void setPosterImgUrl(String imgUrl) {
        setPreference(PreferenceConfig.POSTER_IMG_URL, imgUrl);
    }

    @Override
    public String getPosterImgUrl() {
        return mPreferences.getString(PreferenceConfig.POSTER_IMG_URL, "");
    }

    @Override
    public void setPosterWebUrl(String webUrl) {
        setPreference(PreferenceConfig.POSTER_WEB_URL, webUrl);
    }

    @Override
    public String getPosterWebUrl() {
        return mPreferences.getString(PreferenceConfig.POSTER_WEB_URL, "");
    }

    @Override
    public void setPosterStartTime(long startTime) {
        setPreference(PreferenceConfig.POSTER_START_TIME, startTime);
    }

    @Override
    public long getPosterStartTime() {
        return mPreferences.getLong(PreferenceConfig.POSTER_START_TIME, 0);
    }

    @Override
    public void setPosterEndTime(long endTime) {
        setPreference(PreferenceConfig.POSTER_END_TIME, endTime);
    }

    @Override
    public long getPosterEndTime() {
        return mPreferences.getLong(PreferenceConfig.POSTER_END_TIME, 0);
    }

    @Override
    public void setRebBlueLight(boolean isFirst) {
        setPreference(PreferenceConfig.REB_BLUE_LIGHT, isFirst);
    }

    @Override
    public boolean getRebBlueLight() {
        return mPreferences.getBoolean(PreferenceConfig.REB_BLUE_LIGHT, false);
    }

    @Override
    public void setInputWifiPa(boolean isInput) {
        setPreference(PreferenceConfig.INPUT_WIFI_PASSWORD, isInput);
    }

    @Override
    public boolean getInputWifiPa() {
        return mPreferences.getBoolean(PreferenceConfig.INPUT_WIFI_PASSWORD, false);
    }

    @Override
    public void setMyCamCount(int count) {
        setPreference(PreferenceConfig.MY_CAM_COUNT, count);
    }

    @Override
    public int getMyCamCount() {
        return mPreferences.getInt(PreferenceConfig.MY_CAM_COUNT, 0);
    }

    @Override
    public boolean getFilmEditIsShowd() {
        return mPreferences.getBoolean(PreferenceConfig.FILM_EDIT_TIP_SHOW, false);
    }

    @Override
    public void setFilmEditIsShowd(boolean isShowd) {
        setPreference(PreferenceConfig.FILM_EDIT_TIP_SHOW, isShowd);
    }

    @Override
    public void setFilmEditTime(long startTime, long endTime) {
        setPreference(PreferenceConfig.FILM_EDIT_START_TIME, startTime);
        setPreference(PreferenceConfig.FILM_EDIT_END_TIME, endTime);
    }

    @Override
    public long getFilmEditStartTime() {
        return mPreferences.getLong(PreferenceConfig.FILM_EDIT_START_TIME, 0);
    }

    @Override
    public long getFilmEditEndTime() {
        return mPreferences.getLong(PreferenceConfig.FILM_EDIT_END_TIME, 0);
    }

    @Override
    public void setFilmEditIsFaild(boolean isFaild) {
        setPreference(PreferenceConfig.FILM_EDIT_IS_FAILD, isFaild);
    }

    @Override
    public boolean getFilmEditIsFaild() {
        return mPreferences.getBoolean(PreferenceConfig.FILM_EDIT_IS_FAILD, false);
    }

    @Override
    public void setIpMode(String ipModeJson) {
        setPreference(PreferenceConfig.SETUP_IP_MODE, ipModeJson);
    }

    @Override
    public String getIpMode() {
        return mPreferences.getString(PreferenceConfig.SETUP_IP_MODE, "");
    }

    public boolean isRegistedPush(String uid, int pushType) {
        return mPreferences.getBoolean(PreferenceConfig.REGISTED_PUSH+"_"+uid+"_"+pushType, false);
    }

    @Override
    public void setRegisterPush(String uid, int pushType, boolean registed) {
        setPreference(PreferenceConfig.REGISTED_PUSH+"_"+uid+"_"+pushType, registed);
    }

    @Override
    public Map<String, String> getPushConfig(int pushType) {
        Map<String, String> map = new HashMap<String, String>();
        if(pushType == PushType.GETUI) {
            String clientId = getString(PreferenceConfig.GETUI_PUSHCONF+"_clientId");
            map.put("clientId", clientId);
        } else {
            String userId = getString(PreferenceConfig.BAIDU_PUSHCONF+"_userId");
            String channelId = getString(PreferenceConfig.BAIDU_PUSHCONF+"_channelId");
            map.put("userId", userId);
            map.put("channelId", channelId);
        }
        return map;
    }

    public boolean existPushConfig(int pushType) {
        if(pushType == PushType.GETUI) {
            String clientId = getString(PreferenceConfig.GETUI_PUSHCONF+"_clientId");
            return !TextUtils.isEmpty(clientId);
        } else {
            String userId = getString(PreferenceConfig.BAIDU_PUSHCONF+"_userId");
            String channelId = getString(PreferenceConfig.BAIDU_PUSHCONF+"_channelId");
            return !TextUtils.isEmpty(userId) && !TextUtils.isEmpty(channelId);
        }
    }

    @Override
    public void setBaiduPushConfig(String userId, String channelId) {
        if(TextUtils.isEmpty(userId) || TextUtils.isEmpty(channelId)) {
            return;
        }
        setPreference(PreferenceConfig.BAIDU_PUSHCONF+"_userId", userId);
        setPreference(PreferenceConfig.BAIDU_PUSHCONF+"_channelId", channelId);
    }

    @Override
    public void setGetuiPushConfig(String clientId) {
        if(TextUtils.isEmpty(clientId)) {
            return;
        }
        setPreference(PreferenceConfig.GETUI_PUSHCONF+"_clientId", clientId);
    }

    @Override
    public void setDevAddErrorTimes(int times) {
        setPreference(PreferenceConfig.DEV_ADD_ERROR_TIMES, times);
    }

    @Override
    public int getDevAddErrorTimes() {
        return mPreferences.getInt(PreferenceConfig.DEV_ADD_ERROR_TIMES,0);
    }

    private boolean getBoolean(String key) {
        return mPreferences.getBoolean(key, false);
    }

    @SuppressWarnings("unused")
    private String getString(String key) {
        return mPreferences.getString(key, "");
    }

    private int getInt(String key) {
        return mPreferences.getInt(key, 0);
    }

    //保存配置
    private void setPreference(String key, Object value) {
        if (TextUtils.isEmpty(key) || key == null)
            return;
        SharedPreferences.Editor editor = mPreferences.edit();
        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        }
        editor.commit();
    }
}
