package com.iermu.client.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Vector;
import java.util.regex.Pattern;


/**
 * @author wcy
 */
@SuppressLint("SimpleDateFormat")
public class DateUtil {

    public static final int DAY_SECOND_NUM = 86400;     // 一天的秒数：86400
    public static final int TIME_ZONE_OFFECT = 28800;   //百度\羚羊云时间戳 增加8个小时(历史数据问题)
    public static final int SECOND_NUM_MINITE_15 = 900; // 15分钟的秒数

    // 格式：年－月－日 小时：分钟：秒
    public static final String FORMAT_ONE = "yyyy-MM-dd HH:mm:ss";
    // 格式：年－月－日 小时：分钟
    public static final String FORMAT_TWO = "yyyy-MM-dd HH:mm";
    // 格式：年月日 小时分钟秒
    public static final String FORMAT_THREE = "yyyyMMdd-HHmmss";
    public static final String FORMAT_THREE2 = "yyyy-MM-dd-HH:mm";

    public static final String FORMAT_FOUR = "yyyy年MM月dd日";
    public static final String FORMAT_FOUR2 = "yy年M月d日";
    public static final String FORMAT_FOUR3 = "yy/M/d";
    public static final String FORMAT_FOUR4 = "yyyy年MM月dd日 HH:mm:ss";
    public static final String FORMAT_FOUR5 = "yyyy年MM月dd日 HH点mm分";
    public static final String FORMAT6 = "MM月dd日 HH:mm:ss";
    public static final String FORMAT7 = "yy-MM-dd HH:mm:ss";
    public static final String FORMAT8 = "yyMMdd";
    public static final String FORMAT9 = "yyyy-MM-dd-HH-mm-ss";

    // 格式：年－月－日
    public static final String LONG_DATE_FORMAT = "yyyy-MM-dd";
    public static final String LONG_DATE_FORMAT2 = "yyyy/MM/dd";
    // 格式：月－日
    public static final String SHORT_DATE_FORMAT = "MM-dd";
    // 格式：xx月xx日
    public static final String SHORT_DATE_FORMAT2 = "M月d日";
    public static final String SHORT_DATE_FORMAT3 = "MM月dd日";
    // 格式：小时：分钟：秒
    public static final String LONG_TIME_FORMAT = "HH:mm:ss";
    // 格式：小时：分钟
    public static final String SHORT_TIME_FORMAT = "HH:mm";
    //格式：年-月
    public static final String MONTG_DATE_FORMAT = "yyyy-MM";

    // 年的加减
    public static final int SUB_YEAR = Calendar.YEAR;
    // 月加减
    public static final int SUB_MONTH = Calendar.MONTH;
    // 天的加减
    public static final int SUB_DAY = Calendar.DATE;
    // 小时的加减
    public static final int SUB_HOUR = Calendar.HOUR;
    // 分钟的加减
    public static final int SUB_MINUTE = Calendar.MINUTE;
    // 秒的加减
    public static final int SUB_SECOND = Calendar.SECOND;

    public static final String dayNames[] = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 输出格式 :
     * 推迟到今天
     * 推迟到明天
     * 推迟到后天
     * 推迟到5/18
     * 推迟到2014/5/18
     *
     * @param date
     * @return
     */
    public static String laterDateTx(Date date) {
        if (date == null) {
            return "";
        }
        if (inToday(date)) {
            return "推迟到今天";
        } else if (inTomorrow(date)) {
            return "推迟到明天";
        } else if (inNowYear(date)) {
            return "推迟到" + formatDate(date, SHORT_DATE_FORMAT3);
        } else {
            return "推迟到" + formatDate(date, LONG_DATE_FORMAT2);
        }
    }

