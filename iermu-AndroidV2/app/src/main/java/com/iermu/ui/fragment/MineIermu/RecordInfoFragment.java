package com.iermu.ui.fragment.MineIermu;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.IMimeCamBusiness;
import com.iermu.client.IMineRecordBusiness;
import com.iermu.client.IUserCenterBusiness;
import com.iermu.client.listener.OnCamSettingListener;
import com.iermu.client.listener.OnRecordChangedListener;
import com.iermu.client.listener.OnScreenerPictureListener;
import com.iermu.client.listener.OnSetCronByTypeListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamCron;
import com.iermu.client.model.CamCvr;
import com.iermu.client.model.CamDate;
import com.iermu.client.model.CamLive;
import com.iermu.client.model.CronRepeat;
import com.iermu.client.model.ScreenClip;
import com.iermu.client.model.constant.CamSettingType;
import com.iermu.client.model.constant.CronType;
import com.iermu.client.model.constant.PhotoType;
import com.iermu.client.util.DateUtil;
import com.iermu.client.util.LanguageUtil;
import com.iermu.lan.model.CamRecord;
import com.iermu.ui.activity.WebActivity;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.util.Util;
import com.iermu.ui.view.CommonCommitDialog;
import com.iermu.ui.view.SwitchButtonNew;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhangxq on 16/1/21.
 */
