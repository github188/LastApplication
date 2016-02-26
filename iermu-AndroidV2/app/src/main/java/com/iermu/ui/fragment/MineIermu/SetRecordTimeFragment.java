package com.iermu.ui.fragment.MineIermu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.listener.OnSetCronByTypeListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamCron;
import com.iermu.client.model.CamCvr;
import com.iermu.client.model.CronRepeat;
import com.iermu.client.model.constant.CronType;
import com.iermu.client.util.DateUtil;
import com.iermu.client.util.LanguageUtil;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.fragment.camseting.ChooseTimeFragment;
import com.iermu.ui.fragment.camseting.WeekCycleFragment;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.CommonCommitDialog;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import org.apache.http.util.LangUtils;

import java.util.Date;

/**
 * Created by zhangxq on 16/1/21.
 */
public class SetRecordTimeFragment extends BaseFragment implements View.OnClickListener,
        OnSetCronByTypeListener, ChooseTimeFragment.OnChoiceTimeListener, WeekCycleFragment.OnWeekCycleListener {
    private static final String KEY_DEVICEID = "deviceId";

    @ViewInject(R.id.textViewTime)
    TextView textViewTime;
    @ViewInject(R.id.textViewRepeat)
    TextView textViewRepeat;

    private ICamSettingBusiness settingBusiness;
    private String deviceId;
    private CommonCommitDialog commitDialog;
    private CamCron camCron = new CamCron();
    private CronRepeat repeat = new CronRepeat();

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(R.string.record_set_time);
        setCommonFinish(R.string.save);
        setCommonFinishShow();
        setCommonFinishEnbled();
        setCommonFinishClick(new OnCommonClickListener() {
            @Override
            public void onClick(View v) {
                CamCvr camCvr = settingBusiness.getCamCvr(deviceId);
                if (camCvr == null) {
                    camCvr = new CamCvr();
                }
                settingBusiness.setCvrCron(deviceId, camCvr.isCvr(), camCron.isCron(), camCron.getStart(), camCron.getEnd(), camCron.getRepeat());
                commitDialog = new CommonCommitDialog(getActivity());
                commitDialog.show();
                commitDialog.setStatusText(getString(R.string.dialog_commit));
            }
        });
    }

    /**
     * 打开录像信息页面
     *
     * @param deviceId
     * @return
     */
    public static Fragment actionInstance(String deviceId) {
        SetRecordTimeFragment fragment = new SetRecordTimeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DEVICEID, deviceId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_set_record_time, null);
        ViewHelper.inject(this, view);

        deviceId = (String) getArguments().get(KEY_DEVICEID);

        settingBusiness = ErmuBusiness.getCamSettingBusiness();
        settingBusiness.registerListener(OnSetCronByTypeListener.class, this);

        // 获取录像设置信息，复制一份到当前类
        CamCvr camCvr = settingBusiness.getCamCvr(deviceId);
        if (camCvr != null && camCvr.getCron() != null) {
            CamCron camCron = camCvr.getCron();
            this.camCron.setRepeat(camCron.getRepeat());
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
            }
        }

        setTimeAndRepeat();

        return view;
    }

    @OnClick(value = {R.id.viewTime, R.id.viewRepeat})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewTime:
                ChooseTimeFragment fragment = (ChooseTimeFragment) ChooseTimeFragment.actionInstance(getActivity(), deviceId, textViewTime.getText().toString(), CronType.CVR);
                fragment.setChoiceTimeListener(this);
                addToBackStack(fragment);
                break;
            case R.id.viewRepeat:
                WeekCycleFragment fragment1 = (WeekCycleFragment) WeekCycleFragment.actionInstance(getActivity(), deviceId, CronType.CVR, repeat, 0);
                fragment1.setWeekCycleListener(this);
                addToBackStack(fragment1);
                break;
            default:
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setTimeAndRepeat();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        settingBusiness.unRegisterListener(OnSetCronByTypeListener.class, this);
    }

    @Override
    public void onSetCron(CronType type, Business business) {
        commitDialog.dismiss();
        if (business.isSuccess()) {
            popBackStack();
        } else {
            ErmuApplication.toast(getString(R.string.record_set_time_faild) + business.getErrorCode());
        }
    }

    /**
     * 设置时间段和重复
     */
    private void setTimeAndRepeat() {
        if (camCron != null) {
            String startTime = DateUtil.formatDate(camCron.getStart(), DateUtil.SHORT_TIME_FORMAT);
            String endTime = DateUtil.formatDate(camCron.getEnd(), DateUtil.SHORT_TIME_FORMAT);
            String time = getString(R.string.form_time) + " " + startTime + " " + getString(R.string.to_time) + " " + endTime;
            textViewTime.setText(time);

            repeat = camCron.getRepeat();
            if (repeat != null) {
                String week = getWeek(repeat);
                textViewRepeat.setText(week);
            }
        }
    }

    private String getWeek(CronRepeat repeat) {
        StringBuilder sb = new StringBuilder();
        boolean workDay = repeat.isMonday() && repeat.isTuesday() && repeat.isWednesday() && repeat.isThursday() && repeat.isFriday();
        boolean weekend = repeat.isSaturday() && repeat.isSunday();
        String str = getString(R.string.every_day);
        String mWorkDay = getString(R.string.work_day);
        if (workDay && weekend) {
            sb.append(str);
        } else if (workDay && !repeat.isSaturday() && !repeat.isSunday()) {
            sb.append(mWorkDay);
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
    public void choiceTime(Date startDate, Date endDate, boolean isSave) {
        camCron.setStart(startDate);
        camCron.setEnd(endDate);
        setTimeAndRepeat();
    }

    @Override
    public void weekCycle(CronRepeat cronRepeat, boolean isSave) {
        repeat = cronRepeat;
        setTimeAndRepeat();
    }
}