    /**
     * 输出格式
     * 逾期 (xx天)
     * 今天截止
     * 明天截止
     * XX天后截止
     * 9/6截止
     * 2014/9/6截止
     *
     * @param date
     * @return
     */
    public static String toEndDateTx(Date date) {
        if (date == null) {
            return "";
        }
        if (inToday(date)) {
            return "今天截止";
        } else if (inTomorrow(date)) {
            return "明天截止";
        } else if (dayDiff2(date, new Date()) > 0) {
            return "逾期";
        } else if (inNowYear(date)) {
            return formatDate(date, SHORT_DATE_FORMAT3) + "截止";
        } else {
            return formatDate(date, LONG_DATE_FORMAT2) + "截止";
        }
    }
//    public static String toEndDateTx(Tasks task, int type){
//    	if(task == null) {
//    		return "";
//    	}
//    	if(inToday(task.getEndDate())) {
//    		return "今天截止";
//    	} else if(inTomorrow(task.getEndDate())) {
//    		return "明天截止";
//    	} else if(dayDiff2(task.getEndDate(), new Date()) > 0) {
//
//    		return "逾期" + BoxUtil.appointDayDiff(task, type) + "天";
//    	} else if(inNowYear(task.getEndDate())){
//    		return formatDate(task.getEndDate(), SHORT_DATE_FORMAT3) + "截止";
//    	} else {
//    		return formatDate(task.getEndDate(), LONG_DATE_FORMAT2) + "截止";
//    	}
//    }
//    public static String toEndDateTx(RoundTxView count, Tasks task, int type) {
//    	if(task == null) {
//    		return "";
//    	}
//    	if(inToday(task.getEndDate())) {
//    		count.setTextColor(count.getContext().getResources().getColor(R.color.white));
//    		count.setRoundBackgroundColor(count.getContext().getResources().getColor(R.color.inbox_item_red_cor));
//    		return "今天截止";
//    	} else if(inTomorrow(task.getEndDate())) {
//    		count.setTextColor(count.getContext().getResources().getColor(R.color.gray));
//    		count.setRoundBackgroundColor(count.getContext().getResources().getColor(R.color.white));
//    		return "明天截止";
//    	} else if(dayDiff2(task.getEndDate(), new Date()) > 0) {
//    		count.setTextColor(count.getContext().getResources().getColor(R.color.white));
//    		count.setRoundBackgroundColor(count.getContext().getResources().getColor(R.color.inbox_item_red_cor));
//    		return "逾期" + BoxUtil.appointDayDiff(task, type) + "天";
//    	} else if(inNowYear(task.getEndDate())){
//    		count.setTextColor(count.getContext().getResources().getColor(R.color.gray));
//    		count.setRoundBackgroundColor(count.getContext().getResources().getColor(R.color.white));
//    		return formatDate(task.getEndDate(), SHORT_DATE_FORMAT3) + "截止";
//    	} else {
//    		count.setTextColor(count.getContext().getResources().getColor(R.color.gray));
//    		count.setRoundBackgroundColor(count.getContext().getResources().getColor(R.color.white));
//    		return formatDate(task.getEndDate(), LONG_DATE_FORMAT2) + "截止";
//    	}
//	}

    public static String toDoneAct(Date date) {
        if (date == null)
            return "";
        if (inToday(date)) {
            return "今天完成";
        } else if (inYesterday(date)) {
            return "昨天完成";
        } else {
            return formatDate(date, FORMAT_FOUR) + "完成";
        }
    }

    /**
     * 输出格式
     * 逾期 (xx天)
     * 当天完成
     * 提前 (xx天)
     *
     * @param date
     * @return
     */
    public static String toMyAppointEndTx2(Date doneDate, Date endDate) {
        if (doneDate == null || endDate == null)
            return "";
        if (dayDiff2(doneDate, endDate) == 0) {
            return "当天完成";
        } else if (dayDiff2(doneDate, endDate) < 0) {
            return "逾期";
        } else {
            return "提前";
        }
    }
    /**
     * 输出格式
     * 		逾期 xx天
     * 		当天完成
     * 		提前 xx天
     * @param task 任务
     * @param type 类型
     * @return
     */
//	public static String toMyAppointEndTx2(Tasks task,int type){
//		if(task.getActDate() == null || task.getEndDate() == null)
//			return "";
//		if(dayDiff2(task.getActDate(), task.getEndDate()) == 0) {
//
//    		return "当天完成";
//    	} else if(dayDiff2(task.getActDate(), task.getEndDate()) < 0) {
//    		return "逾期" + BoxUtil.appointDayDiff(task, type) + "天";
//    	} else {
//    		return "提前" + BoxUtil.appointDayDiff(task, type) + "天";
//    	}
//	}
//	public static String toMyAppointEndTx2(RoundTxView count, Tasks task,
//			int type) {
//		if(task.getActDate() == null || task.getEndDate() == null)
//			return "";
//		if(dayDiff2(task.getActDate(), task.getEndDate()) == 0) {
//			count.setRoundBackgroundColor(count.getContext().getResources().getColor(R.color.done_action));
//			count.setTextColor(count.getContext().getResources().getColor(R.color.white));
//    		return "当天完成";
//    	} else if(dayDiff2(task.getActDate(), task.getEndDate()) < 0) {
//    		count.setRoundBackgroundColor(count.getContext().getResources().getColor(R.color.inbox_item_red_cor));
//			count.setTextColor(count.getContext().getResources().getColor(R.color.white));
//    		return "逾期" + BoxUtil.appointDayDiff(task, type) + "天";
//    	} else {
//    		count.setRoundBackgroundColor(count.getContext().getResources().getColor(R.color.done_action));
//			count.setTextColor(count.getContext().getResources().getColor(R.color.white));
//    		return "提前" + BoxUtil.appointDayDiff(task, type) + "天";
//    	}
//	}

    /**
     * 输出格式:
     * 9:32 	//今天 	--今天
     * 10:40	//昨天 	--昨天
     * 09-06		--今年
     * 12-03-29	--非今年
     *
     * @return
     */
    public static String toDateTx(Date date) {
        if (inToday(date)) {
            return formatDate(date, SHORT_TIME_FORMAT);//"今天 " +
        } else if (inYesterday(date)) {
            return formatDate(date, SHORT_TIME_FORMAT);//"昨天 " +
        } else if (inNowYear(date)) {
            return formatDate(date, SHORT_DATE_FORMAT);
        } else {
            return formatDate(date, LONG_DATE_FORMAT);
        }
    }

