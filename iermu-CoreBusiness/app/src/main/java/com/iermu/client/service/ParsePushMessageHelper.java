package com.iermu.client.service;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.business.dao.AlarmImageDataWrapper;
import com.iermu.client.business.dao.CamSettingDataWrapper;
import com.iermu.client.model.AlarmImageData;
import com.iermu.client.model.CamLive;
import com.iermu.client.util.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 推送消息解析帮助类
 *
 * Created by wcy on 16/1/6.
 */
public class ParsePushMessageHelper {

    /**
     * 解析百度推送消息数据(报警图片、...)
     *
     * 1.解析报警推送消息数据
     * 2.保存报警数据
     * 3.判断当前设备报警开关为开启状态
     * 4.发送广播到UI层
     *
     * @param message 原始消息串
     * @param s1
     */
    public static void handleBaiduPushMessage(Context context, String message, String s1) {
        parsePushMessgae(context, message);
    }

    /**
     * 解析个推推送消息数据(报警图片、...)
     * @param payload   原始消息串
     * @param taskId
     * @param messageId
     */
    public static void handleGetuiPushMessage(Context context, byte[] payload, String appid, String taskId, String messageId) {
        if (payload == null) {
            return;
        }
        String data = new String(payload);
        parsePushMessgae(context, data);
    }

    private static void parsePushMessgae(Context context, String message) {
        AlarmImageData data = parseJson(message);
        if(data == null) return;

        String title = data.getTitle();
        String description  = data.getDescription();
        String deviceId     = data.getDeviceId();
        String recdatetime  = data.getRecdatetime();
        String alarmtime    = data.getAlarmtime();

        String uid = ErmuBusiness.getAccountAuthBusiness().getUid();
        AlarmImageDataWrapper.save(title, description, deviceId, recdatetime, alarmtime);
        boolean openAlarmPush = CamSettingDataWrapper.isOpenAlarmPush(uid, deviceId);
        if (openAlarmPush) {
            if(ErmuApplication.isRunInBackground(context)) {//通知栏广播
                CamLive live = ErmuBusiness.getMimeCamBusiness().getCamLive(deviceId);
                String contentTitle = (live!=null) ? live.getDescription() : "";
                Intent intent = new Intent();
                intent.setAction(ErmuApplication.INTENT_NOTICATION_PUSH);
                intent.putExtra("deviceId",     deviceId);
                intent.putExtra("title", contentTitle);
                intent.putExtra("description", description);
                context.sendBroadcast(intent);
            }

            //小红点广播
            Intent intent1 = new Intent();
            intent1.setAction(ErmuApplication.INTENT_NEW_ALARM);
            intent1.putExtra("deviceId",     deviceId);
            context.sendBroadcast(intent1);
        }
    }

    /**
     * 解析JSON数据
     * @param str
     * @return
     *
     * {"title":"A","description":"﻿发现画面变化，建议立即查看","notification_builder_id":0
     *  ,"notification_basic_style":7,"open_type":2
     *  ,"custom_content":{"type":3,"value":0,"recflag":1,"natid":"137894428619"
     *                      ,"recdatetime":"2016-1-12 16:7:30","recchan":1,"alarmtime":"2016-1-12 16:7:30"
     *                    }
     * }
     */
    private static AlarmImageData parseJson(String str) {
        if(TextUtils.isEmpty(str)) return null;
        AlarmImageData data = null;
        try {
            JSONObject json = new JSONObject(str);
            data            = new AlarmImageData();
            String title        = json.optString(Field.TITLE);
            String description  = json.optString(Field.DESCRIPTION);
            JSONObject obj      = json.optJSONObject(Field.CUSTOM_CONTENT);
            String natid        = obj.optString(Field.NATID);
            String recdatetime  = DateUtil.formatTimeFile(obj.optString(Field.RECDATETIME));
            String alarmtime    = DateUtil.formatTimeFile(obj.optString(Field.ALARMTIME));

            data.setDeviceId(natid);
            data.setDescription(description);
            data.setTitle(title);
            data.setRecdatetime(recdatetime);
            data.setAlarmtime(alarmtime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    class Field {
        public static final String TITLE            = "title";
        public static final String DESCRIPTION      = "description";
        public static final String CUSTOM_CONTENT   = "custom_content";
        public static final String NATID            = "natid";
        public static final String RECDATETIME      = "recdatetime";
        public static final String ALARMTIME        = "alarmtime";
    }
}
