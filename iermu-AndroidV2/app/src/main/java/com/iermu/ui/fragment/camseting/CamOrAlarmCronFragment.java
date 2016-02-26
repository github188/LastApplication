package com.iermu.ui.fragment.camseting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.listener.OnCamSettingListener;
import com.iermu.client.listener.OnSetCronByTypeListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamCron;
import com.iermu.client.model.CronRepeat;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.CamSettingType;
import com.iermu.client.model.constant.CronType;
import com.iermu.client.util.DateUtil;
import com.iermu.client.util.LanguageUtil;
import com.iermu.client.util.Logger;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.CommonCommitDialog;
import com.iermu.ui.view.SettingButtonLoad;
import com.iermu.ui.view.SwitchButtonNew;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xjy on 15/6/25.
 */
public class CamOrAlarmCronFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener, OnCamSettingListener, OnSetCronByTypeListener, View.OnTouchListener, ChooseTimeFragment.OnChoiceTimeListener, WeekCycleFragment.OnWeekCycleListener {

    @ViewInject(R.id.set_time_quantum)  RelativeLayout setTimeQuantum;
    @ViewInject(R.id.set_repeat)        RelativeLayout setRepeat;
    @ViewInject(R.id.time_txt)          TextView mTime;
    @ViewInject(R.id.power_cron_btn)    SwitchButtonNew powerCronBtn;
    @ViewInject(R.id.time_text)         TextView mSetTimeTxt;
    @ViewInject(R.id.week)              TextView mWeek;
    @ViewInject(R.id.top_text)          TextView mtopText;
    @ViewInject(R.id.switchbutton_loading)  SettingButtonLoad buttonLoad;
    @ViewInject(R.id.view_line)         View mViewLine;
    @ViewInject(R.id.view_line_two)     View mViewLineTwo;

