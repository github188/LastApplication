package com.iermu.client.business.api.converter;

import com.iermu.client.model.CamAlarm;
import com.iermu.client.model.CamCron;

import org.json.JSONObject;

/**
 * 摄像机报警信息数据转换器
 *
 * Created by zhoushaopei on 15/7/8.
 */
public class CamAlarmConverter {

    /**
     * 解析JSON数据
     * @param json
     * @return
     */
    public static CamAlarm fromJson(JSONObject json) {
        CamAlarm alarm = new CamAlarm();
        int notice      = json.optInt(Field.ALARM_NOTICE);
        int mail        = json.optInt(Field.ALARM_MAIL);
        int audio       = json.optInt(Field.ALARM_AUDIO);
        int move        = json.optInt(Field.ALARM_MOVE);
        int audioLevel  = json.optInt(Field.ALARM_AUDIO_LEVEL);
        int moveLevel   = json.optInt(Field.ALARM_MOVE_LEVEL);

        CamCron camCron = CamCronConverter.fromJsonAlarm(json);
        alarm.setNotice(notice == 1 ? true : false);
        alarm.setMail(mail == 1 ? true : false);
        alarm.setAudio(audio == 1 ? true : false);
        alarm.setMove(move == 1 ? true : false);
        alarm.setAudioLevel(audioLevel);
        alarm.setMoveLevel(moveLevel);
        alarm.setAlarmCron(camCron);
        return alarm;
    }

    class Field {
        public static final String ALARM_NOTICE     = "alarm_push";
        public static final String ALARM_MAIL       = "alarm_mail";
        public static final String ALARM_AUDIO      = "alarm_audio";
        public static final String ALARM_AUDIO_LEVEL= "alarm_audio_level";
        public static final String ALARM_MOVE       = "alarm_move";
        public static final String ALARM_MOVE_LEVEL = "alarm_move_level";
    }
}