    public static String toDateTxs(Date date) {
        return formatDate(date, SHORT_DATE_FORMAT);
    }

    /**
     * 输出格式:
     * 今天 9:32	--今天
     * 昨天 10:40	--昨天
     * 明天 10:40	--明天
     * 9月6日		--今年
     * 12年3月29日	--非今年
     *
     * @param date
     * @return
     */
    public static String toStartDateTx(Date date) {
        if (inToday(date)) {
            return "今天 " + formatDate(date, SHORT_TIME_FORMAT);
        } else if (inYesterday(date)) {
            return "昨天 " + formatDate(date, SHORT_TIME_FORMAT);
        } else if (inTomorrow(date)) {
            return "明天 " + formatDate(date, SHORT_TIME_FORMAT);
        } else if (inNowYear(date)) {
            return formatDate(date, SHORT_DATE_FORMAT2);
        } else {
            return formatDate(date, FORMAT_FOUR2);
        }
    }

    /**
     * 输出格式:
     * 今天			--今天
     * 昨天 		--昨天
     * 明天 		--明天
     * 9月6日		--今年
     * 12年3月29日	--非今年
     *
     * @param date
     * @return
     */
    public static String formatDateTx(Date date) {
        if (inToday(date)) {
            return "今天";
        } else if (inYesterday(date)) {
            return "昨天";
        } else if (inTomorrow(date)) {
            return "明天";
        } else if (inNowYear(date)) {
            return formatDate(date, SHORT_DATE_FORMAT2);
        } else {
            return formatDate(date, FORMAT_FOUR2);
        }
    }


