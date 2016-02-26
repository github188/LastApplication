package com.iermu.ui.fragment.camseting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.business.dao.CamSettingDataWrapper;
import com.iermu.client.listener.OnCamSettingListener;
import com.iermu.client.listener.OnSetCamAlarmListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamAlarm;
import com.iermu.client.model.CamCron;
import com.iermu.client.model.CamSettingData;
import com.iermu.client.model.CronRepeat;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.CamSettingType;
import com.iermu.client.model.constant.CronType;
import com.iermu.client.util.DateUtil;
import com.iermu.client.util.LanguageUtil;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.CommonCommitDialog;
import com.lingyang.sdk.util.Utils;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xjy on 15/7/27.
 */
public class AlarmTimeFragment extends BaseFragment implements
        ChooseTimeFragment.OnChoiceTimeListener, WeekCycleFragment.OnWeekCycleListener, OnSetCamAlarmListener, OnCamSettingListener {

    @ViewInject(R.id.low_img)
    ImageView mLowImg;
    @ViewInject(R.id.level_img)
    ImageView mLevelImg;
    @ViewInject(R.id.heigh_img)
    ImageView mHeighImg;
    @ViewInject(R.id.time_t)
    TextView mTime;
    @ViewInject(R.id.alarm_on_off)
    Button alarmOnOff;
    @ViewInject(R.id.alarm_week)
    TextView mWeek;

    CommonCommitDialog dialog;
    private Date startDate;
    private Date endDate;
    private CamCron camCron = new CamCron();
    private static int LEVEL = -1;
    private String deviceId;
    private CronType cronType;
    private CamAlarm camAlarm;
    boolean isAlarmOpen;
    ICamSettingBusiness business;
    private CamSettingDataWrapper settingDataWrapper;
    private boolean isSave;
    private CronRepeat repeat = new CronRepeat();

    private static final String INTENT_DEVICEID = "deviceId";
    private static final String INTENT_CRONTYPE = "type";

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(R.string.alarm_time);
    }

    public static Fragment actionInstance(FragmentActivity activity, String deviceId, CronType type) {
        Fragment fragment = new AlarmTimeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_DEVICEID, deviceId);
        bundle.putSerializable(INTENT_CRONTYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_alarmtime, container, false);
        ViewHelper.inject(this, view);

        startDate = DateUtil.getBeginToday();
        endDate = DateUtil.getEndToday();
        deviceId = getArguments().getString(INTENT_DEVICEID);
        cronType = (CronType) getArguments().getSerializable(INTENT_CRONTYPE);
        business = ErmuBusiness.getCamSettingBusiness();
        settingDataWrapper = new CamSettingDataWrapper();
        String uid = ErmuBusiness.getAccountAuthBusiness().getUid();
        camAlarm = business.getCamAlarm(deviceId);
        if (camAlarm != null) {
            setLevel(camAlarm.getMoveLevel());
        } else {
            setLevel(1);
        }

        CamSettingData settingData = settingDataWrapper.getCamSettingData(uid, deviceId);
        if (settingData != null) {
            isAlarmOpen = (settingData.getIsAlarmOpen() == 1);
            if (isAlarmOpen) {
                alarmOnOff.setText(getString(R.string.reset_alarm));
            } else {
                alarmOnOff.setText(getString(R.string.start_alarm));
            }
        }
        business.syncCamAlarm(deviceId);
        business.registerListener(OnSetCamAlarmListener.class, this);
        business.registerListener(OnCamSettingListener.class, this);
        refreshView();
        return view;
    }

    @OnClick(value = {R.id.set_quantum, R.id.set_repeat, R.id.alarm_eamil,
            R.id.level, R.id.heigh_level, R.id.low_level, R.id.alarm_on_off})
    public void onClick(View v) {
        boolean networkAvailable = Utils.isNetworkAvailable(getActivity());
        if (!networkAvailable) {
            ErmuApplication.toast(getResources().getString(R.string.no_net));
            return;
        }
        switch (v.getId()) {
            case R.id.set_quantum:
                String time = mTime.getText().toString().trim();
                Fragment fragmentTime = ChooseTimeFragment.actionInstance(getActivity(), deviceId, time, cronType);
                ((ChooseTimeFragment) fragmentTime).setChoiceTimeListener(this);
                addToBackStack(getActivity(), fragmentTime);
                break;
            case R.id.alarm_on_off:
                dialog = new CommonCommitDialog(getActivity());
                dialog.show();
                String str = getResources().getString(R.string.dialog_commit);
                dialog.setStatusText(str);
                ErmuBusiness.getCamSettingBusiness().setCamAlarm(deviceId, LEVEL, startDate, endDate, repeat);
                break;
            case R.id.set_repeat:
                Fragment fragmentRepeat = WeekCycleFragment.actionInstance(getActivity(), deviceId, CronType.ALARM, repeat, 0);
                ((WeekCycleFragment) fragmentRepeat).setWeekCycleListener(this);
                addToBackStack(getActivity(), fragmentRepeat);
                break;
            case R.id.low_level:
                LEVEL = 0;
                setLevel(LEVEL);
                break;
            case R.id.level:
                LEVEL = 1;
                setLevel(LEVEL);
                break;
            case R.id.heigh_level:
                LEVEL = 2;
                setLevel(LEVEL);
                break;
        }
    }

    private void setLevel(int level) {
        switch (level) {
            case 0:
                mLowImg.setVisibility(View.VISIBLE);
                mLevelImg.setVisibility(View.GONE);
                mHeighImg.setVisibility(View.GONE);
                break;
            case -1:
            case 1:
                LEVEL = 1;
                mLowImg.setVisibility(View.GONE);
                mLevelImg.setVisibility(View.VISIBLE);
                mHeighImg.setVisibility(View.GONE);
                break;
            case 2:
                mLowImg.setVisibility(View.GONE);
                mLevelImg.setVisibility(View.GONE);
                mHeighImg.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ICamSettingBusiness business = ErmuBusiness.getCamSettingBusiness();
        business.unRegisterListener(OnSetCamAlarmListener.class, this);
        business.unRegisterListener(OnCamSettingListener.class, this);
    }

    private void refreshView() {
        CamCron camCron = ErmuBusiness.getCamSettingBusiness().getAlarmCron(deviceId);
        // 复制camcron到当前对象
        if (camCron != null) {
            this.camCron.setStart(camCron.getStart());
            this.camCron.setEnd(camCron.getEnd());
            this.camCron.setCron(camCron.isCron());
            if (camCron.getRepeat() != null) {
                CronRepeat repeat = camCron.getRepeat();
                this.repeat.setMonday(repeat.isMonday());
                this.repeat.setTuesday(repeat.isTuesday());
                this.repeat.setWednesday(repeat.isWednesday());
                this.repeat.setThursday(repeat.isThursday());
                this.repeat.setFriday(repeat.isFriday());
                this.repeat.setSaturday(repeat.isSaturday());
                this.repeat.setSunday(repeat.isSunday());
                this.camCron.setRepeat(this.repeat);

            }
        } else {
            startDate = DateUtil.getBeginToday();
            endDate = DateUtil.getEndToday();
            String everyDayTxt = getResources().getString(R.string.every_day);
            mWeek.setText(everyDayTxt);
            initData(startDate, endDate);
            repeat = new CronRepeat();
            repeat.setDefault();
            return;
        }

        startDate = this.camCron.getStart();
        endDate = this.camCron.getEnd();
        repeat = this.camCron.getRepeat();
        String week = getWeek(repeat);
        if (!TextUtils.isEmpty(week)) {
            mWeek.setText(week);
        } else {
            mWeek.setText(getResources().getString(R.string.every_day));
            repeat.setDefault();
        }
        initData(startDate, endDate);
    }

    private void initData(Date startDate, Date endDate) {
        String startTime = getCurrentTime(startDate);
        String endTime = getCurrentTime(endDate);
        String form = getResources().getString(R.string.form_time) + " ";
        String to = " " + getResources().getString(R.string.to_time) + " ";
        String time = form + startTime + to + endTime;
        mTime.setText(time);
    }

    private String getCurrentTime(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String format = sdf.format(data);
        return format;
    }


    private String getWeek(CronRepeat repeat) {
        StringBuilder sb = new StringBuilder();
        boolean workDay = repeat.isMonday() && repeat.isTuesday() && repeat.isWednesday() && repeat.isThursday() && repeat.isFriday();
        boolean noWorkDay = !repeat.isMonday() && !repeat.isTuesday() && !repeat.isWednesday() && !repeat.isThursday() && !repeat.isFriday();
        boolean weekend = repeat.isSaturday() && repeat.isSunday();
        String everyDayTxt = getResources().getString(R.string.every_day);
        String workDayTxt = getResources().getString(R.string.work_day);
        String weekendTxt = getResources().getString(R.string.week_end);
        if (workDay && weekend) {
            sb.append(everyDayTxt);
        } else if (workDay && !repeat.isSaturday() && !repeat.isSunday()) {
            sb.append(workDayTxt);
        } else if (weekend && noWorkDay) {
            sb.append(weekendTxt);
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
                    sb.deleteCharAt((length>=2) ? length-2: length-1);//减去中文末尾的顿号和空格
                }
            }
        }
        return sb.toString();
    }

    @Override
    public void weekCycle(CronRepeat cronRepeat, boolean isSave) {
        repeat = cronRepeat;
        String week = getWeek(repeat);
        if (!TextUtils.isEmpty(week)) mWeek.setText(week);
    }

    @Override
    public void choiceTime(Date startDate, Date endDate, boolean isSave) {
        this.startDate = startDate;
        this.endDate = endDate;
        String startTime = getCurrentTime(startDate);
        String endTime = getCurrentTime(endDate);
        String form = getResources().getString(R.string.form_time) + " ";
        String to = " " + getResources().getString(R.string.to_time) + " ";
        String time = form + startTime + to + endTime;
        mTime.setText(time);
    }

    @Override
    public void onSetCamAlarm(String devId, Business bus) {
        if (!deviceId.equals(devId)) return;
        if (dialog != null) dialog.dismiss();
        switch (bus.getCode()) {
            case BusinessCode.PUSH_FAILED:
                ErmuApplication.toast(getString(R.string.bind_baidu_push_fail));
                ErmuBusiness.getStatisticsBusiness().statPushFail(deviceId);
                break;
            case BusinessCode.REGISTE_FILED:
                ErmuApplication.toast(getString(R.string.open_push_fail) + "(" + bus.getErrorMsg() + ")");//推送通道失败
                break;
            case BusinessCode.SUCCESS:
                popBackStack();
                break;
            default:
                ErmuApplication.toast(getString(R.string.network_error_please_check) + "(" + bus.getErrorCode() + ")");
                break;
        }
    }

    @Override
    public void onCamSetting(CamSettingType type, String devId, Business business) {
        if (!deviceId.equals(devId)) return;
        if (type == CamSettingType.ALARM) {
            refreshView();
            if (!business.isSuccess()) {
                ErmuApplication.toast(getString(R.string.network_error_please_check) + "(" + business.getErrorCode() + ")");
            }
        }
    }
}
