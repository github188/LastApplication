package com.iermu.ui.fragment.camseting;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iermu.R;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.model.CamCron;
import com.iermu.client.model.constant.CronType;
import com.iermu.client.util.DateUtil;
import com.iermu.client.util.Logger;
import com.iermu.ui.fragment.BaseFragment;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.iermu.ui.fragment.camseting.CustomTimeFragment.*;

/**
 * Created by xjy on 15/6/25.
 */
public class  ChooseTimeFragment extends BaseFragment implements OnCustomTimeListener {

    @ViewInject(R.id.all_day_img)   ImageView mAllDayImg;
    @ViewInject(R.id.day_img)       ImageView mDayImg;
    @ViewInject(R.id.night_img)     ImageView mNightImg;
    @ViewInject(R.id.custom_img)    ImageView mCustomImg;
    @ViewInject(R.id.cos_time)      TextView mCosTime;

    private String allDay = "从00:00至23:59";
    private String Day = "从08:00至20:00";
    private String nightDay = "从20:00至08:00";
    private Date startDate;
    private Date endDate;
    private String time;
    private CamCron camCron;
    private CronType cronType;
    private static final String INTENT_CRONTYPE = "cron_type";
    private static final String INTENT_DEVICEID = "deviceId";
    private static final String INTENT_TIME = "time";
    private String deviceId;
    private boolean isSave = false;
    private OnChoiceTimeListener listener;

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(getString(R.string.setting_choice_time))
        .setCommonBackClick(new OnCommonClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
                if (listener != null && startDate != null && endDate != null) {
                    listener.choiceTime(startDate, endDate, isSave);
                }
            }
        });
    }

    public static Fragment actionInstance(FragmentActivity activity) {
        return new ChooseTimeFragment();
    }

    public void setChoiceTimeListener(OnChoiceTimeListener listener) {
        this.listener = listener;
    }

    public static Fragment actionInstance(FragmentActivity activity,String deviceId,String time,CronType cronType) {
        Fragment fragment = new ChooseTimeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_CRONTYPE, cronType);
        bundle.putString(INTENT_DEVICEID,deviceId);
        bundle.putString(INTENT_TIME,time);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_choose_time, container, false);
        ViewHelper.inject(this, view);
        deviceId = getArguments().getString(INTENT_DEVICEID);
        cronType = (CronType) getArguments().getSerializable(INTENT_CRONTYPE);
        time = getArguments().getString(INTENT_TIME);
        refreshCustomView();
        initView();
        return view;
    }

    @OnClick(value ={R.id.set_all_day,R.id.set_only_day,R.id.set_only_night,R.id.set_custom_time})
   public void onClick(View v){
         switch (v.getId()){
         case R.id.set_all_day:
             isSave = true;
             startDate = DateUtil.getBeginToday();
             endDate = DateUtil.getEndToday();
//             setCamCronTime(startDate,endDate);
             mAllDayImg.setVisibility(View.VISIBLE);
             mDayImg.setVisibility(View.GONE);
             mNightImg.setVisibility(View.GONE);
             mCustomImg.setVisibility(View.GONE);
             break;
         case R.id.set_only_day:
             isSave = true;
             startDate = DateUtil.moringTime(new Date());
             endDate = DateUtil.nightTime(new Date());
//             setCamCronTime(startDate,endDate);
             mAllDayImg.setVisibility(View.GONE);
             mDayImg.setVisibility(View.VISIBLE);
             mNightImg.setVisibility(View.GONE);
             mCustomImg.setVisibility(View.GONE);
             break;
         case R.id.set_only_night:
             isSave = true;
             startDate = DateUtil.nightTime(new Date());
             endDate = DateUtil.moringTime(new Date());
//             setCamCronTime(startDate,endDate);
             mAllDayImg.setVisibility(View.GONE);
             mDayImg.setVisibility(View.GONE);
             mNightImg.setVisibility(View.VISIBLE);
             mCustomImg.setVisibility(View.GONE);
             break;
         case R.id.set_custom_time:
             mAllDayImg.setVisibility(View.GONE);
             mDayImg.setVisibility(View.GONE);
             mNightImg.setVisibility(View.GONE);
             mCustomImg.setVisibility(View.VISIBLE);
             CustomTimeFragment fragment = (CustomTimeFragment) CustomTimeFragment.actionInstance(getActivity(),cronType,deviceId);
             fragment.setCustomTimeListener(this);
             addToBackStack(getActivity(), fragment);
             break;
         }
   }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (listener != null && startDate != null && endDate != null) {
                listener.choiceTime(startDate, endDate, isSave);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView() {
        if (!TextUtils.isEmpty(time)) {
            mAllDayImg.setVisibility(View.GONE);
            if (time.equals(allDay)) {
                mAllDayImg.setVisibility(View.VISIBLE);
            } else if(time.equals(Day)) {
                mDayImg.setVisibility(View.VISIBLE);
            } else if (time.equals(nightDay)) {
                mNightImg.setVisibility(View.VISIBLE);
            } else {
                mCustomImg.setVisibility(View.VISIBLE);
            }
        }
    }

    private void refreshCustomView() {
        ICamSettingBusiness business = ErmuBusiness.getCamSettingBusiness();
        if(cronType == CronType.POWER) {            //摄像机定时
            camCron = business.getCamCron(deviceId);
        } else if(cronType == CronType.CVR) {       //云录制定时
            camCron = business.getCvrCron(deviceId);
        } else if(cronType == CronType.ALARM) {     //消息报警定时
            camCron = business.getAlarmCron(deviceId);
        }
        if(camCron == null) return;
        Date startDate = camCron.getStart();
        Date endDate = camCron.getEnd();
        String startTime = getCurrentTime(startDate);
        String endTime = getCurrentTime(endDate);
        String form = getActivity().getString(R.string.form_time) + " ";
        String to = " " + getActivity().getString(R.string.to_time) + " ";
        String text = form + startTime + to + endTime;
        Logger.i(text);
        mCosTime.setText(text);
    }

    private String getCurrentTime(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String format = sdf.format(data);
        return format;
    }

    @Override
    public void customTime(Date startDate, Date endDate, boolean isSave) {
        this.startDate = startDate;
        this.endDate = endDate;
        String startTime = getCurrentTime(startDate);
        String endTime = getCurrentTime(endDate);
        this.isSave = isSave;
        String form = getActivity().getString(R.string.form_time) + " ";
        String to = " " + getActivity().getString(R.string.to_time) + " ";
        String text = form + startTime + to +endTime;
        mCosTime.setText(text);
    }

    public interface OnChoiceTimeListener{
        void choiceTime(Date startDate, Date endDate, boolean isSave);
    }
}
