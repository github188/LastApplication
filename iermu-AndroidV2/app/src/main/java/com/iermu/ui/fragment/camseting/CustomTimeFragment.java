package com.iermu.ui.fragment.camseting;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.model.CamCron;
import com.iermu.client.model.constant.CronType;
import com.iermu.client.util.DateUtil;
import com.iermu.ui.fragment.BaseFragment;

import com.iermu.ui.util.PickerChooseAdapter;
import com.iermu.ui.util.PickerDialogUtil;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xjy on 15/6/25.
 */
public class CustomTimeFragment extends BaseFragment {

    @ViewInject(R.id.set_custom_start)  RelativeLayout setCustomStart;
    @ViewInject(R.id.set_custom_close)  RelativeLayout setCustomClose;
    @ViewInject(R.id.set_start_text)    TextView startDateTime;
    @ViewInject(R.id.set_close_text)    TextView endDateTime;
    @ViewInject(R.id.time_start)        TextView timeStart;
    @ViewInject(R.id.time_close)        TextView timeClose;


    private Date startDate; //开始时间
    private Date endDate;   //结束时间
    private boolean isSave; //是否保存

    private static final String INTENT_CRONTYPE = "cron_type";
    private PickerChooseAdapter chooseAdapter;
    private CronType cronType;
    private String deviceId;
    private CamCron camCron;
    private OnCustomTimeListener listener;
    private static final String INTENT_DEVICEID = "deviceId";

    public static Fragment actionInstance(FragmentActivity activity, CronType cronType, String deviceId) {
        Fragment fragment = new CustomTimeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_CRONTYPE, cronType);
        bundle.putString(INTENT_DEVICEID, deviceId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(R.string.custom_time)
                .setCommonBackClick(new OnCommonClickListener() {
                    @Override
                    public void onClick(View v) {
//                        onSetCamCronTime();
                        popBackStack();
                        if (listener != null) {
                            listener.customTime(startDate, endDate, isSave);
                        }
                    }
                });
    }

    public void setCustomTimeListener(OnCustomTimeListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_custom_time, container, false);
        ViewHelper.inject(this, view);
        Bundle bundle = getArguments();
        deviceId = bundle.getString(INTENT_DEVICEID);
        cronType = (CronType) bundle.getSerializable(INTENT_CRONTYPE);
        ICamSettingBusiness business = ErmuBusiness.getCamSettingBusiness();
        if (cronType == CronType.POWER) {
            camCron = business.getCamCron(deviceId);
        } else if (cronType == CronType.ALARM) {
            camCron = business.getAlarmCron(deviceId);
        } else if (cronType == CronType.CVR) {
            camCron = business.getCvrCron(deviceId);
        }

        if (camCron != null) {
            startDate = camCron.getStart();
            endDate = camCron.getEnd();
        } else {
            startDate = DateUtil.getBeginToday();
            endDate = DateUtil.getEndToday();
        }
        refreshView();
        return view;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_BACK:
            if (listener != null) {
                listener.customTime(startDate, endDate, isSave);
            }
            break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void refreshView() {
        if (cronType == CronType.ALARM) {
            timeStart.setText(R.string.alarm_open_time);
            timeClose.setText(R.string.alarm_close_time);
        } else if (cronType == CronType.CVR) {
            timeStart.setText(R.string.open_time);
            timeClose.setText(R.string.close_time);
        } else if (cronType == CronType.POWER) {
            timeStart.setText(R.string.dev_open_time);
            timeClose.setText(R.string.dev_close_time);
        }
        String start = formatTime(startDate);
        String end = formatTime(endDate);
        startDateTime.setText(start);
        endDateTime.setText(end);
    }

    @OnClick(value = {R.id.set_custom_start, R.id.set_custom_close, R.id.back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_custom_start:
                isSave = true;
                chooseAdapter = new PickerChooseAdapter() {
                    @Override
                    public void onTimeChoose(Date time) {
                        super.onTimeChoose(time);
                        startDate = time;
                        refreshView();
                    }
                };
                initData(startDate);
                break;
            case R.id.set_custom_close:
                isSave = true;
                chooseAdapter = new PickerChooseAdapter() {
                    @Override
                    public void onTimeChoose(Date time) {
                        super.onTimeChoose(time);
                        endDate = time;
                        refreshView();
                    }
                };
                initData(endDate);
                break;
//            case R.id.back:
////                onSetCamCronTime();
//                popBackStack();
//                if (listener != null) {
//                    listener.customTime(startDate, endDate, isSave);
//                }
//                break;
        }
    }

//    private void onSetCamCronTime() {
//        ICamSettingBusiness business = ErmuBusiness.getCamSettingBusiness();
//        if (cronType == CronType.POWER) {
//            business.setCamCronTime(deviceId, startDate, endDate);
//        } else if (cronType == CronType.CVR) {
//            business.setCvrCronTime(deviceId, startDate, endDate);
//        } else if (cronType == CronType.ALARM) {
//            business.setAlarmCronTime(deviceId, startDate, endDate);
//        }
//    }

    private void initData(Date initDate) {
        PickerDialogUtil dialogUtil = PickerDialogUtil.initDatePicker(getActivity())
                .configInitDate(initDate)
                .configListener(chooseAdapter);
        dialogUtil.showTimePicker();
    }

    private String formatTime(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String format = sdf.format(data);
        return format;
    }

    public interface OnCustomTimeListener {
        void customTime(Date startDate, Date endDate, boolean isSave);
    }

}
