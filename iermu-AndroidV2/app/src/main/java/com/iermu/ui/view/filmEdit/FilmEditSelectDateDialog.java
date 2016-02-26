package com.iermu.ui.view.filmEdit;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.model.CamDate;
import com.iermu.client.util.DateUtil;
import com.iermu.client.util.Logger;
import com.iermu.ui.view.kankan.wheel.widget.OnWheelChangedListener;
import com.iermu.ui.view.kankan.wheel.widget.WheelView;
import com.iermu.ui.view.kankan.wheel.widget.adapters.ListWheelAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 通用弹框提示组件
 * <p/>
 * Created by zhangxq on 15/12/10.
 */
public class FilmEditSelectDateDialog extends Dialog implements OnWheelChangedListener, RadioGroup.OnCheckedChangeListener {
    private Context context;
    private TextView buttonCut;
    private TextView buttonCancel;
    private WheelView datePicker;
    private WheelView hourPicker;
    private WheelView minutePicker;
    private RadioGroup radioGroup;
    private TextView textViewToast;

    private List<String> dates;
    private List<String> hours;
    private List<String> minutes;

    private String selectDateStr;
    private String selectHourStr;
    private String selectMinuteStr;
    private int selectMinute = 10;

    public FilmEditSelectDateDialog(Context context) {
        super(context, R.style.load_dialog);
        this.context = context;
    }

    public void init(List<CamDate> camDateList) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_record_cut_selectdate_dialog);
        this.setCanceledOnTouchOutside(false);
        buttonCut = (TextView) findViewById(R.id.textViewDateCut);
        buttonCancel = (TextView) findViewById(R.id.textViewDateCancel);
        datePicker = (WheelView) findViewById(R.id.datePicker);
        hourPicker = (WheelView) findViewById(R.id.hourPicker);
        minutePicker = (WheelView) findViewById(R.id.minutePicker);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroupRecordCut);
        radioGroup.setOnCheckedChangeListener(this);
        textViewToast = (TextView) findViewById(R.id.textViewToast);

        // 初始化时间数据
        dates = new ArrayList<String>();
        hours = new ArrayList<String>();
        minutes = new ArrayList<String>();
        for (int i = camDateList.size() - 1; i >= 0; i--) {
            CamDate camDate = camDateList.get(i);
            long time = camDate.getDayStartTime() * 1000l;
            String dateStr = DateUtil.formatDate(new Date(time), DateUtil.LONG_DATE_FORMAT) + "  ";

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            switch (day) {
                case 2:
                    dateStr += context.getResources().getString(R.string.clip_mon);
                    break;
                case 3:
                    dateStr += context.getResources().getString(R.string.clip_tue);
                    break;
                case 4:
                    dateStr += context.getResources().getString(R.string.clip_wed);
                    break;
                case 5:
                    dateStr += context.getResources().getString(R.string.clip_thu);
                    break;
                case 6:
                    dateStr += context.getResources().getString(R.string.clip_fri);
                    break;
                case 7:
                    dateStr += context.getResources().getString(R.string.clip_sat);
                    break;
                case 1:
                    dateStr += context.getResources().getString(R.string.clip_sun);
                    break;
            }
            dates.add(dateStr);
        }

        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                hours.add("0" + i);
            } else {
                hours.add(i + "");
            }
        }

        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                minutes.add("0" + i);
            } else {
                minutes.add(i + "");
            }
        }

        datePicker.setViewAdapter(new ListWheelAdapter<String>(context, R.layout.wheel_item_view, R.id.textViewWheel, dates));
        datePicker.addChangingListener(this);
        datePicker.setCurrentItem(0);
        datePicker.setDrawShadows(false);
        datePicker.setVisibleItems(3);

        hourPicker.setViewAdapter(new ListWheelAdapter<String>(context, R.layout.wheel_item_view, R.id.textViewWheel, hours));
        hourPicker.addChangingListener(this);
        hourPicker.setCurrentItem(0);
        hourPicker.setDrawShadows(false);
        hourPicker.setVisibleItems(3);
        hourPicker.setCyclic(true);

        minutePicker.setViewAdapter(new ListWheelAdapter<String>(context, R.layout.wheel_item_view, R.id.textViewWheel, minutes));
        minutePicker.addChangingListener(this);
        minutePicker.setCurrentItem(0);
        minutePicker.setDrawShadows(false);
        minutePicker.setVisibleItems(3);
        minutePicker.setCyclic(true);

        radioGroup.check(R.id.radioButton2);
    }

    /**
     * 设置日期选中
     *
     * @param currentPlayTime
     */
    public void setSelectTime(int currentPlayTime) {
        long time = currentPlayTime * 1000l;
        String dateStr = DateUtil.formatDate(new Date(time), DateUtil.LONG_DATE_FORMAT);
        for (int i = 0; i < dates.size(); i++) {
            if (dateStr.equals(dates.get(i).substring(0, 10))) {
                datePicker.setCurrentItem(i);
                selectDateStr = dates.get(i);
            }
        }

        int hour = Integer.parseInt(DateUtil.formatDate(new Date(time), "HH"));
        int minute = Integer.parseInt(DateUtil.formatDate(new Date(time), "mm"));
        hourPicker.setCurrentItem(hour);
        minutePicker.setCurrentItem(minute);
        selectHourStr = hour + "";
        selectMinuteStr = minute + "";
    }

    /**
     * 设置确定按钮监听
     *
     * @param listener
     */
    public void setButtonEditListener(View.OnClickListener listener) {
        this.buttonCut.setOnClickListener(listener);
    }

    /**
     * 设置取消按钮监听
     *
     * @param listener
     */
    public void setButtonCancelListener(View.OnClickListener listener) {
        this.buttonCancel.setOnClickListener(listener);
    }

    /**
     * 获取当前选中时间
     *
     * @return
     */
    public int getCurrentSelectTime() {
        if (selectHourStr == null || selectDateStr == null || selectMinuteStr == null) {
            return 0;
        }

        String selectDate = selectDateStr.substring(0, 10) + " " + selectHourStr + ":" + selectMinuteStr;
        Logger.d("selectDateDialog:" + selectDate);
        int time = (int) (DateUtil.stringtoDate(selectDate, DateUtil.FORMAT_TWO).getTime() / 1000);
        Logger.d("clipStartTime:" + time);
        return time;
    }

    // toast信号量
    private int toastNum;
    /**
     * 显示提示
     */
    public void showToast(Handler handler) {
        textViewToast.setVisibility(View.VISIBLE);
        toastNum++;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toastNum--;
                if (toastNum == 0) {
                    textViewToast.setVisibility(View.GONE);
                }
            }
        }, 2000);
    }

    /**
     * 获取当前选中截取时长
     *
     * @return
     */
    public int getCurrentSelectMinute() {
        return selectMinute;
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        Logger.d("onWheelChanged:" + (wheel.getId() == datePicker.getId()));
        if (wheel.getId() == datePicker.getId()) {
            selectDateStr = dates.get(newValue);
        } else if (wheel.getId() == hourPicker.getId()) {
            selectHourStr = hours.get(newValue);
        } else if (wheel.getId() == minutePicker.getId()) {
            selectMinuteStr = minutes.get(newValue);
        }

        Logger.d("onWheelChanged:" + selectDateStr + " " + selectHourStr + ":" + selectMinuteStr);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.radioButton1) {
            selectMinute = 1;
        } else if (checkedId == R.id.radioButton2) {
            selectMinute = 10;
        } else if (checkedId == R.id.radioButton3) {
            selectMinute = 30;
        }
    }
}
