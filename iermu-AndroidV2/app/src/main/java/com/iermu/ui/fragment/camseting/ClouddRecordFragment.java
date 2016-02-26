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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.IMimeCamBusiness;
import com.iermu.client.listener.OnCamSettingListener;
import com.iermu.client.listener.OnSetCronByTypeListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamCron;
import com.iermu.client.model.CamCvr;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.CronRepeat;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.CamSettingType;
import com.iermu.client.model.constant.ConnectType;
import com.iermu.client.model.constant.CronType;
import com.iermu.client.util.DateUtil;
import com.iermu.client.util.LanguageUtil;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.SettingButtonLoad;
import com.iermu.ui.view.CommonCommitDialog;
import com.iermu.ui.view.SwitchButtonNew;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xjy on 15/6/25.
 */
public class ClouddRecordFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener, OnCamSettingListener
                                            , OnSetCronByTypeListener, BaseFragment.OnCommonClickListener, WeekCycleFragment.OnWeekCycleListener, ChooseTimeFragment.OnChoiceTimeListener, View.OnTouchListener {

    @ViewInject(R.id.set_repeat_cloud)  RelativeLayout setRepeat;
    @ViewInject(R.id.set_time_part)     RelativeLayout setTimeQuantum;
    @ViewInject(R.id.startcloud_bt)     SwitchButtonNew startcloudBtn;
    @ViewInject(R.id.cloudtime_btn)     SwitchButtonNew cloudtimeBtn;
    @ViewInject(R.id.time_tt)           TextView mTime;
    @ViewInject(R.id.week_text)         TextView mWeek;
    @ViewInject(R.id.set_time_lay)      LinearLayout mSetTimeLay;
    @ViewInject(R.id.text_cloud)        RelativeLayout mTextCloud;
    @ViewInject(R.id.switchbutton_loading)  SettingButtonLoad buttonLoad;
    @ViewInject(R.id.switchbutton_loading_1)SettingButtonLoad buttonLoad1;
    @ViewInject(R.id.cloud_line)            View mCloudLine;
    @ViewInject(R.id.cloud_line_two)        View mCloudLineTwo;



    CommonCommitDialog dialog;
    private Date startDate;
    private Date endDate;
    private CronRepeat cronRepeat;
    private boolean isSave;
    private boolean cronChecked;
    private boolean cvrChecked;
    private CamCvr camCvr;
    private String deviceId;
    private CronType cronType;
    private static final String INTENT_DEVICEID = "deviceid";
    private static final String INTENT_CRONTYPE = "cron_type";
    private static int connectType = -1;


    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(R.string.iermu_cloud_camera)
        .setCommonFinish(R.string.save)
        .setCommonFinishClick(new OnCommonClickListener() {
            @Override
            public void onClick(View v) {
                cvrChecked  = !startcloudBtn.isChecked();
                cronChecked = !cloudtimeBtn.isChecked();
                dialog = new CommonCommitDialog(getActivity());
                dialog.show();
                dialog.setStatusText(getString(R.string.dialog_commit));
                if (connectType == ConnectType.LINYANG) {
                    ErmuBusiness.getCamSettingBusiness().setCvr(deviceId, cvrChecked);
                } else {
                    ErmuBusiness.getCamSettingBusiness().setCvrCron(deviceId, cvrChecked, cronChecked, startDate, endDate, cronRepeat);
                }
            }
        });
    }

    public static Fragment actionInstance(FragmentActivity activity, String deviceId, CronType cronType) {
        Fragment fragment = new ClouddRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_DEVICEID, deviceId);
        bundle.putSerializable(INTENT_CRONTYPE, cronType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_cloud_record_transcribe, container, false);
        ViewHelper.inject(this, view);

        setCommonFinishHided();

        initView();
        deviceId = getArguments().getString(INTENT_DEVICEID);
        cronType = (CronType) getArguments().getSerializable(INTENT_CRONTYPE);
        IMimeCamBusiness business1 = ErmuBusiness.getMimeCamBusiness();
        CamLive camLive = business1.getCamLive(deviceId);
        connectType = camLive.getConnectType();
        ICamSettingBusiness business = ErmuBusiness.getCamSettingBusiness();
        if (connectType == ConnectType.LINYANG) {
            mSetTimeLay.setVisibility(View.GONE);
            camCvr = business.getCamCvr(deviceId);
            mTextCloud.setVisibility(View.GONE);
            if (camCvr != null) {
                boolean cvr = camCvr.isCvr();
                startcloudBtn.setSwitchOn(cvr);
            }
        } else {
            mSetTimeLay.setVisibility(View.VISIBLE);
        }
        business.registerListener(OnCamSettingListener.class, this);
        business.registerListener(OnSetCronByTypeListener.class, this);
        business.syncCamCvr(deviceId);
        startcloudBtn.setVisibility(View.INVISIBLE);
        cloudtimeBtn.setVisibility(View.INVISIBLE);
        buttonLoad.setVisibility(View.VISIBLE);
        buttonLoad1.setVisibility(View.VISIBLE);
        buttonLoad.startAnimation();
        buttonLoad1.startAnimation();
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ICamSettingBusiness business = ErmuBusiness.getCamSettingBusiness();
        business.unRegisterListener(OnCamSettingListener.class, this);
        business.unRegisterListener(OnSetCronByTypeListener.class, this);
    }

    private void initView() {
        startcloudBtn.setOnCheckedChangeListener(this);
        cloudtimeBtn.setOnCheckedChangeListener(this);
        startcloudBtn.setOnTouchListener(this);
        cloudtimeBtn.setOnTouchListener(this);
    }


    @Override
    public void onCheckedChanged(CompoundButton v, boolean isChecked) {
        switch (v.getId()) {
        case R.id.startcloud_bt:
        this.cvrChecked = isChecked;
        break;
        case R.id.cloudtime_btn:
        this.cronChecked = isChecked;
            setTimeQuantum.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            setRepeat.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            mCloudLine.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            mCloudLineTwo.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        break;
        }
    }

    @OnClick(value = {R.id.set_repeat_cloud, R.id.set_time_part, R.id.set_cloud_state})
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.set_repeat_cloud:
            Fragment fragmentMore = WeekCycleFragment.actionInstance(getActivity(), deviceId, CronType.CVR, cronRepeat, 0);
            ((WeekCycleFragment)fragmentMore).setWeekCycleListener(this);
            addToBackStack(getActivity(), fragmentMore);
            break;
        case R.id.set_time_part:
            String time = mTime.getText().toString().trim();
            Fragment fragmentTimepart = ChooseTimeFragment.actionInstance(getActivity(), deviceId, time, cronType);
            ((ChooseTimeFragment) fragmentTimepart).setChoiceTimeListener(this);
            addToBackStack(getActivity(), fragmentTimepart);
            break;
        case R.id.set_cloud_state:
            Fragment fragment = CloudStatusFragment.actionInstance(getActivity());
            addToBackStack(getActivity(), fragment);
            break;
        }
    }

    private void refreshView() {
        camCvr = ErmuBusiness.getCamSettingBusiness().getCamCvr(deviceId);
        CamCron camCron = (camCvr!=null) ? camCvr.getCron() : null;
        if (camCron == null) {
            startDate = DateUtil.getBeginToday();
            endDate = DateUtil.getEndToday();
            String everyDayTxt = getResources().getString(R.string.every_day);
            mWeek.setText(everyDayTxt);
            initData(startDate, endDate);
            cronRepeat = new CronRepeat();
            cronRepeat.setDefault();
            return;
        }

        startDate = camCron.getStart();
        endDate = camCron.getEnd();
        cronRepeat = camCron.getRepeat();
        String startTime = getCurrentTime(startDate);
        String endTime = getCurrentTime(endDate);
        String form = getActivity().getString(R.string.form_time) + " ";
        String to = " " + getActivity().getString(R.string.to_time) + " ";
        String time = form + startTime + to + endTime;
        boolean isChecked = camCron.isCron();
        boolean isStartCloud = (camCvr != null) ? (camCvr.isCvr()) : false;
        String week = getWeek(cronRepeat);
        mTime.setText(time);
        if (!TextUtils.isEmpty(week)) {
            mWeek.setText(week);
        } else {
            mWeek.setText(getResources().getString(R.string.every_day));
            cronRepeat.setDefault();
        }
        startcloudBtn.setSwitchOn(isStartCloud);
        cloudtimeBtn.setSwitchOn(isChecked);
        setRepeat.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        setTimeQuantum.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        mCloudLine.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        mCloudLineTwo.setVisibility(isChecked ? View.VISIBLE : View.GONE);
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
        String weekendTxt = getResources().getString(R.string.week_end);
        if (workDay && repeat.isSaturday() && repeat.isSunday()) {
            sb.append(getString(R.string.every_day));
        } else if (workDay && !repeat.isSunday() && !repeat.isSaturday()) {
            sb.append(getString(R.string.work_day));
        } else if (weekend && noWorkDay) {
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
                    sb.deleteCharAt((length>=2) ? length-2: length-1);//减去中文末尾的顿号和空格
                }
            }
        }
        return sb.toString();
    }

    @Override
    public void onCamSetting(CamSettingType type, String deviceId, Business bus) {
        if (dialog != null) dialog.dismiss();
        buttonLoad.stopAnimation();
        buttonLoad1.stopAnimation();
        buttonLoad.setVisibility(View.INVISIBLE);
        buttonLoad1.setVisibility(View.INVISIBLE);
        startcloudBtn.setVisibility(View.VISIBLE);
        cloudtimeBtn.setVisibility(View.VISIBLE);
        refreshView();
        if (!bus.isSuccess()) {
            ErmuApplication.toast(getString(R.string.network_error_please_check) +"("+bus.getErrorCode()+")");
        }
    }

    @Override
    public void weekCycle(CronRepeat cronRepeat, boolean isSave) {
        this.isSave = isSave;
        this.cronRepeat = cronRepeat;
        String week = getWeek(cronRepeat);
        if (!TextUtils.isEmpty(week)) mWeek.setText(week);
        if (isSave) setCommonFinishShow();
    }

    @Override
    public void choiceTime(Date startDate, Date endDate, boolean isSave) {
        this.startDate = startDate;
        this.endDate = endDate;
        String startTime = getCurrentTime(startDate);
        String endTime = getCurrentTime(endDate);
        String form = getActivity().getString(R.string.form_time) + " ";
        String to = " " + getActivity().getString(R.string.to_time) + " ";
        String time = form + startTime + to + endTime;
        mTime.setText(time);
        this.isSave = isSave;
        if (isSave) setCommonFinishShow();
    }

    @Override
    public void onSetCron(CronType type, Business bus) {
        if(dialog != null) dialog.dismiss();
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
}