    private boolean cronChecked;//定时开关状态
    private Date startDate;     //开始时间
    private Date endDate;       //结束时间
    private CronRepeat repeat;  //重复周期
    private String deviceId;
    CommonCommitDialog dialog;
    private CronType cronType;
    private static final String INTENT_CRONTYPE = "type";
    private static final String INTENT_DEVICEID = "deviceid";
    private ICamSettingBusiness business;
    private CamCron camCron;
    private static final int WEEK = 1;
    private boolean isSave = false;

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        cronType = (CronType) getArguments().getSerializable(INTENT_CRONTYPE);
        if (cronType == CronType.ALARM) {
            setCommonActionBar(R.string.police_set_time)
                .setCommonFinish(R.string.save)
                .setCommonFinishClick(new OnCommonClickListener() {
                    @Override
                    public void onClick(View v) {
                        cronChecked = !powerCronBtn.isChecked();
                        dialog = new CommonCommitDialog(getActivity());
                        dialog.show();
                        dialog.setStatusText(getString(R.string.dialog_commit));
                        ErmuBusiness.getCamSettingBusiness().setCamCron(deviceId, cronChecked, startDate, endDate, repeat);
                    }
                });
        } else if (cronType == CronType.POWER) {
            setCommonActionBar(R.string.iermu_ontime_on_off)
                .setCommonFinish(R.string.save)
                .setCommonFinishClick(new OnCommonClickListener() {
                    @Override
                    public void onClick(View v) {
                        cronChecked = !powerCronBtn.isChecked();
                        dialog = new CommonCommitDialog(getActivity());
                        dialog.show();
                        dialog.setStatusText(getString(R.string.dialog_commit));
                        ErmuBusiness.getCamSettingBusiness().setCamCron(deviceId, cronChecked, startDate, endDate, repeat);
                    }
                });
        }
    }

    public static Fragment actionInstance(FragmentActivity activity, CronType type, String deviceId) {
        Fragment fragment = new CamOrAlarmCronFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_CRONTYPE, type);
        bundle.putString(INTENT_DEVICEID, deviceId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ontime_on_off, container, false);
        ViewHelper.inject(this, view);
        super.setCommonFinishHided();
        cronType = (CronType) getArguments().getSerializable(INTENT_CRONTYPE);
        deviceId = getArguments().getString(INTENT_DEVICEID);

        business = ErmuBusiness.getCamSettingBusiness();
        business.registerListener(OnCamSettingListener.class, this);
        business.registerListener(OnSetCronByTypeListener.class, this);

        powerCronBtn.setOnCheckedChangeListener(this);
        powerCronBtn.setOnTouchListener(this);
        powerCronBtn.setVisibility(View.INVISIBLE);
        buttonLoad.setVisibility(View.VISIBLE);
        buttonLoad.startAnimation();
        cronType = (CronType) getArguments().getSerializable(INTENT_CRONTYPE);
        if (cronType == CronType.ALARM) {
            business.syncCamAlarm(deviceId);
        } else if (cronType == CronType.POWER) {
            business.syncCamPowerCron(deviceId);
        }
        return view;
    }

    //刷新View
    private void refreshView() {
        if (cronType == CronType.ALARM) {
            camCron = ErmuBusiness.getCamSettingBusiness().getAlarmCron(deviceId);
            mtopText.setText(getString(R.string.set_alarm_time_txt));
            mSetTimeTxt.setText(R.string.set_police_time);
        } else if (cronType == CronType.POWER) {
            camCron = ErmuBusiness.getCamSettingBusiness().getCamCron(deviceId);
            mSetTimeTxt.setText(R.string.police_start_time);
            mtopText.setText(getString(R.string.set_cam_online_time_txt));
        }
        if (camCron == null) {
            startDate = DateUtil.getBeginToday();
            endDate = DateUtil.getEndToday();
            String everyDayTxt = getResources().getString(R.string.every_day);
            mWeek.setText(everyDayTxt);
            repeat = new CronRepeat();
            repeat.setDefault();
            initData(startDate, endDate);
            return;
        }

        Logger.i(" isCron:" + camCron.isCron());
        startDate   = camCron.getStart();
        endDate     = camCron.getEnd();
        repeat      = camCron.getRepeat();
        boolean isChecked = camCron.isCron();
        String startTime = getCurrentTime(startDate);
        String endTime = getCurrentTime(endDate);
        String formText = getString(R.string.form_time) + " ";
        String toText = " " + getString(R.string.to_time) + " ";
        String time = formText + startTime + toText + endTime;
        String week = getWeek(repeat);
        mTime.setText(time);
        if (!TextUtils.isEmpty(week)) {
            mWeek.setText(week);
        } else {
            mWeek.setText(getResources().getString(R.string.every_day));
            repeat.setDefault();
        }
        powerCronBtn.setSwitchOn(isChecked);
        setTimeQuantum.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        setRepeat.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        mViewLine.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        mViewLineTwo.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    }

    private void initData(Date startDate, Date endDate) {
        String startTime = getCurrentTime(startDate);
        String endTime = getCurrentTime(endDate);
        String form = getResources().getString(R.string.form_time) + " ";
        String to = " " + getResources().getString(R.string.to_time) + " ";
        String time = form + startTime + to + endTime;
        mTime.setText(time);
    }

    @OnClick(value = {R.id.set_time_quantum, R.id.set_repeat})
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.set_time_quantum:
            String time = mTime.getText().toString().trim();
            Fragment fragment1 = ChooseTimeFragment.actionInstance(getActivity(), deviceId, time, cronType);
            ((ChooseTimeFragment) fragment1).setChoiceTimeListener(this);
            addToBackStack(fragment1);
            break;
        case R.id.set_repeat:
            Fragment fragment2 = WeekCycleFragment.actionInstance(getActivity(), deviceId, cronType, repeat,WEEK);
            ((WeekCycleFragment)fragment2).setWeekCycleListener(this);
            addToBackStack(fragment2);
            break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        this.cronChecked = isChecked;
        setTimeQuantum.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        setRepeat.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        mViewLine.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        mViewLineTwo.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        //if (cronType == CronType.ALARM) {
        //    ErmuBusiness.getCamSettingBusiness().setAlarmNotice(deviceId, isChecked);
        //} else if (cronType == CronType.POWER) {
        //    ErmuBusiness.getCamSettingBusiness().setCamCron(deviceId, isChecked);
        //}

    }

    private String getWeek(CronRepeat repeat) {
        StringBuilder sb = new StringBuilder();
        boolean workDay = repeat.isMonday() && repeat.isTuesday() && repeat.isWednesday() && repeat.isThursday() && repeat.isFriday();
        boolean noWorkDay = !repeat.isMonday() && !repeat.isTuesday() && !repeat.isWednesday() && !repeat.isThursday() && !repeat.isFriday();
        boolean weekend = repeat.isSaturday() && repeat.isSunday();
        if (workDay && weekend) {
            sb.append(getString(R.string.every_day));
        } else if (workDay && !repeat.isSaturday() && !repeat.isSunday()) {
            sb.append(getString(R.string.work_day));
        }  else if (weekend && noWorkDay) {
            sb.append(getString(R.string.week_end));
        } else {
            sb.append(getActivity().getString(R.string.week_txt_null))
                    .append(repeat.isMonday() ? getString(R.string.clip_mon)+" " : "")
                    .append(repeat.isTuesday() ? getString(R.string.clip_tue)+" " : "")
                    .append(repeat.isWednesday() ? getString(R.string.clip_wed)+" " : "")
                    .append(repeat.isThursday() ? getString(R.string.clip_thu)+" " : "")
                    .append(repeat.isFriday() ? getString(R.string.clip_fri)+" " : "")
                    .append(repeat.isSaturday() ? getString(R.string.clip_sat)+" " : "")
                    .append(repeat.isSunday() ? getString(R.string.clip_sun)+" " : "");
            int length = sb.length();
            if (length > 0) {
                String language = LanguageUtil.getLanguage();
                if (language.equals("zh")) {
                    sb.deleteCharAt((length>=2) ? length-2: length-1);//减去中文末尾的顿号和空格:2个长度; 英文顿号1个长度
                }
            }
        }
        return sb.toString();
    }

    private String getCurrentTime(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String format = sdf.format(data);
        return format;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ICamSettingBusiness business = ErmuBusiness.getCamSettingBusiness();
        business.unRegisterListener(OnCamSettingListener.class, this);
        business.unRegisterListener(OnSetCronByTypeListener.class, this);
    }

    @Override
    public void onCamSetting(CamSettingType type, String devId, Business business) {
        if(!deviceId.equals(devId)) return;
        if (dialog != null) dialog.dismiss();
        buttonLoad.stopAnimation();
        buttonLoad.setInvisible(buttonLoad);
        buttonLoad.setVisible(powerCronBtn);
        if (type == CamSettingType.CAM_CRON || type == CamSettingType.ALARM_CRON) {
            refreshView();
            if (!business.isSuccess()){
                ErmuApplication.toast(getString(R.string.network_error_please_check) +"("+business.getErrorCode()+")");
            }
        }
    }

    @Override
    public void onSetCron(CronType type, Business bus) {
        if (dialog != null) dialog.dismiss();
        switch (bus.getCode()) {
        case BusinessCode.CONNECT_API_FAILED:
        case BusinessCode.SEND_COMMAND_FAILED:
        case BusinessCode.UPDATE_DEVSETTING_FAILED:
            ErmuApplication.toast(getString(R.string.network_error_please_check) +"("+bus.getErrorCode()+")");
            break;
        case BusinessCode.SUCCESS:
            super.setCommonFinishHided();
            break;
        default:
            ErmuApplication.toast(getString(R.string.network_error_please_check) +"("+bus.getErrorCode()+")");
            break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
       super.setCommonFinishShow();
        return false;
    }

    @Override
    public void choiceTime(Date startDate, Date endDate, boolean isSave) {
        this.startDate = startDate;
        this.endDate = endDate;
        String startTime = getCurrentTime(startDate);
        String endTime = getCurrentTime(endDate);
        String formText = getString(R.string.form_time) + " ";
        String toText = " " + getString(R.string.to_time) + " ";
        String time = formText + startTime + toText + endTime;
        mTime.setText(time);
        if (isSave) super.setCommonFinishShow();
    }

    @Override
    public void weekCycle(CronRepeat cronRepeat, boolean isSave) {
        this.isSave = isSave;
        this.repeat = cronRepeat;
        String week = getWeek(repeat);
        if (!TextUtils.isEmpty(week)) mWeek.setText(week);
        if (isSave) super.setCommonFinishShow();
    }
}