public class RecordInfoFragment extends BaseFragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, OnCamSettingListener,
        OnSetCronByTypeListener, OnRecordChangedListener,
        OnScreenerPictureListener {
    private static final String KEY_DEVICEID = "deviceId";

    @ViewInject(R.id.imageViewLine)
    ImageView imageViewLine;
    @ViewInject(R.id.imageViewRedDot)
    ImageView imageViewRedDot;
    @ViewInject(R.id.textViewTop)
    TextView textViewTop;
    @ViewInject(R.id.textViewDayNum)
    TextView textViewDayNum;
    @ViewInject(R.id.textViewHourNum)
    TextView textViewHourNum;
    @ViewInject(R.id.textViewRecordNum)
    TextView textViewRecordNum;
    @ViewInject(R.id.switchButtonRecordAuto)
    SwitchButtonNew switchButtonRecordAuto;
    @ViewInject(R.id.viewSetTime)
    View viewSetTime;
    @ViewInject(R.id.textViewTimeInfo)
    TextView textViewTimeInfo;
    @ViewInject(R.id.textViewEndTime)
    TextView textViewEndTime;
    @ViewInject(R.id.textViewOverDateInfo)
    TextView textViewOverDateInfo;
    @ViewInject(R.id.imageViewSet)
    ImageView imageViewSet;
    @ViewInject(R.id.imageViewSwitchLoad)
    ImageView imageViewSwitchLoad;
    @ViewInject(R.id.imageViewSetLoad)
    ImageView imageViewSetLoad;

    private IMineRecordBusiness recordBusiness;
    private IMimeCamBusiness camBusiness;
    private ICamSettingBusiness settingBusiness;
    private IUserCenterBusiness userCenterBusiness;
    private String deviceId;
    private CamLive camLive;
    private CamCvr camCvr;
    private CommonCommitDialog commitDialog;
    private boolean isOverDate; // 记录云录制是否已过期

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(R.string.record_info);
        setCommonFinishHided();
    }

    /**
     * 打开录像信息页面
     *
     * @param deviceId
     * @return
     */
    public static Fragment actionInstance(String deviceId) {
        RecordInfoFragment fragment = new RecordInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_DEVICEID, deviceId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_card_info, null);
        ViewHelper.inject(this, view);

        AnimationDrawable animationDrawable = (AnimationDrawable) imageViewRedDot.getDrawable();
        animationDrawable.start();
        AnimationDrawable animationDrawable1 = (AnimationDrawable) imageViewLine.getDrawable();
        animationDrawable1.start();

        deviceId = (String) getArguments().get(KEY_DEVICEID);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        viewSetTime.setEnabled(false);

        // 初始化云录制信息
        camBusiness = ErmuBusiness.getMimeCamBusiness();
        camLive = camBusiness.getCamLive(deviceId);
        String cvrDay = camLive.getCvrDay(); // 云录制时间
        String remainDay = ""; // 剩余过期时间
        int cvrEndTime = (int) camLive.getCvrEndTime();
        String endTimeStr = DateUtil.formatDate(new Date(cvrEndTime * 1000l), DateUtil.LONG_DATE_FORMAT);
        textViewEndTime.setText(String.format(getString(R.string.record_end_day), " " + endTimeStr));
        int currentTime = (int) (new Date().getTime() / 1000);
        if (cvrEndTime > currentTime) {
            int remainDayNum = (cvrEndTime - currentTime) / DateUtil.DAY_SECOND_NUM;
            if ((cvrEndTime - currentTime) % DateUtil.DAY_SECOND_NUM > 0) {
                remainDayNum++;
            }
            remainDay = String.valueOf(remainDayNum);
            String overDayInfo = String.format(getString(R.string.record_over_day_info), " " + cvrDay + " ", " " + remainDay + " ");
            SpannableString spanOverDayInfo = new SpannableString(overDayInfo);
            int startIndexDay = overDayInfo.indexOf(cvrDay);
            int startIndexRemainDay = overDayInfo.indexOf(remainDay);

            // 处理获取的两个字符串位置相同的情况
            if (startIndexDay == startIndexRemainDay) {
                if (cvrDay.equals(remainDay)) {
                    startIndexRemainDay = overDayInfo.indexOf(remainDay, 1);
                } else {
                    if (Integer.parseInt(cvrDay) > Integer.parseInt(remainDay)) {
                        startIndexRemainDay = overDayInfo.indexOf(remainDay, 1);
                    } else {
                        startIndexDay = overDayInfo.indexOf(cvrDay, 1);
                    }
                }
            }
            spanOverDayInfo.setSpan(new AbsoluteSizeSpan(Util.DensityUtil.dip2px(getActivity(), 24)), startIndexDay, startIndexDay + cvrDay.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            spanOverDayInfo.setSpan(new ForegroundColorSpan(0xff00acef), startIndexDay, startIndexDay + cvrDay.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            spanOverDayInfo.setSpan(new AbsoluteSizeSpan(Util.DensityUtil.dip2px(getActivity(), 24)), startIndexRemainDay, startIndexRemainDay + remainDay.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            spanOverDayInfo.setSpan(new ForegroundColorSpan(0xff00acef), startIndexRemainDay, startIndexRemainDay + remainDay.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            textViewOverDateInfo.setText(spanOverDayInfo);
        } else {
            isOverDate = true;
            textViewEndTime.setVisibility(View.INVISIBLE);
            String endDate = DateUtil.formatDate(new Date(cvrEndTime * 1000l), DateUtil.LONG_DATE_FORMAT);
            String textOverDate = String.format(getString(R.string.record_over_day), " " + endDate);
            textViewOverDateInfo.setText(textOverDate);
            textViewOverDateInfo.setTextColor(0xffff6633);
            imageViewRedDot.setVisibility(View.GONE);
            imageViewLine.setVisibility(View.GONE);
            textViewTop.setText(getString(R.string.record_overdate));
        }

        // 初始化录像信息
        recordBusiness = ErmuBusiness.getMineRecordBusiness();
        recordBusiness.registerListener(OnRecordChangedListener.class, this);
        List<CamRecord> camRecords = recordBusiness.getRecordList(deviceId);
        if (camRecords.size() == 0) {
            recordBusiness.initData(deviceId, Integer.parseInt(camLive.getCvrDay()));
            recordBusiness.findRecordList(deviceId);
        }
        setRecordInfo();

        // 初始化剪辑信息
        userCenterBusiness = ErmuBusiness.getUserCenterBusiness();
        userCenterBusiness.registerListener(OnScreenerPictureListener.class, this);
        List<ScreenClip> videos = userCenterBusiness.getScreenClip(PhotoType.FILM_EDIT);
        if (videos == null || videos.size() == 0) {
            userCenterBusiness.getUserClip();
        }
        setFildEditInfo();

        // 初始化设置信息
        settingBusiness = ErmuBusiness.getCamSettingBusiness();
        settingBusiness.registerListener(OnCamSettingListener.class, this);
        settingBusiness.registerListener(OnSetCronByTypeListener.class, this);
        camCvr = settingBusiness.getCamCvr(deviceId);
        if (camCvr != null) {
            switchLoadStatus();
            if (camCvr.isCvr()) {
                setRecordStatus(true);
            } else {
                setRecordStatus(false);
            }
            CamCron camCron = camCvr.getCron();
            initCamCron(camCron);
            viewSetTime.setEnabled(true);
        } else {
            settingBusiness.syncCamCvr(deviceId);
        }
        setRecordPlanInfo();
        switchButtonRecordAuto.setOnCheckedChangeListener(this);

        return view;
    }

    @OnClick(value = {R.id.buttonBuy, R.id.viewSetTime})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonBuy:
                WebActivity.actionStartWeb(getActivity(), WebActivity.PAGE_CVRBUY);
                break;
            case R.id.viewSetTime:
                Fragment fragment = SetRecordTimeFragment.actionInstance(deviceId);
                addToBackStack(fragment);
                break;
            default:
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            camCvr = settingBusiness.getCamCvr(deviceId);
            setRecordPlanInfo();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        settingBusiness.unRegisterListener(OnCamSettingListener.class, this);
        settingBusiness.unRegisterListener(OnSetCronByTypeListener.class, this);
        recordBusiness.unRegisterListener(OnRecordChangedListener.class, this);
        userCenterBusiness.unRegisterListener(OnScreenerPictureListener.class, this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        CamCron camCron = null;
        if (camCvr == null) {
            camCvr = new CamCvr();
            camCron = new CamCron();
        } else {
            camCron = camCvr.getCron();
            if (camCron == null) {
                camCron = new CamCron();
            }
        }
        settingBusiness.setCvrCron(deviceId, isChecked, camCron.isCron(), camCron.getStart(), camCron.getEnd(), camCron.getRepeat());
        commitDialog = new CommonCommitDialog(getActivity());
        commitDialog.show();
        commitDialog.setStatusText(getString(R.string.dialog_commit));
    }

    @Override
    public void onCamSetting(CamSettingType type, String deviceId, Business business) {
        if (business.isSuccess()) {
            switchLoadStatus();
            camCvr = settingBusiness.getCamCvr(deviceId);
            if (camCvr != null) {
                if (camCvr.isCvr()) {
                    setRecordStatus(true);
                } else {
                    setRecordStatus(false);
                }
                CamCron camCron = camCvr.getCron();
                initCamCron(camCron);
                viewSetTime.setEnabled(true);
            }
            setRecordPlanInfo();
        } else {
            if (!isOverDate) {
                ErmuApplication.toast(getString(R.string.record_get_set_fail) + business.getErrorCode());
                String status = getResources().getString(R.string.off_line);
                if (camLive.isPowerOff()) {
                    status = getResources().getString(R.string.close);
                }
                textViewTimeInfo.setText(String.format(getString(R.string.record_can_not_get), " " + status));
            }
            switchLoadToFail();
        }
    }

    @Override
    public void onSetCron(CronType type, Business business) {
        commitDialog.dismiss();
        if (business.isSuccess()) {
            camCvr = settingBusiness.getCamCvr(deviceId);
            setRecordPlanInfo();
            setRecordStatus(camCvr.isCvr());
        } else {
            if (switchButtonRecordAuto.isChecked()) {
                switchButtonRecordAuto.setSwitchOn(true);
            } else {
                switchButtonRecordAuto.setSwitchOn(false);
            }
            ErmuApplication.toast(getString(R.string.record_set_faild) + business.getErrorCode());
        }
    }

    @Override
    public void onRecordChanged(Business business) {
        if (business.isSuccess()) {
            setRecordInfo();
        } else {
            if (!isOverDate) {
                ErmuApplication.toast(getString(R.string.record_get_faild) + business.getErrorCode());
            }
        }
    }

    @Override
    public void onScreenPicture(List<ScreenClip> pictures, int type) {
        setFildEditInfo();
    }

    /**
     * 打开或者关闭云录制
     *
     * @param isOpen
     */
    private void setRecordStatus(boolean isOpen) {
        if (isOpen) {
            if (!isOverDate) {
                imageViewRedDot.setVisibility(View.VISIBLE);
                imageViewLine.setVisibility(View.VISIBLE);
                textViewTop.setText(getString(R.string.record_opened));
            }
            switchButtonRecordAuto.setSwitchOn(true);
        } else {
            if (!isOverDate) {
                imageViewRedDot.setVisibility(View.GONE);
                imageViewLine.setVisibility(View.GONE);
                textViewTop.setText(getString(R.string.record_closed));
            }
            switchButtonRecordAuto.setSwitchOn(false);
        }
    }

    /**
     * 切换界面元素到非加载状态
     */
    private void switchLoadStatus() {
        switchButtonRecordAuto.setVisibility(View.VISIBLE);
        imageViewSet.setVisibility(View.VISIBLE);
        if (!isOverDate) {
            imageViewLine.setVisibility(View.VISIBLE);
            imageViewRedDot.setVisibility(View.VISIBLE);
        }
        imageViewSwitchLoad.setVisibility(View.INVISIBLE);
        imageViewSetLoad.setVisibility(View.INVISIBLE);
    }

    /**
     * 切换界面元素到失败状态
     */
    private void switchLoadToFail() {
        imageViewSwitchLoad.setVisibility(View.INVISIBLE);
        imageViewSetLoad.setVisibility(View.INVISIBLE);
        switchButtonRecordAuto.setVisibility(View.VISIBLE);
        switchButtonRecordAuto.setSwitchOn(false);
        switchButtonRecordAuto.setEnabled(false);
        viewSetTime.setEnabled(false);
    }

    /**
     * 设置录像计划说明文字
     */
    private void setRecordPlanInfo() {
        String week = "";
        String time = "";
        if (camCvr != null) {
            CamCron camCron = camCvr.getCron();
            if (camCron != null) {
                String startTime = DateUtil.formatDate(camCron.getStart(), DateUtil.SHORT_TIME_FORMAT);
                String endTime = DateUtil.formatDate(camCron.getEnd(), DateUtil.SHORT_TIME_FORMAT);
                time = startTime + " " + getString(R.string.to_time) + " " + endTime + " ";

                CronRepeat repeat = camCron.getRepeat();

                if (repeat != null) {
                    week = getWeek(repeat);
                }
                if (TextUtils.isEmpty(week) || TextUtils.isEmpty(time)) {
                    week = getString(R.string.every_day);
                }
            } else {
                week = getString(R.string.every_day);
                time = "00:00 " + getString(R.string.to_time) + " 23:59";
            }
        } else {
            week = getString(R.string.every_day);
            time = "00:00 " + getString(R.string.to_time) + " 23:59";
        }

        String info = week + "，" + time + getString(R.string.record_find_exception);
        textViewTimeInfo.setText(info);
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

    /**
     * 初始化camCron
     */
    private void initCamCron(CamCron camCron) {
        if (camCron == null) {
            camCron = new CamCron();
            Date startDate = DateUtil.getBeginToday();
            Date endDate = DateUtil.getEndToday();
            camCron.setStart(startDate);
            camCron.setEnd(endDate);
            camCvr.setCron(camCron);
        }

        CronRepeat cronRepeat = camCron.getRepeat();
        if (cronRepeat == null) {
            cronRepeat = new CronRepeat();
            cronRepeat.setDefault();
            camCron.setRepeat(cronRepeat);
        } else {
            boolean hasOneDay = cronRepeat.isMonday() || cronRepeat.isTuesday()
                    || cronRepeat.isWednesday() || cronRepeat.isThursday()
                    || cronRepeat.isFriday() || cronRepeat.isSaturday()
                    || cronRepeat.isSunday();
            if (!hasOneDay) {
                cronRepeat.setDefault();
            }
        }
    }

    /**
     * 设置录像信息
     */
    private void setRecordInfo() {
        List<CamDate> camDates = recordBusiness.getDayTimeList(deviceId);
        int dayNum = 0; // 已录制天数
        for (CamDate camDate : camDates) {
            if (camDate.isExistRecord()) {
                dayNum++;
            }
        }
        List<CamRecord> camRecords = recordBusiness.getRecordList(deviceId);
        long secondNum = 0; // 录制总秒数
        for (CamRecord camRecord : camRecords) {
            secondNum += (camRecord.getEndTime() - camRecord.getStartTime());
        }
        long hourNum = secondNum / 3600; // 录制总时长

        SpannableString dayNumStr = new SpannableString(dayNum + " " + getString(R.string.record_day));
        dayNumStr.setSpan(new AbsoluteSizeSpan(Util.DensityUtil.dip2px(getActivity(), 24)), 0, String.valueOf(dayNum).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        textViewDayNum.setText(dayNumStr);
        SpannableString hourNumStr = new SpannableString(hourNum + " " + getString(R.string.record_hour));
        hourNumStr.setSpan(new AbsoluteSizeSpan(Util.DensityUtil.dip2px(getActivity(), 24)), 0, String.valueOf(hourNum).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        textViewHourNum.setText(hourNumStr);
    }

    /**
     * 设置剪辑信息
     */
    private void setFildEditInfo() {
        List<ScreenClip> videos = userCenterBusiness.getScreenClip(PhotoType.FILM_EDIT);
        if (videos == null) {
            videos = new ArrayList<ScreenClip>();
        }
        int filmEditSize = videos.size(); // 保存的视频段数
        SpannableString recordNumStr = new SpannableString(filmEditSize + " " + getString(R.string.record_section));
        recordNumStr.setSpan(new AbsoluteSizeSpan(Util.DensityUtil.dip2px(getActivity(), 24)), 0, String.valueOf(filmEditSize).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        textViewRecordNum.setText(recordNumStr);
    }
}