    /**
     * 从当前时间，向后推迟3个小时
     *
     * @return
     */
    public static Date laterThreeHour() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, 3);
        return cal.getTime();
    }

    /**
     * 推迟到今天晚上20：30
     *
     * @return
     */
    public static Date laterTody() {
        Calendar cal = Calendar.getInstance();
        return spliceToDate(cal.getTime(), "20:30:00");
    }

    /**
     * 推迟到明天早上8：00
     *
     * @return
     */
    public static Date laterTomorrow() {
        return spliceToDate(nextDay(new Date(), 1), "8:00:00");
    }

    /**
     * 推迟到本周六早上8：00
     * 周日，则推迟到周日早上8:00
     *
     * @return
     */
    public static Date laterThisWeek() {
        Calendar cal = Calendar.getInstance();
        int week = getWeek(cal.getTime()) - 1;
        int days = 0;
        if (week != 0) {
            days = 7 - week - 1;
        }
        cal.add(Calendar.DAY_OF_MONTH, days);
        return spliceToDate(cal.getTime(), "8:00:00");
    }

    /**
     * 推迟到下周一早上8：00
     *
     * @return
     */
    public static Date laterNextWeek() {
        Calendar cal = Calendar.getInstance();
        int week = getWeek(cal.getTime()) - 1;
        int days = 0;
        if (week == 0) {
            days = 1;
        } else {
            days = 7 - week + 1;
        }
        cal.add(Calendar.DAY_OF_MONTH, days);
        return spliceToDate(cal.getTime(), "8:00:00");
    }

    /**
     * 推迟到这个月最后一天早上8：00
     *
     * @return
     */
    public static Date laterThisMonth() {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int num = monthNumDay(cal);
        cal.add(Calendar.DAY_OF_MONTH, num - day);
        return spliceToDate(cal.getTime(), "8:00:00");
    }

    /**
     * 从今天起推迟第90天早上8：00
     *
     * @return
     */
    public static Date laterThreeMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 2);
        return spliceToDate(cal.getTime(), "8:00:00");
    }

    /**
     * 获得本季度最后一天
     * 当前为本季度最后一天延迟到下一季度最后一天
     *
     * @return
     */
    public static Date lastDayInSeason() {
        int array[][] = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}, {10, 11, 12}};
        Calendar cal = Calendar.getInstance();
        int currDay = cal.get(Calendar.DAY_OF_MONTH);
        int currMonth = cal.get(Calendar.MONTH) + 1;

        int season = 0;
        if (currMonth >= 1 && currMonth <= 3) {
            season = 0;
        } else if (currMonth >= 4 && currMonth <= 6) {
            season = 1;
        } else if (currMonth >= 7 && currMonth <= 9) {
            season = 2;
        } else if (currMonth >= 10 && currMonth <= 12) {
            season = 3;
        }
        int endMonth = array[season][2];

        cal.add(Calendar.MONTH, endMonth - currMonth);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        int endDay = cal.get(Calendar.DAY_OF_MONTH);

        if (currMonth == endMonth
                && currDay == endDay) {
            cal.add(Calendar.MONTH, 3);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
        return spliceToDate(cal.getTime(), "8:00:00");
    }

    /**
     * 给指定Date, 拼接指定时间字符
     *
     * @param date    date
     * @param timeStr "00:00:00"
     * @return 拼接后的Date
     */
    private static Date spliceToDate(Date date, String timeStr) {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_ONE);
        String simpleFormat = formatDate(date, LONG_DATE_FORMAT) + " " + timeStr;
        try {
            date = format.parse(simpleFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static boolean inYesterday(Date date) {
        boolean flag = false;
        Date beforDate = beforDay();
        String nowFormat = formatDate(beforDate, LONG_DATE_FORMAT);
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_ONE);
        String beginFormat = nowFormat + " 00:00:00";
        String endFormat = nowFormat + " 23:59:59";

        try {
            Date begin = format.parse(beginFormat);
            Date end = format.parse(endFormat);
            flag = (date.before(end) && date.after(begin)) || date.compareTo(begin) == 0 || date.compareTo(end) == 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 判断指定日期是否在今天内
     *
     * @param date
     * @return
     */
    public static boolean inToday(Date date) {
        boolean flag = false;
        Date nowDate = new Date();
        String nowFormat = formatDate(nowDate, LONG_DATE_FORMAT);
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_ONE);
        String beginFormat = nowFormat + " 00:00:00";
        String endFormat = nowFormat + " 23:59:59";

        try {
            Date begin = format.parse(beginFormat);
            Date end = format.parse(endFormat);
            flag = (date.before(end) && date.after(begin)) || date.compareTo(begin) == 0 || date.compareTo(end) == 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 判断指定日期是否在明天以内
     *
     * @param date
     * @return
     */
    public static boolean inTomorrow(Date date) {
        boolean flag = false;
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_ONE);
        Date tomDate = nextDay(new Date(), 1);
        try {
            Date begin = format.parse(beginFormat(tomDate));
            Date end = format.parse(endFormat(tomDate));
            flag = (date.before(end) && date.after(begin)) || date.compareTo(begin) == 0 || date.compareTo(end) == 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 判断指定时间是否在本周以内
     *
     * @param date
     * @return
     */
    public static boolean inThisWeek(Date date) {
        Date begin = getThisWeekBeginTime();
        Date end = getThisWeekEndTime();
        return (date.before(end) && date.after(begin)) || date.compareTo(begin) == 0 || date.compareTo(end) == 0;
    }

    /**
     * 判断指定时间是否在下周以内
     *
     * @param date
     * @return
     */
    public static boolean inNextWeek(Date date) {
        Date begin = getNextWeekBeginTime();
        Date end = getNextWeekEndTime();
        return (date.before(end) && date.after(begin)) || date.compareTo(begin) == 0 || date.compareTo(end) == 0;
    }

    /**
     * 判断指定日期是否在当前月份内
     *
     * @param date
     * @return
     */
    public static boolean inThisMonth(Date date) {
        Date begin = thisMonthBeginTime();
        Date end = thisMonthEndTime();
        return (date.before(end) && date.after(begin)) || date.compareTo(begin) == 0 || date.compareTo(end) == 0;
    }

    //public static boolean inThreeMonth(Date date) {
    //	boolean flag = false;
    //	Date tomDate = DateUtil.nextDay(new Date(), 90);
    //	String tomFormat = formatDate(tomDate, LONG_DATE_FORMAT);
    //	SimpleDateFormat format = new SimpleDateFormat(FORMAT_ONE);
    //	String beginFormat = tomFormat + " 00:00:00";
    //	String endFormat = tomFormat + " 23:59:59";
    //	try {
    //		Date begin = format.parse(beginFormat);
    //		Date end = format.parse(endFormat);
    //		flag = date.before(end) && date.after(begin);
    //	} catch (ParseException e) {
    //		e.printStackTrace();
    //	}
    //	return flag;
    //}

    /**
     * 判断指定时间是否在今年
     *
     * @param date
     * @return
     */
    public static boolean inNowYear(Date date) {
        int now = getToYear();
        int year = getYear(date);
        return (now == year);
    }

    /**
     * 获取本周开始时间
     *
     * @return
     */
    public static Date getThisWeekBeginTime() {
        Date beginTime = null;
        Calendar cal = Calendar.getInstance();
        int week = getWeek(cal.getTime());
        week = week - 1;
        int days = 0;
        if (week == 0) {
            days = 6;
        } else {
            days = week - 1;
        }
        cal.add(Calendar.DAY_OF_MONTH, -days);
        beginTime = beginFormatDate(cal.getTime());
        return beginTime;
    }

    /**
     * 获取本周结束时间
     *
     * @return
     */
    public static Date getThisWeekEndTime() {
        Date endTime = null;
        Calendar cal = Calendar.getInstance();
        int week = getWeek(cal.getTime());
        week = week - 1;
        int days = 0;
        if (week != 0) {
            days = 7 - week;
        }
        cal.add(Calendar.DAY_OF_MONTH, days);
        endTime = endFormatDate(cal.getTime());
        return endTime;
    }

    /**
     * 获取下周的开始时间
     *
     * @return
     */
    public static Date getNextWeekBeginTime() {
        Date beginTime = null;
        Calendar cal = Calendar.getInstance();
        int week = getWeek(cal.getTime());
        week = week - 1;
        int days = 0;
        if (week == 0) {
            days = 1;
        } else {
            days = 7 - week + 1;
        }
        cal.add(Calendar.DAY_OF_MONTH, days);
        beginTime = beginFormatDate(cal.getTime());
        return beginTime;
    }

    /**
     * 获取下周的结束时间
     *
     * @return
     */
    public static Date getNextWeekEndTime() {
        Date endTime = null;
        Calendar cal = Calendar.getInstance();
        int week = getWeek(cal.getTime());
        week = week - 1;
        int days = 0;
        if (week == 0) {
            days = 7;
        } else {
            days = 7 - week + 7;
        }
        cal.add(Calendar.DAY_OF_MONTH, days);
        endTime = endFormatDate(cal.getTime());
        return endTime;
    }

    /**
     * 获取指定时间,对应为一周的第几天
     *
     * @param date 指定的时间
     * @return 1-7
     */
    public static int getWeek(Date date) {
        int week = 0;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        week = cal.get(Calendar.DAY_OF_WEEK);
        return week;
    }

    /**
     * 获取当前月份开始时间
     *
     * @return
     */
    private static Date thisMonthBeginTime() {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        cal.add(Calendar.DAY_OF_MONTH, -day + 1);
        return beginFormatDate(cal.getTime());
    }

    /**
     * 获取当前月份结束时间
     *
     * @return
     */
    private static Date thisMonthEndTime() {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int num = thisMonthNumDay();
        cal.add(Calendar.DAY_OF_MONTH, num - day);
        return endFormatDate(cal.getTime());
    }

    private static int[] monDays = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    /**
     * 获取当前月份总天数
     *
     * @return
     */
    private static int thisMonthNumDay() {
        Calendar calendar = Calendar.getInstance();
        return monthNumDay(calendar);
    }

    /**
     * 获取指定时间的月份总天数
     *
     * @param calendar
     * @return
     */
    private static int monthNumDay(Calendar calendar) {
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        int num = 0;
        if (2 == month) {
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
                num = monDays[1]++;
            else
                num = monDays[1];
        } else {
            num = monDays[month - 1];
        }
        return num;
    }

    /**
     * 返回指定日期的开始时间
     *
     * @param date
     * @return
     */
    public static Date beginFormatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_ONE);
        try {
            date = format.parse(formatDate(date, LONG_DATE_FORMAT) + " 00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date moringTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_ONE);
        try {
            date = format.parse(formatDate(date, LONG_DATE_FORMAT) + " 08:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date nightTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_ONE);
        try {
            date = format.parse(formatDate(date, LONG_DATE_FORMAT) + " 20:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date endTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_ONE, Locale.CHINA);
        try {
            date = format.parse(formatDate(date, LONG_DATE_FORMAT) + " 24:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 返回指定日期的结束时间
     *
     * @param date
     * @return
     */
    public static Date endFormatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_ONE);
        try {
            date = format.parse(formatDate(date, LONG_DATE_FORMAT) + " 23:59:59");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * @param date
     * @return
     */
    public static Date targetFormatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(FORMAT_ONE);
        try {
            date = format.parse(formatDate(date, LONG_DATE_FORMAT) + " 08:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 返回指定时间的开始时间格式
     *
     * @param date
     * @return
     */
    private static String beginFormat(Date date) {
        return formatDate(date, LONG_DATE_FORMAT) + " 00:00:00";
    }

    /**
     * 返回指定时间的结束时间格式
     *
     * @param date
     * @return
     */
    private static String endFormat(Date date) {
        return formatDate(date, LONG_DATE_FORMAT) + " 23:59:59";
    }

    /**
     * 返回指定时间的日期时间格式
     *
     * @param date
     * @param format
     * @return
     */
    public static String formatDate(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 把符合日期格式的字符串转换为日期类型
     *
     * @param dateStr
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static Date stringtoDate(String dateStr, String format) {
        Date d = null;
        SimpleDateFormat formater = new SimpleDateFormat(format);
        try {
            formater.setLenient(false);
            d = formater.parse(dateStr);
        } catch (Exception e) {
            // log.error(e);
            d = null;
        }
        return d;
    }

    /**
     * 把符合日期格式的字符串转换为日期类型
     */
    @SuppressLint("SimpleDateFormat")
    public static Date stringtoDate(String dateStr, String format,
                                    ParsePosition pos) {
        Date d = null;
        SimpleDateFormat formater = new SimpleDateFormat(format);
        try {
            formater.setLenient(false);
            d = formater.parse(dateStr, pos);
        } catch (Exception e) {
            d = null;
        }
        return d;
    }

    /**
     * 把日期转换为字符串
     *
     * @param date
     * @return
     */
    public static String dateToString(Date date, String format) {
        String result = "";
        SimpleDateFormat formater = new SimpleDateFormat(format);
        try {
            result = formater.format(date);
        } catch (Exception e) {
            // log.error(e);
        }
        return result;
    }

    /**
     * 获取当前时间的指定格式
     *
     * @param format
     * @return
     */
    public static String getCurrDate(String format) {
        return dateToString(new Date(), format);
    }

    /**
     * @param dateStr
     * @param amount
     * @return
     */
    public static String dateSub(int dateKind, String dateStr, int amount) {
        Date date = stringtoDate(dateStr, FORMAT_ONE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(dateKind, amount);
        return dateToString(calendar.getTime(), FORMAT_ONE);
    }

    /**
     * 两个日期相减
     *
     * @param firstTime
     * @param secTime
     * @return 相减得到的秒数
     */
    public static long timeSub(String firstTime, String secTime) {
        long first = stringtoDate(firstTime, FORMAT_ONE).getTime();
        long second = stringtoDate(secTime, FORMAT_ONE).getTime();
        return (second - first) / 1000;
    }

    /**
     * 获得某月的天数
     *
     * @param year  int
     * @param month int
     * @return int
     */
    public static int getDaysOfMonth(String year, String month) {
        int days = 0;
        if (month.equals("1") || month.equals("3") || month.equals("5")
                || month.equals("7") || month.equals("8") || month.equals("10")
                || month.equals("12")) {
            days = 31;
        } else if (month.equals("4") || month.equals("6") || month.equals("9")
                || month.equals("11")) {
            days = 30;
        } else {
            if ((Integer.parseInt(year) % 4 == 0 && Integer.parseInt(year) % 100 != 0)
                    || Integer.parseInt(year) % 400 == 0) {
                days = 29;
            } else {
                days = 28;
            }
        }

        return days;
    }

    /**
     * 获取某年某月的天数
     *
     * @param year  int
     * @param month int 月份[1-12]
     * @return int
     */
    public static int getDaysOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获得当前日期
     *
     * @return int
     */
    public static int getToday() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DATE);
    }

    /**
     * 获得当前月份
     *
     * @return int
     */
    public static int getToMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获得当前年份
     *
     * @return int
     */
    public static int getToYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 返回日期的天
     *
     * @param date Date
     * @return int
     */
    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DATE);
    }

    /**
     * 返回日期的年
     *
     * @param date Date
     * @return int
     */
    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 返回日期的月份，1-12
     *
     * @param date Date
     * @return int
     */
    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 计算两个日期相差的天数，如果date2 > date1 返回正数，否则返回负数
     *
     * @param date1 Date
     * @param date2 Date
     * @return long
     */
    public static long dayDiff(Date date1, Date date2) {
        return (date2.getTime() - date1.getTime()) / 86400000;
    }

    /**
     * 计算两个日期相差的天数 (不精确的时间计算)
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long dayDiff2(Date date1, Date date2) {
        date1 = beginFormatDate(date1);
        date2 = endFormatDate(date2);
        return (long) Math.floor((double) (date2.getTime() - date1.getTime()) / 86400000);
//    	return (date2.getTime() - date1.getTime()) / 86400000;
    }

    /**
     * 比较两个日期相差的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int daysBetween(Date date1, Date date2) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        long time1 = cal.getTimeInMillis();
        cal.setTime(date2);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        new GregorianCalendar();

        return Integer.parseInt(String.valueOf(between_days));
    }

    public static int diffdates(Date date1, Date date2) {
        int result = 0;
        GregorianCalendar gc1 = new GregorianCalendar();
        GregorianCalendar gc2 = new GregorianCalendar();
        gc1.setTime(date1);
        gc2.setTime(date2);
        result = getDays(gc1, gc2);
        return result;
    }

    public static int getDays(GregorianCalendar g1, GregorianCalendar g2) {
        int elapsed = 0;
        GregorianCalendar gc1, gc2;
        if (g2.after(g1)) {
            gc2 = (GregorianCalendar) g2.clone();
            gc1 = (GregorianCalendar) g1.clone();
        } else {
            gc2 = (GregorianCalendar) g1.clone();
            gc1 = (GregorianCalendar) g2.clone();
        }
        gc1.clear(Calendar.MILLISECOND);
        gc1.clear(Calendar.SECOND);
        gc1.clear(Calendar.MINUTE);
        gc1.clear(Calendar.HOUR_OF_DAY);
        gc2.clear(Calendar.MILLISECOND);
        gc2.clear(Calendar.SECOND);
        gc2.clear(Calendar.MINUTE);
        gc2.clear(Calendar.HOUR_OF_DAY);
        while (gc1.before(gc2)) {
            gc1.add(Calendar.DATE, 1);
            elapsed++;
        }
        return elapsed;
    }

    /**
     * 比较两个日期的年差
     *
     * @param after
     * @return
     */
    public static int yearDiff(String before, String after) {
        Date beforeDay = stringtoDate(before, LONG_DATE_FORMAT);
        Date afterDay = stringtoDate(after, LONG_DATE_FORMAT);
        return getYear(afterDay) - getYear(beforeDay);
    }

    /**
     * 比较指定日期与当前日期的差
     *
     * @param after
     * @return
     */
    public static int yearDiffCurr(String after) {
        Date beforeDay = new Date();
        Date afterDay = stringtoDate(after, LONG_DATE_FORMAT);
        return getYear(beforeDay) - getYear(afterDay);
    }

    /**
     * 比较指定日期与当前日期的差
     *
     * @param before
     * @return
     * @author chenyz
     */
    public static long dayDiffCurr(String before) {
        Date currDate = DateUtil.stringtoDate(currDay(), LONG_DATE_FORMAT);
        Date beforeDate = stringtoDate(before, LONG_DATE_FORMAT);
        return (currDate.getTime() - beforeDate.getTime()) / 86400000;

    }

    /**
     * 获取每月的第一周
     *
     * @param year
     * @param month
     * @return
     * @author chenyz
     */
    public static int getFirstWeekdayOfMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.SATURDAY); // 星期天为第一天
        c.set(year, month - 1, 1);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取每月的最后一周
     *
     * @param year
     * @param month
     * @return
     * @author chenyz
     */
    public static int getLastWeekdayOfMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.SATURDAY); // 星期天为第一天
        c.set(year, month - 1, getDaysOfMonth(year, month));
        return c.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获得当前日期字符串，格式"yyyy_MM_dd_HH_mm_ss"
     *
     * @return
     */
    public static String getCurrent() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        StringBuffer sb = new StringBuffer();
        sb.append(year).append("_").append(addzero(month, 2))
                .append("_").append(addzero(day, 2)).append("_")
                .append(addzero(hour, 2)).append("_").append(
                addzero(minute, 2)).append("_").append(
                addzero(second, 2));
        return sb.toString();
    }

    /**
     * 获得当前日期字符串，格式"yyyy-MM-dd HH:mm:ss"
     *
     * @return
     */
    public static String getNow() {
        Calendar today = Calendar.getInstance();
        return dateToString(today.getTime(), FORMAT_ONE);
    }

    /**
     * 判断日期是否有效,包括闰年的情况
     *
     * @param date YYYY-mm-dd
     * @return
     */
    public static boolean isDate(String date) {
        StringBuffer reg = new StringBuffer(
                "^((\\d{2}(([02468][048])|([13579][26]))-?((((0?");
        reg.append("[13578])|(1[02]))-?((0?[1-9])|([1-2][0-9])|(3[01])))");
        reg.append("|(((0?[469])|(11))-?((0?[1-9])|([1-2][0-9])|(30)))|");
        reg.append("(0?2-?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][12");
        reg.append("35679])|([13579][01345789]))-?((((0?[13578])|(1[02]))");
        reg.append("-?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))");
        reg.append("-?((0?[1-9])|([1-2][0-9])|(30)))|(0?2-?((0?[");
        reg.append("1-9])|(1[0-9])|(2[0-8]))))))");
        Pattern p = Pattern.compile(reg.toString());
        return p.matcher(date).matches();
    }

    /**
     * 取得指定日期过 months 月后的日期 (当 months 为负数表示指定月之前);
     *
     * @param date  日期 为null时表示当天
     * @param months 相加(相减)的月数
     */
    public static Date nextMonth(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }

    /**
     * 取得指定日期过 day 天后的日期 (当 day 为负数表示指日期之前);
     *
     * @param date  日期 为null时表示当天
     * @param day 相加(相减)的月数
     */
    public static Date nextDay(Date date, int day) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }
        cal.add(Calendar.DAY_OF_YEAR, day);
        return cal.getTime();
    }

    /**
     * 取得距离今天 day 日的日期
     *
     * @param day
     * @param format
     * @return
     * @author chenyz
     */
    public static String nextDay(int day, String format) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, day);
        return dateToString(cal.getTime(), format);
    }

    /**
     * 取得指定日期过 day 周后的日期 (当 day 为负数表示指定月之前)
     *
     * @param date 日期 为null时表示当天
     */
    public static Date nextWeek(Date date, int week) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }
        cal.add(Calendar.WEEK_OF_MONTH, week);
        return cal.getTime();
    }

    /**
     * 获取当前的日期(yyyy-MM-dd)
     */
    public static String currDay() {
        return DateUtil.dateToString(new Date(), DateUtil.LONG_DATE_FORMAT);
    }

    /**
     * 获取当天的日期，不带时间值的
     *
     * @return
     */
    public static Date getDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取今天的日期，时间值为00:00:00
     *
     * @return
     */
    public static Date getBeginToday() {
        return beginFormatDate(new Date());
    }

    /**
     * 获取今天的日期，时间值为23:59:59
     *
     * @return
     */
    public static Date getEndToday() {
        return endFormatDate(new Date());
    }

    /**
     * 获取昨天的日期
     *
     * @return
     */
    public static String befoDay() {
        return befoDay(DateUtil.LONG_DATE_FORMAT);
    }

    /**
     * 根据时间类型获取昨天的日期
     *
     * @param format
     * @return
     * @author chenyz
     */
    public static String befoDay(String format) {
        return DateUtil.dateToString(DateUtil.nextDay(new Date(), -1), format);
    }

    /**
     * 获取昨天的Date
     *
     * @return
     */
    public static Date beforDay() {
        return DateUtil.nextDay(new Date(), -1);
    }

    /**
     * 获取明天的日期
     */
    public static String afterDay() {
        return DateUtil.dateToString(DateUtil.nextDay(new Date(), 1),
                DateUtil.LONG_DATE_FORMAT);
    }

    /**
     * 取得当前时间距离1900/1/1的天数
     *
     * @return
     */
    public static int getDayNum() {
        int daynum = 0;
        GregorianCalendar gd = new GregorianCalendar();
        Date dt = gd.getTime();
        GregorianCalendar gd1 = new GregorianCalendar(1900, 1, 1);
        Date dt1 = gd1.getTime();
        daynum = (int) ((dt.getTime() - dt1.getTime()) / (24 * 60 * 60 * 1000));
        return daynum;
    }

    /**
     * getDayNum的逆方法(用于处理Excel取出的日期格式数据等)
     *
     * @param day
     * @return
     */
    public static Date getDateByNum(int day) {
        GregorianCalendar gd = new GregorianCalendar(1900, 1, 1);
        Date date = gd.getTime();
        date = nextDay(date, day);
        return date;
    }

    /**
     * 针对yyyy-MM-dd HH:mm:ss格式,显示yyyymmdd
     */
    public static String getYmdDateCN(String datestr) {
        if (datestr == null)
            return "";
        if (datestr.length() < 10)
            return "";
        StringBuffer buf = new StringBuffer();
        buf.append(datestr.substring(0, 4)).append(datestr.substring(5, 7))
                .append(datestr.substring(8, 10));
        return buf.toString();
    }

    /**
     * 获取本月第一天
     *
     * @param format
     * @return
     */
    public static String getFirstDayOfMonth(String format) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 1);
        return dateToString(cal.getTime(), format);
    }

    /**
     * 获取本月最后一天
     *
     * @param format
     * @return
     */
    public static String getLastDayOfMonth(String format) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 1);
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DATE, -1);
        return dateToString(cal.getTime(), format);
    }

    /**
     * 将元数据前补零，补后的总长度为指定的长度，以字符串的形式返回
     *
     * @param sourceDate
     * @param formatLength
     * @return 重组后的数据
     */
    public static String addzero(int sourceDate, int formatLength) {
        /*
		 * 0 指前面补充零
		 * formatLength 字符总长度为 formatLength
		 * d 代表为正数
		 */
        String newString = String.format("%0" + formatLength + "d", sourceDate);
        return newString;
    }

    public static boolean isLeapYear(String year) {
        int y = Integer.valueOf(year);
        if ((y % 4 == 0 && y % 100 != 0) || y % 400 == 0) {
            return true;
        }
        return false;
    }

    /**
     * 根据给定的时间差返回一个Date对象，主要用在校准客户端与服务器时间上
     *
     * @param timeDelta
     * @return
     */
    public static Date getCalibrateTime(long timeDelta) {
        Date date = new Date();
        date = new Date(date.getTime() - timeDelta);
        return date;
    }

    /**
     * 获取时：分
     *
     * @param time
     * @return
     */
    public static String getHourAndMin(long time) {
        return formatDate(new Date(time), SHORT_TIME_FORMAT);
    }

    /**
     * 获取时：xx年xx月xx日
     *
     * @param time
     * @return
     */
    public static String getYearMothDay(long time) {//TODO 000
        return formatDate(new Date(time), SHORT_DATE_FORMAT3);
    }

    /**
     * 获取时：yyyy-MM-dd-HH:mm
     *
     * @param time
     * @return
     */
    public static String getPicTime(long time) {
        return formatDate(new Date(time), FORMAT_THREE2);
    }

    /**
     * 获取时：yyyy-MM-dd HH:mm
     *
     * @param time
     * @return
     */
    public static String getTitleTime(long time) {
        return formatDate(new Date(time), FORMAT_TWO);
    }

    /**
     * 返回格式化后的时间
     *
     * @param strTime 时间
     * @return yyyy-MM-dd HH:mm:SS
     */
    public static String formatTimeFile(String strTime) {
        int[] ret = null;
        String[] str = split(strTime, " ");
        if (str.length > 1) {
            String[] strD = split(str[0].trim(), "-");
            String[] strT = split(str[1].trim(), ":");
            if (strD.length == 3 && strT.length == 3) {
                ret = new int[6];
                try {
                    ret[0] = Integer.parseInt(strD[0]);
                    ret[1] = Integer.parseInt(strD[1]);
                    ret[2] = Integer.parseInt(strD[2]);
                    ret[3] = Integer.parseInt(strT[0]);
                    ret[4] = Integer.parseInt(strT[1]);
                    ret[5] = Integer.parseInt(strT[2]);
                } catch (Exception e) {
                    ret = null;
                }
            }
        }

        return String.format("%04d", ret[0])
                + String.format("%02d", ret[1])
                + String.format("%02d", ret[2])
                + String.format("%02d", ret[3])
                + String.format("%02d", ret[4])
                + String.format("%02d", ret[5]);
    }

    /**
     * Split string into multiple strings
     *
     * @param original  : Original string
     * @param separator : Separator string in original string
     * @return Splitted string array
     */
    private static String[] split(String original, String separator) {
        if (original == null) return null;
        Vector<String> nodes = new Vector<String>();
        nodes.removeAllElements();
        // Parse nodes into vector
        int index = original.indexOf(separator);
        while (index >= 0) {
            nodes.addElement(original.substring(0, index));
            original = original.substring(index + separator.length());
            index = original.indexOf(separator);
        }
        // Get the last node
        nodes.addElement(original);

        // Create splitted string array
        String[] result = new String[nodes.size()];
        if (nodes.size() > 0) {
            nodes.copyInto(result);
        }
        return result;
    }
}
