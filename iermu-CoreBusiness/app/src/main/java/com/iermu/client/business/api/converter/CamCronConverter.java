package com.iermu.client.business.api.converter;

import android.text.TextUtils;

import com.iermu.client.model.CamCron;
import com.iermu.client.model.CronRepeat;
import com.iermu.client.util.DateUtil;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 摄像机定时(摄像机定时、录像定时、报警定时)
 *
 * Created by wcy on 15/7/8.
 */
public class CamCronConverter {

    public static CamCron fromJsonAlarm(JSONObject json) {
        int cron     = json.optInt(AlarmField.ALARM_CRON);
        String start = json.optString(AlarmField.ALARM_START);
        String end   = json.optString(AlarmField.ALARM_END);
        String repeat= json.optString(AlarmField.ALARM_REPEAT);
        Date startTime = stringtoDate(start);
        Date endTime = stringtoDate(end);
        CronRepeat cronRepeat = stringtoCronRepeat(repeat);

        boolean invalid = isInvalid(startTime, endTime);
        if(invalid) {  //判断当前摄像机定时的时间值是否无效, 如果用户有设置过有效值, 则使用用户上次设置的值
            startTime = DateUtil.getBeginToday();
            endTime   = DateUtil.getEndToday();
        }

        CamCron camCron = new CamCron();
        camCron.setCron((cron==1));
        camCron.setStart(startTime);
        camCron.setEnd(endTime);
        camCron.setRepeat(cronRepeat);
        return camCron;
    }

    public static CamCron fromJsonAsPower(JSONObject jsonPower) {
        int cron     = jsonPower.optInt(PowerField.POWER_CRON);
        String start = jsonPower.optString(PowerField.POWER_START);
        String end   = jsonPower.optString(PowerField.POWER_END);
        String repeat= jsonPower.optString(PowerField.POWER_REPEAT);
        Date startTime = stringtoDate(start);
        Date endTime = stringtoDate(end);
        CronRepeat cronRepeat = stringtoCronRepeat(repeat);

        boolean invalid = isInvalid(startTime, endTime);
        if(invalid) {  //判断当前摄像机定时的时间值是否无效, 如果用户有设置过有效值, 则使用用户上次设置的值
            startTime = DateUtil.getBeginToday();
            endTime   = DateUtil.getEndToday();
        }

        CamCron camCron = new CamCron();
        camCron.setCron((cron==1));
        camCron.setStart(startTime);
        camCron.setEnd(endTime);
        camCron.setRepeat(cronRepeat);
        return camCron;
    }

    public static CamCron fromJsonAsCvr(JSONObject jsonCvr) {
        int cron     = jsonCvr.optInt(CvrField.CVR_CRON);
        String start = jsonCvr.optString(CvrField.CVR_START);
        String end   = jsonCvr.optString(CvrField.CVR_END);
        String repeat= jsonCvr.optString(CvrField.CVR_REPEAT);
        Date startTime = stringtoDate(start);
        Date endTime = stringtoDate(end);
        CronRepeat cronRepeat = stringtoCronRepeat(repeat);

        boolean invalid = isInvalid(startTime, endTime);
        if(invalid) {  //判断当前摄像机定时的时间值是否无效, 如果用户有设置过有效值, 则使用用户上次设置的值
            startTime = DateUtil.getBeginToday();
            endTime   = DateUtil.getEndToday();
        }

        CamCron camCron = new CamCron();
        camCron.setCron((cron==1));
        camCron.setStart(startTime);
        camCron.setEnd(endTime);
        camCron.setRepeat(cronRepeat);
        return camCron;
    }

    /**
     * 解析JSON数据
     * @param json
     * @return
     */
    public static CamCron fromJson(JSONObject json) {
        int cron     = json.optInt(CvrField.CVR_CRON);
        String start = json.optString(CvrField.CVR_START);
        String end   = json.optString(CvrField.CVR_END);
        String repeat= json.optString(CvrField.CVR_REPEAT);
        Date startTime = stringtoDate(start);
        Date endTime = stringtoDate(end);
        CronRepeat cronRepeat = stringtoCronRepeat(repeat);

        boolean invalid = isInvalid(startTime, endTime);
        if(invalid) {  //判断当前摄像机定时的时间值是否无效, 如果用户有设置过有效值, 则使用用户上次设置的值
            startTime = DateUtil.getBeginToday();
            endTime   = DateUtil.getEndToday();
        }

        CamCron camCron = new CamCron();
        camCron.setCron((cron==1));
        camCron.setStart(startTime);
        camCron.setEnd(endTime);
        camCron.setRepeat(cronRepeat);
        return camCron;
    }

