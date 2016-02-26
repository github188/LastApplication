package com.iermu.ui.fragment.camseting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.iermu.R;
import com.iermu.client.ErmuApplication;
import com.iermu.client.ErmuBusiness;
import com.iermu.client.ICamSettingBusiness;
import com.iermu.client.listener.OnCamSettingListener;
import com.iermu.client.listener.OnSetCamEmailCronListener;
import com.iermu.client.listener.OnSetDevLevelListener;
import com.iermu.client.model.Business;
import com.iermu.client.model.CamAlarm;
import com.iermu.client.model.constant.BusinessCode;
import com.iermu.client.model.constant.CamSettingType;
import com.iermu.ui.fragment.BaseFragment;
import com.iermu.ui.view.CommonCommitDialog;
import com.iermu.ui.view.SwitchButtonNew;
import com.viewinject.ViewHelper;
import com.viewinject.annotation.ViewInject;
import com.viewinject.annotation.event.OnClick;

/**
 * Created by xjy on 15/7/22.
 */
public class CamAlarmFramgent extends BaseFragment implements View.OnClickListener, OnCamSettingListener, CompoundButton.OnCheckedChangeListener, OnSetCamEmailCronListener, OnSetDevLevelListener {

    @ViewInject(R.id.alarm_eamil)   RelativeLayout mEmail;
    @ViewInject(R.id.email_switch)  SwitchButtonNew mEmailNotify;
    @ViewInject(R.id.level)         RelativeLayout level;
    @ViewInject(R.id.low_level)     RelativeLayout emLow;
    @ViewInject(R.id.heigh_level)   RelativeLayout mHeith;
    @ViewInject(R.id.low_img)       ImageView mLowImg;
    @ViewInject(R.id.level_img)     ImageView mLevelImg;
    @ViewInject(R.id.heigh_img)     ImageView mHeighImg;

    CommonCommitDialog dialog;
    private boolean isCron;
    private static int LEVEL = -1;
    private String deviceId;
    private ICamSettingBusiness business;
    private CamAlarm alarm;
    private static final String INTENT_DEVICEID = "deviceid";

    @Override
    protected void onCreateActionBar(BaseFragment fragment) {
        setCommonActionBar(R.string.set_police);
    }

    public static Fragment actionInstance(FragmentActivity activity, String deviceId) {
        Fragment fragment = new CamAlarmFramgent();
        Bundle bundle = new Bundle();
        bundle.putString(INTENT_DEVICEID, deviceId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_alarm, container, false);
        ViewHelper.inject(this, view);
        mEmailNotify.setOnCheckedChangeListener(this);

        deviceId = getArguments().getString(INTENT_DEVICEID);
        business = ErmuBusiness.getCamSettingBusiness();
        business.registerListener(OnCamSettingListener.class, this);
        business.registerListener(OnSetCamEmailCronListener.class, this);
        business.registerListener(OnSetDevLevelListener.class, this);
//        business.syncCamSetting(deviceId);
        refreshView();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ICamSettingBusiness business = ErmuBusiness.getCamSettingBusiness();
        business.unRegisterListener(OnCamSettingListener.class, this);
        business.unRegisterListener(OnSetCamEmailCronListener.class, this);
        business.unRegisterListener(OnSetDevLevelListener.class, this);
    }

    @OnClick(value = {R.id.alarm_eamil, R.id.level, R.id.heigh_level, R.id.low_level})
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.alarm_eamil:
            Fragment fragment = CamEmailFragment.actionInstance(getActivity(), deviceId);
            addToBackStack(getActivity(), fragment);
            break;
        case R.id.low_level:
            LEVEL = 0;
            setLevel(LEVEL, true);
            break;
        case R.id.level:
            LEVEL = 1;
            setLevel(LEVEL, true);
            break;
        case R.id.heigh_level:
            LEVEL = 2;
            setLevel(LEVEL, true);
            break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String str = getResources().getString(R.string.dialog_commit);
        isCron = isChecked;
        dialog = new CommonCommitDialog(getActivity());
        dialog.show();
        dialog.setStatusText(str);
        ErmuBusiness.getCamSettingBusiness().setCamEamilCron(deviceId, isChecked);
        if (isChecked) {
            mEmail.setVisibility(View.VISIBLE);
        } else {
            mEmail.setVisibility(View.INVISIBLE);
        }
    }

    private void setLevel(int level, boolean isUpdate) {
        String str = getResources().getString(R.string.dialog_commit);
        if (isUpdate) {
            dialog = new CommonCommitDialog(getActivity());
            dialog.show();
            dialog.setStatusText(str);
            ErmuBusiness.getCamSettingBusiness().setAlarmMoveLevel(deviceId, level);
        }
        switch (level) {
        case 0:
            mLowImg.setVisibility(View.VISIBLE);
            mLevelImg.setVisibility(View.GONE);
            mHeighImg.setVisibility(View.GONE);
            break;
        case 1:
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

    private void refreshView() {
        alarm = ErmuBusiness.getCamSettingBusiness().getCamAlarm(deviceId);
        if (alarm == null) return;
        int move = alarm.getMoveLevel();
        boolean mail = alarm.isMail();
        setLevel(move, false);
        if (mail) {
            mEmailNotify.setSwitchOn(true);
            mEmail.setVisibility(View.VISIBLE);
        } else {
            mEmailNotify.setSwitchOn(false);
            mEmail.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSetAlarmMial(Business bus,boolean isCron) {
        switch (bus.getCode()) {
            case BusinessCode.CONNECT_API_FAILED:
            case BusinessCode.SEND_COMMAND_FAILED:
            case BusinessCode.UPDATE_DEVSETTING_FAILED:
                mEmailNotify.setSwitchOn(isCron);
                if (dialog != null) dialog.dismiss();
                ErmuApplication.toast(getString(R.string.network_error_please_check) +"("+bus.getErrorCode()+")");
                break;
            case BusinessCode.SUCCESS:
                if (dialog != null) dialog.dismiss();
                //ErmuApplication.toast("设置成功");
                break;
            default:
                mEmailNotify.setSwitchOn(isCron);
                if (dialog != null) dialog.dismiss();
                ErmuApplication.toast(getString(R.string.network_error_please_check) +"("+bus.getErrorCode()+")");
                break;
        }
    }

    @Override
    public void onSetDevLevel(Business bus) {
        if (dialog != null) dialog.dismiss();
        switch (bus.getCode()) {
            case BusinessCode.CONNECT_API_FAILED:
            case BusinessCode.SEND_COMMAND_FAILED:
            case BusinessCode.UPDATE_DEVSETTING_FAILED:
                ErmuApplication.toast(getString(R.string.network_error_please_check) +"("+bus.getErrorCode()+")");
                break;
            case BusinessCode.SUCCESS:
                break;
            default:
                ErmuApplication.toast(getString(R.string.network_error_please_check) +"("+bus.getErrorCode()+")");
                break;
        }
    }

    @Override
    public void onCamSetting(CamSettingType type, String deviceId, Business business) {
        if(dialog != null)  dialog.dismiss();
        refreshView();
        if (!business.isSuccess()) {
            ErmuApplication.toast(getString(R.string.network_error_please_check) +"("+business.getErrorCode()+")");
        }
    }
}