    /**
     * 转化CronRepeat对象为服务器需要的'1111111'格式
     * @param repeat
     * @return
     */
    public static String fromCronRepeat(CronRepeat repeat) {
        StringBuilder sb = new StringBuilder();
        if(repeat != null) {
            sb.append(repeat.isMonday()     ? 1 : 0)
              .append(repeat.isTuesday()    ? 1 : 0)
              .append(repeat.isWednesday()  ? 1 : 0)
              .append(repeat.isThursday()   ? 1 : 0)
              .append(repeat.isFriday()     ? 1 : 0)
              .append(repeat.isSaturday()   ? 1 : 0)
              .append(repeat.isSunday()     ? 1 : 0);
        } else {
            sb.append("1111111");
        }
        return sb.toString();
    }

    /**
     * 转化时间为 HHmmss 格式
     * @param date
     * @return
     */
    public static String fromCronTime(Date date){
        SimpleDateFormat df = new SimpleDateFormat("HHmm");//TODO 默认忽略秒数'ss'
        return df.format(date) + "00";
    }


    private static CronRepeat stringtoCronRepeat(String repeatStr) {
        CronRepeat repeat = new CronRepeat();
        if(TextUtils.isEmpty(repeatStr)) {
            repeat.setDefault();
        } else {
            for(int i=0; i<repeatStr.length(); i++) {
                char charAt = repeatStr.charAt(i);
                boolean on = (charAt == '1');
                switch(i){
                case 0: repeat.setMonday(on);   break;
                case 1: repeat.setTuesday(on);  break;
                case 2: repeat.setWednesday(on);break;
                case 3: repeat.setThursday(on); break;
                case 4: repeat.setFriday(on);   break;
                case 5: repeat.setSaturday(on); break;
                case 6: repeat.setSunday(on);   break;
                }

            }
        }
        if(repeat.isInValid()) {
            repeat.setDefault();
        }
        return repeat;
    }

    //接收时间格式'000000'(HHmmss), 转化为Date对象
    private static Date stringtoDate(String dateStr) {
        Date d = null;
        SimpleDateFormat formater = new SimpleDateFormat("HHmmss");
        try {
            formater.setLenient(false);
            d = formater.parse(dateStr);
        } catch (Exception e) {
            d = null;
        }
        return d;
    }

    //判断摄像机开关机|云录制定时的无效值
    private static boolean isInvalid(Date begin, Date end) {
        Date beginDate  = DateUtil.getBeginToday();
        if(begin == null || end == null) {
            return true;
        }
        String beginDateStr = fromCronTime(beginDate);
        String beginStr = fromCronTime(begin);
        String endStr   = fromCronTime(end);

        return (beginDateStr.equals(beginStr)) && (beginDateStr.equals(endStr));
    }

    class Field {
        public static final String CRON     = "cron";
        public static final String START    = "start";
        public static final String END      = "end";
        public static final String REPEAT   = "repeat";
    }

    class CvrField {
        public static final String CVR_CRON     = "cvr_cron";
        public static final String CVR_START    = "cvr_start";
        public static final String CVR_END      = "cvr_end";
        public static final String CVR_REPEAT   = "cvr_repeat";
    }

    class PowerField {
        public static final String POWER_CRON   = "power_cron";
        public static final String POWER_START  = "power_start";
        public static final String POWER_END    = "power_end";
        public static final String POWER_REPEAT = "power_repeat";
    }

    class AlarmField {
        public static final String ALARM_CRON   = "alarm_cron";
        public static final String ALARM_START  = "alarm_start";
        public static final String ALARM_END    = "alarm_end";
        public static final String ALARM_REPEAT = "alarm_repeat";
    }

}
